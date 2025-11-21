package br.com.fiap.mindcare.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "{login.email.notblank}")
    @Email(message = "{login.email.invalid}")
    private String email;

    @NotBlank(message = "{login.senha.notblank}")
    private String senha;
}
