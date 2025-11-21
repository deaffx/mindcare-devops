package br.com.fiap.mindcare.model.dto;

import br.com.fiap.mindcare.model.Meta;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDTO {
    private Long id;

    @NotBlank(message = "{meta.titulo.notblank}")
    @Size(min = 3, max = 200, message = "{meta.titulo.size}")
    private String titulo;

    @Size(max = 1000, message = "{meta.descricao.size}")
    private String descricao;

    @Min(value = 1, message = "{meta.duracao.min}")
    private Integer duracaoDias;

    @NotNull(message = "{meta.categoria.notnull}")
    private Meta.Categoria categoria;

    @NotNull(message = "{meta.tipo.notnull}")
    private Meta.Tipo tipo = Meta.Tipo.PRAZO;

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalDate ultimoCheckin;
    private Meta.Status status;
    private Integer diasConsecutivos;
    private Integer totalCheckpoints;
    private Integer progresso;
    private Integer diasRestantes;
    private Boolean podeRealizarCheckin;
    private Boolean checkinPerdido;
}
