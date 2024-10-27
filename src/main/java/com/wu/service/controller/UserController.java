package com.wu.service.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wu.service.entity.User;
import com.wu.service.entity.vo.FindPasswordVo;
import com.wu.service.entity.vo.RegisterVo;
import com.wu.service.service.UserService;
import com.wu.service.utils.KeyGenerator;
import com.wu.service.utils.MD5;
import com.wu.service.utils.R;
import com.wu.service.utils.AES;
import com.wu.service.utils.PublicKeyDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.net.URLDecoder;

import static com.wu.service.utils.RSADecryption.decryptWithPrivateKey;


@RestController
@RequestMapping("/educenter/member")
@CrossOrigin
//@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    //登录
    @ApiOperation(value = "登录")
    @PostMapping("login")
    public R loginUser(@RequestBody User user) {
        //member对象封装手机号和密码
        //调用service方法实现登录
        //返回token值，使用jwt生成
        String token= userService.login(user);
        User mem=userService.login1(user);
        //System.out.println(mem);
        Map<String, Object> thing = new HashMap<>();
        thing.put("token",token);
        thing.put("mem",mem);
        return R.ok().data("thing", thing);
    }

    //会话密钥第二次握手 ，返回公钥
    @ApiOperation(value = "获取服务器公钥")
    @PostMapping("getpublickey")
    public R getPublicKey(@RequestBody String account) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        // 先获取KeyStore 并加载文件
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream("keystore.jks"), "1234".toCharArray());

        // 获取证书
        Certificate certificate = keyStore.getCertificate("mykeypair");

        // 获取公钥
        PublicKey publicKey = certificate.getPublicKey();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

        String base64PublicKey = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());

        return R.ok().data("publicKey", base64PublicKey);
    }

    // 会话密钥第三次握手，接收前端RSA加密后的会话密钥
    @ApiOperation(value = "获取会话密钥")
    @PostMapping("posttmpkey/{id}")
    public R PostTmpKey(@RequestBody String secret,@PathVariable String id,User user) throws Exception {
        //获取服务器私钥
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream("keystore.jks"), "1234".toCharArray());
        PrivateKey privateKey = (PrivateKey) keyStore.getKey("mykeypair", "12345".toCharArray());
        String base64 = URLDecoder.decode(secret,"UTF-8");
        System.out.println(base64);
        //base64解密
        byte[] encryptedSessionKeyBytes = Base64.getMimeDecoder().decode(base64);
        System.out.println(Arrays.toString(encryptedSessionKeyBytes));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        // 会话密钥
        String outStr = new String(cipher.doFinal(encryptedSessionKeyBytes));
        System.out.println(id);
        System.out.println(outStr);

        //会话密钥存储sql
        QueryWrapper<User> w=new QueryWrapper<>();
        w.eq("id",id);
        User user1 =new User();
        user1.setId(user.getId());
        user1.setSessionkey(outStr);
        boolean b = userService.updateById(user1);
        if (b){
            return R.ok();
        }else{
            return R.error();
        }
    }

    //注册
    @PostMapping("register")
    public R registerUser(@RequestBody RegisterVo registerVo) {
        userService.register(registerVo);
        return R.ok();
    }

    //找回密码
    @PostMapping("findpassword")
    public R findPassword(@RequestBody FindPasswordVo findPasswordVo) throws Exception {

        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("account",findPasswordVo.getAccount());
        User user = userService.getOne(wrapper);
        String temKey = user.getSessionkey();


        FindPasswordVo newVo = new FindPasswordVo();

        newVo.setAccount(findPasswordVo.getAccount());
        newVo.setPassword(AES.decrypt(findPasswordVo.getPassword(),temKey));
        newVo.setCode(AES.decrypt(findPasswordVo.getCode(),temKey));

        int isupdate = userService.findPassword(newVo);
        if(isupdate>0) {return R.ok();}
        else {return R.error();}
    }

    //查询用户信息
    @ApiOperation(value = "根据用户表id查询用户信息")
    @GetMapping("getMemberInfo/{id}")
    public R getMemberInfo(@PathVariable String id) throws Exception {
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        User user = userService.getOne(wrapper);
        String temKey = user.getSessionkey();

        User user1 =new User();

        user1.setId(user.getId());
        user1.setNeicun(user.getNeicun());
        user1.setAvatar(user.getAvatar());
        user1.setNickname(user.getNickname());
        user1.setPassword(AES.encrypt(user.getPassword(), temKey));
        user1.setAccount(AES.encrypt(user.getAccount(), temKey));
        user1.setSessionkey(AES.encrypt(user.getSessionkey(), temKey));
        user1.setPublickey(AES.encrypt(user.getPublickey(),temKey));

        return R.ok().data("member", user1);
    }

    //修改用户信息
    @ApiOperation(value = "更新用户信息")
    @PostMapping("updateMemberInfo")
    public R updateMemberInfo(@RequestBody User user) throws Exception {

        System.out.println(user);
        String id = user.getId();
        QueryWrapper<User> w=new QueryWrapper<>();
        w.eq("id",id);

        User one = userService.getOne(w);

        String temKey =one.getSessionkey();

        User user1 =new User();
        user1.setId(user.getId());
        user1.setNeicun(one.getNeicun());
        user1.setAvatar(one.getAvatar());
        user1.setNickname(AES.decrypt(user.getNickname(),temKey));
        user1.setPassword(MD5.encrypt(AES.decrypt(user.getPassword(), temKey)));
        System.out.println(AES.decrypt(user.getNickname(),temKey));
        boolean b = userService.updateById(user1);
        if (b){
            return R.ok();
        }else{
            return R.error();
        }
    }

    //修改密码和昵称
    @ApiOperation(value = "更新用户密码")
    @PostMapping("updatepassword")
    public R updatePassword(@RequestBody User user) throws Exception {
        boolean flag = true;
        String id = user.getId();
        QueryWrapper<User> w=new QueryWrapper<>();
        w.eq("id",id);

        User one = userService.getOne(w);
        if (!MD5.encrypt(AES.decrypt(user.getPassword(),one.getSessionkey())).equals(one.getPassword())) {flag = false;return R.error();};

        String temKey =one.getSessionkey();

        User user1 =new User();
        user1.setId(user.getId());
        user1.setNeicun(one.getNeicun());
        user1.setAvatar(one.getAvatar());
        user1.setNickname(AES.decrypt(user.getNickname(),temKey));
        user1.setPassword(MD5.encrypt(AES.decrypt(user.getNewpassword(), temKey)));
        System.out.println(AES.decrypt(user.getNickname(),temKey));
        flag = userService.updateById(user1);
        if (flag){
            return R.ok();
        }else{
            return R.error();
        }
    }

    //修改用户信息 通过account修改，主要用于公钥
    @ApiOperation(value = "更新公钥")
    @PostMapping("updatePublicKey")
    public R updatePublicKey(@RequestBody User user) throws Exception {
        String account = user.getAccount();
        QueryWrapper<User> w=new QueryWrapper<>();
        w.eq("account",account);
        User one = userService.getOne(w);
        User user1 =new User();
        user1.setId(one.getId());
        user1.setPublickey(user.getPublickey());
        boolean b = userService.updateById(user1);
        if (b){
            return R.ok();
        }else{
            return R.error();
        }
    }
}

