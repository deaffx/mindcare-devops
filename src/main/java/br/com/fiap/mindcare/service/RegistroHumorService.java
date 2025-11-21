package br.com.fiap.mindcare.service;

import br.com.fiap.mindcare.exception.ResourceNotFoundException;
import br.com.fiap.mindcare.model.RegistroHumor;
import br.com.fiap.mindcare.model.Usuario;
import br.com.fiap.mindcare.model.dto.RegistroHumorDTO;
import br.com.fiap.mindcare.repository.RegistroHumorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistroHumorService {

    private final RegistroHumorRepository registroHumorRepository;
    private final UsuarioService usuarioService;

    @Transactional
    @CacheEvict(value = {"humor", "estatisticas"}, allEntries = true)
    public void registrarSimples(Integer nivelHumor, String emocao, String descricao, String emailUsuario) {
        log.info("Registrando humor para usuário: {}", emailUsuario);

        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);

        RegistroHumor registro = new RegistroHumor();
        registro.setNivelHumor(nivelHumor);
        registro.setEmocao(emocao);
        registro.setDescricao(descricao);
        registro.setData(LocalDate.now());
        registro.setUsuario(usuario);

        registroHumorRepository.save(registro);
        log.info("Humor registrado com sucesso");
    }

    @Transactional
    @CacheEvict(value = "humor", allEntries = true)
    public RegistroHumorDTO registrar(RegistroHumorDTO dto, String emailUsuario) {
        log.info("Registrando humor para usuário: {}", emailUsuario);

        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);

        // Verifica se já existe registro para hoje
        LocalDate hoje = LocalDate.now();
        List<RegistroHumor> registrosHoje = registroHumorRepository.findByUsuarioAndDataOrderByCriadoEmDesc(usuario, hoje);
        if (!registrosHoje.isEmpty()) {
            throw new br.com.fiap.mindcare.exception.BusinessException(
                "Já existe um registro de humor para hoje");
        }

        RegistroHumor registro = new RegistroHumor();
        registro.setNivelHumor(dto.getNivelHumor());
        registro.setEmocao(dto.getEmocao());
        registro.setDescricao(dto.getDescricao());
        registro.setData(hoje);
        registro.setUsuario(usuario);

        registro = registroHumorRepository.save(registro);

        RegistroHumorDTO result = toDTO(registro);
        log.info("Humor registrado com sucesso para usuário: {}", usuario.getId());
        return result;
    }

    @Transactional(readOnly = true)
    public Page<RegistroHumorDTO> listarPorUsuario(String emailUsuario, Pageable pageable) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        return registroHumorRepository.findByUsuarioOrderByDataDesc(usuario, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<RegistroHumorDTO> buscarPorPeriodo(String emailUsuario, LocalDate inicio, LocalDate fim) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        return registroHumorRepository
                .findByUsuarioAndDataBetweenOrderByDataDesc(usuario, inicio, fim)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "humor", key = "'ultimos-' + #emailUsuario + '-' + #quantidade")
    public List<RegistroHumorDTO> buscarUltimos(String emailUsuario, int quantidade) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        List<RegistroHumorDTO> lista = registroHumorRepository
                .findUltimosRegistros(usuario, PageRequest.of(0, quantidade))
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        // Se hoje tem registro, substitui o primeiro por o mais recente de hoje
        LocalDate hoje = LocalDate.now();
        List<RegistroHumor> registrosHoje = registroHumorRepository.findByUsuarioAndDataOrderByCriadoEmDesc(usuario, hoje);
        if (!registrosHoje.isEmpty()) {
            RegistroHumorDTO maisRecenteHoje = toDTO(registrosHoje.get(0));
            // Remove qualquer registro de hoje da lista
            lista.removeIf(r -> hoje.equals(r.getData()));
            // Adiciona o mais recente de hoje no início
            lista.add(0, maisRecenteHoje);
        }
        return lista;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "estatisticas", key = "'media-semanal-' + #emailUsuario")
    public Double calcularMediaSemanal(String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        LocalDate hoje = LocalDate.now();
        LocalDate seteDiasAtras = hoje.minusDays(7);

        Double media = registroHumorRepository.calcularMediaHumor(usuario, seteDiasAtras, hoje);
        return media != null ? media : 0.0;
    }

    @Transactional(readOnly = true)
    public RegistroHumorDTO buscarPorId(Long id, String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        RegistroHumor registro = registroHumorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro não encontrado"));

        if (!registro.getUsuario().getId().equals(usuario.getId())) {
            throw new br.com.fiap.mindcare.exception.BusinessException(
                    "Você não tem permissão para acessar este registro");
        }

        return toDTO(registro);
    }

    @Transactional
    @CacheEvict(value = {"humor", "estatisticas"}, allEntries = true)
    public void deletar(Long id, String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        RegistroHumor registro = registroHumorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro não encontrado"));

        if (!registro.getUsuario().getId().equals(usuario.getId())) {
            throw new br.com.fiap.mindcare.exception.BusinessException(
                    "Você não tem permissão para deletar este registro");
        }

        registroHumorRepository.delete(registro);
        log.info("Registro de humor deletado: {}", id);
    }

    private RegistroHumorDTO toDTO(RegistroHumor registro) {
        RegistroHumorDTO dto = new RegistroHumorDTO();
        dto.setId(registro.getId());
        dto.setNivelHumor(registro.getNivelHumor());
        dto.setEmocao(registro.getEmocao());
        dto.setDescricao(registro.getDescricao());
        dto.setData(registro.getData());
        dto.setCriadoEm(registro.getCriadoEm());
        return dto;
    }
}
