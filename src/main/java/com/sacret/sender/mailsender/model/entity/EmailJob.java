package com.sacret.sender.mailsender.model.entity;

import com.sacret.sender.mailsender.model.enumaration.JobStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class EmailJob {
    @Id
    @GenericGenerator(name = "id", strategy = "com.sacret.sender.mailsender.config.IdGenerator")
    @GeneratedValue(generator = "id")
    String id;
    @CreationTimestamp
    LocalDateTime created;
    LocalDateTime finished;
    Integer count;
    String subject;
    @Column(length = 1500)
    String text;
    @OneToMany(mappedBy = "emailJob", cascade = CascadeType.ALL)
    List<EmailHistory> emailHistoryList;
    @Enumerated(EnumType.STRING)
    JobStatus jobStatus;

    public void setEmailHistoryList(List<EmailHistory> emailHistoryList) {
        this.emailHistoryList = emailHistoryList;
        this.emailHistoryList.forEach(emailHistory -> emailHistory.setEmailJob(this));
    }
}
