package com.sacret.sender.mailsender.service;

import com.sacret.sender.mailsender.exception.BaseException;
import com.sacret.sender.mailsender.model.entity.EmailJob;
import com.sacret.sender.mailsender.model.enumaration.ErrorCode;
import com.sacret.sender.mailsender.model.enumaration.JobStatus;
import com.sacret.sender.mailsender.repository.EmailJobRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobStatusService {

    EmailJobRepository emailJobRepository;

    public EmailJob setJobStatusAndSave(EmailJob job, JobStatus status) {
        job.setJobStatus(status);
        job =  emailJobRepository.save(job);

        LOG.info("EmailJobId: {} has status: {}", job.getId(), job.getJobStatus().toString());
        return job;
    }

    public String getJobStatus(String jobId) {
        return emailJobRepository.findById(jobId).orElseThrow(() -> {
           LOG.warn("JobId: {} is not found", jobId);
           return new BaseException(HttpStatus.BAD_REQUEST, ErrorCode.ERR_JOB_NOT_FOUND);
       }).getJobStatus().toString();
    }
}
