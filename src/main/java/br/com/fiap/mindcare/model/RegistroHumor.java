package br.com.fiap.mindcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_humor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroHumor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{registro.humor.notnull}")
    @Min(value = 1, message = "{registro.nivel.min}")
    @Max(value = 5, message = "{registro.nivel.max}")
    @Column(name = "nivel_humor", nullable = false)
    private Integer nivelHumor;

    @NotNull(message = "{registro.emocao.notnull}")
    @Size(max = 50, message = "{registro.emocao.size}")
    @Column(nullable = false, length = 50)
    private String emocao;

    @Size(max = 500, message = "{registro.descricao.size}")
    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private LocalDate data;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
