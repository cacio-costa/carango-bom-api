package br.com.caelum.carangobom.dashboard;

import br.com.caelum.carangobom.veiculo.VeiculoRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@AllArgsConstructor
public class DashboardController {

    private final VeiculoRepository veiculoRepository;

    @GetMapping("/dashboard")
    public List<MontanteDto> dashboard() {
        return veiculoRepository.consolidaMontanteDeVeiculosPorMarca()
                .stream()
                .map(registro -> new MontanteDto(registro))
                .collect(toList());
    }

}
