package com.restApi.RestApi;

import com.restApi.RestApi.Services.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@Configuration
@EnableScheduling
public class RestApiApplication {

	@Autowired
	TransferService transferService;

	private static final Logger log = LoggerFactory.getLogger(RestApiApplication.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public static void main(String[] args) {
		SpringApplication.run(RestApiApplication.class, args);
	}

		@Scheduled(fixedRate = 10000)
		public void executeTransfers() {
		log.info("The time is now {}", dateFormat.format(new Date()));
		transferService.finishTransfers();
		}

}
