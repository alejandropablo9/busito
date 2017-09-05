package mashup.tecemer.com.busito.modelo;


/**
 * Created by Alejandro on 31/05/2017.
 */

public class Bus {

    private long id;
    private Coordenadas origen;
    private Coordenadas destino;
    private String identifier;

    public Bus(){}

    public Bus(int id, Coordenadas origen, Coordenadas destino, String identifier) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.identifier = identifier;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Coordenadas getOrigen() {
        return origen;
    }

    public void setOrigen(Coordenadas origen) {
        this.origen = origen;
    }

    public Coordenadas getDestino() {
        return destino;
    }

    public void setDestino(Coordenadas destino) {
        this.destino = destino;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String presenter(){
        return "autobus no. " + id;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "id=" + id +
                ", destino='" + destino + '\'' +
                ", origen='" + origen + '\'' +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}
