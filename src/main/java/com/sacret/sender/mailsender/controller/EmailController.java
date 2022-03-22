package com.sacret.sender.mailsender.controller;

import com.sacret.sender.mailsender.model.dto.JobRequestDTO;
import com.sacret.sender.mailsender.model.dto.JobResponseDTO;
import com.sacret.sender.mailsender.model.response.CustomResponse;
import com.sacret.sender.mailsender.service.EmailSenderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/emails")
public class EmailController {

    EmailSenderService emailSenderService;

    @PostMapping
    public CustomResponse<JobResponseDTO> sendEmails(@RequestBody @Valid JobRequestDTO dto) {
        return new CustomResponse<JobResponseDTO>()
                .setData(emailSenderService.startEmailJob(dto))
                .setStatus(CustomResponse.Status.OK);
    }
}
