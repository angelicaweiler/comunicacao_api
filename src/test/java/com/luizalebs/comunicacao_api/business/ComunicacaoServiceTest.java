package com.luizalebs.comunicacao_api.business;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.converter.ComunicacaoConverter;
import com.luizalebs.comunicacao_api.business.service.ComunicacaoService;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.repositories.ComunicacaoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Date;

import static com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum.SMS;
import static com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum.CANCELADO;
import static com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum.PENDENTE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ComunicacaoServiceTest {

    private static final Date DATA_HORA_ENVIO = new Date(2020, 12, 7, 12, 25, 00);
    private static final String EMAIL_DESTINATARIO = "email@email.com";
    private static final String NOME_DESTINATARIO = "Joao da Silva";
    private static final String MENSAGEM = "Seja Bem Vindo";
    private static final ModoEnvioEnum MODO_DE_ENVIO = SMS;
    private static final StatusEnvioEnum STATUS_ENVIO = PENDENTE;
    private static final StatusEnvioEnum STATUS_ENVIO_CANCEL = CANCELADO;
    private static final String NUMERO_TELEFONE = "51 98548563";

    @InjectMocks
    ComunicacaoService service;

    @Mock
    ComunicacaoRepository repository;

    @Spy
    ComunicacaoConverter converter;


    @Test
    public void deveSalvarComunicacaoComSucesso() {

        when(repository.save(buildComunicacaoparaEntity()))
                .thenReturn(buildComunicacaoparaEntity());

        ComunicacaoOutDTO dto = service.agendarComunicacao(buildComunicacaoInDTO());

        assertEquals(buildComunicacaoparaEntity().getDataHoraenvio(), dto.getDataHoraEnvio());
        assertEquals(buildComunicacaoparaEntity().getStatusEnvio(), dto.getStatusEnvio());
        assertEquals(buildComunicacaoparaEntity().getNomeDestinatario(), dto.getNomeDestinatario());
        assertEquals(buildComunicacaoparaEntity().getEmailDestinatario(), dto.getEmailDestinatario());
        assertEquals(buildComunicacaoparaEntity().getTelefoneDestinatario(), dto.getTelefoneDestinatario());
        assertEquals(buildComunicacaoparaEntity().getMensagem(), dto.getMensagem());
        assertEquals(buildComunicacaoparaEntity().getModoDeEnvio(), dto.getModoDeEnvio());
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoCasoOcorraErroAoAgendarComunicacao() throws ParseException {
        when(repository.save(buildComunicacaoparaEntity()))
                .thenThrow(new RuntimeException("erro").getCause());

        service.agendarComunicacao(buildComunicacaoInDTO());

    }

    @Test
    public void deveRetornarOStatusDoAgendamentoComSucesso() {

        when(repository.findByEmailDestinatario(EMAIL_DESTINATARIO))
                .thenReturn(buildComunicacaoparaEntity());

        ComunicacaoOutDTO dto = service.buscarStatusComunicacao(EMAIL_DESTINATARIO);

        verify(repository, times(1)).findByEmailDestinatario(EMAIL_DESTINATARIO);
        assertEquals(buildComunicacaoparaDTO().getNomeDestinatario(), dto.getNomeDestinatario());
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoCasoNãoRetornadoAgendamentoComSucesso() {

        when(repository.findByEmailDestinatario(EMAIL_DESTINATARIO))
                .thenReturn(null);

        service.buscarStatusComunicacao(EMAIL_DESTINATARIO);
    }

    @Test
    public void deveAlterarStatusDoAgendamentoComSucesso() {

        when(repository.findByEmailDestinatario(EMAIL_DESTINATARIO))
                .thenReturn(buildComunicacaoparaEntity());
        when(repository.save(buildComunicacaoparaEntity()))
                .thenReturn(buildComunicacaoparaEntity());

        ComunicacaoOutDTO dto = service.buscarStatusComunicacao(EMAIL_DESTINATARIO);
        dto.setStatusEnvio(STATUS_ENVIO_CANCEL);

        verify(repository, times(1)).findByEmailDestinatario(EMAIL_DESTINATARIO);
        assertEquals(buildComunicacaoparaDTOesperadoStatus().getStatusEnvio(), dto.getStatusEnvio());
    }

    @Test(expected = RuntimeException.class)
    public void deveLancarExcecaoCasoNãoEncontradoStatusDoAgendamento() {

        when(repository.findByEmailDestinatario(EMAIL_DESTINATARIO))
                .thenReturn(null);

        service.alterarStatusComunicacao(EMAIL_DESTINATARIO);
    }


    private static ComunicacaoOutDTO buildComunicacaoparaDTO() {

        return ComunicacaoOutDTO.builder()
                .dataHoraEnvio(DATA_HORA_ENVIO)
                .emailDestinatario(EMAIL_DESTINATARIO)
                .nomeDestinatario(NOME_DESTINATARIO)
                .mensagem(MENSAGEM)
                .modoDeEnvio(MODO_DE_ENVIO)
                .statusEnvio(STATUS_ENVIO)
                .telefoneDestinatario(NUMERO_TELEFONE)
                .build();
    }

    private static ComunicacaoInDTO buildComunicacaoInDTO() {

        return ComunicacaoInDTO.builder()
                .dataHoraEnvio(DATA_HORA_ENVIO)
                .emailDestinatario(EMAIL_DESTINATARIO)
                .nomeDestinatario(NOME_DESTINATARIO)
                .mensagem(MENSAGEM)
                .modoDeEnvio(MODO_DE_ENVIO)
                .statusEnvio(STATUS_ENVIO)
                .telefoneDestinatario(NUMERO_TELEFONE)
                .build();
    }

    private static ComunicacaoEntity buildComunicacaoparaEntity() {
        return ComunicacaoEntity.builder()
                .dataHoraenvio(DATA_HORA_ENVIO)
                .emailDestinatario(EMAIL_DESTINATARIO)
                .nomeDestinatario(NOME_DESTINATARIO)
                .mensagem(MENSAGEM)
                .modoDeEnvio(MODO_DE_ENVIO)
                .statusEnvio(STATUS_ENVIO)
                .telefoneDestinatario(NUMERO_TELEFONE)
                .build();
    }

    private static ComunicacaoOutDTO buildComunicacaoparaDTOesperadoStatus() {
        return ComunicacaoOutDTO.builder()
                .dataHoraEnvio(DATA_HORA_ENVIO)
                .emailDestinatario(EMAIL_DESTINATARIO)
                .nomeDestinatario(NOME_DESTINATARIO)
                .mensagem(MENSAGEM)
                .modoDeEnvio(MODO_DE_ENVIO)
                .statusEnvio(STATUS_ENVIO_CANCEL)
                .telefoneDestinatario(NUMERO_TELEFONE)
                .build();
    }

}
