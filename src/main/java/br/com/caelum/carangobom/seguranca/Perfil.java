package br.com.caelum.carangobom.seguranca;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Perfil implements GrantedAuthority {

    public enum PERFIS {
        ADMIN;

        public String getRole() {
            return "ROLE_" + name();
        }
    }

    @Id
    private String nome;

    @Override
    public String getAuthority() {
        return "ROLE_" + nome;
    }
}
