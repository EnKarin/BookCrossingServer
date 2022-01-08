package ru.bookcrossing.BookcrossingServer.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {

    @Schema(description = "Список пользователей")
    private List<UserDTOResponse> userList;
}
