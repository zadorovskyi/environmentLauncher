package services;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class PropertiesResolver {
    protected static Properties properties;

    @PostConstruct
    public void init() {
        properties = new Properties();
        InputStream is = null;
        try {
            is = getClass().getResource("/application.properties").openStream();
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

