package br.com.fiap.mindcare.controller.mvc;

import br.com.fiap.mindcare.model.dto.ChatRequestDTO;
import br.com.fiap.mindcare.service.AuthService;
import br.com.fiap.mindcare.service.MensagemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final MensagemService mensagemService;
    private final AuthService authService;

    @GetMapping
    public String chat(Model model) {
        String email = authService.getEmailUsuarioLogado();
        var mensagens = mensagemService.listarUltimas(email, 50);

        model.addAttribute("mensagens", mensagens);
        model.addAttribute("request", new ChatRequestDTO());
        return "chat/index";
    }

    @PostMapping
    public String conversar(@RequestParam("mensagem") String mensagem, Model model) {
        String email = authService.getEmailUsuarioLogado();

        // Cria o request DTO
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMensagem(mensagem);
        request.setContexto("Conversa geral");
        
        try {
            mensagemService.conversar(request, email);
        } catch (Exception e) {
            log.error("Erro ao processar mensagem: {}", e.getMessage());
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao processar mensagem. Tente novamente.");
        }
        
        return "redirect:/chat";
    }

    @GetMapping("/mensagens")
    public String listarMensagens(Model model, @PageableDefault(size = 20) Pageable pageable) {
        String email = authService.getEmailUsuarioLogado();
        model.addAttribute("mensagens", mensagemService.listarPorUsuario(email, pageable));
        return "chat/mensagens";
    }

    @PostMapping("/marcar-todas-lidas")
    @ResponseBody
    public String marcarTodasLidas() {
        String email = authService.getEmailUsuarioLogado();
        mensagemService.marcarTodasComoLidas(email);
        return "ok";
    }
}
