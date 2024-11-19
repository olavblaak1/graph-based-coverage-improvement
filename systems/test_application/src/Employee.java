// Employee.java
public class Employee extends Person {
    private String position;

    public Employee(String name, String position) {
        super(name);
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public void print() {
        System.out.println("Employee: " + getName() + ", Position: " + position);
    }
}