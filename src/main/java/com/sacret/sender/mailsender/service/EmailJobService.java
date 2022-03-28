package com.sacret.sender.mailsender.service;

import com.sacret.sender.mailsender.config.Constants;
import com.sacret.sender.mailsender.exception.BaseException;
import com.sacret.sender.mailsender.model.dto.JobDetailsResponseDTO;
import com.sacret.sender.mailsender.model.dto.JobIdDTO;
import com.sacret.sender.mailsender.model.dto.JobRequestDTO;
import com.sacret.sender.mailsender.model.dto.JobResponseDTO;
import com.sacret.sender.mailsender.model.entity.EmailHistory;
import com.sacret.sender.mailsender.model.entity.EmailJob;
import com.sacret.sender.mailsender.model.enumaration.EmailStatus;
import com.sacret.sender.mailsender.model.enumaration.ErrorCode;
import com.sacret.sender.mailsender.model.enumaration.JobStatus;
import com.sacret.sender.mailsender.repository.EmailJobRepository;
import com.sacret.sender.mailsender.repository.EmailRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailJobService {

    EmailSenderService emailSenderService;
    EmailJobRepository emailJobRepository;
    JobStatusService jobStatusService;
    EmailRepository emailRepository;
    ModelMapper modelMapper;
    Constants constants;

    public List<JobIdDTO> getJobs() {
        return emailJobRepository.findAll().stream()
                .map(job -> modelMapper.map(job, JobIdDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public JobDetailsResponseDTO getJob(String jobId) {
        EmailJob emailJob = emailJobRepository.findById(jobId).orElseThrow(() -> {
            LOG.warn("JobId: {} is not found", jobId);
           return new BaseException(HttpStatus.BAD_REQUEST, ErrorCode.ERR_JOB_NOT_FOUND);
        });

        return modelMapper.map(emailJob, JobDetailsResponseDTO.class);
    }

    public JobResponseDTO startEmailJob(JobRequestDTO dto) {
        LOG.debug("Incoming dto: {}", dto);

        EmailJob job = prepareJob(dto);
        emailSenderService.sendEmails(job);

        return modelMapper.map(job, JobResponseDTO.class);
    }

    private EmailJob prepareJob(JobRequestDTO dto) {
        EmailJob job = modelMapper.map(dto, EmailJob.class);
        prepareEmailData(job);

        return jobStatusService.setJobStatusAndSave(job, JobStatus.RECEIVED);
    }

    private void prepareEmailData(EmailJob job) {
        checkEmailData(job);
        if (Objects.isNull(job.getEmailHistoryList())) {
            Pattern regexPattern = Pattern.compile(constants.getEmailPattern());
            job.setEmailHistoryList(
                    emailRepository.findAll(PageRequest.of(0,job.getCount())).stream()
                            .filter(email -> regexPattern.matcher(email.getValue()).matches())
                            .map(email -> new EmailHistory().setEmail(email.getValue()))
                            .collect(Collectors.toList())
            );
        }
        job.getEmailHistoryList().forEach(
                emailHistory -> emailHistory
                        .setStatus(EmailStatus.RECEIVED_FOR_SENDING)
                        .setTime(LocalDateTime.now())
        );

        LOG.info("{} emails available for processing", job.getEmailHistoryList().size());
    }

    private void checkEmailData(EmailJob job) {
        if (Objects.nonNull(job.getEmailHistoryList()) && job.getEmailHistoryList().isEmpty()) {
            LOG.warn("Received invalid email values from request");
            throw new BaseException(HttpStatus.BAD_REQUEST, ErrorCode.ERR_EMAIL_RECEIVED);
        } else if (Objects.isNull(job.getEmailHistoryList()) && emailRepository.count() == 0) {
            LOG.warn("No emails are available in the database");
            throw new BaseException(HttpStatus.BAD_REQUEST, ErrorCode.ERR_EMAIL_DB);
        }
    }
}
