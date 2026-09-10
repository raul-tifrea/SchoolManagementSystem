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

    public List<Subject> getSubjectsForStudent(int userId) throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT sub.id, sub.name, sub.study_year " +
                     "FROM subjects sub " +
                     "JOIN enrollments e ON e.subject_id = sub.id " +
                     "JOIN students st ON e.student_id = st.id " +
                     "WHERE st.user_id = ? " +
                     "ORDER BY sub.name";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subjects.add(new Subject(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("study_year")
                    ));
                }
            }
        }
        return subjects;
    }

    public List<Subject> getSubjectsForTeacher(int userId) throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT sub.id, sub.name, sub.study_year " +
                     "FROM subjects sub " +
                     "JOIN teachers t ON sub.teacher_id = t.id " +
                     "WHERE t.user_id = ? " +
                     "ORDER BY sub.study_year, sub.name";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subjects.add(new Subject(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("study_year")
                    ));
                }
            }
        }
        return subjects;
    }

    public List<Subject> getAllSubjects() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT id, name, study_year FROM subjects ORDER BY study_year, name";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                subjects.add(new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("study_year")
                ));
            }
        }
        return subjects;
    }
}
