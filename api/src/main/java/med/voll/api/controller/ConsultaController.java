package med.voll.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import med.voll.api.domain.consulta.AgendaDeConsultaService;
import med.voll.api.domain.consulta.DatosAgendarConsulta;
import med.voll.api.domain.consulta.DatosCancelacionConsulta;
import med.voll.api.domain.medico.Medico;

@RestController
@RequestMapping("/consultas")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Consultas", description = "Operaciones relacionadas con las consultas")
public class ConsultaController {

    @Autowired
    private AgendaDeConsultaService servicio;

    @PostMapping
    @Transactional
    @Operation(
        summary = "Crea una nueva consulta",
        description = "Este metodo permite crear una nueva consulta",
        responses = {
            @ApiResponse(responseCode = "201", description = "Consulta creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<Object> agendar(@RequestBody @Valid DatosAgendarConsulta datos) {
        var response = servicio.agendar(datos);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Transactional
    @Operation(
        summary = "Elimina una consulta",
        description = "Este metodo permite eliminar una consulta del listado",
        responses = {
            @ApiResponse(responseCode = "204", description = "Consulta eliminado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<Object> cancelar(@RequestBody DatosCancelacionConsulta datos) {
        servicio.cancelar(datos);
        return ResponseEntity.ok().body("Su consulta a sido cancelada exitosamente");
    }
}
