package br.com.caelum.carangobom.seguranca.jwt;

import br.com.caelum.carangobom.seguranca.dominio.Perfil;
import br.com.caelum.carangobom.seguranca.dominio.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class GerenciadorDeTokenJwtTest {

    String chave = "chavesimetrica";
    int seteDiasEmMilissegundos = 604800000;

    Usuario usuario = new Usuario("Fulano", "senha", Set.of(new Perfil("ADMIN")));

    @Test
    void deveGerarTokenComExpiracaoParametrizada() {
        GerenciadorDeTokenJwt gerenciador = new GerenciadorDeTokenJwt(chave, seteDiasEmMilissegundos);

        String token = gerenciador.generaToken(usuario);
        Date seteDiasAFrente = new Date(System.currentTimeMillis() + seteDiasEmMilissegundos);

        Jws<Claims> jwt = getJwt(token);
        assertThat(jwt.getBody().getExpiration()).isCloseTo(seteDiasAFrente, 60000);
        assertThat(jwt.getBody().getSubject()).isEqualTo("Fulano");
        assertThat(jwt.getBody().get("perfis", List.class)).containsOnly("ADMIN");
    }

    @Test
    void tokenNaoVencidoDeveSerConsideradoValido() {
        GerenciadorDeTokenJwt gerenciador = new GerenciadorDeTokenJwt(chave, seteDiasEmMilissegundos);
        assertThat(gerenciador.isValid(getTokenValido())).isTrue();
    }

    @Test
    void tokenVencidoDeveSerConsideradoInValido() {
        GerenciadorDeTokenJwt gerenciador = new GerenciadorDeTokenJwt(chave, seteDiasEmMilissegundos);
        assertThat(gerenciador.isValid(getTokenExpirado())).isFalse();
    }

    @Test
    void deveExtrairUsuarioComSeusPapeisPapeisDoToken() {
        GerenciadorDeTokenJwt gerenciador = new GerenciadorDeTokenJwt(chave, seteDiasEmMilissegundos);
        Usuario usuario = gerenciador.getUserFromToken(getTokenValido());

        assertThat(usuario)
            .hasFieldOrPropertyWithValue("username", "Fulano")
            .extracting(u -> u.getNomesDosPerfis())
                .asList()
                    .contains("ADMIN");
    }

    private Jws<Claims> getJwt(String token) {
        return Jwts.parser().setSigningKey(chave.getBytes()).parseClaimsJws(token);
    }

    private String getTokenValido() {
        return getToken(new Date());
    }

    private String getTokenExpirado() {
        return getToken(new Date(System.currentTimeMillis() - (seteDiasEmMilissegundos + 60000)));
    }

    private String getToken(Date date) {
        return Jwts.builder()
                .setIssuer("Carango Bom API")
                .setSubject(usuario.getUsername())
                .claim("perfis", usuario.getNomesDosPerfis())
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + seteDiasEmMilissegundos))
                .signWith(SignatureAlgorithm.HS512, chave.getBytes()).compact();
    }

}