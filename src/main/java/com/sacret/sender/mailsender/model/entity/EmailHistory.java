package com.sacret.sender.mailsender.model.entity;

import com.sacret.sender.mailsender.model.enumaration.EmailStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class EmailHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String email;
    LocalDateTime time;
    @Enumerated(EnumType.STRING)
    EmailStatus status;
    @ManyToOne(optional = false)
    @JoinColumn(name = "email_job_id")
    EmailJob emailJob;
}
