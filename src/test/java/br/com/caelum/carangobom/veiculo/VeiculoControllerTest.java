package br.com.caelum.carangobom.veiculo;

import br.com.caelum.carangobom.marca.MarcaRepository;
import br.com.caelum.carangobom.veiculo.dto.VeiculoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class VeiculoControllerTest {

    @Mock
    MarcaRepository marcaRepository;

    @Mock
    VeiculoRepository veiculoRepository;

    VeiculoController controller;
    Veiculo palio = new Veiculo(1L, "Palio", 2008, new BigDecimal("15000.00"), 1L);
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:8080");

    @BeforeEach
    void configura() {
        openMocks(this);
        controller = new VeiculoController(marcaRepository, veiculoRepository);
    }

    @Test
    void deveRetornarVeiculoSeExistir() {
        when(veiculoRepository.findById(palio.getId())).thenReturn(Optional.of(palio));

        ResponseEntity<VeiculoDto> resposta = controller.veiculoPorId(palio.getId());
        assertEquals(HttpStatus.OK, resposta.getStatusCode());

        VeiculoDto veiculoDto = resposta.getBody();
        assertEquals(veiculoDto.getAno(), palio.getAno());
        assertEquals(veiculoDto.getId(), palio.getId());
        assertEquals(veiculoDto.getModelo(), palio.getModelo());
    }

    @Test
    void deveCadastrarNovoVeiculo() {
        Veiculo gol = new Veiculo(2L, "Gol", 2021, new BigDecimal("70000.00"), 2L);
        when(veiculoRepository.save(gol)).thenReturn(gol);

        ResponseEntity<Veiculo> resposta = controller.cadastraVeiculo(gol, uriBuilder);
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals("http://localhost:8080/veiculos/2", resposta.getHeaders().getLocation().toString());

        verify(veiculoRepository).save(gol);
    }

    @Test
    void deveAlterarVeiculoSeEleExistir() {
        when(veiculoRepository.findById(palio.getId())).thenReturn(Optional.of(palio));

        ResponseEntity<Veiculo> resposta = controller.alteraVeiculo(palio.getId(), palio);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());

        verify(veiculoRepository).save(palio);
    }

    @Test
    void deveDeletarVeiculoSeEleExistir() {
        when(veiculoRepository.findById(palio.getId())).thenReturn(Optional.of(palio));

        ResponseEntity<?> resposta = controller.deletaVeiculo(palio.getId());
        assertEquals(HttpStatus.OK, resposta.getStatusCode());

        verify(veiculoRepository).delete(palio);
    }

}