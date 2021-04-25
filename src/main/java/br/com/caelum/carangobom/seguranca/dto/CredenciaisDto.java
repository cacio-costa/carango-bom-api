package br.com.caelum.carangobom.seguranca.dto;

import br.com.caelum.carangobom.seguranca.Usuario;
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

    public UsernamePasswordAuthenticationToken getAutenticacao() {
        return new UsernamePasswordAuthenticationToken(username, password);
    }

    public Usuario getUsuario() {
        return new Usuario(username, password);
    }

}
