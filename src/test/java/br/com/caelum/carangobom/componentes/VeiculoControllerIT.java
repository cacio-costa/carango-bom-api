package br.com.caelum.carangobom.componentes;

import br.com.caelum.carangobom.testcontainer.MySQLCustomContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@WithMockUser
public class VeiculoControllerIT {

    static final int QUANTIDADE_ORIGINAL_REGISTROS = 9;

    static final MySQLContainer<MySQLCustomContainer> mysql = MySQLCustomContainer.getInstance();

    static {
        mysql.withDatabaseName("carangobom");
        mysql.start();
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void deveListarVeiculosOrdenadosPorNomeDaMarcaEModelo() throws Exception {
        verificaListagemInicial()
                .andExpect(jsonPath("$[8].id", is(3)))
                .andExpect(jsonPath("$[8].modelo", is("Polo")))
                .andExpect(jsonPath("$[8].marca.id", is(2)))
                .andExpect(jsonPath("$[8].ano", is(2021)))
                .andExpect(jsonPath("$[8].valor", is(78000.0)));
    }

    private ResultActions verificaListagemInicial() throws Exception {
        return mockMvc.perform(get("/veiculos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(QUANTIDADE_ORIGINAL_REGISTROS)));
    }
}
