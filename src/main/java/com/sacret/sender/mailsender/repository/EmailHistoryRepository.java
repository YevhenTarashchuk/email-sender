package com.sacret.sender.mailsender.repository;

import com.sacret.sender.mailsender.model.entity.EmailHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailHistoryRepository extends JpaRepository<EmailHistory, Long> {
}
