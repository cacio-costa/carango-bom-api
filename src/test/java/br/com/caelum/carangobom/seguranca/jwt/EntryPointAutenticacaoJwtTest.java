package br.com.caelum.carangobom.seguranca.jwt;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class EntryPointAutenticacaoJwtTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    AuthenticationException exception = new BadCredentialsException("Erro simulado no teste");

    @Test
    void deveRetornar401AoFalharAutenticacao() throws IOException, ServletException {
        openMocks(this);

        EntryPointAutenticacaoJwt entryPoint = new EntryPointAutenticacaoJwt();
        entryPoint.commence(request, response, exception);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Você não está autorizado a acessar este recurso.");
    }

}