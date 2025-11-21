package br.com.fiap.mindcare.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;

    @NotBlank(message = "{usuario.nome.notblank}")
    @Size(min = 3, max = 100, message = "{usuario.nome.size}")
    private String nome;

    @NotBlank(message = "{usuario.email.notblank}")
    @Email(message = "{usuario.email.invalid}")
    private String email;

    @Size(max = 100, message = "{usuario.cargo.size}")
    private String cargo;

    private Boolean receberNotificacoes;
    private String preferenciaIdioma;
    private String role;
}
