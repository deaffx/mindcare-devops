package br.com.fiap.mindcare.controller.rest;

import br.com.fiap.mindcare.model.dto.ChatRequestDTO;
import br.com.fiap.mindcare.model.dto.MensagemIADTO;
import br.com.fiap.mindcare.service.AuthService;
import br.com.fiap.mindcare.service.MensagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensagens")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MensagemRestController {

    private final MensagemService mensagemService;
    private final AuthService authService;

    @PostMapping("/chat")
    public ResponseEntity<MensagemIADTO> conversar(@Valid @RequestBody ChatRequestDTO request) {
        String email = authService.getEmailUsuarioLogado();
        MensagemIADTO resposta = mensagemService.conversar(request, email);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping
    public ResponseEntity<Page<MensagemIADTO>> listar(
            @PageableDefault(size = 20, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        String email = authService.getEmailUsuarioLogado();
        Page<MensagemIADTO> mensagens = mensagemService.listarPorUsuario(email, pageable);
        return ResponseEntity.ok(mensagens);
    }

    @GetMapping("/nao-lidas")
    public ResponseEntity<List<MensagemIADTO>> listarNaoLidas() {
        String email = authService.getEmailUsuarioLogado();
        List<MensagemIADTO> mensagens = mensagemService.listarNaoLidas(email);
        return ResponseEntity.ok(mensagens);
    }

    @GetMapping("/ultimas")
    public ResponseEntity<List<MensagemIADTO>> listarUltimas(
            @RequestParam(defaultValue = "10") int quantidade) {
        String email = authService.getEmailUsuarioLogado();
        List<MensagemIADTO> mensagens = mensagemService.listarUltimas(email, quantidade);
        return ResponseEntity.ok(mensagens);
    }

    @GetMapping("/contar-nao-lidas")
    public ResponseEntity<Long> contarNaoLidas() {
        String email = authService.getEmailUsuarioLogado();
        long count = mensagemService.contarNaoLidas(email);
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{id}/marcar-lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id) {
        String email = authService.getEmailUsuarioLogado();
        mensagemService.marcarComoLida(id, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/marcar-todas-lidas")
    public ResponseEntity<Void> marcarTodasComoLidas() {
        String email = authService.getEmailUsuarioLogado();
        mensagemService.marcarTodasComoLidas(email);
        return ResponseEntity.noContent().build();
    }
}
