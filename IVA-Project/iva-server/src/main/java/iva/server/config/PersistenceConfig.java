package iva.server.config;

import iva.server.persistence.AnswerRepository;
import iva.server.persistence.InterestModelRepository;
import iva.server.persistence.QuestionRepository;
import iva.server.persistence.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("iva.server.persistence")
@EnableTransactionManagement
public class PersistenceConfig {
	
	@Bean
	public UserRepository.EventHandler userRepositoryEventHandler() {
		return new UserRepository.EventHandler();
	}
	
	@Bean
	public InterestModelRepository.EventHandler modelRepositoryEventHandler() {
		return new InterestModelRepository.EventHandler();
	}
	
	@Bean
	public QuestionRepository.EventHandler questionRepositoryEventHandler() {
		return new QuestionRepository.EventHandler();
	}
	
	@Bean
	public AnswerRepository.EventHandler answerRepositoryEventHandler() {
		return new AnswerRepository.EventHandler();
	}
	
}
