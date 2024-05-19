package med.voll.api.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.paciente.DatosActualizarPaciente;
import med.voll.api.domain.paciente.DatosListadoPaciente;
import med.voll.api.domain.paciente.DatosRegistroPaciente;
import med.voll.api.domain.paciente.DatosRespuestaPaciente;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.domain.paciente.PacienteRepository;

@RestController
@RequestMapping("/pacientes")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Pacientes", description = "Operaciones relacionadas con pacientes")
//@Secured("ADMIN")//<-Este es el metodo que se habilita desde el SecurityConfiguration se puede aplicar tanto a nivel de clase ->
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @PostMapping
    @Operation(
        summary = "Crea un nuevo paciente",
        description = "Este metodo permite crear un nuevo médico",
        responses = {
            @ApiResponse(responseCode = "201", description = "Paciente creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    //@Secured("ADMIN")//<->Como a nivel de metodo de clase.
    public ResponseEntity<DatosRespuestaPaciente> registrarPaciente(@RequestBody @Valid DatosRegistroPaciente datosRegistroPaciente, UriComponentsBuilder uriComponentsBuilder) {
        Paciente paciente = pacienteRepository.save(new Paciente(datosRegistroPaciente));
        DatosRespuestaPaciente datosRespuestaPaciente = new DatosRespuestaPaciente(paciente.getId(), paciente.getNombre(), paciente.getEmail(), paciente.getTelefono(), paciente.getDocumento(), new DatosDireccion(paciente.getDireccion().getCalle(), paciente.getDireccion().getDistrito(), paciente.getDireccion().getCiudad(), paciente.getDireccion().getNumero(), paciente.getDireccion().getComplemento()));
        URI url = uriComponentsBuilder.path("/pacientes/{id}").buildAndExpand(paciente.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuestaPaciente);
    }

    @GetMapping
    @Operation(
        summary = "Recupera la lista de los pacientes",
        description = "Este metodo permite listar los pacientes activos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de pacientes recuperada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<Page<DatosListadoPaciente>> listarPacientes(@PageableDefault(size = 10, sort = "nombre", direction = Direction.DESC) Pageable paginacion) {
        // return pacienteRepository.findAll(paginacion).map(DatosListadoPaciente::new);
        return ResponseEntity.ok(pacienteRepository.findByActivoTrue(paginacion).map(DatosListadoPaciente::new));
    }

    @PutMapping
    @Transactional
    @Operation(
        summary = "Actualizar los datos de un paciente",
        description = "Este metodo permite modíficar los datos de un paciente",
        responses = {
            @ApiResponse(responseCode = "200", description = "Paciente actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<DatosRespuestaPaciente> actualizarPaciente(@RequestBody @Valid DatosActualizarPaciente datosActualizarPaciente) {
        Paciente paciente = pacienteRepository.getReferenceById(datosActualizarPaciente.id());
        paciente.actualizarDatos(datosActualizarPaciente);
        DatosRespuestaPaciente datosRespuestaPaciente = new DatosRespuestaPaciente(paciente.getId(), paciente.getNombre(), paciente.getEmail(), paciente.getTelefono(), paciente.getDocumento(), new DatosDireccion(paciente.getDireccion().getCalle(), paciente.getDireccion().getDistrito(), paciente.getDireccion().getCiudad(), paciente.getDireccion().getNumero(), paciente.getDireccion().getComplemento()));
        return ResponseEntity.ok(datosRespuestaPaciente);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(
        summary = "Elimina un paciente",
        description = "Este metodo permite eliminar un paciente del listado",
        responses = {
            @ApiResponse(responseCode = "204", description = "Paciente eliminado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<Object> eliminarPaciente(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.getReferenceById(id);
        paciente.desactivarPaciente();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Transactional
    @Operation(
        summary = "Recupera los datos de un paciente",
        description = "Este metodo recuperar los datos de un paciente desde la base de datos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Datos del paciente listado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<DatosRespuestaPaciente> retornarDatosPaciente(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.getReferenceById(id);
        var datosRespuestaPaciente = new DatosRespuestaPaciente(paciente.getId(), paciente.getNombre(), paciente.getEmail(), paciente.getTelefono(), paciente.getDocumento(), new DatosDireccion(paciente.getDireccion().getCalle(), paciente.getDireccion().getDistrito(), paciente.getDireccion().getCiudad(), paciente.getDireccion().getNumero(), paciente.getDireccion().getComplemento()));
        return ResponseEntity.ok(datosRespuestaPaciente);
    }
}
