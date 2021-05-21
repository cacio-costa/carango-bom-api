package br.com.caelum.carangobom.seguranca.jwt;

import br.com.caelum.carangobom.seguranca.Perfil;
import br.com.caelum.carangobom.seguranca.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class GerenciadorDeTokenJwt {

    private final String secret;
    private final long expirationInMillis;

    public GerenciadorDeTokenJwt(@Value("${jwt.secret}") String secret,
                               @Value("${jwt.expiration}") long expirationInMillis) {
        this.secret = secret;
        this.expirationInMillis = expirationInMillis;
    }

    public String generaToken(Usuario usuario) {

        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationInMillis);

        return Jwts.builder()
                .setIssuer("Carango Bom API")
                .setSubject(usuario.getUsername())
                .claim("perfis", usuario.getNomesDosPerfis())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes()).compact();
    }

    public boolean isValid(String jwt) {
        try {
            Jwts.parser().setSigningKey(this.secret.getBytes()).parseClaimsJws(jwt);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public Usuario getUserFromToken(String jwt) {
        Claims claims = Jwts.parser().setSigningKey(this.secret.getBytes()).parseClaimsJws(jwt).getBody();
        Usuario usuario = new Usuario();
        usuario.setUsername(claims.getSubject());

        List<String> perfis = claims.get("perfis", List.class);
        perfis.forEach(perfil -> usuario.adicionaPerfil(Perfil.PERFIS.valueOf(perfil)));

        return usuario;
    }

}
