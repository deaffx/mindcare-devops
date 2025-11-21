package br.com.fiap.mindcare.controller.mvc;

import br.com.fiap.mindcare.model.Usuario;
import br.com.fiap.mindcare.service.AuthService;
import br.com.fiap.mindcare.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    @GetMapping
    public String visualizar(Model model) {
        String email = authService.getEmailUsuarioLogado();
        model.addAttribute("usuario", usuarioService.buscarEntidadePorEmail(email));
        return "perfil/index";
    }
    
    @PostMapping("/editar")
    @ResponseBody
    public ResponseEntity<?> editar(@RequestBody Map<String, String> dados) {
        try {
            String email = authService.getEmailUsuarioLogado();
            Usuario usuario = usuarioService.buscarEntidadePorEmail(email);
            
            usuario.setNome(dados.get("nome"));
            usuario.setCargo(dados.get("cargo"));
            usuario.setPreferenciaIdioma(dados.get("preferenciaIdioma"));
            
            usuarioService.atualizar(usuario.getId(), toDTO(usuario));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    private br.com.fiap.mindcare.model.dto.UsuarioDTO toDTO(Usuario usuario) {
        br.com.fiap.mindcare.model.dto.UsuarioDTO dto = new br.com.fiap.mindcare.model.dto.UsuarioDTO();
        dto.setNome(usuario.getNome());
        dto.setCargo(usuario.getCargo());
        dto.setPreferenciaIdioma(usuario.getPreferenciaIdioma());
        dto.setReceberNotificacoes(usuario.getReceberNotificacoes());
        return dto;
    }
}
