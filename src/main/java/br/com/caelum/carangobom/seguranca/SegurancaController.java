package br.com.caelum.carangobom.seguranca;

import br.com.caelum.carangobom.seguranca.dto.AutenticacaoDto;
import br.com.caelum.carangobom.seguranca.dto.CredenciaisDto;
import br.com.caelum.carangobom.seguranca.dto.UsuarioDto;
import br.com.caelum.carangobom.seguranca.exception.UsuarioExistenteException;
import br.com.caelum.carangobom.seguranca.jwt.GerenciadorDeTokenJwt;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@Transactional
@AllArgsConstructor
public class SegurancaController {

    private final UsuarioService usuarioService;
    private final GerenciadorDeTokenJwt gerenciadorDeToken;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    @GetMapping("/usuarios")
    public List<UsuarioDto> lista() {
        return usuarioService.listaTodos()
                .stream()
                .map(UsuarioDto::new)
                .collect(toList());
    }

    @GetMapping("/usuarios/{username}")
    public ResponseEntity<UsuarioDto> peloUsername(@PathVariable String username) {
        return usuarioService.recuperaPeloUsername(username)
                .map(UsuarioDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioDto> cria(@Valid @RequestBody CredenciaisDto credenciais, UriComponentsBuilder uriBuilder) throws UsuarioExistenteException {
        Usuario novoUsuario = usuarioService.salva(credenciais.toUsuario());

        URI uri = uriBuilder.path("/usuarios/{username}").buildAndExpand(novoUsuario.getUsername()).toUri();
        return ResponseEntity.created(uri).body(new UsuarioDto(novoUsuario));
    }

    @PutMapping("/usuarios/{username}/senha")
    public ResponseEntity<UsuarioDto> alteraSenha(@Valid @RequestBody CredenciaisDto credenciais) {
        try {
            Usuario usuario = usuarioService.alteraSenha(credenciais.toUsuario());
            return ResponseEntity.ok(new UsuarioDto(usuario));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/usuarios/{username}")
    public ResponseEntity<UsuarioDto> removeUsuario(@PathVariable String username) {
        try {
            Usuario usuario = usuarioService.removeUsuario(username);
            return ResponseEntity.ok(new UsuarioDto(usuario));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/autenticacao")
    public ResponseEntity<AutenticacaoDto> autentica(@RequestBody CredenciaisDto credenciais) {
        Authentication credentials = credenciais.toAutenticacao();

        try {
            Authentication autenticacao = authenticationManager.authenticate(credentials);

            Usuario usuario = (Usuario) autenticacao.getPrincipal();
            String tokenJwt = gerenciadorDeToken.generaToken(usuario);

            return ResponseEntity.ok(new AutenticacaoDto(new UsuarioDto(usuario), tokenJwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
