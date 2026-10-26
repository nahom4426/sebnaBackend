package Medco.PremuimFinancing.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.create();  // You can specify a base URL if you want: WebClient.create("http://example.com")
    }
}
