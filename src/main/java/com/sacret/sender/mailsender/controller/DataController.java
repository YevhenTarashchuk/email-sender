package com.sacret.sender.mailsender.controller;

import com.sacret.sender.mailsender.model.dto.EmailDTO;
import com.sacret.sender.mailsender.model.response.CustomResponse;
import com.sacret.sender.mailsender.service.DataService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

import static com.sacret.sender.mailsender.model.response.CustomResponse.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/data")
public class DataController {

    private static final String DATA_FILE_REQUEST_PARAM = "data";

    DataService dataService;

    @GetMapping
    public CustomResponse<EmailDTO> download() {
        return new CustomResponse<EmailDTO>()
                .setData(dataService.download())
                .setStatus(Status.OK);
    }

    @PostMapping
    public CustomResponse<Map<String, Integer>> upload(@RequestBody @NotNull final EmailDTO dto) {
        return new CustomResponse<Map<String, Integer>>()
                .setData(Collections.singletonMap("totalAdded", dataService.upload(dto.getEmails())))
                .setStatus(Status.OK);
    }

    @PostMapping("/upload")
    public CustomResponse<Map<String, Integer>> uploadFile(@RequestParam(DATA_FILE_REQUEST_PARAM) final MultipartFile file) {
        return new CustomResponse<Map<String, Integer>>()
                .setData(Collections.singletonMap("totalAdded", dataService.upload(file)))
                .setStatus(Status.OK);
    }

    @DeleteMapping
    public CustomResponse<Map<String, Long>> clearData() {
        return new CustomResponse<Map<String, Long>>()
                .setData(Collections.singletonMap("totalRemoved", dataService.clearData()))
                .setStatus(Status.OK);
    }
}
