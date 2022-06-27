package io.github.enkarin.bookcrossing.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Сущность для идентификатора чата с часовым поясом")
public class ZonedUserCorrKeyDto {

    @Schema(description = "Идентификатор первого пользователя")
    private final int firstUserId;

    @Schema(description = "Идентификатор второго пользователя")
    private final int secondUserId;

    @Schema(description = "Часовой пояс")
    private final int zone;

    public ZonedUserCorrKeyDto(final int firstUserId, final int secondUserId, final int zone) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.zone = zone;
    }
}
