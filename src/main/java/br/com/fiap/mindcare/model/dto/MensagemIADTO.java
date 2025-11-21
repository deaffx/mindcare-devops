package br.com.fiap.mindcare.model.dto;

import br.com.fiap.mindcare.model.MensagemIA;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensagemIADTO {
    private Long id;
    private String conteudo;
    private MensagemIA.TipoMensagem tipo;
    private String contexto;
    private Boolean lida;
    private LocalDateTime criadoEm;
}
