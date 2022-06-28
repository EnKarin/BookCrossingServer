package io.github.enkarin.bookcrossing.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    private ZonedUserCorrKeyDto(final int firstUserId, final int secondUserId, final int zone) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.zone = zone;
    }

    @JsonCreator
    public static ZonedUserCorrKeyDto create(final int firstUserId, final int secondUserId, final int zone) {
        return new ZonedUserCorrKeyDto(firstUserId, secondUserId, zone);
    }
}
