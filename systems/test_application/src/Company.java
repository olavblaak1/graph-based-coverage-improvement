// Company.java
import java.util.ArrayList;
import java.util.List;

public class Company implements Printable {
    private String name;
    private List<Department> departments;

    public Company(String name) {
        this.name = name;
        this.departments = new ArrayList<>();
    }

    public void addDepartment(Department department) {
        departments.add(department);
    }

    @Override
    public void print() {
        System.out.println("Company: " + name);
        for (Department department : departments) {
            department.print();
        }
    }
}