package mashup.tecemer.com.busito.modelo;

/**
 * Created by Alejandro on 03/06/2017.
 */

public class Coordenadas {

    private double latitud;
    private double longitud;

    public Coordenadas() {
    }

    public Coordenadas(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return "Coordenadas{" +
                "latitud=" + latitud +
                ", longitud=" + longitud +
                '}';
    }
}
