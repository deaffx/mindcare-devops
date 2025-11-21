package br.com.fiap.mindcare.service;

import br.com.fiap.mindcare.exception.BusinessException;
import br.com.fiap.mindcare.exception.ResourceNotFoundException;
import br.com.fiap.mindcare.model.Meta;
import br.com.fiap.mindcare.model.ProgressoMeta;
import br.com.fiap.mindcare.model.Usuario;
import br.com.fiap.mindcare.model.dto.MetaDTO;
import br.com.fiap.mindcare.repository.MetaRepository;
import br.com.fiap.mindcare.repository.ProgressoMetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetaService {

    private final MetaRepository metaRepository;
    private final ProgressoMetaRepository progressoMetaRepository;
    private final UsuarioService usuarioService;

    @Transactional
    @CacheEvict(value = "metas", allEntries = true)
    public void criarSimples(String titulo, String descricao, String categoriaStr, String tipoStr, String dataFimStr, Integer diasObjetivo, String emailUsuario) {

        log.info("Título: {}, Tipo: {}, Categoria: {}, DataFim: {}, DiasObjetivo: {}", titulo, tipoStr, categoriaStr, dataFimStr, diasObjetivo);

        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        Meta.Tipo tipo = Meta.Tipo.valueOf(tipoStr);
        
        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim = null;
        Integer duracaoDias = null;

        Meta meta = new Meta();
        meta.setTitulo(titulo);
        meta.setDescricao(descricao);
        meta.setCategoria(Meta.Categoria.valueOf(categoriaStr));
        meta.setTipo(tipo);
        meta.setDataInicio(dataInicio);
        meta.setStatus(Meta.Status.ATIVA);
        meta.setDiasConsecutivos(0);
        meta.setUsuario(usuario);

        if (tipo == Meta.Tipo.PRAZO) {
            // Meta com prazo: calcular duração e data fim
            dataFim = LocalDate.parse(dataFimStr);
            duracaoDias = (int) java.time.temporal.ChronoUnit.DAYS.between(dataInicio, dataFim);
            meta.setDataFim(dataFim);
            meta.setDuracaoDias(duracaoDias);
            meta.setTotalCheckpoints(0);
        } else {
            // Meta consecutiva: definir dias objetivo
            meta.setDuracaoDias(diasObjetivo);
            meta.setTotalCheckpoints(diasObjetivo);
        }

        log.info("Salvando meta no banco...");
        Meta savedMeta = metaRepository.save(meta);
        log.info("=== META CRIADA COM SUCESSO! ID={} ===", savedMeta.getId());
    }

    @Transactional
    @CacheEvict(value = "metas", allEntries = true)
    public MetaDTO criar(MetaDTO dto, String emailUsuario) {
        log.info("Criando nova meta para usuário: {}", emailUsuario);

        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);

        Meta meta = new Meta();
        meta.setTitulo(dto.getTitulo());
        meta.setDescricao(dto.getDescricao());
        meta.setDuracaoDias(dto.getDuracaoDias());
        meta.setCategoria(dto.getCategoria());
        meta.setTipo(dto.getTipo() != null ? dto.getTipo() : Meta.Tipo.PRAZO);
        meta.setDataInicio(LocalDate.now());
        
        // Para metas CONSECUTIVO, não há data fim
        if (meta.getTipo() == Meta.Tipo.CONSECUTIVO) {
            meta.setDataFim(LocalDate.now().plusYears(10));
        } else {
            meta.setDataFim(LocalDate.now().plusDays(dto.getDuracaoDias()));
        }
        
        meta.setStatus(Meta.Status.ATIVA);
        meta.setDiasConsecutivos(0);
        meta.setTotalCheckpoints(0);
        meta.setUsuario(usuario);

        meta = metaRepository.save(meta);

        log.info("Meta criada com sucesso - ID: {}, Tipo: {}, DataFim: {}, Status: {}", 
                 meta.getId(), meta.getTipo(), meta.getDataFim(), meta.getStatus());
        return toDTO(meta);
    }

    @Transactional(readOnly = true)
    public Page<MetaDTO> listarPorUsuario(String emailUsuario, Pageable pageable) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        return metaRepository.findByUsuarioOrderByCriadoEmDesc(usuario, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "metas", key = "'ativas-' + #emailUsuario")
    public List<MetaDTO> listarAtivas(String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        List<Meta> metas = metaRepository.findMetasAtivas(usuario, LocalDate.now());
        log.info("Listando metas ativas para {}: encontradas {} metas (data limite: {})", 
                 emailUsuario, metas.size(), LocalDate.now());
        return metas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MetaDTO> listarPorStatus(String emailUsuario, Meta.Status status) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        return metaRepository.findByUsuarioAndStatusOrderByCriadoEmDesc(usuario, status)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "metas", key = "#id")
    public MetaDTO buscarPorId(Long id, String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Você não tem permissão para acessar esta meta");
        }

        return toDTO(meta);
    }

    @CacheEvict(value = "metas", allEntries = true)
    @Transactional
    public MetaDTO registrarProgresso(Long metaId, String emailUsuario, String observacao) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        Meta meta = metaRepository.findById(metaId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Você não tem permissão para atualizar esta meta");
        }

        if (meta.getStatus() != Meta.Status.ATIVA) {
            throw new BusinessException("Não é possível registrar progresso em meta não ativa");
        }

        if (!meta.podeRealizarCheckin()) {
            throw new BusinessException("Você já realizou o check-in hoje");
        }

        LocalDate hoje = LocalDate.now();

        // Cria novo progresso
        ProgressoMeta progresso = new ProgressoMeta();
        progresso.setMeta(meta);
        progresso.setData(hoje);
        progresso.setConcluido(true);
        progresso.setObservacao(observacao);
        progressoMetaRepository.save(progresso);

        // Atualiza meta baseado no tipo
        if (meta.getTipo() == Meta.Tipo.CONSECUTIVO) {
            // Para metas consecutivas
            if (meta.checkinPerdido()) {
                // Perdeu a sequência, reinicia
                meta.setDiasConsecutivos(1);
                log.warn("Sequência reiniciada para meta {}: check-in perdido", metaId);
            } else {
                // Continua a sequência
                meta.setDiasConsecutivos(meta.getDiasConsecutivos() + 1);
            }
            
            // Verifica se atingiu o objetivo
            if (meta.getDiasConsecutivos() >= meta.getDuracaoDias()) {
                meta.setStatus(Meta.Status.CONCLUIDA);
                log.info("Meta consecutiva {} concluída! {} dias consecutivos alcançados", metaId, meta.getDiasConsecutivos());
            }
        } else {
            // Para metas com prazo
            meta.setTotalCheckpoints(meta.getTotalCheckpoints() + 1);
            meta.setDiasConsecutivos(meta.getDiasConsecutivos() + 1);
            
            // Verifica se completou a meta
            if (meta.getTotalCheckpoints() >= meta.getDuracaoDias()) {
                meta.setStatus(Meta.Status.CONCLUIDA);
            }
        }

        meta.setUltimoCheckin(hoje);
        meta = metaRepository.save(meta);
        log.info("Check-in registrado para meta {}: {} dias consecutivos", metaId, meta.getDiasConsecutivos());

        return toDTO(meta);
    }

    @Transactional
    public MetaDTO atualizar(Long id, MetaDTO dto, String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Você não tem permissão para atualizar esta meta");
        }

        meta.setTitulo(dto.getTitulo());
        meta.setDescricao(dto.getDescricao());
        
        if (dto.getStatus() != null) {
            meta.setStatus(dto.getStatus());
        }

        meta = metaRepository.save(meta);
        return toDTO(meta);
    }

    @Transactional
    public void deletar(Long id, String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Você não tem permissão para deletar esta meta");
        }

        metaRepository.delete(meta);
        log.info("Meta deletada: {}", id);
    }

    @Transactional(readOnly = true)
    public long contarMetasConcluidas(String emailUsuario) {
        Usuario usuario = usuarioService.buscarEntidadePorEmail(emailUsuario);
        return metaRepository.countMetasConcluidas(usuario);
    }

    private MetaDTO toDTO(Meta meta) {
        MetaDTO dto = new MetaDTO();
        dto.setId(meta.getId());
        dto.setTitulo(meta.getTitulo());
        dto.setDescricao(meta.getDescricao());
        dto.setDuracaoDias(meta.getDuracaoDias());
        dto.setCategoria(meta.getCategoria());
        dto.setTipo(meta.getTipo());
        dto.setDataInicio(meta.getDataInicio());
        dto.setDataFim(meta.getDataFim());
        dto.setUltimoCheckin(meta.getUltimoCheckin());
        dto.setStatus(meta.getStatus());
        dto.setDiasConsecutivos(meta.getDiasConsecutivos());
        dto.setTotalCheckpoints(meta.getTotalCheckpoints());
        dto.setProgresso(meta.calcularProgresso());
        dto.setDiasRestantes(meta.diasRestantes());
        dto.setPodeRealizarCheckin(meta.podeRealizarCheckin());
        dto.setCheckinPerdido(meta.checkinPerdido());
        return dto;
    }
}
