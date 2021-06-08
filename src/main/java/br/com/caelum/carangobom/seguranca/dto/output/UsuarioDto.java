package br.com.caelum.carangobom.seguranca.dto.output;

import br.com.caelum.carangobom.seguranca.dominio.Perfil;
import br.com.caelum.carangobom.seguranca.dominio.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Getter
@AllArgsConstructor
public class UsuarioDto {

    private String username;
    private Set<String> perfis = new HashSet<>();

    public UsuarioDto(Usuario usuario) {
        username = usuario.getUsername();
        perfis = usuario.getPerfis()
                .stream()
                .map(Perfil::getNome)
                .collect(toSet());
    }

}
