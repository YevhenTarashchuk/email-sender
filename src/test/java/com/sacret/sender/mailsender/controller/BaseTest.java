package com.sacret.sender.mailsender.controller;

import com.sacret.sender.mailsender.repository.EmailJobRepository;
import com.sacret.sender.mailsender.repository.EmailRepository;
import com.sacret.sender.mailsender.service.DataService;
import com.sacret.sender.mailsender.service.EmailSenderService;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public abstract class BaseTest {
    
    @Autowired
    MockMvc mockMvc;
    @Autowired
    EmailRepository emailRepository;
    @Autowired
    EmailJobRepository emailJobRepository;

    @SpyBean
    DataService dataService;
    @SpyBean
    EmailSenderService emailSenderService;

    HttpMessageConverter httpMessageConverter;

    MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    String json(Object object) {
        MockHttpOutputMessage message = new MockHttpOutputMessage();
        try {
            httpMessageConverter.write(object, contentType, message);
        }catch (IOException e){
            e.printStackTrace();
        }
        return message.getBodyAsString();
    }

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters){
        httpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        Assert.assertNotNull("the JSON message converter must not be null",
                httpMessageConverter);
    }
}
