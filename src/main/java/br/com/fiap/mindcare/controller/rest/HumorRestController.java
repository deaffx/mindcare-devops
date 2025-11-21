package br.com.fiap.mindcare.controller.rest;

import br.com.fiap.mindcare.model.dto.RegistroHumorDTO;
import br.com.fiap.mindcare.service.AuthService;
import br.com.fiap.mindcare.service.RegistroHumorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/humor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HumorRestController {

    private final RegistroHumorService humorService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<RegistroHumorDTO> registrar(@Valid @RequestBody RegistroHumorDTO dto) {
        String email = authService.getEmailUsuarioLogado();
        RegistroHumorDTO resultado = humorService.registrar(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @GetMapping
    public ResponseEntity<Page<RegistroHumorDTO>> listar(
            @PageableDefault(size = 10, sort = "data", direction = Sort.Direction.DESC) Pageable pageable) {
        String email = authService.getEmailUsuarioLogado();
        Page<RegistroHumorDTO> registros = humorService.listarPorUsuario(email, pageable);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroHumorDTO> buscarPorId(@PathVariable Long id) {
        String email = authService.getEmailUsuarioLogado();
        RegistroHumorDTO registro = humorService.buscarPorId(id, email);
        return ResponseEntity.ok(registro);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<RegistroHumorDTO>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        String email = authService.getEmailUsuarioLogado();
        List<RegistroHumorDTO> registros = humorService.buscarPorPeriodo(email, inicio, fim);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/ultimos")
    public ResponseEntity<List<RegistroHumorDTO>> buscarUltimos(
            @RequestParam(defaultValue = "7") int quantidade) {
        String email = authService.getEmailUsuarioLogado();
        List<RegistroHumorDTO> registros = humorService.buscarUltimos(email, quantidade);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/media-semanal")
    public ResponseEntity<Double> calcularMediaSemanal() {
        String email = authService.getEmailUsuarioLogado();
        Double media = humorService.calcularMediaSemanal(email);
        return ResponseEntity.ok(media);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        String email = authService.getEmailUsuarioLogado();
        humorService.deletar(id, email);
        return ResponseEntity.noContent().build();
    }
}
