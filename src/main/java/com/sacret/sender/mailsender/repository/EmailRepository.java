package com.sacret.sender.mailsender.repository;

import com.sacret.sender.mailsender.model.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, String> {

    List<Email> findAllByEmailNotContainsIgnoreCase(String email);

}
