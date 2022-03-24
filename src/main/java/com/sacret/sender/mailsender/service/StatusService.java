package com.sacret.sender.mailsender.service;

import com.sacret.sender.mailsender.model.entity.EmailHistory;
import com.sacret.sender.mailsender.model.entity.EmailJob;
import com.sacret.sender.mailsender.model.enumaration.JobStatus;
import com.sacret.sender.mailsender.repository.EmailHistoryRepository;
import com.sacret.sender.mailsender.repository.EmailJobRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatusService {

    EmailJobRepository emailJobRepository;
    EmailHistoryRepository emailHistoryRepository;

    public EmailJob setJobStatusAndSave(EmailJob job, JobStatus status) {
        job.setJobStatus(status);
        job =  emailJobRepository.save(job);

        LOG.info("EmailJobId: {} has status: {}", job.getId(), job.getJobStatus().toString());
        return job;
    }

    public void saveEmailHistory(List<EmailHistory> emailHistoryList) {
        emailHistoryRepository.saveAll(emailHistoryList);
    }
}
