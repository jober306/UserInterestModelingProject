package iva.server.config;

import heliumnife.connector.ActiveMQClientConnector;
import heliumnife.connector.ClientConnector;
import heliumnife.server.ephyra.model.transfer.ResultDTO;
import iva.server.ephyra.services.EphyraService;
import iva.server.ephyra.services.EphyraServiceHandler;

import java.net.URI;
import java.net.URISyntaxException;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("iva.server.ephyra")
public class EphyraConfig {

	@Value("${iva.ephyra.url:tcp://localhost:61616}")
	private String brokerUrl;

	@Value("${iva.ephyra.queue:ephyra.qaservice}")
	private String queueName;

	@Value("${iva.ephyra.timeout:0}")
	private Integer timeout;

	@Bean
	public EphyraService ephyraService() {
		return new EphyraServiceHandler(activemqConnector());
	}

	private ClientConnector activemqConnector() {
		try {
			ClientConnector connector = new ActiveMQClientConnector(queueName,
					new URI(brokerUrl), ResultDTO[].class);
			connector.setTimeout(timeout);
			return connector;
		} catch (JMSException e) {
			throw new RuntimeException("Failed to open ActiveMQConnector "
					+ "for queue '" + queueName + "' on broker " + brokerUrl, e);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Broker URL syntax error in "
					+ brokerUrl, e);
		}
	}

}
