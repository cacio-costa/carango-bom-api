package br.com.caelum.carangobom.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
public class MontanteDto {

    private String nomeDaMarca;
    private Integer quantidadeDeVeiculos;
    private BigDecimal montante;

    public MontanteDto(Map<String, Object> registro) {
        nomeDaMarca = registro.get("nome").toString();
        quantidadeDeVeiculos = Integer.valueOf(registro.get("quantidadeVeiculos").toString());
        montante = new BigDecimal(registro.get("montante").toString());
    }

}
