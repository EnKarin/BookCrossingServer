package ru.bookcrossing.BookcrossingServer.admin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.bookcrossing.BookcrossingServer.user.model.User;

import java.util.List;

@Data
public class AdmUserListResponse {
    @Schema(description = "Список пользователей со служебной информацией")
    private List<User> userList;
}
