package com.sacret.sender.mailsender.model.dto;

import com.sacret.sender.mailsender.model.entity.EmailHistory;
import com.sacret.sender.mailsender.model.enumaration.JobStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobDetailsResponseDTO {
    String id;
    LocalDateTime created;
    LocalDateTime finished;
    Integer count;
    Integer acceptedForProcessing;
    String subject;
    String text;
    List<EmailHistory> emailHistoryList;
    JobStatus jobStatus;
}
