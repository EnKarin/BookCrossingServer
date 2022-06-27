package io.github.enkarin.bookcrossing.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Schema(description = "Сущность для сообщения")
public class MessageRequest {

    @Schema(description = "Идентификатор чата")
    private final UsersCorrKeyDto usersCorrKeyDto;

    @Getter
    @Schema(description = "Текст сообщения")
    @NotBlank(message = "message: Сообщение должно состоять хотя бы из одного видимого символа")
    private final String text;

    public MessageRequest(final UsersCorrKeyDto usersCorrKeyDto, final String text) {
        this.usersCorrKeyDto = usersCorrKeyDto;
        this.text = text;
    }

    public UsersCorrKeyDto getUsersCorrKeyDto() {
        return UsersCorrKeyDto.fromFirstAndSecondId(usersCorrKeyDto.getFirstUserId(),
                usersCorrKeyDto.getSecondUserId());
    }
}
