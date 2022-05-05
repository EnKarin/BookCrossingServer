package ru.bookcrossing.BookcrossingServer.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Сущность для идентификатора чата")
public class UsersCorrKeyDto {

    private int firstUserId;

    private int secondUserId;
}
