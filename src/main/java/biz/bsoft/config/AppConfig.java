package biz.bsoft.config;

import biz.bsoft.OrdersApplication;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Properties;

/**
 * Created by vbabin on 27.03.2016.
 */
//@EnableWebMvc
@Configuration
@ComponentScan({ "biz.bsoft.orders.service.*" })
@PropertySource("classpath:email.properties")
@EnableAsync
@Import({ SecurityConfig.class})
public class AppConfig  extends SpringBootServletInitializer {
    private final Logger logger =
            LoggerFactory.getLogger(getClass());

    @Autowired
    private Environment env;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OrdersApplication.class);
    }


    @Bean(name = "OBJECT_MAPPER_BEAN")
    public ObjectMapper jsonObjectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
                .modules(new JSR310Module())
                .build();
    }

     @Bean
     public MessageSource messageSource() {
     final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
         messageSource.setBasename("classpath:messages");
         messageSource.setUseCodeAsDefaultMessage(true);
         messageSource.setDefaultEncoding("UTF-8");
         messageSource.setAlwaysUseMessageFormat(true);
         messageSource.setCacheSeconds(0);
         return messageSource;
     }

   /* @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver
                = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }*/

    @Bean
    public JavaMailSenderImpl javaMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();

        try {
            mailSenderImpl.setHost(env.getRequiredProperty("smtp.host"));
            mailSenderImpl.setPort(env.getRequiredProperty("smtp.port", Integer.class));
            mailSenderImpl.setProtocol(env.getRequiredProperty("smtp.protocol"));
            mailSenderImpl.setUsername(env.getRequiredProperty("smtp.username"));
            mailSenderImpl.setPassword(env.getRequiredProperty("smtp.password"));
            mailSenderImpl.setDefaultEncoding("UTF-8");
        } catch (IllegalStateException ise) {
            logger.error("Could not resolve email.properties.s.  See email.properties.s.sample");
            throw ise;
        }
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }
}
