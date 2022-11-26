public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Statuses status;

    public Task(String name, String description, Statuses status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    protected void setStatus(Statuses status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }
}