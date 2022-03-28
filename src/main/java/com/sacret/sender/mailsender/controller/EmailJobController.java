package com.sacret.sender.mailsender.controller;

import com.sacret.sender.mailsender.model.dto.JobDetailsResponseDTO;
import com.sacret.sender.mailsender.model.dto.JobIdDTO;
import com.sacret.sender.mailsender.model.dto.JobRequestDTO;
import com.sacret.sender.mailsender.model.dto.JobResponseDTO;
import com.sacret.sender.mailsender.model.response.CustomResponse;
import com.sacret.sender.mailsender.service.EmailJobService;
import com.sacret.sender.mailsender.service.JobStatusService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/jobs")
public class EmailJobController {

    EmailJobService emailJobService;
    JobStatusService jobStatusService;

    @PostMapping
    public CustomResponse<JobResponseDTO> sendEmails(@RequestBody @Valid JobRequestDTO dto) {
        return new CustomResponse<JobResponseDTO>()
                .setData(emailJobService.startEmailJob(dto))
                .setStatus(CustomResponse.Status.OK);
    }

    @GetMapping
    public CustomResponse<Map<String,List<JobIdDTO>>> getJobs() {
        return new CustomResponse<Map<String,List<JobIdDTO>>>()
                .setData(Collections.singletonMap("jobs", emailJobService.getJobs()))
                .setStatus(CustomResponse.Status.OK);
    }

    @GetMapping("/{jobId}")
    public CustomResponse<JobDetailsResponseDTO> getJob(@PathVariable String jobId) {
        return new CustomResponse<JobDetailsResponseDTO>()
                .setData(emailJobService.getJob(jobId))
                .setStatus(CustomResponse.Status.OK);
    }

    @GetMapping("/{jobId}/status")
    public CustomResponse<Map<String, String>> getJobStatus(@PathVariable String jobId) {
        return new CustomResponse<Map<String, String>>()
                .setData(Collections.singletonMap("jobStatus", jobStatusService.getJobStatus(jobId)))
                .setStatus(CustomResponse.Status.OK);
    }
}
