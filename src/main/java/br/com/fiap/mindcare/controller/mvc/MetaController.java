package br.com.fiap.mindcare.controller.mvc;

import br.com.fiap.mindcare.model.dto.MetaDTO;
import br.com.fiap.mindcare.service.AuthService;
import br.com.fiap.mindcare.service.MetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/metas")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;
    private final AuthService authService;

    @GetMapping
    public String listar(Model model, @PageableDefault(size = 10) Pageable pageable) {
        String email = authService.getEmailUsuarioLogado();
        model.addAttribute("metas", metaService.listarPorUsuario(email, pageable));
        model.addAttribute("metasAtivas", metaService.listarAtivas(email));
        model.addAttribute("totalConcluidas", metaService.contarMetasConcluidas(email));
        return "metas/index";
    }

    @GetMapping("/nova")
    public String novoFormulario(Model model) {
        // Não adiciona meta ao model para nova meta
        return "metas/formulario";
    }

    @PostMapping
    public String criar(@RequestParam(value = "titulo") String titulo,
                       @RequestParam(value = "descricao") String descricao,
                       @RequestParam(value = "categoria") String categoria,
                       @RequestParam(value = "tipo") String tipo,
                       @RequestParam(value = "dataFim", required = false) String dataFim,
                       @RequestParam(value = "diasObjetivo", required = false) Integer diasObjetivo,
                       RedirectAttributes redirectAttributes) {
        try {
            String email = authService.getEmailUsuarioLogado();
            metaService.criarSimples(titulo, descricao, categoria, tipo, dataFim, diasObjetivo, email);
            
            redirectAttributes.addFlashAttribute("mensagemSucesso", 
                    "Meta criada com sucesso!");
            return "redirect:/metas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", 
                    "Erro ao criar meta: " + e.getMessage());
            return "redirect:/metas/nova";
        }
    }

    @GetMapping("/{id}")
    public String detalhar(@PathVariable("id") Long id, Model model) {
        String email = authService.getEmailUsuarioLogado();
        MetaDTO meta = metaService.buscarPorId(id, email);
        model.addAttribute("meta", meta);
        return "metas/detalhes";
    }

    @GetMapping("/{id}/editar")
    public String editarFormulario(@PathVariable("id") Long id, Model model) {
        String email = authService.getEmailUsuarioLogado();
        model.addAttribute("meta", metaService.buscarPorId(id, email));
        return "metas/formulario";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable("id") Long id,
                           @Valid @ModelAttribute("meta") MetaDTO dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "metas/formulario";
        }

        String email = authService.getEmailUsuarioLogado();
        metaService.atualizar(id, dto, email);
        
        redirectAttributes.addFlashAttribute("mensagemSucesso", 
                "Meta atualizada com sucesso!");
        
        return "redirect:/metas/" + id;
    }

    @PostMapping("/{id}/progresso")
    public String registrarProgresso(@PathVariable("id") Long id,
                                    @RequestParam(value = "observacao", required = false) String observacao,
                                    RedirectAttributes redirectAttributes) {
        String email = authService.getEmailUsuarioLogado();
        metaService.registrarProgresso(id, email, observacao);
        
        redirectAttributes.addFlashAttribute("mensagemSucesso", 
                "Progresso registrado com sucesso! Continue assim! 🎯");
        
        return "redirect:/metas/" + id;
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        String email = authService.getEmailUsuarioLogado();
        metaService.deletar(id, email);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Meta deletada com sucesso!");
        return "redirect:/metas";
    }
}
