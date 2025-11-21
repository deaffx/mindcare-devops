package br.com.fiap.mindcare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IAService {

    private final ChatClient.Builder chatClientBuilder;

    private static final String SYSTEM_PROMPT = """
            Você é o MindBot, um assistente virtual empático e motivacional do aplicativo MindCare.
            Seu objetivo é promover bem-estar emocional e incentivar hábitos saudáveis.
            
            Diretrizes:
            - Seja sempre empático, positivo e acolhedor
            - Use uma linguagem clara e amigável
            - Mantenha respostas curtas (máximo 150 palavras)
            - Foque em saúde mental, produtividade e equilíbrio emocional
            - Use emojis ocasionalmente para humanizar a conversa
            - Evite jargões médicos complexos
            - Nunca substitua orientação médica profissional
            """;

    public String gerarMensagemHumor(Integer nivelHumor, String emocao, String nomeUsuario) {
        log.info("Gerando mensagem de humor para usuário: {}", nomeUsuario);

        String prompt = String.format("""
                O usuário %s registrou seu humor hoje:
                - Nível: %d/5
                - Emoção: %s
                
                Gere uma mensagem curta (2-3 frases) de incentivo ou apoio personalizada.
                """, nomeUsuario, nivelHumor, emocao);

        return gerarResposta(prompt);
    }

    public String gerarMensagemMeta(String tituloMeta, String nomeUsuario) {
        log.info("Gerando mensagem de meta para usuário: {}", nomeUsuario);

        String prompt = String.format("""
                O usuário %s criou uma nova meta: "%s"
                
                Gere uma mensagem motivacional curta (2-3 frases) para incentivá-lo a começar.
                """, nomeUsuario, tituloMeta);

        return gerarResposta(prompt);
    }

    public String gerarMensagemProgressoMeta(String tituloMeta, int porcentagem, String nomeUsuario) {
        log.info("Gerando mensagem de progresso de meta: {}%", porcentagem);

        String prompt = String.format("""
                O usuário %s atingiu %d%% de progresso na meta: "%s"
                
                Gere uma mensagem de comemoração e incentivo (2-3 frases).
                """, nomeUsuario, porcentagem, tituloMeta);

        return gerarResposta(prompt);
    }

    public String gerarMensagemConclusaoMeta(String tituloMeta, String nomeUsuario) {
        log.info("Gerando mensagem de conclusão de meta para usuário: {}", nomeUsuario);

        String prompt = String.format("""
                O usuário %s COMPLETOU a meta: "%s" 🎉
                
                Gere uma mensagem de parabéns entusiasta (2-3 frases) celebrando a conquista.
                """, nomeUsuario, tituloMeta);

        return gerarResposta(prompt);
    }

    public String gerarBoasVindas(String nomeUsuario) {
        log.info("Gerando mensagem de boas-vindas para: {}", nomeUsuario);

        String prompt = String.format("""
                Um novo usuário chamado %s acabou de se cadastrar no MindCare.
                
                Gere uma mensagem calorosa de boas-vindas (2-3 frases) explicando brevemente 
                que o app ajuda com bem-estar emocional e metas pessoais.
                """, nomeUsuario);

        return gerarResposta(prompt);
    }

    public String conversarComBot(String mensagem, String nomeUsuario, String contexto) {
        log.info("Chat com bot - usuário: {}", nomeUsuario);

        String prompt = String.format("""
                Usuário: %s
                Contexto: %s
                
                Mensagem: %s
                
                Responda como MindBot, sendo empático e prestativo.
                """, nomeUsuario, contexto != null ? contexto : "Conversa geral", mensagem);

        return gerarResposta(prompt);
    }

    public String gerarResposta(String userPrompt) {
        try {
            ChatClient chatClient = chatClientBuilder.build();
            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(SYSTEM_PROMPT),
                    new UserMessage(userPrompt)
            ));

            String resposta = chatClient.prompt(prompt)
                    .call()
                    .content();

            return resposta != null ? resposta.trim() : "Desculpe, não consegui gerar uma resposta no momento.";
            
        } catch (org.springframework.ai.retry.NonTransientAiException e) {
            log.error("Erro ao chamar API de IA: {}", e.getMessage());
            return gerarRespostaFallback(userPrompt);
        } catch (Exception e) {
            log.error("Erro inesperado na geração de resposta: {}", e.getMessage());
            return gerarRespostaFallback(userPrompt);
        }
    }

    private String gerarRespostaFallback(String userPrompt) {
        log.info("Usando resposta padrão (Groq não configurada)");
        // Respostas padrão quando a API não está disponível
        if (userPrompt.toLowerCase().contains("humor") || userPrompt.toLowerCase().contains("emoção")) {
            return "Obrigado por compartilhar como você está se sentindo! Lembre-se que cada dia é uma nova oportunidade. 💙";
        } else if (userPrompt.toLowerCase().contains("meta") && userPrompt.toLowerCase().contains("completou")) {
            return "Parabéns por completar sua meta! Essa conquista mostra sua dedicação e força de vontade. Continue assim! 🎉";
        } else if (userPrompt.toLowerCase().contains("meta") && userPrompt.toLowerCase().contains("progresso")) {
            return "Ótimo progresso! Você está no caminho certo. Continue dando pequenos passos todos os dias! 🎯";
        } else if (userPrompt.toLowerCase().contains("meta")) {
            return "Que ótimo definir essa meta! Pequenos passos diários te levarão longe. Continue assim! 🎯";
        } else if (userPrompt.toLowerCase().contains("boas-vindas") || userPrompt.toLowerCase().contains("cadastr")) {
            return "Bem-vindo ao MindCare! Estou aqui para te ajudar a cuidar do seu bem-estar emocional e alcançar suas metas. Vamos juntos nessa jornada! 🌟";
        } else {
            return "Olá! Estou aqui para apoiar você. Como posso ajudar hoje? 😊";
        }
    }
}
