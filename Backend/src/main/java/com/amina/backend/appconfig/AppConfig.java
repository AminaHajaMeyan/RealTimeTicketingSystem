package com.amina.backend.appconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuration class for application-level settings.
 * <p>
 * This class provides configuration for Cross-Origin Resource Sharing (CORS),
 * allowing the frontend application to interact with the backend.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
@Configuration
public class AppConfig {

    /**
     * Configures and returns a {@link CorsFilter} bean.
     * <p>
     * This filter allows requests from the specified origin (e.g., `<a href="http://localhost:4200">...</a>`)
     * and enables cross-origin access for all headers and methods.
     * </p>
     *
     * @return A configured {@link CorsFilter} instance.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
