package io.github.enkarin.bookcrossing.utils;

import io.github.enkarin.bookcrossing.constant.ErrorMessage;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class Util {

    public static Map<String, String> createErrorMap(final ErrorMessage message) {
        return Map.of("error", message.getCode());
    }

    public static Map<String, String> createErrorMap(final String code) {
        return Map.of("error", code);
    }
}
