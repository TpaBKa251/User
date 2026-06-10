package ru.tpu.hostel.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void customOpenAPIWithSuccess() {
        OpenAPI openAPI = new OpenApiConfig().customOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("User Service Papich API");
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearerAuth");
    }
}
