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

    public List<Subject> getAllSubjects() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT id, name FROM subjects ORDER BY name";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                subjects.add(new Subject(rs.getInt("id"), rs.getString("name")));
            }
        }
        return subjects;
    }

    public List<Subject> getSubjectsForStudent(int studentId) throws SQLException{
        List<Subject> subjects = new ArrayList<>();
        String query = "SELECT DISTINCT sub.id, sub.name FROM subjects sub JOIN grades g ON sub.id = g.subject_id JOIN students st ON g.student_id = st.id WHERE st.user_id = ? ORDER BY sub.name";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, studentId);
            try(ResultSet result = statement.executeQuery()){
                while(result.next()){
                    subjects.add(new Subject(result.getInt("id"), result.getString("name")));
                }
            }
            return subjects;
        }
    }


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
