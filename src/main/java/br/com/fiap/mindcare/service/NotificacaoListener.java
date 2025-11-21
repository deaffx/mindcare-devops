package br.com.fiap.mindcare.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class NotificacaoListener {

    @RabbitListener(queues = "mindcare.notifications")
    public void processarNotificacao(Map<String, Object> notificacao) {
        try {
            Long usuarioId = ((Number) notificacao.get("usuarioId")).longValue();
            String mensagem = (String) notificacao.get("mensagem");
            String tipo = (String) notificacao.get("tipo");
            
            log.info("Notificação recebida - Usuário: {}, Tipo: {}", usuarioId, tipo);
            log.info("Mensagem: {}", mensagem);
            
            // Aqui você pode implementar lógica adicional:
            // - Enviar push notification
            // - Enviar email
            // - Enviar SMS
            // - Integrar com Firebase Cloud Messaging
            // - Enviar para websocket para atualização em tempo real
            
            processarPorTipo(usuarioId, mensagem, tipo);
            
        } catch (Exception e) {
            log.error("Erro ao processar notificação", e);
        }
    }

    private void processarPorTipo(Long usuarioId, String mensagem, String tipo) {
        switch (tipo) {
            case "BEM_VINDO":
                log.info("Processando mensagem de boas-vindas para usuário {}", usuarioId);
                // Lógica específica para boas-vindas
                break;
                
            case "HUMOR":
                log.info("Processando notificação de humor para usuário {}", usuarioId);
                // Lógica específica para humor
                break;
                
            case "META":
                log.info("Processando notificação de meta para usuário {}", usuarioId);
                // Lógica específica para metas
                break;
                
            case "CHECKPOINT":
                log.info("Processando notificação de checkpoint para usuário {}", usuarioId);
                // Lógica específica para checkpoints
                break;
                
            default:
                log.warn("Tipo de notificação desconhecido: {}", tipo);
        }
    }
}
