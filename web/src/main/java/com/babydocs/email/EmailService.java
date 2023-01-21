package com.babydocs.email;

import com.babydocs.constants.AppConstants;
import com.babydocs.model.Mail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    public void sendMail(Mail mail, MailType mailType) {
        if (mailType == MailType.PASSWORD_RESET) {
            setUpPasswordResetMail(mail);
        }
    }

    private void setUpPasswordResetMail(Mail mail) {
        final Context context = new Context();
        context.setVariable("app_name", AppConstants.APP_NAME);
        context.setVariable("team", AppConstants.TEAM);
        context.setVariable("name", "Hi " + mail.getName() + ",");
        context.setVariable("message", mail.getMessage());
        context.setVariable("code", "Code: " + mail.getRandomCode());
        context.setVariable("valid", AppConstants.FORGOT_PASS_VALID_MINS);
        context.setVariable("link", mail.getUsername());
        String body = templateEngine.process("reset-password-template", context);
        //  System.out.println(body);
        // send the html template
        sendPreparedMail(mail.getToEmail(), mail.getSubject(), body, true);
    }

    private void sendPreparedMail(String to, String subject, String text, Boolean isHtml) {
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, isHtml);
            javaMailSender.send(mail);
        } catch (Exception e) {
            log.error("Unable to send email:" + e);
            throw new RuntimeException("Unable to send email:" + e);

        }
    }

}
