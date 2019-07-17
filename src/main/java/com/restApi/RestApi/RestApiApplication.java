package com.restApi.RestApi;

import com.restApi.RestApi.Services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@Configuration
@EnableScheduling
public class RestApiApplication {

	private final TransferService transferService;

	@Autowired
	public RestApiApplication(TransferService transferService) {
		this.transferService = transferService;
	}

	public static void main(String[] args) {
		SpringApplication.run(RestApiApplication.class, args);
	}

	@Scheduled(fixedRate = 60000)
	public void executeTransfers() {
	transferService.finishTransfers();
	}
}
