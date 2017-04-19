package com.inter3i.sun.api.ota.v1.service;

import com.inter3i.sun.api.ota.v1.service.dataimport.ICommonDataService;
import com.inter3i.sun.api.ota.v1.service.dataimport.ITravelDataService;
import com.inter3i.sun.api.ota.v1.service.impl.DefaultEsService;
import com.inter3i.sun.api.ota.v1.service.impl.DefaultTestService;
import com.inter3i.sun.api.ota.v1.service.impl.dataimport.CommonDataService;
import com.inter3i.sun.api.ota.v1.service.impl.dataimport.TravelDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class ServiceFactory {
    private static final ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

    @Configuration
    public static class AppConfig {
        @Bean
        TestService testService() {
            return new DefaultTestService();
        }

        @Bean
        EsService esService() {
            return new DefaultEsService();
        }

        @Bean
        ITravelDataService dataImportService() {
            return new TravelDataService();
        }

        @Bean
        ICommonDataService commonDataService() {
            return new CommonDataService();
        }
    }

    public static TestService testService() {
        return context.getBean(TestService.class);
    }

    public static EsService esService() {
        return context.getBean(EsService.class);
    }

    public static ITravelDataService travelDataService() {
        return context.getBean(ITravelDataService.class);
    }

    public static ICommonDataService commonDataService() {
        return context.getBean(ICommonDataService.class);
    }

}
