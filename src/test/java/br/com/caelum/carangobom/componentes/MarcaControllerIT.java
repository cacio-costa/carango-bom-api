package br.com.caelum.carangobom.componentes;

import br.com.caelum.carangobom.marca.Marca;
import br.com.caelum.carangobom.testcontainer.MySQLCustomContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@WithMockUser
public class MarcaControllerIT {

    static final int QUANTIDADE_ORIGINAL_REGISTROS = 7;

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
    void deveListarMarcasOrdenadasAlfabeticamente() throws Exception {
        verificaListagemInicial()
            .andExpect(jsonPath("$[6].id", is(2)))
            .andExpect(jsonPath("$[6].nome", is("Volkswagen")));
    }

    @Test
    void deveRecuperarMarcaPorIdExistente() throws Exception {
        validaMarca(new Marca(1L, "Fiat"))
            .andExpect(status().isOk());
    }

    @Test
    void deveCadastrarMarcaNaoCadastrada() throws Exception {
        String json = mapper.writeValueAsString(new Marca("Ferrari"));

        MockHttpServletRequestBuilder requisicao = post("/marcas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requisicao)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/marcas/8"));
    }

    @Test
    void naoDeveCadastrarMarcaSemNome() throws Exception {
        String json = mapper.writeValueAsString(new Marca(""));

        MockHttpServletRequestBuilder requisicao = post("/marcas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        verificaErrosDeValidacao(requisicao);
        verificaListagemInicial();
    }

    @Test
    void deveAlterarNomeDeMarcaExistente() throws Exception {
        Marca marcaAlterada = new Marca(1L, "Fiat - ALTERADA");

        MockHttpServletRequestBuilder requisicao = put("/marcas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(marcaAlterada));

        mockMvc.perform(requisicao)
                .andDo(print())
                .andExpect(status().isOk());

        validaMarca(marcaAlterada);
    }

    @Test
    void naoDeveAlterarMarcaSemNome() throws Exception {
        Marca marcaComNomeInvalido = new Marca(1L, "");

        MockHttpServletRequestBuilder requisicao = put("/marcas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(marcaComNomeInvalido));

        verificaErrosDeValidacao(requisicao);

        Marca marcaInalterada = new Marca(1L, "Fiat");
        validaMarca(marcaInalterada);
    }

    @Test
    void deveExcluirMarcaExistente() throws Exception {
        mockMvc.perform(delete("/marcas/1"))
            .andDo(print())
            .andExpect(status().isOk());

        mockMvc.perform(get("/marcas/1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    private ResultActions validaMarca(Marca marca) throws Exception {
        return mockMvc.perform(get("/marcas/" + marca.getId()))
                .andDo(print())
                .andExpect(jsonPath("$.id", equalTo(marca.getId().intValue())))
                .andExpect(jsonPath("$.nome", equalTo(marca.getNome())));
    }

    private ResultActions verificaErrosDeValidacao(MockHttpServletRequestBuilder requisicao) throws Exception {
        return mockMvc.perform(requisicao)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.quantidadeDeErros", is(2)))
                .andExpect(jsonPath("$.erros", hasSize(2)))
                .andExpect(jsonPath("$.erros[*].mensagem", containsInAnyOrder("Deve ser preenchido.", "Deve ter 2 ou mais caracteres.")));
    }

    private ResultActions verificaListagemInicial() throws Exception {
        return mockMvc.perform(get("/marcas"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(QUANTIDADE_ORIGINAL_REGISTROS)));
    }
}