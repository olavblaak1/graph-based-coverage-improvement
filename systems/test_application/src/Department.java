// Department.java
import java.util.ArrayList;
import java.util.List;

public class Department implements Printable {
    private String name;
    private List<Employee> employees;

    public Department(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    @Override
    public void print() {
        System.out.println("Department: " + name);
        for (Employee employee : employees) {
            employee.print();
        }
    }
}