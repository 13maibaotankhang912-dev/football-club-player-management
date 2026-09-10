package model;

public abstract class BaseEntity implements Identifiable {

    private String id;

    protected BaseEntity(String id) {
        this.id = id;
    }

    @Override
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    // equals/hashCode use uppercase ID so CL-0001 and cl-0001 are the same entity
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseEntity other = (BaseEntity) obj;
        return id != null && id.equalsIgnoreCase(other.id);
    }

    @Override
    public int hashCode() {
        // Must be consistent with equals: uppercase so "CL-0001".hashCode() == "cl-0001".hashCode()
        return id == null ? 0 : id.toUpperCase().hashCode();
    }
}
