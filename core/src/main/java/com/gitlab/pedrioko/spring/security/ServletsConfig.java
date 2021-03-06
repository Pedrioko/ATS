package com.gitlab.pedrioko.spring.security;

import com.gitlab.pedrioko.core.api.StaticResouceLocation;
import com.gitlab.pedrioko.core.hibernate.interceptors.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zkoss.web.util.resource.ClassWebResource;
import org.zkoss.zk.ui.http.HttpSessionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * The Class ServletsConfig.
 */
@Configuration
public class ServletsConfig {
    private static final String ZUL_VIEW_RESOLVER_SUFFIX = ".zul";
    private static String UPDATE_URI = "/zkau"; //servlet mapping for ZK's update servlet
    private static String ZUL_VIEW_RESOLVER_PREFIX = UPDATE_URI + ClassWebResource.PATH_PREFIX + "/zul/";
    @Autowired
    private List<StaticResouceLocation> staticResouceLocations;


    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener();
    }

    @Bean
    public HibernatePropertiesCustomizer getInterceptor(UserInterceptor userInterceptor) {
        return new HibernatePropertiesCustomizer() {
            @Override
            public void customize(Map<String, Object> hibernateProperties) {
                hibernateProperties.put("hibernate.session_factory.interceptor", userInterceptor);
            }
        };
    }

    /**
     * Mvc configurer.
     *
     * @return the web mvc configurer
     */
    @Bean
    public WebMvcConfigurer mvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("GET", "PUT", "POST");
            }

            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
                converters.add(byteArrayHttpMessageConverter());
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                if (staticResouceLocations != null)
                    staticResouceLocations.forEach(e -> {
                        registry.addResourceHandler(e.getPath()).addResourceLocations(e.getLocations());
                    });
            }
        };
    }


    //@Bean
    public JavaMailSenderImpl emailSender(@Value("#{'${mail.host}'}") String emailHost,
                                          @Value("#{'${mail.port}'}") String emailPort, @Value("#{'${mail.username}'}") String username,
                                          @Value("#{'${mail.password}'}") String password) {
        JavaMailSenderImpl emailSender = new JavaMailSenderImpl();
        emailSender.setHost(emailHost);
        emailSender.setPort(Integer.parseInt(emailPort));
        emailSender.setUsername(username);
        emailSender.setPassword(password);
        Properties mailProps = new Properties();
        mailProps.setProperty("mail.transport.protocol", "smtp");
        mailProps.setProperty("mail.smtp.auth", "true");
        mailProps.setProperty("mail.smtp.starttls.enable", "true");
        mailProps.setProperty("mail.debug", "false");
        emailSender.setJavaMailProperties(mailProps);
        return emailSender;
    }

    @Bean
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
        final ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        arrayHttpMessageConverter.setSupportedMediaTypes(getSupportedMediaTypes());

        return arrayHttpMessageConverter;
    }

    private List<MediaType> getSupportedMediaTypes() {
        final List<MediaType> list = new ArrayList<>();
        list.add(MediaType.IMAGE_JPEG);
        list.add(MediaType.IMAGE_PNG);
        list.add(MediaType.APPLICATION_OCTET_STREAM);
        return list;
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getResolver() throws IOException {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSizePerFile(500242880);// 5MB
        return resolver;
    }
}
