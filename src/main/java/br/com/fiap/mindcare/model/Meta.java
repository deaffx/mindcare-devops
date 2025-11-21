package br.com.fiap.mindcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "metas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{meta.titulo.notblank}")
    @Size(min = 3, max = 200, message = "{meta.titulo.size}")
    @Column(nullable = false, length = 200)
    private String titulo;

    @Size(max = 1000, message = "{meta.descricao.size}")
    @Column(length = 1000)
    private String descricao;

    @Min(value = 1, message = "{meta.duracao.min}")
    @Column(name = "duracao_dias")
    private Integer duracaoDias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Tipo tipo = Tipo.PRAZO;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "ultimo_checkin")
    private LocalDate ultimoCheckin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ATIVA;

    @Column(name = "dias_consecutivos")
    private Integer diasConsecutivos = 0;

    @Column(name = "total_checkpoints")
    private Integer totalCheckpoints = 0;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "meta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgressoMeta> progressos = new ArrayList<>();

    public enum Categoria {
        SAUDE, ALIMENTACAO, EXERCICIO, SONO, FOCO,
        HABITO, ESTUDO, MEDITACAO, HIDRATACAO, OUTRO
    }

    public enum Status {
        ATIVA, CONCLUIDA, CANCELADA, PAUSADA
    }

    public enum Tipo {
        PRAZO,
        CONSECUTIVO
    }

    public int calcularProgresso() {
        if (tipo == Tipo.CONSECUTIVO) {
            // Metas consecutivas: progresso é baseado nos dias consecutivos alcançados vs objetivo
            if (duracaoDias == null || duracaoDias == 0) return 0;
            int progresso = (int) ((diasConsecutivos * 100.0) / duracaoDias);
            return Math.min(progresso, 100);
        }
        // Metas com prazo
        if (duracaoDias == null || duracaoDias == 0) return 0;
        return (int) ((totalCheckpoints * 100.0) / duracaoDias);
    }

    public int diasRestantes() {
        if (tipo == Tipo.CONSECUTIVO) {
            return 0;
        }
        if (duracaoDias == null) return 0;
        return Math.max(0, duracaoDias - totalCheckpoints);
    }

    public boolean podeRealizarCheckin() {
        if (status != Status.ATIVA) return false;
        if (ultimoCheckin == null) return true;
        return !ultimoCheckin.equals(LocalDate.now());
    }

    public boolean checkinPerdido() {
        if (tipo != Tipo.CONSECUTIVO || ultimoCheckin == null) return false;
        return ultimoCheckin.isBefore(LocalDate.now().minusDays(1));
    }
}
