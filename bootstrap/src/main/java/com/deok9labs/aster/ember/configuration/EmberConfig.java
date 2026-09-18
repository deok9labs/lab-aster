package com.deok9labs.aster.ember.configuration;

import com.deok9labs.aster.ember.application.port.out.LoadCurrentSchedulePort;
import com.deok9labs.aster.ember.application.port.out.SaveMemberSchedulePort;
import com.deok9labs.aster.ember.application.service.ScheduleService;
import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Ember 유스케이스와 한국 시간 기준 clock을 조립한다. */
@Configuration
public class EmberConfig {

    @Bean
    Clock emberClock() {
        return Clock.system(ZoneId.of("Asia/Seoul"));
    }

    @Bean
    ScheduleService scheduleService(
            LoadCurrentSchedulePort loadPort,
            SaveMemberSchedulePort savePort,
            Clock emberClock) {
        return new ScheduleService(loadPort, savePort, emberClock);
    }

}
