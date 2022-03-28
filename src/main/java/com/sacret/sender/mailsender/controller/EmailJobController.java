package com.sacret.sender.mailsender.controller;

import com.sacret.sender.mailsender.model.dto.JobRequestDTO;
import com.sacret.sender.mailsender.model.dto.JobResponseDTO;
import com.sacret.sender.mailsender.model.response.CustomResponse;
import com.sacret.sender.mailsender.service.EmailJobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/job")
public class EmailJobController {

    EmailJobService emailJobService;

    @PostMapping
    public CustomResponse<JobResponseDTO> sendEmails(@RequestBody @Valid JobRequestDTO dto) {
        return new CustomResponse<JobResponseDTO>()
                .setData(emailJobService.startEmailJob(dto))
                .setStatus(CustomResponse.Status.OK);
    }
}
