import java.util.Scanner;

//pretty sure alam nyo na ito
public class App {
    final static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        Studentdatabase students = new Studentdatabase();

        int choice;

        do {
            System.out.println("\nStudent Database Management System");
            System.out.println("1. Create Student Account");
            System.out.println("2. Update Student Information");
            System.out.println("3. Delete Student Account");
            System.out.println("4. View All Students");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");

            choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Creating Student Account...");
                    System.out.print("Enter first name: ");
                    String fname = input.nextLine();
                    System.out.print("Enter last name: ");
                    String lname = input.nextLine();
                    System.out.print("Enter section: ");
                    String section = input.nextLine();
                    System.out.print("Enter student ID: ");
                    int id = input.nextInt();
                    System.out.print("Enter year: ");
                    int year = input.nextInt();
                    students.createStudentData(fname, lname, section, id, year);
                    break;

                case 2:
                    System.out.println("Updating Student Information...");
                    System.out.print("Enter student ID to update: ");
                    int updateId = input.nextInt();
                    input.nextLine();
                    students.updateStudentData(updateId);
                    break;

                case 3:
                    System.out.println("Deleting Student Account...");
                    System.out.print("Enter student ID to delete: ");
                    int deleteId = input.nextInt();
                    students.deleteStudentData(deleteId);
                    break;

                case 4:
                    System.out.println("Viewing All Students...");
                    students.displayAllStudents();
                    break;

                case 5:
                    System.out.println("Exiting program...");
                    break;

                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }

        } while (choice != 5);

    }
}
