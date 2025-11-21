package br.com.fiap.mindcare.controller.mvc;

import br.com.fiap.mindcare.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UsuarioService usuarioService;
    private final RegistroHumorService humorService;
    private final MetaService metaService;
    private final MensagemService mensagemService;
    private final IAService iaService;

    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        var usuarioEntity = usuarioService.buscarEntidadePorEmail(email);
        var usuario = usuarioService.buscarPorId(usuarioEntity.getId());
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("ultimosHumores", humorService.buscarUltimos(email, 7));
        model.addAttribute("metasAtivas", metaService.listarAtivas(email));
        model.addAttribute("mensagensNaoLidas", mensagemService.listarNaoLidas(email));
        
        try {
            model.addAttribute("mediaHumorSemanal", humorService.calcularMediaSemanal(email));
        } catch (Exception e) {
            model.addAttribute("mediaHumorSemanal", 0.0);
        }
        
        try {
            model.addAttribute("totalMetasConcluidas", metaService.contarMetasConcluidas(email));
        } catch (Exception e) {
            model.addAttribute("totalMetasConcluidas", 0);
        }
        
        // Frase motivacional do dia
        String fraseDoDia = iaService.gerarResposta("Gere uma frase motivacional curta para o dia de hoje. Não repita frases de dias anteriores.");
        model.addAttribute("fraseDoDia", fraseDoDia);
        return "dashboard/index";
    }
}
