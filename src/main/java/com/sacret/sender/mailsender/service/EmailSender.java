package com.sacret.sender.mailsender.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailSender {

    JavaMailSender mailSender;

    public void sendEmail(String [] emails) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setSubject("Останови войну пока еще не поздно!");
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String text = new String(IOUtils.toByteArray(Objects.requireNonNull(this.getClass().getResource("/template/emailTemplate.html"))));
        helper.setText(text,true);
        helper.setTo(emails);
        mailSender.send(message);

        LOG.info(Arrays.asList(emails).toString());
    }
}
