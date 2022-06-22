package io.github.enkarin.bookcrossing.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Сущность для идентификатора чата с часовым поясом")
public class ZonedUserCorrKeyDto {

    private int firstUserId;

    private int secondUserId;

    private int zone;
}
