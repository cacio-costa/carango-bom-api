package br.com.caelum.carangobom.seguranca.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AutenticacaoDto {

    private UsuarioDto usuario;
    private String token;

}


