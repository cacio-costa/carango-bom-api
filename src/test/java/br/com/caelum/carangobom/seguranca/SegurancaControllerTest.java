package br.com.caelum.carangobom.seguranca;

import br.com.caelum.carangobom.seguranca.dominio.Perfil;
import br.com.caelum.carangobom.seguranca.dominio.Usuario;
import br.com.caelum.carangobom.seguranca.dto.input.CredenciaisDto;
import br.com.caelum.carangobom.seguranca.dto.output.AutenticacaoDto;
import br.com.caelum.carangobom.seguranca.dto.output.UsuarioDto;
import br.com.caelum.carangobom.seguranca.exception.UsuarioExistenteException;
import br.com.caelum.carangobom.seguranca.jwt.GerenciadorDeTokenJwt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class SegurancaControllerTest {

    @Mock UsuarioService usuarioService;
    @Mock GerenciadorDeTokenJwt gerenciadorDeToken;
    @Mock AuthenticationManager authenticationManager;

    SegurancaController controller;

    Usuario fulano = new Usuario("fulano", "123", Set.of(new Perfil("ADMIN")));
    Usuario beltrano = new Usuario("beltrano", "321");

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:8080");

    @BeforeEach
    void inicializa() {
        openMocks(this);
        controller = new SegurancaController(usuarioService, gerenciadorDeToken, authenticationManager);
    }

    @Test
    void deveRetornarUsuarioComUsernameExistente() {
        when(usuarioService.recuperaPeloUsername(fulano.getUsername())).thenReturn(Optional.of(fulano));

        ResponseEntity<UsuarioDto> resposta = controller.peloUsername(fulano.getUsername());
        assertEquals(HttpStatus.OK, resposta.getStatusCode());

        UsuarioDto dto = resposta.getBody();
        assertEquals(fulano.getUsername(), dto.getUsername());
        assertEquals(Set.of("ADMIN"), dto.getPerfis());
    }

    @Test
    void deveRetornarNotFoundSeNaoExistirUsername() {
        when(usuarioService.recuperaPeloUsername(fulano.getUsername())).thenReturn(Optional.empty());

        ResponseEntity<UsuarioDto> resposta = controller.peloUsername(fulano.getUsername());
        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void deveRetornarCreatedAoCriarNovoUsuario() throws UsuarioExistenteException {
        CredenciaisDto credenciaisDto = new CredenciaisDto("beltrano", "123");
        when(usuarioService.salva(any(Usuario.class))).thenReturn(beltrano);

        ResponseEntity<UsuarioDto> resposta = controller.cria(credenciaisDto, uriBuilder);
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals("http://localhost:8080/usuarios/beltrano", resposta.getHeaders().getLocation().toString());

        UsuarioDto usuarioCriado = resposta.getBody();
        assertEquals("beltrano", usuarioCriado.getUsername());
    }

    @Test
    void deveRetornarOkAoAlterarSenhaDeUsuarioExistente() throws UsuarioExistenteException {
        CredenciaisDto credenciaisDto = new CredenciaisDto("beltrano", "NOVA_SENHA");
        when(usuarioService.alteraSenha(any(Usuario.class))).thenReturn(beltrano);

        ResponseEntity<UsuarioDto> resposta = controller.alteraSenha(credenciaisDto);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
    }

    @Test
    void deveRetornarNotFoundQuandoAlterarSenhaDeUsuarioInexistente() throws UsuarioExistenteException {
        CredenciaisDto credenciaisDto = new CredenciaisDto("beltrano", "NOVA_SENHA");
        when(usuarioService.alteraSenha(any(Usuario.class))).thenThrow(UsernameNotFoundException.class);

        ResponseEntity<UsuarioDto> resposta = controller.alteraSenha(credenciaisDto);
        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void deveRetornarOkAoRemoverUsuarioExistente() throws UsuarioExistenteException {
        when(usuarioService.removeUsuario(fulano.getUsername())).thenReturn(fulano);

        ResponseEntity<UsuarioDto> resposta = controller.removeUsuario(fulano.getUsername());
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
    }

    @Test
    void deveRetornarNotFoundQuandoRemoverUsuarioInexistente() throws UsuarioExistenteException {
        when(usuarioService.removeUsuario(fulano.getUsername())).thenThrow(UsernameNotFoundException.class);

        ResponseEntity<UsuarioDto> resposta = controller.removeUsuario(fulano.getUsername());
        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void deveGerarTokenQuandoAutenticarCredenciaisCorretas() throws UsuarioExistenteException {
        CredenciaisDto credenciaisDto = new CredenciaisDto("beltrano", "senha123");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(new UsernamePasswordAuthenticationToken(beltrano, "senha123"));
        when(gerenciadorDeToken.generaToken(any(Usuario.class))).thenReturn("TOKEN_SIMULADO");

        ResponseEntity<AutenticacaoDto> resposta = controller.autentica(credenciaisDto);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());

        AutenticacaoDto autenticacao = resposta.getBody();
        assertEquals("beltrano", autenticacao.getUsuario().getUsername());
        assertEquals("TOKEN_SIMULADO", autenticacao.getToken());
    }

    @Test
    void deveRetornarBadRequestAoFalharAutenticacao() throws UsuarioExistenteException {
        CredenciaisDto credenciaisDto = new CredenciaisDto("beltrano", "senha123");
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException("MSG"));

        ResponseEntity<AutenticacaoDto> resposta = controller.autentica(credenciaisDto);
        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
    }

}