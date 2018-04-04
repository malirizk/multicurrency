package com.cryptocurrency.mutlicurrency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.cryptocurrency.mutlicurrency" })
//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class MutlicurrencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MutlicurrencyApplication.class, args);
	}
}
