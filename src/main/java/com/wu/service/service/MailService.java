package com.wu.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class MailService {

    @Autowired
    private  JavaMailSender mailSender;
    public boolean send(String code, String email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("lsr3066454871@qq.com");
            message.setTo(email);
            message.setSubject("CtrlYun验证码");
            message.setText("验证码：" + code);

            mailSender.send(message);

            return true; // 邮件发送成功
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 邮件发送失败
        }
    }
}
