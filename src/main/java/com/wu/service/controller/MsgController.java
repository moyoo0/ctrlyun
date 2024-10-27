package com.wu.service.controller;


import com.wu.service.service.MsmService;
import com.wu.service.service.impl.SmsSampleImpl;
import com.wu.service.service.MailService;
import com.wu.service.utils.R;
import com.wu.service.utils.RandomUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;



import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/edumsm/msm")
//@CrossOrigin
@CrossOrigin
public class MsgController {

    private final MsmService msmService;
    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;

    public MsgController(MsmService msmService, MailService mailService, RedisTemplate<String, String> redisTemplate) {
        this.msmService = msmService;
        this.mailService = mailService;
        this.redisTemplate = redisTemplate;
    }

    @ApiOperation(value = "发送验证码")
    @GetMapping("sendmobile/{phone}")
    public R code(@PathVariable String phone){
       // System.out.println(phone);
       // String code = redisTemplate.opsForValue().get(phone);

        //if(!StringUtils.isEmpty(code)) return R.error().message("短信发送失败");


        //Map<String, Object> param = new HashMap<>();
        //param.put("code", code);
        String random = RandomUtil.getFourBitRandom();
        //boolean isSend = msmService.send(param, phone);
        boolean isSend = SmsSampleImpl.send(random, phone);
        if(isSend){
            redisTemplate.opsForValue().set(phone, random, 5, TimeUnit.MINUTES);
            return R.ok();
        }else {
            return R.error().message("短信发送失败");
        }
    }

    @ApiOperation(value = "发送验证码")
    @GetMapping("sendmail/{emai}")
    public R sesdMsmmail(@PathVariable String emai) {
        //String code = redisTemplate.opsForValue().get(phone);
        //System.out.println(code + "111");
        //if (!StringUtils.isEmpty(code)) {
            //System.out.println("2");
            //return R.error().message("邮件发送失败");
        //}
        //生成随机验证码
        String random = RandomUtil.getFourBitRandom();
        boolean isSend =mailService.send(random,emai);
        if (isSend) {
            //发送成功放到redis里面
            redisTemplate.opsForValue().set(emai, random, 5, TimeUnit.MINUTES);
            return R.ok();
        } else {
            return R.error().message("短信发送失败");
        }
    }
}