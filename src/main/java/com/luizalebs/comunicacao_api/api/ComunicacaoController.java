package com.luizalebs.comunicacao_api.api;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.service.ComunicacaoService;
import com.sun.istack.NotNull;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comunicacao")
@Api(value = "Comunicação")
public class ComunicacaoController {

    private final ComunicacaoService service;

    public ComunicacaoController(ComunicacaoService service) {
        this.service = service;
    }

    @ApiOperation(value = "Realiza o agendamento da Comunicacao", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Agendamento efetuado com sucesso", response = ComunicacaoOutDTO.class),
            @ApiResponse(code = 500, message = "Erro ao realizar o agendamento", response = ResponseEntity.class),
    })
    @PostMapping("/agendar")
    public ResponseEntity<ComunicacaoOutDTO> agendar(@RequestBody ComunicacaoInDTO dto)  {
        return ResponseEntity.ok(service.agendarComunicacao(dto));
    }

    @ApiOperation(value = "Retorna as informações e Status de agendamento de acordo com o email informado", httpMethod = "GET")
    @ApiParam(name = "emailDestinatario", value = "email do destinatario", required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna o status de acordo com o email fornecido"),
            @ApiResponse(code = 500, message = "Erro ao realizar a consulta por email"),
    })
    @GetMapping("/")
    public ResponseEntity<ComunicacaoOutDTO> buscarStatus(@RequestParam String emailDestinatario) {
        return ResponseEntity.ok(service.buscarStatusComunicacao(emailDestinatario));
    }

    @ApiOperation(value = "Altera o Status de agendamento de acordo com o email informado", httpMethod = "PATCH")
    @ApiParam(name = "emailDestinatario", value = "email do destinatario", required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Altera o status de acordo com o email fornecido"),
            @ApiResponse(code = 500, message = "Erro ao realizar a consulta por email"),
    })
    @PatchMapping("/cancelar")
    public ResponseEntity<ComunicacaoOutDTO> cancelarStatus(@RequestParam String emailDestinatario) {
        return ResponseEntity.ok(service.alterarStatusComunicacao(emailDestinatario));
    }
}
