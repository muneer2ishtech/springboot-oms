package fi.ishtech.practice.oms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// @formatter:off
@SpringBootApplication(scanBasePackages = {
		"fi.ishtech.springboot.jwtauth",
		"fi.ishtech.practice.oms"
})
@EntityScan(basePackages = {
		"fi.ishtech.springboot.jwtauth.entity",
		"fi.ishtech.practice.oms.entity"
})
@EnableJpaRepositories(basePackages = {
		"fi.ishtech.springboot.jwtauth.repo",
		"fi.ishtech.practice.oms.repo"
})
// @formatter:on
public class OmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmsApplication.class, args);
	}

}