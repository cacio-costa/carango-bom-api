package br.com.caelum.carangobom.marca;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface MarcaRepository extends Repository<Marca, Long> {

    void delete(Marca marca);
    Marca save(Marca marca);

    Optional<Marca> findById(Long id);

    List<Marca> findAllByOrderByNome();
}
