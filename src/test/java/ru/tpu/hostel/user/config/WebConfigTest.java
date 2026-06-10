package ru.tpu.hostel.user.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebConfigTest {

    @Test
    void addResourceHandlersWithSuccess() {
        WebConfig webConfig = new WebConfig();
        ReflectionTestUtils.setField(webConfig, "uploadDir", "/tmp/uploads");
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);
        when(registry.addResourceHandler("users/images/**")).thenReturn(registration);
        when(registration.addResourceLocations(anyString())).thenReturn(registration);

        webConfig.addResourceHandlers(registry);

        verify(registry).addResourceHandler("users/images/**");
        verify(registration).addResourceLocations("file:/tmp/uploads/");
    }
}
