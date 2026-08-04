package usatec.com.br.curso_front.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Usa o HttpClient nativo do Java Moderno que suporta PATCH de verdade!
        return new RestTemplate(new JdkClientHttpRequestFactory());
    }
}
