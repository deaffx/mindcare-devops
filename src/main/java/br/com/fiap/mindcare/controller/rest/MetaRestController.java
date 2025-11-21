package br.com.fiap.mindcare.controller.rest;

import br.com.fiap.mindcare.model.Meta;
import br.com.fiap.mindcare.model.dto.MetaDTO;
import br.com.fiap.mindcare.service.AuthService;
import br.com.fiap.mindcare.service.MetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MetaRestController {

    private final MetaService metaService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<MetaDTO> criar(@Valid @RequestBody MetaDTO dto) {
        String email = authService.getEmailUsuarioLogado();
        MetaDTO resultado = metaService.criar(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @GetMapping
    public ResponseEntity<Page<MetaDTO>> listar(
            @PageableDefault(size = 10, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        String email = authService.getEmailUsuarioLogado();
        Page<MetaDTO> metas = metaService.listarPorUsuario(email, pageable);
        return ResponseEntity.ok(metas);
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<MetaDTO>> listarAtivas() {
        String email = authService.getEmailUsuarioLogado();
        List<MetaDTO> metas = metaService.listarAtivas(email);
        return ResponseEntity.ok(metas);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MetaDTO>> listarPorStatus(@PathVariable Meta.Status status) {
        String email = authService.getEmailUsuarioLogado();
        List<MetaDTO> metas = metaService.listarPorStatus(email, status);
        return ResponseEntity.ok(metas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaDTO> buscarPorId(@PathVariable Long id) {
        String email = authService.getEmailUsuarioLogado();
        MetaDTO meta = metaService.buscarPorId(id, email);
        return ResponseEntity.ok(meta);
    }

    @PostMapping("/{id}/progresso")
    public ResponseEntity<MetaDTO> registrarProgresso(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String email = authService.getEmailUsuarioLogado();
        String observacao = body != null ? body.get("observacao") : null;
        MetaDTO meta = metaService.registrarProgresso(id, email, observacao);
        return ResponseEntity.ok(meta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody MetaDTO dto) {
        String email = authService.getEmailUsuarioLogado();
        MetaDTO meta = metaService.atualizar(id, dto, email);
        return ResponseEntity.ok(meta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        String email = authService.getEmailUsuarioLogado();
        metaService.deletar(id, email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estatisticas/concluidas")
    public ResponseEntity<Long> contarConcluidas() {
        String email = authService.getEmailUsuarioLogado();
        long total = metaService.contarMetasConcluidas(email);
        return ResponseEntity.ok(total);
    }
}
