package med.voll.api.domain.direccion;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {

    private String calle;
    private Integer numero;
    private String complemento;
    private String distrito;
    private String ciudad;

    public Direccion(DatosDireccion datosDireccion) {
        this.calle = datosDireccion.calle();
        this.numero = datosDireccion.numero();
        this.distrito = datosDireccion.distrito();
        this.complemento = datosDireccion.complemento();
        this.ciudad = datosDireccion.ciudad();
    }

    public Direccion actualizarDatos(DatosDireccion datosDireccion) {
        this.calle = datosDireccion.calle();
        this.numero = datosDireccion.numero();
        this.distrito = datosDireccion.distrito();
        this.complemento = datosDireccion.complemento();
        this.ciudad = datosDireccion.ciudad();
        return this;
    }
}
