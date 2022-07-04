package io.github.enkarin.bookcrossing.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "BookCrossing",
                version = "1.0.0",
                description = "Server implementation for a book exchange application",
                contact = @io.swagger.v3.oas.annotations.info.Contact(name = "Karina Elagina",
                        email = "karina.elagina2013@yandex.ru")
        )
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final Schema<?> newErrorSchema = new Schema<Map<String, String>>()
                .addProperties("message", new StringSchema().example("correspondence: Чата не существует"));
        return new OpenAPI()
                .components(new Components()
                .addSchemas("NewErrorBody", newErrorSchema)
        );
    }
}
