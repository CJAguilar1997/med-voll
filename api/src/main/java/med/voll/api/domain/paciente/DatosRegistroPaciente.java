package med.voll.api.domain.paciente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import med.voll.api.domain.direccion.DatosDireccion;

public record DatosRegistroPaciente (
    @NotBlank(message = "Nombre es obligatorio")
    String nombre, 
    @NotBlank(message = "Email es obligatorio")
    @Email
    String email, 
    @NotBlank(message = "Teléfono es obligatorio")
    String telefono, 
    @NotBlank(message = "Documento es obligatorio")
    String documento, 
    @NotNull
    @Valid
    DatosDireccion datosDireccion) {

}
