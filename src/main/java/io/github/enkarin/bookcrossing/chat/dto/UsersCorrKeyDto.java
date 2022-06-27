package io.github.enkarin.bookcrossing.chat.dto;

import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Сущность для идентификатора чата")
public class UsersCorrKeyDto {

    @Schema(description = "Идентификатор первого пользователя")
    private final int firstUserId;

    @Schema(description = "Идентификатор второго пользователя")
    private final int secondUserId;

    private UsersCorrKeyDto(final int firstUserId, final int secondUserId) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
    }

    public static UsersCorrKeyDto fromCorrespondence(final Correspondence correspondence) {
        return new UsersCorrKeyDto(correspondence.getUsersCorrKey().getFirstUser().getUserId(),
                correspondence.getUsersCorrKey().getSecondUser().getUserId());
    }

    public static UsersCorrKeyDto fromFirstAndSecondId(final int firstUserId, final int secondUserId) {
        return new UsersCorrKeyDto(firstUserId, secondUserId);
    }
}
