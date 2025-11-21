package br.com.fiap.mindcare.service;

import br.com.fiap.mindcare.config.JwtTokenProvider;
import br.com.fiap.mindcare.model.dto.AuthResponseDTO;
import br.com.fiap.mindcare.model.dto.LoginDTO;
import br.com.fiap.mindcare.model.dto.UsuarioDTO;
import br.com.fiap.mindcare.model.dto.UsuarioRegistroDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioService usuarioService;
    private final NotificacaoService notificacaoService;

    public AuthResponseDTO login(LoginDTO loginDTO) {
        log.info("Tentativa de login para: {}", loginDTO.getEmail());
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getSenha()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        
        var usuarioEntity = usuarioService.buscarEntidadePorEmail(loginDTO.getEmail());
        UsuarioDTO usuario = usuarioService.buscarPorId(usuarioEntity.getId());

        log.info("Login bem-sucedido para: {}", loginDTO.getEmail());
        return new AuthResponseDTO(token, usuario);
    }

    public AuthResponseDTO registrar(UsuarioRegistroDTO registroDTO) {
        log.info("Registrando novo usuário: {}", registroDTO.getEmail());
        
        UsuarioDTO usuario = usuarioService.registrar(registroDTO);
        
        // Enviar mensagem de boas-vindas
        notificacaoService.enviarBoasVindas(usuario.getId(), usuario.getNome());
        
        // Autenticar automaticamente após registro
        LoginDTO loginDTO = new LoginDTO(registroDTO.getEmail(), registroDTO.getSenha());
        return login(loginDTO);
    }

    public String getEmailUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
