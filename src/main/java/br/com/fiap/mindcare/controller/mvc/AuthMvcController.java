package br.com.fiap.mindcare.controller.mvc;

import br.com.fiap.mindcare.model.dto.UsuarioRegistroDTO;
import br.com.fiap.mindcare.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthMvcController {

    private final UsuarioService usuarioService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/perform-register")
    public String register(@Valid @ModelAttribute("usuario") UsuarioRegistroDTO usuarioDTO,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        
        log.info("=== INÍCIO DO REGISTRO ===");
        log.info("Dados recebidos - Nome: {}, Email: {}, Cargo: {}, Idioma: {}", 
                 usuarioDTO.getNome(), usuarioDTO.getEmail(), 
                 usuarioDTO.getCargo(), usuarioDTO.getPreferenciaIdioma());
        
        if (result.hasErrors()) {
            log.error("Erros de validação encontrados:");
            result.getAllErrors().forEach(error -> 
                log.error("  - {}", error.getDefaultMessage())
            );
            model.addAttribute("error", "Por favor, corrija os erros no formulário");
            return "auth/register";
        }

        try {
            log.info("Tentando registrar usuário: {}", usuarioDTO.getEmail());

            var usuarioSalvo = usuarioService.registrar(usuarioDTO);
            
            log.info("Usuário registrado com sucesso! ID: {}", usuarioSalvo.getId());
            
            // Autentica após registro
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuarioDTO.getEmail());
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.info("Usuário autenticado automaticamente");
            
            redirectAttributes.addFlashAttribute("message", "Conta criada com sucesso!");
            return "redirect:/dashboard";
            
        } catch (Exception e) {
            log.error("ERRO ao registrar usuário: {}", e.getMessage(), e);
            model.addAttribute("error", "Erro ao criar conta: " + e.getMessage());
            model.addAttribute("usuario", usuarioDTO);
            return "auth/register";
        }
    }
}
