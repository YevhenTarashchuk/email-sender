package com.sacret.sender.mailsender.controller;

import com.sacret.sender.mailsender.exception.BaseException;
import com.sacret.sender.mailsender.model.dto.EmailDTO;
import com.sacret.sender.mailsender.model.entity.Email;
import com.sacret.sender.mailsender.model.enumaration.ErrorCode;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class DataControllerTest extends BaseTest {

    @Test
    @Sql("/sql/emails.sql")
    public void successDownload() throws Exception {
        mockMvc.perform(get("/data").contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.emails.length()").value(3))
                .andExpect(jsonPath("$.data.emails[*]",
                        Matchers.containsInAnyOrder("test.email.1@ukr.net", "test.email.2@ukr.net", "test.email.3@ukr.net")));

        verify(dataService).download();
        verifyNoMoreInteractions(dataService);
    }

    @Test
    public void successUpload() throws Exception {
        final EmailDTO dto = new EmailDTO()
                .setEmails(Set.of("post.email.1@ukr.net", "post.email.2@ukr.net", "post.not.email", "post.email.3@ukr.net"));

        mockMvc.perform(post("/data").contentType(contentType).content(json(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalAdded").value(3));

        List<Email> result = emailRepository.findAll();

        Assert.assertEquals(3, result.size());
        Assert.assertTrue(result.containsAll(
                List.of(
                        new Email().setValue("post.email.1@ukr.net"),
                        new Email().setValue("post.email.2@ukr.net"),
                        new Email().setValue("post.email.3@ukr.net")))
        );

        verify(dataService).upload(anySet());
        verifyNoMoreInteractions(dataService);
    }

    @Test
    public void successUploadFile() throws Exception {
        final byte[] fileInBytes = getFileInBytes();
        final MockMultipartFile file = new MockMultipartFile("data", "data/test.txt", MediaType.ALL_VALUE, fileInBytes);

        mockMvc.perform(multipart("/data/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalAdded").value(3));

        List<Email> result = emailRepository.findAll();

        Assert.assertEquals(3, result.size());
        Assert.assertTrue(result.containsAll(
                List.of(
                        new Email().setValue("post.file.1@ukr.net"),
                        new Email().setValue("post.file.2@ukr.net"),
                        new Email().setValue("post.file.3@ukr.net")))
        );

        verify(dataService).upload(anySet());
        verify(dataService).upload(file);
        verifyNoMoreInteractions(dataService);
    }

    @Test
    public void failUploadFile() throws Exception {
        final byte[] fileInBytes = getFileInBytes();
        final MockMultipartFile file = new MockMultipartFile("data", "data/test.txt", MediaType.ALL_VALUE, fileInBytes);

        doThrow(new BaseException(HttpStatus.BAD_REQUEST, ErrorCode.ERR_FILE))
                .when(dataService).upload(file);

        mockMvc.perform(multipart("/data/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.error").value("ERR_FILE"))
                .andExpect(jsonPath("$.errorMsg").value("Can not read file"));

        Assert.assertTrue(emailRepository.findAll().isEmpty());

        verify(dataService).upload(file);
        verifyNoMoreInteractions(dataService);
    }

    @Test
    @Sql("/sql/emails.sql")
    public void successClearData() throws Exception {
        mockMvc.perform(delete("/data").contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalRemoved").value(3));

        verify(dataService).clearData();
        verifyNoMoreInteractions(dataService);
    }

    private byte[] getFileInBytes() throws IOException {
        return IOUtils.toByteArray(new ClassPathResource("data/test.txt").getInputStream());
    }
}
