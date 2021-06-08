package br.com.caelum.carangobom.seguranca;

import br.com.caelum.carangobom.seguranca.dominio.Usuario;
import br.com.caelum.carangobom.seguranca.dominio.UsuarioRepository;
import br.com.caelum.carangobom.seguranca.exception.UsuarioExistenteException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.assertj.core.api.Assertions.*;

class UsuarioServiceTest {

    @Mock
    UsuarioRepository repository;

    @Mock
    PasswordEncoder encoder;

    UsuarioService service;
    private Usuario usuario;
    private String senhaCodificada;

    @BeforeEach
    public void configura() {
        openMocks(this);

        service = new UsuarioService(repository, encoder);
        usuario = new Usuario("Fulano", "123");

        senhaCodificada = "$2a$10$sV44tdKNfTvKUOxaElX9";
        when(encoder.encode(usuario.getPassword())).thenReturn(senhaCodificada);
    }

    @Test
    void deveLancarExcecaoSeNaoEncontrarUsuarioPeloNome() {
        when(repository.findByUsername("inexistente")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.loadUserByUsername("inexistente"))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage("Usuário não encontrado: inexistente");
    }

    @Test
    void naoDeveLancarExcecaoSeEncontrarUsuarioPeloNome() {
        when(repository.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));

        UserDetails details = service.loadUserByUsername(usuario.getUsername());
        assertThat(details)
            .isInstanceOf(Usuario.class)
            .extracting("username")
                .isEqualTo(usuario.getUsername());
    }

    @Test
    void deveSalvarUsuarioSeEleNaoExistir() throws UsuarioExistenteException {
        when(repository.findByUsername(usuario.getUsername())).thenReturn(Optional.empty());

        service.salva(usuario);
        verify(repository).save(usuario);
    }

    @Test
    void deveLancarErroAoCadastrarUsuarioJahExistente() throws UsuarioExistenteException {
        when(repository.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> service.salva(usuario))
            .isInstanceOf(UsuarioExistenteException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void novoUsuarioDeveTerSenhaCodificada() throws UsuarioExistenteException {
        when(repository.findByUsername(usuario.getUsername())).thenReturn(Optional.empty());
        when(repository.save(usuario)).thenReturn(usuario);

        Usuario usuarioSalvo = service.salva(usuario);
        Assertions.assertEquals(senhaCodificada, usuarioSalvo.getPassword());
    }

    @Test
    void novoUsuarioDeveTerPerfilADMIN() throws UsuarioExistenteException {
        when(repository.findByUsername(usuario.getUsername())).thenReturn(Optional.empty());
        when(repository.save(usuario)).thenReturn(usuario);

        Usuario usuarioSalvo = service.salva(usuario);
        Assertions.assertTrue(usuario.getNomesDosPerfis().contains("ADMIN"));
    }

    @Test
    void deveAlterarSenhaDoUsuarioQuandoEleExistir() throws UsuarioExistenteException {
        String novaSenha = "1234";
        when(encoder.encode(usuario.getPassword())).thenReturn(novaSenha);
        when(repository.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));

        service.alteraSenha(usuario);
        Assertions.assertEquals(novaSenha, usuario.getPassword());
    }

    @Test
    void deveRemoverUsuarioQuandoEleExistir() throws UsuarioExistenteException {
        when(repository.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));

        service.removeUsuario(usuario.getUsername());
        verify(repository).delete(usuario);
    }

}