package br.com.fiap.mindcare.service;

import br.com.fiap.mindcare.config.RabbitMQConfig;
import br.com.fiap.mindcare.model.MensagemIA;
import br.com.fiap.mindcare.model.Usuario;
import br.com.fiap.mindcare.repository.MensagemIARepository;
import br.com.fiap.mindcare.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacaoService {

    private final RabbitTemplate rabbitTemplate;
    private final MensagemIARepository mensagemIARepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void enviarBoasVindas(Long usuarioId, String nomeUsuario) {
        log.info("Enviando mensagem de boas-vindas para usuário: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new br.com.fiap.mindcare.exception.ResourceNotFoundException(
                        "Usuário não encontrado"));

        String conteudo = String.format(
                "Bem-vindo ao MindCare, %s! 🌟 " +
                "Estamos felizes em tê-lo aqui. Juntos, vamos cuidar da sua saúde mental e " +
                "ajudá-lo a alcançar seus objetivos pessoais. Comece registrando seu humor hoje!",
                nomeUsuario);

        salvarMensagem(usuario, conteudo, MensagemIA.TipoMensagem.BEM_VINDO, "Registro inicial");

        if (usuario.getReceberNotificacoes()) {
            enviarParaFila(usuarioId, conteudo, "BEM_VINDO");
        }
    }

    @Transactional
    public void enviarNotificacaoHumor(Long usuarioId, String mensagem) {
        log.info("Enviando notificação de humor para usuário: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new br.com.fiap.mindcare.exception.ResourceNotFoundException(
                        "Usuário não encontrado"));

        salvarMensagem(usuario, mensagem, MensagemIA.TipoMensagem.HUMOR, "Registro de humor");

        if (usuario.getReceberNotificacoes()) {
            enviarParaFila(usuarioId, mensagem, "HUMOR");
        }
    }

    @Transactional
    public void enviarNotificacaoMeta(Long usuarioId, String mensagem) {
        log.info("Enviando notificação de meta para usuário: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new br.com.fiap.mindcare.exception.ResourceNotFoundException(
                        "Usuário não encontrado"));

        salvarMensagem(usuario, mensagem, MensagemIA.TipoMensagem.META, "Atualização de meta");

        if (usuario.getReceberNotificacoes()) {
            enviarParaFila(usuarioId, mensagem, "META");
        }
    }

    @Transactional
    public void enviarNotificacaoCheckpoint(Long usuarioId, String mensagem) {
        log.info("Enviando notificação de checkpoint para usuário: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new br.com.fiap.mindcare.exception.ResourceNotFoundException(
                        "Usuário não encontrado"));

        salvarMensagem(usuario, mensagem, MensagemIA.TipoMensagem.CHECKPOINT, "Progresso registrado");

        if (usuario.getReceberNotificacoes()) {
            enviarParaFila(usuarioId, mensagem, "CHECKPOINT");
        }
    }

    @Transactional
    public void salvarMensagemChat(Long usuarioId, String mensagem) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new br.com.fiap.mindcare.exception.ResourceNotFoundException(
                        "Usuário não encontrado"));

        salvarMensagem(usuario, mensagem, MensagemIA.TipoMensagem.CHAT, "Conversa com MindBot");
    }

    private void salvarMensagem(Usuario usuario, String conteudo, 
                                 MensagemIA.TipoMensagem tipo, String contextoDescricao) {
        MensagemIA mensagem = new MensagemIA();
        mensagem.setConteudo(conteudo);
        mensagem.setTipo(tipo);
        
        // Criar um JSON válido para o contexto
        String contextoJson = String.format(
            "{\"descricao\": \"%s\", \"timestamp\": \"%s\"}",
            contextoDescricao.replace("\"", "\\\""),
            java.time.LocalDateTime.now().toString()
        );
        mensagem.setContexto(contextoJson);
        
        mensagem.setLida(false);
        mensagem.setUsuario(usuario);

        mensagemIARepository.save(mensagem);
    }

    private void enviarParaFila(Long usuarioId, String mensagem, String tipo) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("usuarioId", usuarioId);
            notification.put("mensagem", mensagem);
            notification.put("tipo", tipo);
            notification.put("timestamp", System.currentTimeMillis());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    "notification." + tipo.toLowerCase(),
                    notification
            );

            log.info("Notificação enviada para fila: tipo={}, usuário={}", tipo, usuarioId);
        } catch (Exception e) {
            log.error("Erro ao enviar notificação para fila", e);
        }
    }
}
