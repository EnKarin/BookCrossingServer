package io.github.enkarin.bookcrossing.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class ZoneOffsetConverter implements Converter<String, ZoneOffset> {

    @Override
    public ZoneOffset convert(final String source) {
        if (source == null || source.isEmpty()) {
            return ZoneOffset.UTC;
        }

        try {
            final int hours = Integer.parseInt(source);
            return ZoneOffset.ofHours(hours);
        } catch (NumberFormatException e) {
            return ZoneOffset.of(source);
        }
    }
}
