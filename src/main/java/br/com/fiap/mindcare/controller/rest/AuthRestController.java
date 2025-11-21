package br.com.fiap.mindcare.controller.rest;

import br.com.fiap.mindcare.model.dto.AuthResponseDTO;
import br.com.fiap.mindcare.model.dto.LoginDTO;
import br.com.fiap.mindcare.model.dto.UsuarioRegistroDTO;
import br.com.fiap.mindcare.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        AuthResponseDTO response = authService.login(loginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody UsuarioRegistroDTO registroDTO) {
        AuthResponseDTO response = authService.registrar(registroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<String> getUsuarioLogado() {
        String email = authService.getEmailUsuarioLogado();
        return ResponseEntity.ok(email);
    }
}
