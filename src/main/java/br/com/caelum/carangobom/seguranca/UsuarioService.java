package br.com.caelum.carangobom.seguranca;

import br.com.caelum.carangobom.seguranca.dominio.Perfil;
import br.com.caelum.carangobom.seguranca.dominio.Usuario;
import br.com.caelum.carangobom.seguranca.dominio.UsuarioRepository;
import br.com.caelum.carangobom.seguranca.exception.UsuarioExistenteException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    public List<Usuario> listaTodos() {
        return usuarioRepository.findAllByOrderByUsername();
    }

    public Optional<Usuario> recuperaPeloUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Usuario salva(Usuario usuario) throws UsuarioExistenteException {
        Optional<Usuario> usuarioJahCadastrado = usuarioRepository.findByUsername(usuario.getUsername());

        if (usuarioJahCadastrado.isPresent()) {
            throw new UsuarioExistenteException("Usuário " + usuario.getUsername() + " já existe.");
        } else {
            String senhaCodificada = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(senhaCodificada);
            usuario.adicionaPerfil(Perfil.PERFIS.ADMIN);

            return usuarioRepository.save(usuario);
        }

    }

    public Usuario alteraSenha(Usuario usuario) {
        Usuario usuarioCadastrado = recuperaPeloUsername(usuario.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário inexistente: " + usuario.getUsername()));

        String senhaCodificada = passwordEncoder.encode(usuario.getPassword());
        usuarioCadastrado.setPassword(senhaCodificada);

        return usuarioCadastrado;
    }

    public Usuario removeUsuario(String username) {
        Usuario usuario = recuperaPeloUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário inexistente: " + username));

        usuarioRepository.delete(usuario);
        return usuario;
    }
}
