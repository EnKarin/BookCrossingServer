package io.github.enkarin.bookcrossing.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserListResponse {

    @Schema(description = "Список пользователей")
    private List<UserDtoResponse> userList;

    public UserListResponse(final List<UserDtoResponse> list) {
        userList = list;
    }
}
