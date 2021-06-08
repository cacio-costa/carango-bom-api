package br.com.caelum.carangobom.dashboard;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

class MontanteDtoTest {

    @Test
    void deveRecuperarEConverterOsDadosCorretosDosRegistros() {
        Map<String, Object> registro = Map.of(
            "nome", "Fiat",
            "quantidadeVeiculos", 10,
            "montante", "123000.50"
        );

        MontanteDto montanteDto = new MontanteDto(registro);
        assertThat(montanteDto)
                .hasFieldOrPropertyWithValue("nomeDaMarca", "Fiat")
                .hasFieldOrPropertyWithValue("quantidadeDeVeiculos", 10)
                .hasFieldOrPropertyWithValue("montante", new BigDecimal("123000.50"));
    }

}