package ru.bookcrossing.BookcrossingServer.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.bookcrossing.BookcrossingServer.entity.User;

import java.util.List;

@Data
public class AdmUserListResponse {
    @Schema(description = "Список пользователей со служебной информацией")
    private List<User> userList;
}
