package br.com.caelum.carangobom.marca;

import br.com.caelum.carangobom.veiculo.Veiculo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Marca {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, message = "Deve ter {min} ou mais caracteres.")
    private String nome;

    public Marca(String nome) {
        this(null, nome);
    }

}
