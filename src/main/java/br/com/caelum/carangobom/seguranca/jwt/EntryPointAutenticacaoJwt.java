package br.com.caelum.carangobom.seguranca.jwt;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class EntryPointAutenticacaoJwt implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException exception)
    throws IOException, ServletException {
        log.error("Um acesso não autorizado foi verificado! {}", exception.getMessage());
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Você não está autorizado a acessar este recurso.");
    }

}
