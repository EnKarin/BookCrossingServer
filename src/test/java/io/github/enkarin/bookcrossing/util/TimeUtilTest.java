package io.github.enkarin.bookcrossing.util;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class TimeUtilTest {

    @Test
    void setClock() {
        final Clock clock = Clock.fixed(Instant.ofEpochSecond(1_711_098_855L), ZoneOffset.ofHours(4));
        TimeUtil.setClock(clock);

        assertThat(TimeUtil.getEpochSeconds()).isEqualTo(clock.instant().getEpochSecond());
    }

    @Test
    void offset() {
        final Clock clock = Clock.system(ZoneOffset.ofHours(3));
        TimeUtil.setClock(clock);

        assertThat(TimeUtil.offset()).isEqualTo(ZoneOffset.ofHours(3));
    }

    @Test
    void dateTimeNow() {
        final Clock clock = Clock.fixed(Instant.ofEpochSecond(3_660), ZoneOffset.ofHours(7));
        TimeUtil.setClock(clock);

        assertThat(TimeUtil.dateTimeNow()).isEqualTo(LocalDateTime.of(1970, 1, 1, 8, 1, 0));
    }

    @Test
    void dateNow() {
        final Clock clock = Clock.fixed(Instant.ofEpochSecond(100_000), ZoneOffset.ofHours(7));
        TimeUtil.setClock(clock);

        assertThat(TimeUtil.dateNow()).isEqualTo(LocalDate.of(1970, 1, 2));
    }

    @Test
    void getEpochSeconds() {
        final Clock clock = Clock.fixed(Instant.ofEpochSecond(1_000), ZoneOffset.ofHours(10));
        TimeUtil.setClock(clock);

        assertThat(TimeUtil.getEpochSeconds()).isEqualTo(1_000);
    }
}
