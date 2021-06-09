package br.com.caelum.carangobom.veiculo;

import br.com.caelum.carangobom.marca.Marca;
import br.com.caelum.carangobom.marca.MarcaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class ValidacaoDeMarcaInexistenteTest {

    @Mock
    MarcaRepository marcaRepository;

    @Mock
    Errors errors;

    ValidacaoDeMarcaInexistente validacao;

    @BeforeEach
    void inicializa() {
        openMocks(this);
        validacao = new ValidacaoDeMarcaInexistente(marcaRepository);
    }

    @Test
    void deveSuporarValidacaoDeVeiculo() {
        assertTrue(validacao.supports(Veiculo.class));
    }

    @Test
    void deveRejeitarValorSeVeiculoEstiverVinculadoAMarcaInexistente() {
        Veiculo palio = new Veiculo(1L, "Palio", 2021, BigDecimal.TEN, 10L);
        when(marcaRepository.findById(palio.getMarcaId())).thenReturn(Optional.empty());

        validacao.validate(palio, errors);
        verify(errors).rejectValue("marcaId", "veiculo.marca.id.inexistente", "Marca inexistente: " + palio.getMarcaId());
    }

    @Test
    void naoDeveRejeitarValorVeiculoEstiverVinculadoAMarcaExistente() {
        Veiculo palio = new Veiculo(1L, "Palio", 2021, BigDecimal.TEN, 10L);
        when(marcaRepository.findById(palio.getMarcaId())).thenReturn(Optional.of(new Marca(10L, "Fiat")));

        validacao.validate(palio, errors);
        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());
    }

}