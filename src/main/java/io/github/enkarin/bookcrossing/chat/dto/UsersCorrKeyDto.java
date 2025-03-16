package io.github.enkarin.bookcrossing.chat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.enkarin.bookcrossing.chat.model.Correspondence;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.annotation.concurrent.Immutable;

@SuperBuilder
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

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UsersCorrKeyDto that)) {
            return false;
        }
        return firstUserId == that.firstUserId && secondUserId == that.secondUserId || firstUserId == that.secondUserId && secondUserId == that.firstUserId;
    }

    @Override
    public int hashCode() {
        int result = firstUserId;
        result = 31 * result + secondUserId;
        return result;
    }
}
