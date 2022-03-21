package com.sacret.sender.mailsender;

import com.sacret.sender.mailsender.model.entity.Email;
import com.sacret.sender.mailsender.repository.EmailRepository;
import com.sacret.sender.mailsender.service.EmailSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SpringBootApplication
public class MailSenderApplication {

	EmailRepository emailRepository;
	EmailSender emailSender;

	public static void main(String[] args) {
		SpringApplication.run(MailSenderApplication.class, args);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {

//			save email to db
//			saveToDB()

//			send email
//			sendEmails();
		};
	}

	private void saveToDB() throws IOException {
		List<String> mails = getMails();
		emailRepository.saveAll(mails.stream().map(mail -> new Email().setValue(mail)).collect(Collectors.toList()));
	}

	private void sendEmails() {
		List<String> mails = emailRepository.findAllByValueNotContainsIgnoreCase("@mail.ru").stream()
				.map(Email::getValue)
				.collect(Collectors.toList());

		System.out.println(mails);

		String[] arr = new String[10];
		for (int count = 0, i = 0; count < mails.size(); count++) {
			arr[i] = mails.get(count);
			if (i == 9) {
				try {
					emailSender.sendEmail(arr);
					emailRepository.deleteAll(Arrays.stream(arr).map(mail -> new Email().setValue(mail)).collect(Collectors.toList()));
				} catch (Exception e) {
					LOG.error("Email not sent");
				}
				i = 0;
			} else {
				i++;
			}
		}
	}

	private List<String> getMails() throws IOException {
		final File file = new File("/emails_1.txt");
		List<String> mails = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				file.isFile() ? new FileInputStream(file) : Objects.requireNonNull(this.getClass().getResourceAsStream("/emails_1.txt"))))) {
			String mail;
			while ((mail = br.readLine()) != null) {
				mails.add(mail);
			}
			return mails;
		}
	}
}
