package com.inter3i.sun.api.ota.v1.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

import static org.springframework.core.io.support.PropertiesLoaderUtils.loadProperties;

/**
 * Created by boxiaotong on 2017/6/5.
 */
@Component
public class PropertyConfig {

    private static Resource resource;

    public PropertyConfig() throws IOException {
        resource = new ClassPathResource("/importdata.properties");
    }

    public static String getValue(String key){
        Properties props = null;
        try {
            props = loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String value=(String) props.get(key);
        return value;
    }
}
