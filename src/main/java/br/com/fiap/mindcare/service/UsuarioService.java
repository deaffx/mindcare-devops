package br.com.fiap.mindcare.service;

import br.com.fiap.mindcare.exception.BusinessException;
import br.com.fiap.mindcare.exception.ResourceNotFoundException;
import br.com.fiap.mindcare.model.Usuario;
import br.com.fiap.mindcare.model.dto.UsuarioDTO;
import br.com.fiap.mindcare.model.dto.UsuarioRegistroDTO;
import br.com.fiap.mindcare.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioDTO registrar(UsuarioRegistroDTO registroDTO) {
        log.info("Registrando novo usuário: {}", registroDTO.getEmail());
        
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new BusinessException("Email já cadastrado no sistema");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(registroDTO.getNome());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(registroDTO.getSenha()));
        usuario.setCargo(registroDTO.getCargo());
        usuario.setPreferenciaIdioma(registroDTO.getPreferenciaIdioma());
        usuario.setRole(Usuario.Role.USER);
        usuario.setAtivo(true);
        usuario.setReceberNotificacoes(true);

        usuario = usuarioRepository.save(usuario);
        log.info("Usuário registrado com sucesso: {}", usuario.getId());

        return toDTO(usuario);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "usuarios", key = "#id")
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return toDTO(usuario);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "usuarios", key = "#email")
    public Usuario buscarEntidadePorEmail(String email) {
        return usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioDTO> listarTodos(Pageable pageable) {
        return usuarioRepository.findByAtivoTrue(pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioDTO> buscar(String busca, Pageable pageable) {
        return usuarioRepository.buscarUsuarios(busca, pageable)
                .map(this::toDTO);
    }

    @Transactional
    @CacheEvict(value = "usuarios", allEntries = true)
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        usuario.setNome(dto.getNome());
        usuario.setCargo(dto.getCargo());
        usuario.setReceberNotificacoes(dto.getReceberNotificacoes());
        usuario.setPreferenciaIdioma(dto.getPreferenciaIdioma());

        usuario = usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Transactional
    public void desativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setCargo(usuario.getCargo());
        dto.setReceberNotificacoes(usuario.getReceberNotificacoes());
        dto.setPreferenciaIdioma(usuario.getPreferenciaIdioma());
        dto.setRole(usuario.getRole().name());
        return dto;
    }
}
