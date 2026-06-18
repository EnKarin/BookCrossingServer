package io.github.enkarin.bookcrossing.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class ZoneOffsetConverter implements Converter<String, ZoneOffset> {

    @Override
    public ZoneOffset convert(String source) {
        if (source == null || source.isEmpty()) {
            return ZoneOffset.UTC;
        }

        // Try parsing as hours first (e.g., "0", "2", "-5")
        try {
            int hours = Integer.parseInt(source);
            return ZoneOffset.ofHours(hours);
        } catch (NumberFormatException e) {
            // Fall back to parsing as ISO format (e.g., "+02:00", "Z")
            return ZoneOffset.of(source);
        }
    }
}
