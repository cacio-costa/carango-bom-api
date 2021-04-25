package br.com.caelum.carangobom.veiculo.dto;

import br.com.caelum.carangobom.marca.Marca;
import br.com.caelum.carangobom.marca.MarcaRepository;
import br.com.caelum.carangobom.veiculo.Veiculo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class VeiculoDto {

    private Long id;
    private String modelo;
    private Integer ano;
    private BigDecimal valor;
    private Marca marca;

    public static VeiculoDto comMarca(Veiculo veiculo, MarcaRepository marcaRepository) {
        Marca marca = marcaRepository.findById(veiculo.getMarcaId()).orElse(new Marca(-1L, "NÃO INFORMADA"));
        return new VeiculoDto(veiculo.getId(), veiculo.getModelo(), veiculo.getAno(), veiculo.getValor(), marca);
    }
}
