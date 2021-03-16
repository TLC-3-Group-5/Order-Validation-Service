package io.turntabl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class OrderValidatorApplication {

	@Autowired
	private Environment env;

	private static final String MARKETDATAWEBHOOKPATH = "/webhooks/market-data";

	public static void main(String[] args) {
		SpringApplication.run(OrderValidatorApplication.class, args);
	}

	@Bean
	private static RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
  public CommandLineRunner run(RestTemplate restTemplate) throws RestClientException {
		return args -> {
			// * Use a webhook
			// subscribe to exchange
			restTemplate.postForEntity(
				"https://exchange.matraining.com/md/subscription",
				env.getProperty("app.host").concat(MARKETDATAWEBHOOKPATH),
				null
			);
		};
	}
}
