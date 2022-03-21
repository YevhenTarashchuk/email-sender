package com.sacret.sender.mailsender.model.entity;

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
import java.util.List;

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
    String text;
    @ElementCollection
    @CollectionTable(name = "email_history")
    List<EmailHistory> emailHistoryList;
}
