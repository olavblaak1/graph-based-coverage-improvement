import Employee.Client;
import Employee.Employee;
import Employee.Manager;

import Project.Project;
import Project.Task;

import Company.Department;

// Main.java
public class Main {
    public static void main(String[] args) {
        Employee emp1 = new Employee("Alice", "Developer");
        Employee emp2 = new Employee("Bob", "Manager");
        Manager mgr = new Manager("Charlie", "Senior Manager");
        mgr.addTeamMember(emp1);
        mgr.addTeamMember(emp2);

        Client client = new Client("Acme Corp");
        Project project = new Project("Project X", client);
        project.addTeamMember(emp1);
        project.addTeamMember(emp2);
        project.addTask(new Task("Develop feature A", emp1));
        project.addTask(new Task("Manage team", emp2));

        Department dept = new Department("IT");
        dept.addEmployee(emp1);
        dept.addEmployee(emp2);
        dept.addEmployee(mgr);
        dept.addProject(project);

        dept.print();
    }
}