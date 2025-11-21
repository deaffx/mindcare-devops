package br.com.fiap.mindcare.controller.mvc;

import br.com.fiap.mindcare.model.dto.RegistroHumorDTO;
import br.com.fiap.mindcare.service.AuthService;
import br.com.fiap.mindcare.service.RegistroHumorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/humor")
@RequiredArgsConstructor
public class HumorController {

    private final RegistroHumorService humorService;
    private final AuthService authService;

    @GetMapping
    public String listar(Model model, @PageableDefault(size = 10) Pageable pageable) {
        String email = authService.getEmailUsuarioLogado();
        model.addAttribute("registros", humorService.listarPorUsuario(email, pageable));
        model.addAttribute("mediaSemanal", humorService.calcularMediaSemanal(email));
        return "humor/index";
    }

    @GetMapping("/novo")
    public String novoFormulario(Model model) {
        model.addAttribute("registro", new RegistroHumorDTO());
        return "humor/formulario";
    }

    @PostMapping
    public String registrar(@RequestParam(value = "nivelHumor") Integer nivelHumor,
                           @RequestParam(value = "emocao") String emocao,
                           @RequestParam(value = "descricao", required = false) String descricao,
                           RedirectAttributes redirectAttributes) {
        try {
            String email = authService.getEmailUsuarioLogado();
            humorService.registrarSimples(nivelHumor, emocao, descricao, email);
            
            redirectAttributes.addFlashAttribute("mensagemSucesso", 
                    "Humor registrado com sucesso!");
            
            return "redirect:/humor";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", 
                    "Erro ao registrar humor: " + e.getMessage());
            return "redirect:/humor/novo";
        }
    }

    @GetMapping("/{id}")
    public String detalhar(@PathVariable("id") Long id, Model model) {
        String email = authService.getEmailUsuarioLogado();
        model.addAttribute("registro", humorService.buscarPorId(id, email));
        return "humor/detalhes";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        String email = authService.getEmailUsuarioLogado();
        humorService.deletar(id, email);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Registro deletado com sucesso!");
        return "redirect:/humor";
    }
}
