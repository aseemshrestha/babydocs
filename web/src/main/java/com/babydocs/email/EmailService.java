package com.babydocs.email;

import com.babydocs.constants.AppConstants;
import com.babydocs.model.Mail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

@Component
@Slf4j
public class EmailService {
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendMail(Mail mail, MailType mailType) {
        switch (mailType) {
            case PASSWORD_RESET -> setUpPasswordResetMail(mail);
            case WELCOME -> setUpWelcomeMail(mail);
        }
    }

    private void setUpPasswordResetMail(Mail mail) {
        final Context context = new Context();
        context.setVariable("app_name", AppConstants.APP_NAME);
        context.setVariable("team", AppConstants.TEAM);
        context.setVariable("name", "Dear " + StringUtils.capitalize(mail.getName()) + ",");
        context.setVariable("message", mail.getMessage());
        context.setVariable("code", "Code: " + mail.getRandomCode());
        context.setVariable("valid", AppConstants.ForgotPassword.validMins);
        context.setVariable("link", mail.getUsername());
        String body = templateEngine.process("reset-password-template", context);
        sendPreparedMail(mail.getToEmail(), mail.getSubject(), body);
    }

    private void setUpWelcomeMail(Mail mail) {
        final Context context = new Context();
        context.setVariable("app_name", AppConstants.APP_NAME);
        context.setVariable("team", AppConstants.TEAM);
        context.setVariable("name", "Dear " + StringUtils.capitalize(mail.getName()) + ",");
        context.setVariable("message", mail.getMessage());
        context.setVariable("link", mail.getUsername());
        String body = templateEngine.process("welcome-msg-template", context);
        sendPreparedMail(mail.getToEmail(), mail.getSubject(), body);
    }

    private void sendPreparedMail(String to, String subject, String text) {
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
          //  javaMailSender.send(mail);
        } catch (Exception e) {
            log.error("Unable to send email:" + e);
            throw new RuntimeException("Unable to send email:" + e);

        }
    }

}
