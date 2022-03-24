package com.sacret.sender.mailsender.service;

import com.sacret.sender.mailsender.config.Constants;
import com.sacret.sender.mailsender.model.entity.Email;
import com.sacret.sender.mailsender.model.entity.EmailHistory;
import com.sacret.sender.mailsender.model.entity.EmailJob;
import com.sacret.sender.mailsender.model.enumaration.EmailStatus;
import com.sacret.sender.mailsender.model.enumaration.JobStatus;
import com.sacret.sender.mailsender.repository.EmailRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailSenderService {

    StatusService statusService;

    EmailRepository emailRepository;
    JavaMailSender emailSender;
    Constants constants;

    public void sendEmail(String [] emails) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        message.setSubject("Останови войну пока еще не поздно!");
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String text = new String(IOUtils.toByteArray(Objects.requireNonNull(this.getClass().getResource("/template/emailTemplate.html"))));
        helper.setText(text,true);
        helper.setTo(emails);
        emailSender.send(message);

        LOG.info(Arrays.asList(emails).toString());
    }



    @Async
    void sendEmails(EmailJob job) {
        statusService.setJobStatusAndSave(job, JobStatus.PROCESSING);
        int numberOfRecipients = constants.getNumberOfRecipients();
        int size = job.getEmailHistoryList().size();

        for (int i = 0; i < size; i += numberOfRecipients) {
            List<EmailHistory> emails = job.getEmailHistoryList().subList(i, Math.min(size, i + numberOfRecipients));
            try {
                sendEmail(
                        emails.stream().map(EmailHistory::getEmail).toArray(String[]::new),
                        job.getSubject(),
                        job.getText()
                );
                if (Objects.nonNull(job.getCount())) {
                    emailRepository.deleteAll(emails.stream().map(email -> new Email().setValue(email.getEmail())).collect(Collectors.toList()));
                }
                List<EmailHistory> completed = new ArrayList<>();
                for (int j = i; j < Math.min(size, i + numberOfRecipients); j++) {
                    completed.add(job.getEmailHistoryList().get(j).setTime(LocalDateTime.now()).setStatus(EmailStatus.SENT));
                }
                statusService.saveEmailHistory(completed);
            } catch (Exception e) {
                LOG.error("Email not sent: {}", emails.stream().map(EmailHistory::getEmail).collect(Collectors.toList()));
                List<EmailHistory> rejected = new ArrayList<>();
                for (int j = i; j < Math.min(size, i + numberOfRecipients); j++) {
                    rejected.add(job.getEmailHistoryList().get(j).setTime(LocalDateTime.now()).setStatus(EmailStatus.NOT_SENT));
                }
                statusService.saveEmailHistory(rejected);
            }
        }

        job.setFinished(LocalDateTime.now());
        statusService.setJobStatusAndSave(job, JobStatus.FINISHED);
    }

    public void sendEmail(String[] emails, String subject, String text)  throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        message.setSubject(subject);
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setText(text,true);
        helper.setTo(emails);
        emailSender.send(message);

        LOG.info("Email sent to: {}", Arrays.asList(emails));
    }
}
