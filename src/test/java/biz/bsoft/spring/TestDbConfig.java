package biz.bsoft.spring;

import biz.bsoft.config.PersistenceConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by vbabin on 02.11.2016.
 */
@Configuration
@ComponentScan({"biz.bsoft.users.dao","biz.bsoft.orders.dao"})
@Profile("test")
public class TestDbConfig extends PersistenceConfig {
    @Override
    public DataSource dataSource() {
        EmbeddedDatabase datasource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
        return datasource;
    }

    @Override
    protected Properties getHibernateProperties() {
        Properties properties = super.getHibernateProperties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        return properties;
    }
}
