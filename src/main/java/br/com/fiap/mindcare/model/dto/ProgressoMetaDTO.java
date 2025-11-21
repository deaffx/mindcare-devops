package br.com.fiap.mindcare.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressoMetaDTO {
    private Long id;

    @NotNull(message = "{progresso.metaid.notnull}")
    private Long metaId;

    private LocalDate data;
    private Boolean concluido;

    @Size(max = 500, message = "{progresso.observacao.size}")
    private String observacao;
}
