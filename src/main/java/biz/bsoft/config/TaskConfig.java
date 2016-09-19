package biz.bsoft.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by vbabin on 16.09.2016.
 */
@Configuration
@EnableScheduling
@ComponentScan({"biz.bsoft.task"})
public class TaskConfig {
}
