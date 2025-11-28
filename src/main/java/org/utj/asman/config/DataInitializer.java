package org.utj.asman.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.utj.asman.service.SettingService;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SettingService settingService;

    @Override
    public void run(String... args) throws Exception {
        settingService.initDefaultSettings();
    }
}
