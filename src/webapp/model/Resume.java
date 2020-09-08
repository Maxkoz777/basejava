package webapp.model;

/**
 * Initial resume class
 */
public class Resume {

    // Unique identifier
    public String uuid;

    public Resume(String uuid) {
        this.uuid = uuid;
    }

    public Resume(){}

    @Override
    public String toString() {
        return uuid;
    }
}
