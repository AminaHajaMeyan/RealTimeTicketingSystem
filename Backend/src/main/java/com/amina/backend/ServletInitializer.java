package com.amina.backend;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Configures the Spring Boot application to be deployed in a traditional servlet container.
 * <p>
 * This class extends {@link SpringBootServletInitializer}, which provides support for
 * configuring the application when it's launched from a WAR deployment.
 * </p>
 *
 * <p>
 * It overrides the {@link #configure(SpringApplicationBuilder)} method to specify the
 * main application class {@link BackendApplication}.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * Configures the application by linking it to the main application class.
     *
     * @param application The {@link SpringApplicationBuilder} instance.
     * @return The configured {@link SpringApplicationBuilder}.
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BackendApplication.class);
    }
}
