package iva.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@Configuration
@ComponentScan("iva.server.web")
public class WebConfig extends RepositoryRestMvcConfiguration {

}
