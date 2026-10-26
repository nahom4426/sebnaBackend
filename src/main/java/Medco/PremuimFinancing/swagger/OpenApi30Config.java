package Medco.PremuimFinancing.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Premium_financing", version = "1.0"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApi30Config {
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
////                .info(new Info()
////                        .title("Premium Financing API")
////                        .version("1.0")
////                        .description("API Documentation")
////                        .contact(new Contact().name("Support Team").email("support@example.com")))
//                .servers(List.of(
//                        new Server().url("https://anyway-rio-ye-translation.trycloudflare.com") // Force HTTPS
//                ));
//    }
}
