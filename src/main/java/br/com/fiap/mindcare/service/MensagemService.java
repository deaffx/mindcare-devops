package br.com.fiap.mindcare.service;

import br.com.fiap.mindcare.model.MensagemIA;
import br.com.fiap.mindcare.model.Usuario;
import br.com.fiap.mindcare.model.dto.ChatRequestDTO;
import br.com.fiap.mindcare.model.dto.MensagemIADTO;
import br.com.fiap.mindcare.repository.MensagemIARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MensagemService {

    private final MensagemIARepository mensagemIARepository;
    private final UsuarioService usuarioService;
    private final IAService iaService;
    private final NotificacaoService notificacaoService;

    @Transactional
    public MensagemIADTO conversar(ChatRequestDTO request, String emailUsuario) {
        log.info("Processando conversa com bot para usuário: {}", emailUsuario);

        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);

        // Salva a mensagem do usuário
        MensagemIA mensagemUsuario = new MensagemIA();
        mensagemUsuario.setUsuario(usuario);
        mensagemUsuario.setConteudo(request.getMensagem());
        mensagemUsuario.setTipo(MensagemIA.TipoMensagem.USUARIO);
        mensagemUsuario.setContexto(request.getContexto());
        mensagemUsuario.setLida(true);
        mensagemIARepository.save(mensagemUsuario);
        log.info("Mensagem do usuário salva: {}", mensagemUsuario.getId());

        // Gera resposta do bot
        String resposta = iaService.conversarComBot(
                request.getMensagem(),
                usuario.getNome(),
                request.getContexto()
        );

        // Salva a resposta do bot
        notificacaoService.salvarMensagemChat(usuario.getId(), resposta);

        MensagemIADTO dto = new MensagemIADTO();
        dto.setConteudo(resposta);
        dto.setTipo(MensagemIA.TipoMensagem.CHAT);
        dto.setLida(false);

        return dto;
    }

    @Transactional(readOnly = true)
    public Page<MensagemIADTO> listarPorUsuario(String emailUsuario, Pageable pageable) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        return mensagemIARepository.findByUsuarioOrderByCriadoEmDesc(usuario, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<MensagemIADTO> listarNaoLidas(String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        return mensagemIARepository.findByUsuarioAndLidaFalseOrderByCriadoEmDesc(usuario)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MensagemIADTO> listarUltimas(String emailUsuario, int quantidade) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        List<MensagemIADTO> mensagens = mensagemIARepository.findUltimasMensagens(usuario, PageRequest.of(0, quantidade))
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        // Inverte a ordem para mostrar do mais antigo ao mais recente
        java.util.Collections.reverse(mensagens);
        return mensagens;
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        return mensagemIARepository.countMensagensNaoLidas(usuario);
    }

    @Transactional
    public void marcarComoLida(Long mensagemId, String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        MensagemIA mensagem = mensagemIARepository.findById(mensagemId)
                .orElseThrow(() -> new br.com.fiap.mindcare.exception.ResourceNotFoundException(
                        "Mensagem não encontrada"));

        if (!mensagem.getUsuario().getId().equals(usuario.getId())) {
            throw new br.com.fiap.mindcare.exception.BusinessException(
                    "Você não tem permissão para marcar esta mensagem");
        }

        mensagem.setLida(true);
        mensagemIARepository.save(mensagem);
    }

    @Transactional
    public void marcarTodasComoLidas(String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        List<MensagemIA> mensagens = mensagemIARepository
                .findByUsuarioAndLidaFalseOrderByCriadoEmDesc(usuario);

        mensagens.forEach(m -> m.setLida(true));
        mensagemIARepository.saveAll(mensagens);
        
        log.info("Todas as mensagens marcadas como lidas para usuário: {}", usuario.getId());
    }

    private MensagemIADTO toDTO(MensagemIA mensagem) {
        MensagemIADTO dto = new MensagemIADTO();
        dto.setId(mensagem.getId());
        dto.setConteudo(mensagem.getConteudo());
        dto.setTipo(mensagem.getTipo());
        dto.setContexto(mensagem.getContexto());
        dto.setLida(mensagem.getLida());
        dto.setCriadoEm(mensagem.getCriadoEm());
        return dto;
    }
}
