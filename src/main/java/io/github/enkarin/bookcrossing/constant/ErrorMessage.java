package io.github.enkarin.bookcrossing.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorMessage {

    //user ex
    ERROR_1000("1000", "Пароли не совпадают", "Password don't match"),
    ERROR_1001("1001", "Аккаунт заблокирован", "Account is blocked"),
    ERROR_1002("1002", "Пользователь с таким логином уже существует", "User with this username already exists"),
    ERROR_1003("1003", "Пользователь не найден", "User not found"),
    ERROR_1004("1004", "Книга не найдена", "Book not found"),
    ERROR_1005("1005", "Аккаунт не подтверждён", "Account not confirmed"),
    ERROR_1006("1006", "Пользователь с таким почтовым адресом уже существует", "User with this email already exists"),
    ERROR_1007("1007", "Неверное имя пользователя или пароль", "Invalid username or password"),
    ERROR_1008("1008", "Вложение не принадлежит пользователю", "Attachment does not belong to this user"),
    ERROR_1009("1009", "Чата не существует", "Chat not found"),
    ERROR_1010("1010", "Чат с пользователем уже существует", "Chat already exists"),
    ERROR_1011("1011", "С выбранным пользователем нельзя создать чат", "You are unable to create a chat with the selected user"),
    ERROR_1012("1012", "Нет доступа к чату", "No access to chat"),
    ERROR_1013("1013", "Сообщение не может быть пустым", "Message cannot be empty"),
    ERROR_1014("1014", "Сообщения не существует", "Message not exist"),
    ERROR_1015("1015", "Пользователь не является отправителем", "User is not the sender"),
    ERROR_1016("1016", "Вложение не найдено", "Attachment not found"),
    ERROR_1017("1017", "Статус не был найден", "Status not found"),

    //tech ex
    ERROR_2002("2002", "Токен обновления истек", "Refresh token has expired"),
    ERROR_2003("2003", "Токен недействителен", "Token invalid"),
    ERROR_2004("2004", "Токен не найден", "Token not found"),
    ERROR_2005("2005", "Формат должен быть 'origin', 'list' или 'thumb'", "Format should be 'origin', 'list' or 'thumb'"),
    ERROR_2006("2006", "Локаль должна быть 'ru' или 'eng'", "Locale should be 'ru' or 'eng'"),
    ERROR_2007("2007", "Указанный жанр не найден", "Specified genre not found"),
    ERROR_2008("2008", "Невозможно прочитать добавляемый файл", "The file being added cannot be read"),

    //validation ex
    ERROR_3001("3001", "Имя файла не должно быть пустым", "File name cannot be empty"),
    ERROR_3002("3002", "Недопустимый формат файла", "File format is not supported"),
    ERROR_3003("3003", "login: Логин должен содержать хотя бы один видимый символ", "login: Login must include at least one visible character"),
    ERROR_3004("3004", "comment: Комментарий должен содержать хотя бы один видимый символ", "comment: The comment should contain at least one character that is visible"),
    ERROR_3005("3005", "title: Название должно содержать хотя бы один видимый символ", "title: The title must contain at least one visible character"),
    ERROR_3006("3006", "author: Поле \"автор\" должно содержать хотя бы один видимый символ", "author: The \"author\" field must contain at least one visible character"),
    ERROR_3007("3007", "message: Сообщение должно состоять хотя бы из одного видимого символа", "message: The message must consist of at least one visible character"),
    ERROR_3008("3008", "name: Имя должно содержать хотя бы один видимый символ", "name: The name must consist of at least one visible character"),
    ERROR_3009("3009", "password: Пароль должен содержать хотя бы один видимый символ", "password: The password must consist of at least one visible character"),
    ERROR_3010("3010", "password: Пароль должен состоять минимум из восьми символов", "password: The password must be at least eleven characters long"),
    ERROR_3011("3011", "email: Некорректный почтовый адрес", "email: Invalid email"),
    ERROR_3012("3012", "oldPassword: Пароль должен содержать хотя бы один видимый символ", "oldPassword: The password must consist of at least one visible character"),
    ERROR_3013("3013", "userId: Идентификатор не должно быть пустым", "userId: Identifier must be not empty"),
    ERROR_3014("3014", "firstUserId: Идентификатор не должно быть пустым", "firstUserId: Identifier must be not empty"),
    ERROR_3015("3015", "secondUserId: Идентификатор не должно быть пустым", "secondUserId: Identifier must be not empty");

    private final String code;
    private final String ru;
    private final String en;

    @JsonCreator
    ErrorMessage(final String code, final String ru, final String en) {
        this.code = code;
        this.ru = ru;
        this.en = en;
    }
}
