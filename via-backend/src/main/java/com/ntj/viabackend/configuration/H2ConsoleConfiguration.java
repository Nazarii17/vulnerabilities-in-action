package com.ntj.viabackend.configuration;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Console available at http://localhost:8080/h2-console
 *      JDBC URL:   jdbc:h2:mem:demodb
 *      User:       sa
 *      Password:   (empty)
 *      Driver:     org.h2.Driver
 *
 * DEMO ONLY!
 */
@Configuration
@Profile("!prod")
public class H2ConsoleConfiguration {

    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2ConsoleServlet() {
        ServletRegistrationBean<JakartaWebServlet> registrationBean =
                new ServletRegistrationBean<>(new JakartaWebServlet());
        registrationBean.addUrlMappings("/h2-console/*");
        registrationBean.addInitParameter("webAllowOthers", "false"); // restrict to localhost only
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }
}
