package mashup.tecemer.com.busito.estimote;

import com.estimote.sdk.Region;

import java.util.UUID;

/**
 * Created by Alejandro on 03/06/2017.
 */

public class BeaconID {

    private String color;
    private String identifier;
    private int major;
    private int minor;
    private UUID proximityUUID;

    public BeaconID(){}

    public BeaconID(String color, String identifier, int major, int minor, UUID proximityUUID) {
        this.color = color;
        this.identifier = identifier;
        this.proximityUUID = proximityUUID;
        this.major = major;
        this.minor = minor;
    }

    public BeaconID(String color, String identifier, int major, int minor, String UUIDString) {
        this(color, identifier, major, minor, UUID.fromString(UUIDString));
    }

    public UUID getProximityUUID() {
        return proximityUUID;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public void setProximityUUID(UUID proximityUUID) {
        this.proximityUUID = proximityUUID;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Region toBeaconRegion() {
        return new Region(toString(), getProximityUUID(), getMajor(), getMinor());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (getClass() != o.getClass()) {
            return super.equals(o);
        }

        BeaconID other = (BeaconID) o;

        return getProximityUUID().equals(other.getProximityUUID())
                && getMajor() == other.getMajor()
                && getMinor() == other.getMinor();
    }

    @Override
    public String toString() {
        return "BeaconID{" +
                "color='" + color + '\'' +
                ", identifier='" + identifier + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", proximityUUID=" + proximityUUID +
                '}';
    }
}
