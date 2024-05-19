package med.voll.api.domain.consulta;

import med.voll.api.domain.consulta.validaciones.cancelacion.MotivoCancelamiento;

public record DatosCancelacionConsulta(Long idConsulta, MotivoCancelamiento motivo) {

    
}
