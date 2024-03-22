package io.github.enkarin.bookcrossing.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class TimeUtilTest {

    @AfterAll
    static void rollbackClock() {
        TimeUtil.setClock(Clock.system(ZoneOffset.ofHours(7)));
    }

    @Test
    void setClock() {
        final long seconds = 1_711_098_855L;
        TimeUtil.setClock(Clock.fixed(Instant.ofEpochSecond(seconds), ZoneOffset.ofHours(4)));

        assertThat(TimeUtil.getEpochSeconds()).isEqualTo(seconds);
    }

    @Test
    void offset() {
        final ZoneOffset offset = ZoneOffset.ofHours(3);
        TimeUtil.setClock(Clock.system(offset));

        assertThat(TimeUtil.offset()).isEqualTo(offset);
    }

    @Test
    void dateTimeNow() {
        TimeUtil.setClock(Clock.fixed(Instant.ofEpochSecond(3_660), ZoneOffset.ofHours(7)));

        assertThat(TimeUtil.dateTimeNow()).isEqualTo(LocalDateTime.of(1970, 1, 1, 8, 1, 0));
    }

    @Test
    void dateNow() {
        TimeUtil.setClock(Clock.fixed(Instant.ofEpochSecond(100_000), ZoneOffset.ofHours(7)));

        assertThat(TimeUtil.dateNow()).isEqualTo(LocalDate.of(1970, 1, 2));
    }

    @Test
    void getEpochSeconds() {
        final int seconds = 1_000;
        TimeUtil.setClock(Clock.fixed(Instant.ofEpochSecond(seconds), ZoneOffset.ofHours(10)));

        assertThat(TimeUtil.getEpochSeconds()).isEqualTo(seconds);
    }
}
