package com.sacret.sender.mailsender.service;

import com.sacret.sender.mailsender.exception.BaseException;
import com.sacret.sender.mailsender.model.dto.EmailDTO;
import com.sacret.sender.mailsender.model.entity.Email;
import com.sacret.sender.mailsender.model.enumaration.ErrorCode;
import com.sacret.sender.mailsender.repository.EmailRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataService {

    private static final  String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    EmailRepository emailRepository;

    public int upload(Set<String> emails) {
        LOG.info("received {} unique emails in DTO", emails.size());

        Set<String> saved = emailRepository.findAll().stream()
                .map(Email::getValue)
                .collect(Collectors.toSet());

        emails.removeAll(saved);

        Pattern regexPattern = Pattern.compile(EMAIL_PATTERN);
        emails = emails.stream()
                .filter(email -> regexPattern.matcher(email).matches())
                .collect(Collectors.toSet());


        int totalAdded = emailRepository.saveAll(
                emails.stream()
                        .map(email -> new Email().setValue(email))
                        .collect(Collectors.toList())
        ).size();

        LOG.info("added {} emails to db", totalAdded);

        return totalAdded;
    }

    public int upload(MultipartFile file) {
        Set<String> emails = new HashSet<>();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String mail;
            while ((mail = br.readLine()) != null) {
                emails.add(mail);
            }
        } catch (IOException e) {
            throw new BaseException(HttpStatus.BAD_REQUEST, ErrorCode.ERR_FILE);
        }
        return upload(emails);
    }

    public long clearData() {
        long totalRemoved = emailRepository.count();
        emailRepository.deleteAll();

        LOG.info("removed {} emails", totalRemoved);
        return totalRemoved;
    }

    public EmailDTO download() {
        return new EmailDTO()
                .setEmails(
                        emailRepository.findAll().stream()
                                .map(Email::getValue)
                                .collect(Collectors.toSet())
                );
    }
}
