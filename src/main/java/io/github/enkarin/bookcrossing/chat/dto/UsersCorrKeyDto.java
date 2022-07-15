package io.github.enkarin.bookcrossing.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.concurrent.Immutable;

@Immutable
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "Сущность для идентификатора чата")
public class UsersCorrKeyDto {

    @Schema(description = "Идентификатор первого пользователя")
    private final int firstUserId;

    @Schema(description = "Идентификатор второго пользователя")
    private final int secondUserId;

    public static UsersCorrKeyDto fromCorrespondence(final Correspondence correspondence) {
        return new UsersCorrKeyDto(correspondence.getUsersCorrKey().getFirstUser().getUserId(),
                correspondence.getUsersCorrKey().getSecondUser().getUserId());
    }

    @JsonCreator
    public static UsersCorrKeyDto fromFirstAndSecondId(final int firstUserId, final int secondUserId) {
        return new UsersCorrKeyDto(firstUserId, secondUserId);
    }
}
