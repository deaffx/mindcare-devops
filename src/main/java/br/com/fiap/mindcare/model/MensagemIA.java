package br.com.fiap.mindcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens_ia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensagemIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{mensagem.conteudo.notblank}")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMensagem tipo;

    @Column(columnDefinition = "TEXT")
    private String contexto;

    @Column(nullable = false)
    private Boolean lida = false;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public enum TipoMensagem {
        MOTIVACAO, HUMOR, META, CHECKPOINT, BEM_VINDO, CHAT, USUARIO
    }
}
