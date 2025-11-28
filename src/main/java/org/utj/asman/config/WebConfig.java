package org.utj.asman.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration.
 * 
 * Note: File serving for /uploads/** is now handled by FileController
 * instead of resource handlers, which provides better reliability in
 * production Tomcat environments with external storage directories.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Configuration can be added here as needed
}