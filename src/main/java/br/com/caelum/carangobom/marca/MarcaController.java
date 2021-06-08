package br.com.caelum.carangobom.marca;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/marcas")
@AllArgsConstructor
@Transactional
public class MarcaController {

    private final MarcaRepository marcaRepository;

    @GetMapping
    @Cacheable("listagemDeMarcas")
    public List<Marca> listaMarcas() {
        return marcaRepository.findAllByOrderByNome();
    }

    @GetMapping("/{id}")
    @Cacheable("marcaPorId")
    public ResponseEntity<Marca> marcaPorId(@PathVariable Long id) {
        return marcaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @CacheEvict(value = "listagemDeMarcas", allEntries = true)
    public ResponseEntity<Marca> cadastraMarca(@Valid @RequestBody Marca novaMarca, UriComponentsBuilder uriBuilder) {
        Marca marcaSalva = marcaRepository.save(novaMarca);

        URI location = uriBuilder.path("/marcas/{id}")
                .buildAndExpand(marcaSalva.getId())
                .toUri();

        return ResponseEntity.created(location).body(marcaSalva);
    }

    @PutMapping("/{id}")
    @Caching(evict = {
        @CacheEvict(value = "listagemDeMarcas", allEntries = true),
        @CacheEvict(value = "marcaPorId", key = "#id")
    })
    public ResponseEntity<Marca> alteraNome(@PathVariable Long id, @Valid @RequestBody Marca marcaAlterada) {
        return marcaRepository.findById(id)
                .map(marcaCadastrada -> {
                    marcaCadastrada.setNome(marcaAlterada.getNome());
                    return ResponseEntity.ok(marcaCadastrada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Caching(evict = {
        @CacheEvict(value = "listagemDeMarcas", allEntries = true),
        @CacheEvict(value = "marcaPorId", key = "#id")
    })
    public ResponseEntity<Marca> deletaMarca(@PathVariable Long id) {
        Optional<Marca> possivelMarca = marcaRepository.findById(id);
        possivelMarca.ifPresent(marcaRepository::delete);

        return possivelMarca.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}