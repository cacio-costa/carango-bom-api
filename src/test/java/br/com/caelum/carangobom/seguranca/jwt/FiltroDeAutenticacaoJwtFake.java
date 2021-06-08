package br.com.caelum.carangobom.seguranca.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@Profile("test")
public class FiltroDeAutenticacaoJwtFake extends OncePerRequestFilter implements FiltroDeAutenticacaoJwt {
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.warn("PASSOU PELO FILTRO DE TESTE");
        filterChain.doFilter(request, response);
    }
}
