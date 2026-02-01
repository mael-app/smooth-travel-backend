package com.smoothtravel;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "SmoothTravel API",
                version = "0.1.0",
                description = "Multimodal travel planning API combining SNCF and urban transport networks",
                contact = @Contact(name = "SmoothTravel", url = "https://github.com/mael-app/smooth-travel-backend"),
                license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")
        )
)
public class OpenApiConfig extends Application {
}
