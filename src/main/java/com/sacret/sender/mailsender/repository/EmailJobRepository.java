package com.sacret.sender.mailsender.repository;

import com.sacret.sender.mailsender.model.entity.EmailJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailJobRepository extends JpaRepository<EmailJob, String> {
}
