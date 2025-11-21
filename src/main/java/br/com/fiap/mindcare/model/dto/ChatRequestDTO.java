package br.com.fiap.mindcare.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {
    @NotBlank(message = "{chat.mensagem.notblank}")
    private String mensagem;
    
    private String contexto;
}
