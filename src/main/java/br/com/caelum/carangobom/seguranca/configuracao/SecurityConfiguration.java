package br.com.caelum.carangobom.seguranca.configuracao;

import br.com.caelum.carangobom.seguranca.UsuarioService;
import br.com.caelum.carangobom.seguranca.dominio.Perfil;
import br.com.caelum.carangobom.seguranca.jwt.EntryPointAutenticacaoJwt;
import br.com.caelum.carangobom.seguranca.jwt.FiltroDeAutenticacaoJwt;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UsuarioService userService;
    private FiltroDeAutenticacaoJwt filtroDeAutenticacao;
    private EntryPointAutenticacaoJwt entryPoint;
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/autenticacao/**").permitAll()
                .antMatchers(HttpMethod.GET, "/veiculos/**").permitAll()
                .antMatchers(HttpMethod.GET, "/marcas/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/usuarios/**").hasRole(Perfil.PERFIS.ADMIN.name())
                .anyRequest().authenticated()
            .and()
                .cors()
            .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(filtroDeAutenticacao, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                    .authenticationEntryPoint(entryPoint);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**", "/swagger-resources/**");
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(this.passwordEncoder);
    }

    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
