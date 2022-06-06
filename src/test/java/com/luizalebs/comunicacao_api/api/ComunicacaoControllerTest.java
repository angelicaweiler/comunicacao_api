package com.luizalebs.comunicacao_api.api;


import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.converter.ComunicacaoConverter;
import com.luizalebs.comunicacao_api.business.service.ComunicacaoService;
import com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.repositories.ComunicacaoRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum.SMS;
import static com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum.CANCELADO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc

public class ComunicacaoControllerTest {

    private static final Date DATA_HORA_ENVIO = new Date(2020, 12, 7, 12, 25, 00);
    private static final String EMAIL_DESTINATARIO = "email@email.com";
    private static final String NOME_DESTINATARIO = "Joao da Silva";
    private static final String MENSAGEM = "Seja Bem Vindo";
    private static final ModoEnvioEnum MODO_DE_ENVIO = SMS;
    private static final StatusEnvioEnum STATUS_ENVIO_CANCEL = CANCELADO;
    private static final String NUMERO_TELEFONE = "51 98548563";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ComunicacaoService service;

    @Spy
    ComunicacaoConverter converter;

    @InjectMocks
    ComunicacaoController controller;

    @BeforeEach
    public void setUp(WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void deveAgendarComunicacaoComSucesso() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/comunicacao/agendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dataHoraEnvio\" : \"2025-10-12 12:12:00\", \n" +
                                " \"nomeDestinatario\": \"Joao Silva Santos\", " +
                                "\"emailDestinatario\": \"joaodasilvateste@hotmail.com\", " +
                                "\"telefoneDestinatario\" : \"5199865482560\", \"mensagem\": \"Olá seja bem vindo\", " +
                                "\"modoDeEnvio\": \"SMS\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        verify(service).agendarComunicacao(any(ComunicacaoInDTO.class));
        String responseData = mvcResult.getResponse().getContentAsString();
    }

    @Test
    public void deveRetornarStatusdoAgendamentoComSucesso() throws Exception {
        when(service.buscarStatusComunicacao(EMAIL_DESTINATARIO)).thenReturn(buildComunicacaoparaDTO());

        mockMvc.perform(MockMvcRequestBuilders.get("/comunicacao/")
                        .param("emailDestinatario", EMAIL_DESTINATARIO))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("nomeDestinatario").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("emailDestinatario").value("email@email.com"));
    }

    @Test
    public void deveAlteraroStatusdoAgendamentoParaCanceladoComSucesso() throws Exception {
        when(service.alterarStatusComunicacao(EMAIL_DESTINATARIO)).thenReturn(buildComunicacaoparaDTO());

        mockMvc.perform(MockMvcRequestBuilders.patch("/comunicacao/cancelar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("emailDestinatario", EMAIL_DESTINATARIO))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("statusEnvio").value("CANCELADO"));
    }

    private static ComunicacaoOutDTO buildComunicacaoparaDTO() {
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
