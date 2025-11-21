package br.com.fiap.mindcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{usuario.nome.notblank}")
    @Size(min = 3, max = 100, message = "{usuario.nome.size}")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "{usuario.email.notblank}")
    @Email(message = "{usuario.email.invalid}")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "{usuario.senha.notblank}")
    @Size(min = 6, message = "{usuario.senha.size}")
    @Column(nullable = false)
    private String senha;

    @Size(max = 100, message = "{usuario.cargo.size}")
    @Column(length = 100)
    private String cargo;

    @Column(name = "receber_notificacoes")
    private Boolean receberNotificacoes = true;

    @Column(name = "preferencia_idioma", length = 10)
    private String preferenciaIdioma = "pt-BR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(nullable = false)
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroHumor> registrosHumor = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meta> metas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MensagemIA> mensagensIA = new ArrayList<>();

    public enum Role {
        USER, ADMIN
    }
}
