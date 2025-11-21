package br.com.fiap.mindcare.interceptor;

import br.com.fiap.mindcare.model.Usuario;
import br.com.fiap.mindcare.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Slf4j
public class LocaleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            
            WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
            if (context != null) {
                UsuarioService usuarioService = context.getBean(UsuarioService.class);
                LocaleResolver localeResolver = context.getBean(LocaleResolver.class);
                
                try {
                    Usuario usuario = usuarioService.buscarEntidadePorEmail(email);
                    String preferenciaIdioma = usuario.getPreferenciaIdioma();
                    
                    Locale locale;
                    if ("en-US".equals(preferenciaIdioma) || "en_US".equals(preferenciaIdioma)) {
                        locale = new Locale("en", "US");
                    } else {
                        locale = new Locale("pt", "BR");
                    }
                    
                    localeResolver.setLocale(request, response, locale);
                } catch (Exception e) {
                    log.error("Erro ao configurar idioma do usuário", e);
                }
            }
        }
        
        return true;
    }
}
