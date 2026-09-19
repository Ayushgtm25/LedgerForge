package com.spendsmart.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsGlobalConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allow specific origins
        corsConfig.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:3000",
            "http://localhost:80",
            "http://localhost"
        ));

        // Allow credentials
        corsConfig.setAllowCredentials(true);

        // Allow all methods
        corsConfig.setAllowedMethods(Arrays.asList(
            HttpMethod.GET.toString(),
            HttpMethod.POST.toString(),
            HttpMethod.PUT.toString(),
            HttpMethod.DELETE.toString(),
            HttpMethod.OPTIONS.toString(),
            HttpMethod.PATCH.toString()
        ));

        // Allow all headers
        corsConfig.setAllowedHeaders(Arrays.asList("*"));

        // Expose headers
        corsConfig.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count",
            "X-Page-Number",
            "X-Page-Size"
        ));

        // Max age in seconds
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}

