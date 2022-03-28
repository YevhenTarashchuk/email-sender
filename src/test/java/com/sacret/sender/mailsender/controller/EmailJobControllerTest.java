package com.sacret.sender.mailsender.controller;

import com.sacret.sender.mailsender.model.dto.JobRequestDTO;
import com.sacret.sender.mailsender.model.entity.EmailJob;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EmailJobControllerTest extends BaseTest{

    @Test
    public void successSendEmails() throws Exception {
        final JobRequestDTO dto = new JobRequestDTO()
                .setText("test text")
                .setSubject("test subject")
                .setEmails(Set.of("post.email.1@ukr.net", "post.email.2@ukr.net", "post.not.email", "post.email.3@ukr.net"));

        mockMvc.perform(post("/jobs").contentType(contentType).content(json(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data.jobId", Matchers.any(String.class)))
                .andExpect(jsonPath("$.data.acceptedForProcessing").value(3));


        verify(emailSenderService, timeout(5000)).sendEmails(any(EmailJob.class));
    }

    @Test
    @Sql("/sql/job_emails.sql")
    public void successSendEmailsWithCount() throws Exception {
        final JobRequestDTO dto = new JobRequestDTO()
                .setText("test text")
                .setSubject("test subject")
                .setCount(5);

        mockMvc.perform(post("/jobs").contentType(contentType).content(json(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data.jobId", Matchers.any(String.class)))
                .andExpect(jsonPath("$.data.acceptedForProcessing").value(3));


        verify(emailSenderService, timeout(5000)).sendEmails(any(EmailJob.class));
    }

    @Test
    public void failSendEmailsWithCleanData() throws Exception {
        final JobRequestDTO dto = new JobRequestDTO()
                .setText("test text")
                .setSubject("test subject")
                .setCount(5);

        mockMvc.perform(post("/jobs").contentType(contentType).content(json(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.error").value("ERR_EMAIL_DB"))
                .andExpect(jsonPath("$.errorMsg").value("No emails in db"));
    }

    @Test
    public void failSendEmailsWithCountAndEmails() throws Exception {
        final JobRequestDTO dto = new JobRequestDTO()
                .setText("test text")
                .setSubject("test subject")
                .setEmails(Set.of("post.email.1@ukr.net", "post.email.2@ukr.net", "post.not.email", "post.email.3@ukr.net"))
                .setCount(5);

        mockMvc.perform(post("/jobs").contentType(contentType).content(json(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.error").value("ERR_PARAM_INVALID"))
                .andExpect(jsonPath("$.errorMsg").value("Validation failed"));
    }
}
