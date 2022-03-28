package com.sacret.sender.mailsender.config;

import com.sacret.sender.mailsender.exception.BaseException;
import com.sacret.sender.mailsender.model.dto.JobDetailsResponseDTO;
import com.sacret.sender.mailsender.model.dto.JobRequestDTO;
import com.sacret.sender.mailsender.model.dto.JobResponseDTO;
import com.sacret.sender.mailsender.model.entity.EmailHistory;
import com.sacret.sender.mailsender.model.entity.EmailJob;
import com.sacret.sender.mailsender.model.enumaration.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.IOUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;


@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ModelMapperConfig {

    Constants constants;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);

        emailJobMapping(mapper);
        return mapper;
    }

    private void emailJobMapping(ModelMapper modelMapper) {
        Converter<String, String> textConverter = ctx -> {
            try {
                return Objects.isNull(ctx.getSource())
                        ? new String(IOUtils.toByteArray(Objects.requireNonNull(this.getClass().getResource("/template/emailTemplate.html"))))
                        : ctx.getSource();
            } catch (IOException e) {
                throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ERR_EMAIL_TEMPLATE);
            }
        };

        Converter<String, String> subjectContext =
                ctx -> Objects.isNull(ctx.getSource()) ? constants.getSubject() : ctx.getSource();

        Converter<Set<String>, List<EmailHistory>> emailsConverter = ctx -> {
            Pattern regexPattern = Pattern.compile(constants.getEmailPattern());
            return Objects.isNull(ctx.getSource()) ? null : ctx.getSource().stream()
                    .filter(email -> regexPattern.matcher(email).matches())
                    .map(email -> new EmailHistory().setEmail(email))
                    .collect(Collectors.toList());
        };

        Converter<List<EmailHistory>, Integer> quantityConverter =
                ctx -> Objects.isNull(ctx.getSource()) ? null : ctx.getSource().size();

        modelMapper.typeMap(JobRequestDTO.class, EmailJob.class)
                .addMappings(mapper -> {
                    mapper.using(textConverter).map(JobRequestDTO::getText, EmailJob::setText);
                    mapper.using(subjectContext).map(JobRequestDTO::getSubject, EmailJob::setSubject);
                    mapper.using(emailsConverter).map(JobRequestDTO::getEmails, EmailJob::setEmailHistoryList);
        });

        modelMapper.typeMap(EmailJob.class, JobResponseDTO.class)
                .addMappings(mapper -> {
                   mapper.map(EmailJob::getId, JobResponseDTO::setJobId);
                   mapper.using(quantityConverter).map(EmailJob::getEmailHistoryList, JobResponseDTO::setAcceptedForProcessing);
                });

        modelMapper.typeMap(EmailJob.class, JobDetailsResponseDTO.class)
                .addMappings(mapper ->
                        mapper.using(quantityConverter).map(EmailJob::getEmailHistoryList, JobDetailsResponseDTO::setAcceptedForProcessing));
    }
}
