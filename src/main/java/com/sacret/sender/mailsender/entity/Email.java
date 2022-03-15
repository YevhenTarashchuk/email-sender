package com.sacret.sender.mailsender.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "email")
public class Email {
    @Id
    String email;
}
