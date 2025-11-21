package br.com.fiap.mindcare.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroHumorDTO {
    private Long id;

    @NotNull(message = "{registro.humor.notnull}")
    @Min(value = 1, message = "{registro.nivel.min}")
    @Max(value = 5, message = "{registro.nivel.max}")
    private Integer nivelHumor;

    @NotNull(message = "{registro.emocao.notnull}")
    @Size(max = 50, message = "{registro.emocao.size}")
    private String emocao;

    @Size(max = 500, message = "{registro.descricao.size}")
    private String descricao;

    private LocalDate data;
    private LocalDateTime criadoEm;
    private String mensagemMotivacional;
}
