package io.github.enkarin.bookcrossing.configuration;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class TimeSettings {

    @SuppressWarnings("PMD.SingularField")
    @Value("${time.zone-offset}")
    private int defaultZoneOffset;

    @Setter
    private Clock clock = Clock.system(ZoneOffset.ofHours(defaultZoneOffset));

    public ZoneOffset offset() {
        return clock.getZone().getRules().getOffset(clock.instant());
    }

    @SuppressWarnings("illegalMethodCall")
    public LocalDateTime dateTimeNow() {
        return LocalDateTime.now(clock);
    }

    public LocalDate dateNow() {
        return dateTimeNow().toLocalDate();
    }

    public long getEpochSeconds() {
        return clock.instant().getEpochSecond();
    }
}
