package br.com.fiap.mindcare.controller.mvc;

import br.com.fiap.mindcare.model.dto.UsuarioRegistroDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Email ou senha inválidos");
        }
        if (logout != null) {
            model.addAttribute("message", "Logout realizado com sucesso");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("usuario", new UsuarioRegistroDTO());
        return "auth/register";
    }
}
