package br.com.i2e.shop9.adapter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import br.com.i2e.shop9.adapter.queue.S9ResponseListener;

@EntityScan("br.com.i2e.common.model")
@SpringBootApplication //(exclude={DataSourceAutoConfiguration.class}) 
public class S9AdapterApplication {

	@Autowired
	private ApplicationContext applicationContext;	
	public static void main(String[] args) {
		SpringApplication.run(S9AdapterApplication.class, args);
	}

	@Bean
	public CommandLineRunner testContext() {
		return a -> {
			
			System.out.println( ">>>>>>>>>>>: " + applicationContext.getBean(S9ResponseListener.class));
		};
	}
	
	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}
}
