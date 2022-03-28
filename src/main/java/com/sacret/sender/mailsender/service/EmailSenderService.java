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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailSenderService {

    JobStatusService jobStatusService;
    EmailRepository emailRepository;
    JavaMailSender emailSender;
    Constants constants;

    @Async
    public void sendEmails(EmailJob job) {
        jobStatusService.setJobStatusAndSave(job, JobStatus.PROCESSING);
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
                for (int j = i; j < Math.min(size, i + numberOfRecipients); j++) {
                    job.getEmailHistoryList().get(j).setTime(LocalDateTime.now()).setStatus(EmailStatus.SENT);
                }
                jobStatusService.setJobStatusAndSave(job, JobStatus.PROCESSING);
            } catch (Exception e) {
                LOG.error("Email not sent: {}", emails.stream().map(EmailHistory::getEmail).collect(Collectors.toList()));
                for (int j = i; j < Math.min(size, i + numberOfRecipients); j++) {
                    job.getEmailHistoryList().get(j).setTime(LocalDateTime.now()).setStatus(EmailStatus.NOT_SENT);
                }
                jobStatusService.setJobStatusAndSave(job, JobStatus.PROCESSING);
            }
        }

        job.setFinished(LocalDateTime.now());
        jobStatusService.setJobStatusAndSave(job, JobStatus.FINISHED);
    }

    private void sendEmail(String[] emails, String subject, String text)  throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        message.setSubject(subject);
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setText(text,true);
        helper.setTo(emails);
        emailSender.send(message);

        LOG.info("Email sent to: {}", Arrays.asList(emails));
    }
}
