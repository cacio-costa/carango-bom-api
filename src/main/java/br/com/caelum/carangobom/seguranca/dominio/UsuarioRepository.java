package br.com.caelum.carangobom.seguranca.dominio;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends Repository<Usuario, String> {

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findAllByOrderByUsername();

    Usuario save(Usuario usuario);

    void delete(Usuario usuario);
}
