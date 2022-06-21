package ru.bookcrossing.bookcrossingserver.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AdmUserListResponse {
    @Schema(description = "Список пользователей со служебной информацией")
    private List<InfoUsersDto> userList;
}
