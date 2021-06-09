package br.com.caelum.carangobom.veiculo;

import br.com.caelum.carangobom.marca.Marca;
import br.com.caelum.carangobom.marca.MarcaRepository;
import lombok.AllArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@AllArgsConstructor
class ValidacaoDeMarcaInexistente implements Validator {

    private MarcaRepository marcaRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return Veiculo.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Veiculo v = (Veiculo) o;

        Optional<Marca> possivelMarca = marcaRepository.findById(v.getMarcaId());
        if (possivelMarca.isEmpty()) {
            errors.rejectValue("marcaId", "veiculo.marca.id.inexistente", "Marca inexistente: " + v.getMarcaId());
        }
    }

}
