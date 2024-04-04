package io.github.enkarin.bookcrossing.util;

import io.github.enkarin.bookcrossing.configuration.TimeSettings;
import io.github.enkarin.bookcrossing.support.BookCrossingBaseTests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class TimeSettingsTest extends BookCrossingBaseTests {
    @Autowired
    private TimeSettings timeSettings;

    @AfterEach
    void rollbackClock() {
        timeSettings.setClock(Clock.system(ZoneOffset.ofHours(7)));
    }

    @Test
    void setClock() {
        final long seconds = 1_711_098_855L;
        timeSettings.setClock(Clock.fixed(Instant.ofEpochSecond(seconds), ZoneOffset.ofHours(4)));

        assertThat(timeSettings.getEpochSeconds()).isEqualTo(seconds);
    }

    @Test
    void offset() {
        final ZoneOffset offset = ZoneOffset.ofHours(3);
        timeSettings.setClock(Clock.system(offset));

        assertThat(timeSettings.offset()).isEqualTo(offset);
    }

    @Test
    void dateTimeNow() {
        timeSettings.setClock(Clock.fixed(Instant.ofEpochSecond(3_660), ZoneOffset.ofHours(7)));

        assertThat(timeSettings.dateTimeNow()).isEqualTo(LocalDateTime.of(1970, 1, 1, 8, 1, 0));
    }

    @Test
    void dateNow() {
        timeSettings.setClock(Clock.fixed(Instant.ofEpochSecond(100_000), ZoneOffset.ofHours(7)));

        assertThat(timeSettings.dateNow()).isEqualTo(LocalDate.of(1970, 1, 2));
    }

    @Test
    void getEpochSeconds() {
        final int seconds = 1_000;
        timeSettings.setClock(Clock.fixed(Instant.ofEpochSecond(seconds), ZoneOffset.ofHours(10)));

        assertThat(timeSettings.getEpochSeconds()).isEqualTo(seconds);
    }
}
