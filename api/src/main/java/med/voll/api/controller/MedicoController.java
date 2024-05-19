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
import med.voll.api.domain.medico.DatosActualizarMedico;
import med.voll.api.domain.medico.DatosListadoMedico;
import med.voll.api.domain.medico.DatosRegistroMedico;
import med.voll.api.domain.medico.DatosRespuestaMedico;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;

@RestController
@RequestMapping("/medicos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Médicos", description = "Operaciones relacionadas con los médicos")
//@Secured("ADMIN")//<-Este es el metodo que se habilita desde el SecurityConfiguration se puede aplicar tanto a nivel de clase
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    @PostMapping
    @Operation(
        summary = "Crea un nuevo médico",
        description = "Este metodo permite crear un nuevo médico",
        responses = {
            @ApiResponse(responseCode = "201", description = "Médico creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    //@Secured("ADMIN")//<->Como a nivel de metodo de clase.
    public ResponseEntity<DatosRespuestaMedico> registrarMedicos(@RequestBody @Valid DatosRegistroMedico datosRegistroMedico, UriComponentsBuilder uriComponentsBuilder) {
        Medico medico = medicoRepository.save(new Medico(datosRegistroMedico));
        DatosRespuestaMedico datosRespuestaMedico = new DatosRespuestaMedico(medico.getId(), medico.getNombre(), medico.getEmail(), medico.getTelefono(), medico.getDocumento(), new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(), medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(), medico.getDireccion().getComplemento()));
        // URI url = "http://localhost:8080/medicos/" + medico.getId();
        URI url = uriComponentsBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuestaMedico);
    }

    @GetMapping
    @Operation(
        summary = "Recupera la lista de los médicos",
        description = "Este metodo permite listar los médicos activos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de médicos recuperada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<Page<DatosListadoMedico>> listadoMedicos(@PageableDefault(size = 6,sort = "nombre" , direction = Direction.ASC) Pageable paginacion) {
        // return medicoRepository.findAll(paginacion).map(DatosListadoMedico::new);
        return ResponseEntity.ok(medicoRepository.findByActivoTrue(paginacion).map(DatosListadoMedico::new));

    }

    @PutMapping
    @Transactional
    @Operation(
        summary = "Actualizar los datos de un médico",
        description = "Este metodo permite modíficar los datos de un médico",
        responses = {
            @ApiResponse(responseCode = "200", description = "Médico actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<DatosRespuestaMedico> actualizarMedicos(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico) {
        Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());
        medico.actualizarDatos(datosActualizarMedico);
        return ResponseEntity.ok(new DatosRespuestaMedico(medico.getId(), medico.getNombre(), medico.getEmail(), medico.getTelefono(), medico.getDocumento(), new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(), medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(), medico.getDireccion().getComplemento())));
    }

    @DeleteMapping("/{id}")
    @Transactional
    // DELETE LÓGICO
    @Operation(
        summary = "Elimina un médico",
        description = "Este metodo permite eliminar un médico del listado",
        responses = {
            @ApiResponse(responseCode = "204", description = "Médico eliminado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<Object> eliminarMedicos(@PathVariable Long id) {
        Medico medico = medicoRepository.getReferenceById(id);
        medico.desactivarMedico();
        return ResponseEntity.noContent().build();
    }

    // DELETE EN BASE DE DATOS
    // public void eliminarMedicos(@PathVariable Long id) {
    //     Medico medico = medicoRepository.getReferenceById(id);
    //     medicoRepository.delete(medico);
    //}

    @GetMapping("/{id}")
    @Transactional
    @Operation(
        summary = "Recupera los datos de un médico",
        description = "Este metodo recuperar los datos de un médico desde la base de datos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Datos del médico listado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Medico.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
        }
    )
    public ResponseEntity<DatosRespuestaMedico> retornaDatosMedicos(@PathVariable Long id) {
        Medico medico = medicoRepository.getReferenceById(id);
        var datosRespuestaMedico = new DatosRespuestaMedico(medico.getId(), medico.getNombre(), medico.getEmail(), medico.getTelefono(), medico.getDocumento(), new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(), medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(), medico.getDireccion().getComplemento()));
        return ResponseEntity.ok(datosRespuestaMedico);
    }
}
