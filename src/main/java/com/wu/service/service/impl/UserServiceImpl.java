package com.wu.service.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.wu.service.entity.User;
import com.wu.service.entity.vo.FindPasswordVo;
import com.wu.service.entity.vo.RegisterVo;
import com.wu.service.excepyionhandler.SpaceException;
import com.wu.service.mapper.UcenterMemberMapper;
import com.wu.service.service.UserService;
import com.wu.service.utils.JwtUtils;
import com.wu.service.utils.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class UserServiceImpl extends ServiceImpl<UcenterMemberMapper, User> implements UserService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //登录的方法
    public String login(User member) {
        //获取登录手机号和密码
        String account = member.getAccount();
        String password = member.getPassword();
        System.out.println(account + password);
        //手机号和密码非空判断
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            throw new SpaceException(20001, "登录失败");
        }
        //判断手机号是否正确
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account", account);
        User mobileMember = baseMapper.selectOne(wrapper);
        //判断查询对象是否为空
        if (mobileMember == null) {//没有这个手机号
            throw new SpaceException(20001, "登录失败");
        }

        //判断密码
        //因为存储到数据库密码肯定加密的
        //把输入的密码进行加密，再和数据库密码进行比较
        //加密方式 MD5
        if (!MD5.encrypt(password).equals(mobileMember.getPassword())) {
            throw new SpaceException(20001, "登录失败");
        }
        //登录成功
        //生成token字符串，使用jwt工具类
        String jwtToken = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getNickname());
        QueryWrapper<User> wrapper1=new QueryWrapper<>();
        wrapper1.eq("account",member.getAccount());
        User user = baseMapper.selectOne(wrapper1);
        return jwtToken;
    }

    //登录的方法
    public User login1(User member) {
        //获取登录手机号和密码
        String account = member.getAccount();
        String password = member.getPassword();
        System.out.println(account + password);
        //手机号和密码非空判断
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            throw new SpaceException(20001, "手机号或密码为空，登录失败");
        }
        //判断手机号是否正确
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account", account);
        User mobileMember = baseMapper.selectOne(wrapper);
        //判断查询对象是否为空
        if (mobileMember == null) {//没有这个手机号
            throw new SpaceException(20001, "未注册，登录失败");
        }

        //判断密码
        //因为存储到数据库密码肯定加密的
        //把输入的密码进行加密，再和数据库密码进行比较
        //加密方式 MD5
        if (!MD5.encrypt(password).equals(mobileMember.getPassword())) {
            throw new SpaceException(20001, "登录失败");
        }
        //登录成功
        //生成token字符串，使用jwt工具类
        //String jwtToken = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getNickname());
        QueryWrapper<User> wrapper1=new QueryWrapper<>();
        wrapper1.eq("account",member.getAccount());
        User user = baseMapper.selectOne(wrapper1);
        return user;
    }
    //注册的方法
    public void register(RegisterVo registerVo) {
        //获取注册的数据
        String code = registerVo.getCode(); //验证码
        String mobile = registerVo.getAccount(); //手机号
        String nickname = registerVo.getNickname(); //昵称
        String password = registerVo.getPassword(); //密码
        String avatar=registerVo.getAvatar();//头像
        //非空判断
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(code) || StringUtils.isEmpty(nickname)
                ) {
            throw new SpaceException(20001, "不能为空");
        }
        //判断验证码
        //获取redis验证码
        String redisCode = redisTemplate.opsForValue().get(mobile);

        System.out.println(redisCode);
        if (!code.equals(redisCode)) {
            throw new SpaceException(20001, "验证码错误");
        }


        //判断手机号是否重复，表里面存在相同手机号不进行添加
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account", mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new SpaceException(20001, "手机号已存在，注册失败！");
        }
        //数据添加数据库中
        User member = new User();
        member.setAccount(mobile);
        member.setNickname(nickname);
        member.setAvatar(avatar);
        member.setPassword(MD5.encrypt(password));//密码需要加密的
        baseMapper.insert(member);
    }

    //注册的方法  邮箱注册
    @Override
    public void registerByEmail(RegisterVo registerVo) {
        //获取注册的数据
        String code = registerVo.getCode(); //验证码
        String email = registerVo.getAccount(); //邮箱
        String nickname = registerVo.getNickname(); //昵称
        String password = registerVo.getPassword(); //密码
        String avatar=registerVo.getAvatar();//头像
        //非空判断
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(code) || StringUtils.isEmpty(nickname)
        ) {
            throw new SpaceException(20001, "不能为空");
        }
        //判断验证码
        //获取redis验证码
        String redisCode = redisTemplate.opsForValue().get(email);
        System.out.println(redisCode);
        if (!code.equals(redisCode)) {
            throw new SpaceException(20001, "验证码错误");
        }


        //判断邮箱是否重复，表里面存在相同邮箱不进行添加
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account",email);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new SpaceException(20001, "手机号已存在，注册失败！");
        }
        //数据添加数据库中
        User member = new User();
        member.setAccount(email);
        member.setNickname(nickname);
        member.setAvatar(avatar);
        member.setPassword(MD5.encrypt(password));//密码需要加密的
        baseMapper.insert(member);
    }

    //找回密码
    @Override
    public int findPassword(FindPasswordVo findPasswordVo) {
        //获取注册的数据
        String code = findPasswordVo.getCode(); //验证码
        String account = findPasswordVo.getAccount(); //邮箱
        String password = findPasswordVo.getPassword(); //密码
        //非空判断
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(code)
        ) {
            throw new SpaceException(20001, "不能为空");
        }
        //判断验证码
        //获取redis验证码
        String redisCode = redisTemplate.opsForValue().get(account);
        System.out.println(redisCode);
        if (!code.equals(redisCode)) {
            throw new SpaceException(20001, "验证码错误");
        }
        //数据更新
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account);
        User member = baseMapper.selectOne(wrapper);
        if(member == null ) {
            throw new SpaceException(20001, "没用这个账户");
        }
        member.setPassword(MD5.encrypt(password));//密码需要加密的
        int isupdate = baseMapper.update(member,wrapper);
        return isupdate;
    }



//    @Override
//    public boolean updateMember(User ucenterMember) {
//        String mobile = ucenterMember.getMobile(); //手机号
//        String nickname = ucenterMember.getNickname(); //昵称
//        String password = ucenterMember.getPassword(); //密码
//        String avatar=ucenterMember.getAvatar();//头像
//    }

}
