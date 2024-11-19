// Main.java
public class Main {
    public static void main(String[] args) {
        Employee emp1 = new Employee("Alice", "Developer");
        Employee emp2 = new Employee("Bob", "Manager");

        Department dept = new Department("IT");
        dept.addEmployee(emp1);
        dept.addEmployee(emp2);

        Company company = new Company("TechCorp");
        company.addDepartment(dept);

        company.print();
    }
}