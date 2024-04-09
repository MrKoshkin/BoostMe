package BoostMe.api.v1.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationUserDto {
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String username;

    @NotBlank(message = "Пароль не должен быть пустым")
    private String password;

    @NotBlank(message = "Подтверждение пароля не должно быть пустым")
    private String confirmPassword;

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный адрес электронной почты")
    private String email;
}