package br.com.caelum.carangobom.seguranca.dominio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Perfil> perfis = new HashSet<>();

    public Usuario(String username, String password) {
        this(username, password, new HashSet<>());
    }

    public void adicionaPerfil(Perfil.PERFIS perfil) {
        perfis.add(new Perfil(perfil.name()));
    }

    public List<String> getNomesDosPerfis() {
        return perfis.stream()
                .map(Perfil::getNome)
                .collect(toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return perfis;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
