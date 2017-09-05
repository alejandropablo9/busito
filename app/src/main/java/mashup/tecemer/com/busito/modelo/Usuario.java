package mashup.tecemer.com.busito.modelo;

/**
 * Created by Alejandro on 02/06/2017.
 */

public class Usuario {

    private String nombre;
    private String email;
    private String tutor;
    private String numero_emergencia;
    private String roll;

    public Usuario() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public String getNumero_emergencia() {
        return numero_emergencia;
    }

    public void setNumero_emergencia(String numero_emergencia) {
        this.numero_emergencia = numero_emergencia;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", tutor='" + tutor + '\'' +
                ", numero_emergencia='" + numero_emergencia + '\'' +
                ", roll='" + roll + '\'' +
                '}';
    }
}
