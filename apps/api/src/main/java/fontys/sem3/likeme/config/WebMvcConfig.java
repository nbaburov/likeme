package fontys.sem3.likeme.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Indicates that this class provides Spring with configuration information
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // This method is used to configure resource handling for serving static resources
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Maps all requests to static resources (/**) to the specified location in the classpath
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/"); // Specifies the location of static resources
    }
}