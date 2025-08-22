package dataaccess;

import connection.ConnectionFactory;
import model.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    public List<Subject> getSubjectsForTeacher(int teacherId) throws SQLException{
        List<Subject> subjects = new ArrayList<>();
        try(Connection connection = ConnectionFactory.getConnection()) {
            String query = "SELECT sub.id, sub.name FROM subjects sub JOIN teachers t ON sub.teacher_id = t.id WHERE t.user_id = ? ORDER BY sub.name";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, teacherId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    subjects.add(new Subject(result.getInt("id"), result.getString("name")));
                }
            }

        }
        return subjects;

    }
}
