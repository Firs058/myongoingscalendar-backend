package org.myongoingscalendar.manipulations;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.myongoingscalendar.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class EmailManipulations {

    private final JavaMailSender javaMailSender;

    @Autowired
    EmailManipulations(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendRegistrationMail(String domainAddress, UserEntity user) {
        try {
            String confirmationUrl = domainAddress + "/confirm/registration?token=" + user.confirmToken();
            MimeMessage mime = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true);
            String year = (new DateTime().getYear() == 2017) ? String.valueOf(new DateTime().getYear()) : "2017 - " + new DateTime().getYear();
            helper.setFrom("myongoingscalendar@gmail.com");
            helper.setTo(user.email());
            helper.setSubject("MyOngoingsCalendar confirm registration");
            String htmlText = "<div style='background:#111111;text-align:center;" +
                    "font-size:12pt;font-weight:bold;color:#CCCCCC;padding:10px;'>" +
                    "To complete registration and activate you account, follow this link:" +
                    "<br /><br />" +
                    confirmationUrl +
                    "<br /><br />" +
                    "© MyOngoingsCalendar, " + year +
                    "</div>";
            helper.setText(htmlText, true);
            this.javaMailSender.send(mime);
        } catch (MessagingException e) {
            log.error("Cant send email: ", e.toString());
        }
    }

    @Async
    public void sendRecoverPassMail(String domainAddress, UserEntity user) {
        try {
            String confirmationUrl = domainAddress + "/confirm/recover?token=" + user.recoverToken();
            MimeMessage mime = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true);
            String year = (new DateTime().getYear() == 2017) ? String.valueOf(new DateTime().getYear()) : "2017 - " + new DateTime().getYear();
            helper.setFrom("myongoingscalendar@gmail.com");
            helper.setTo(user.email());
            helper.setSubject("MyOngoingsCalendar password recover");
            String htmlText = "<div style='background:#111111;text-align:center;" +
                    "font-size:12pt;font-weight:bold;color:#CCCCCC;padding:10px;'>" +
                    "For recover pass follow this link AND SET NEW PASSWORD:" +
                    "<br /><br />" +
                    confirmationUrl +
                    "<br /><br />" +
                    "© MyOngoingsCalendar, " + year +
                    "</div>";
            helper.setText(htmlText, true);
            this.javaMailSender.send(mime);
        } catch (MessagingException e) {
            log.error("Cant send email: ", e.toString());
        }
    }
}