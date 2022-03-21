package com.sacret.sender.mailsender.config;

import com.sacret.sender.mailsender.exception.BaseException;
import com.sacret.sender.mailsender.model.dto.EmailJobDTO;
import com.sacret.sender.mailsender.model.entity.EmailJob;
import com.sacret.sender.mailsender.model.enumaration.ErrorCode;
import org.apache.commons.io.IOUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Objects;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class ModelMapperConfig {
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
                ctx -> Objects.isNull(ctx.getSource()) ? "Останови войну пока еще не поздно!" : ctx.getSource();

        modelMapper.typeMap(EmailJobDTO.class, EmailJob.class)
                .addMappings(mapper -> {
                    mapper.using(textConverter).map(EmailJobDTO::getText, EmailJob::setText);
                    mapper.using(subjectContext).map(EmailJobDTO::getSubject, EmailJob::setSubject);
        });
    }
}
