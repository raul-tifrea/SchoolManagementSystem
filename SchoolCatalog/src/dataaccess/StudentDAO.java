package dataaccess;

import connection.ConnectionFactory;
import model.Grade;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO{

    public List<Student> getStudentsForTeacher(int teacherUserId) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, name, email FROM students ORDER BY name";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email")
                    ));
                }
            }
        }
        return students;
    }




}
