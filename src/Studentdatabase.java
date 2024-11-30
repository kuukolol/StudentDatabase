import java.sql.*;
import java.util.Scanner;

public class Studentdatabase {
    final private String databaseName = "StudentDatabase"; // database name na gusto mo i store
    final static Scanner input = new Scanner(System.in);

    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;

    // setting up connection for sql and checking if database exist
    public Studentdatabase() {
        String url = "jdbc:mysql://localhost:3306";
        String username = "root";// always root as username
        String password = "password"; // password ng sql mo replace mo na lang
        String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + databaseName; // command pag create ng database
                                                                                      // (if exist or not)
        String checkDatabaseQuery = "SHOW DATABASES LIKE '" + databaseName + "'"; // pakita ung database KUNG meron na
        String useDatabaseQuery = "USE " + databaseName; // sql command to use database
        try {
            connection = DriverManager.getConnection(url, username, password); // connecting sa sql server mo
            statement = connection.createStatement(); // similar siya sa CLI or terminal dito iwriwrite ng java ang
                                                      // command then execute
            resultset = statement.executeQuery(checkDatabaseQuery);
            if (resultset.next()) { // chinecheck IF exist nga ba ung database and table
                statement.executeUpdate(useDatabaseQuery);
                System.out.println("Database and table loaded");
            } else {// if not gagawa siya ng database and TABLE
                statement.executeUpdate(createDatabaseQuery);
                System.out.println("Database been created");
                statement.executeUpdate(useDatabaseQuery);
                createTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // name as IS checheck kung exist nga ba ung id na inenter ni user
    private boolean doesStudentExist(int id) {
        String checkStudentQuery = "SELECT COUNT(*) FROM Students WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkStudentQuery)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // paggawa ng table default null means pag nalimot ni user mag enter ng info
    // null ung value para iwas error
    // 100 or 50 ayan ung character length maximum
    void createTable() {
        // format ng query
        // ColumnName space DataType(if may character limit except int) space default
        // value or ano ung primary key(unique siya for every row to avoid error aka ndi
        // ma double ung row with same id)
        String createTableQuery = "CREATE TABLE Students (" +
                "id INT PRIMARY KEY, " +
                "fname VARCHAR(100) DEFAULT NULL, " +
                "lname VARCHAR(100) DEFAULT NULL, " +
                "section VARCHAR(50) DEFAULT NULL, " +
                "year INT DEFAULT NULL)";
        try {
            statement.executeUpdate(createTableQuery);
            System.out.println("Table 'Students' created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // method paggawa ng data para malagay sa table
    // preparedstatement ito parang string format siya lahat nung naka '?' i
    // rereplace siya ung index ayan ung ilan question mark meron at
    // dun ilalagay ung data
    // rowaffected ito pang ilan row siya which means pang ilang data na sa table ka
    // naglagay example if wala pa row nun is 1
    // kung meron ka na 5 data then nirun mo to result nung rowaffected is 6
    void createStudentData(String fname, String lname, String section, int id, int year) {
        String insertInfoQuery = "INSERT INTO Students (id, fname, lname, section, year) VALUES (?, ?, ?, ?, ?)";
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(insertInfoQuery)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, fname);
            preparedStatement.setString(3, lname);
            preparedStatement.setString(4, section);
            preparedStatement.setInt(5, year);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted into Students.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // as is to updatestudentdata now AS ALWAYS need mo muna i check IF ung id is
    // nasa table mo if not then return or stop mo si user
    // ung first field para lang to guide sa string builder IF isa lang or dalawa
    // lang ung papalitan na content
    void updateStudentData(int id) {

        if (!doesStudentExist(id)) {
            System.out.println("Student with ID " + id + " does not exist.");
            return;
        }

        System.out.println("Enter first name (leave blank if no change):");
        String fname = input.nextLine();
        if (fname.isEmpty())
            fname = null;

        System.out.println("Enter last name (leave blank if no change):");
        String lname = input.nextLine();
        if (lname.isEmpty())
            lname = null;

        System.out.println("Enter section (leave blank if no change):");
        String section = input.nextLine();
        if (section.isEmpty())
            section = null;

        System.out.println("Enter year (leave blank if no change):");
        String yearInput = input.nextLine();
        Integer year = null;
        if (!yearInput.isEmpty()) {
            try {
                year = Integer.parseInt(yearInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid year input. Year will not be updated.");
            }
        }

        StringBuilder updateQuery = new StringBuilder("UPDATE Students SET ");
        boolean firstField = true;

        if (fname != null) {
            if (!firstField)
                updateQuery.append(", ");
            updateQuery.append("fname = ?");
            firstField = false;
        }
        if (lname != null) {
            if (!firstField)
                updateQuery.append(", ");
            updateQuery.append("lname = ?");
            firstField = false;
        }
        if (section != null) {
            if (!firstField)
                updateQuery.append(", ");
            updateQuery.append("section = ?");
            firstField = false;
        }
        if (year != null) {
            if (!firstField)
                updateQuery.append(", ");
            updateQuery.append("year = ?");
            firstField = false;
        }

        updateQuery.append(" WHERE id = ?");

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery.toString())) {
            int paramIndex = 1;

            if (fname != null)
                preparedStatement.setString(paramIndex++, fname);
            if (lname != null)
                preparedStatement.setString(paramIndex++, lname);
            if (section != null)
                preparedStatement.setString(paramIndex++, section);
            if (year != null)
                preparedStatement.setInt(paramIndex++, year);
            preparedStatement.setInt(paramIndex, id);

            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println(rowsUpdated + " row(s) updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // as is rin ito check muna if id ay nasa table then run ung delete command
    void deleteStudentData(int id) {
        if (!doesStudentExist(id)) {
            System.out.println("Student with ID " + id + " does not exist.");
            return;
        }
        String deleteQuery = "DELETE FROM Students WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, id);
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Student with ID " + id + " has been deleted.");
            } else {
                System.out.println("No rows deleted. Something went wrong.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // as is then ung name
    // now para makuha ung info since ang resultset nga ay object need mo siya i
    // iterate(which means looping)
    // gumamit ako ng while loop since ITO ANG MUCH better para ma detect ni java if
    // true or not na meron pang row
    // then to grab the content need mo lang dapat alam ung table column names and
    // ung type nun
    void displayAllStudents() {
        String selectQuery = "SELECT * FROM Students";
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectQuery)) {

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No students found in the database.");
                return;
            }

            System.out.printf("%-10s %-15s %-15s %-10s %-5s\n", "ID", "First Name", "Last Name", "Section", "Year");
            System.out.println("----------------------------------------------------------");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String fname = resultSet.getString("fname");
                String lname = resultSet.getString("lname");
                String section = resultSet.getString("section");
                int year = resultSet.getInt("year");
                System.out.printf("%-10d %-15s %-15s %-10s %-5d\n", id, fname, lname, section, year);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
