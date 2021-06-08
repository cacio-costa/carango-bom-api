package br.com.caelum.carangobom.seguranca.dto.input;

import br.com.caelum.carangobom.seguranca.dominio.Usuario;
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CredenciaisDto {

    @NotBlank @Size(min = 5)
    private String username;

    @NotBlank @Size(min = 6)
    private String password;

    public UsernamePasswordAuthenticationToken toAutenticacao() {
        return new UsernamePasswordAuthenticationToken(username, password);
    }

    public Usuario toUsuario() {
        return new Usuario(username, password);
    }

}
