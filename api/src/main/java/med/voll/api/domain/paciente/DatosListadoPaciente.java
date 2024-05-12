package med.voll.api.domain.paciente;

public record DatosListadoPaciente(Long id, String nombre, String documento , String telefono, String email) {
    
    public DatosListadoPaciente(Paciente paciente) {
        this(paciente.getId(), paciente.getNombre(), paciente.getDocumento(), paciente.getTelefono(), paciente.getEmail());
    }
}
