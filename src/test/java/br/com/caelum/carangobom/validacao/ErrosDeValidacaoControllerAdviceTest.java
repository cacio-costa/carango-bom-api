package br.com.caelum.carangobom.validacao;

import br.com.caelum.carangobom.seguranca.exception.UsuarioExistenteException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ErrosDeValidacaoControllerAdviceTest {

    @Mock
    BindingResult bindingResult;

    @Test
    void deveExtrairErrosDeValidacaoDaException() {
        openMocks(this);

        List<FieldError> erros = List.of(
            new FieldError("marca", "nome", "Não pode ser vazio."),
            new FieldError("marca", "nome", "Deve ter pelo menos 2 letras.")
        );
        when(bindingResult.getFieldErrors()).thenReturn(erros);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        ErrosDeValidacaoControllerAdvice advice = new ErrosDeValidacaoControllerAdvice();

        ListaDeErrosOutputDto listaDeErros = advice.capturaErrosDeValidacao(exception);
        assertEquals(2, listaDeErros.getQuantidadeDeErros());

        assertEquals("nome", listaDeErros.getErros().get(0).getParametro());
        assertEquals("Não pode ser vazio.", listaDeErros.getErros().get(0).getMensagem());

        assertEquals("nome", listaDeErros.getErros().get(1).getParametro());
        assertEquals("Deve ter pelo menos 2 letras.", listaDeErros.getErros().get(1).getMensagem());
    }

    @Test
    void deveInformarErroNoUsernameQuandoErroForDeUsuarioExistente() {
        UsuarioExistenteException excecao = new UsuarioExistenteException("Usuário já existe.");

        ErrosDeValidacaoControllerAdvice advice = new ErrosDeValidacaoControllerAdvice();
        ListaDeErrosOutputDto listaDeErros = advice.capturaCadastroDeUsuarioExistente(excecao);

        assertEquals(1, listaDeErros.getQuantidadeDeErros());

        assertEquals("username", listaDeErros.getErros().get(0).getParametro());
        assertEquals("Usuário já existe.", listaDeErros.getErros().get(0).getMensagem());
    }

}