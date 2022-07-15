package io.github.enkarin.bookcrossing.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotBlank;

@Immutable
@Validated
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Сущность для сообщения")
public class MessageRequest {

    @Schema(description = "Идентификатор чата")
    private final UsersCorrKeyDto usersCorrKeyDto;

    @Getter
    @Schema(description = "Текст сообщения")
    @NotBlank(message = "Сообщение должно состоять хотя бы из одного видимого символа")
    private final String text;

    public UsersCorrKeyDto getUsersCorrKeyDto() {
        return UsersCorrKeyDto.fromFirstAndSecondId(usersCorrKeyDto.getFirstUserId(),
                usersCorrKeyDto.getSecondUserId());
    }

    @JsonCreator
    public static MessageRequest create(final UsersCorrKeyDto usersCorrKeyDto, final String text) {
        return new MessageRequest(usersCorrKeyDto, text);
    }
}
