package br.com.caelum.carangobom.veiculo.dto;

import br.com.caelum.carangobom.marca.Marca;
import br.com.caelum.carangobom.marca.MarcaRepository;
import br.com.caelum.carangobom.veiculo.Veiculo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class VeiculoDto {

    private final Long id;
    private final String modelo;
    private final Integer ano;
    private final BigDecimal valor;
    private final Marca marca;

    public static VeiculoDto comMarca(Veiculo veiculo, MarcaRepository marcaRepository) {
        Marca marca = marcaRepository.findById(veiculo.getMarcaId()).orElse(new Marca(-1L, "NÃO INFORMADA"));
        return new VeiculoDto(veiculo.getId(), veiculo.getModelo(), veiculo.getAno(), veiculo.getValor(), marca);
    }
}
