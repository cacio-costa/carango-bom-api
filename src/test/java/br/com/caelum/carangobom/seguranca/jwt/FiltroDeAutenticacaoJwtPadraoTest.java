package br.com.caelum.carangobom.seguranca.jwt;

import br.com.caelum.carangobom.seguranca.dominio.Perfil;
import br.com.caelum.carangobom.seguranca.dominio.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class FiltroDeAutenticacaoJwtPadraoTest {

    private final String tokenJwtValido = "TOKEN_JWT_VALIDO";
    @Mock
    GerenciadorDeTokenJwt gerenciadorDeTokenJwt;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain chain;

    Usuario usuario = new Usuario("fulano", "senha", Set.of(new Perfil("ADMIN")));

    @BeforeEach
    void inicializa() {
        openMocks(this);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenJwtValido);

        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void deveRegistrarAutenticacaoNoContextoDoSpringSeTokenForValido() throws ServletException, IOException {
        when(gerenciadorDeTokenJwt.isValid(tokenJwtValido)).thenReturn(true);
        when(gerenciadorDeTokenJwt.getUserFromToken(tokenJwtValido)).thenReturn(usuario);

        FiltroDeAutenticacaoJwtPadrao filtro = new FiltroDeAutenticacaoJwtPadrao(gerenciadorDeTokenJwt);
        filtro.doFilterInternal(request, response, chain);

        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(autenticacao);
        assertEquals(Usuario.class, autenticacao.getPrincipal().getClass());
        verify(gerenciadorDeTokenJwt).getUserFromToken(tokenJwtValido);
    }

    @Test
    void naoDeveAutenticarSeTokenForInvalido() throws ServletException, IOException {
        when(gerenciadorDeTokenJwt.isValid(tokenJwtValido)).thenReturn(false);

        FiltroDeAutenticacaoJwtPadrao filtro = new FiltroDeAutenticacaoJwtPadrao(gerenciadorDeTokenJwt);
        filtro.doFilterInternal(request, response, chain);

        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertNull(autenticacao);

        verify(gerenciadorDeTokenJwt, never()).getUserFromToken(anyString());
    }

}