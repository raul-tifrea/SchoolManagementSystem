package dataaccess;

import connection.ConnectionFactory;
import model.Grade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    public List<Grade> getGradesForStudent(int userId, int subjectId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.id, g.grade, sub.name AS subject_name " +
                     "FROM grades g " +
                     "JOIN students st ON g.student_id = st.id " +
                     "JOIN subjects sub ON g.subject_id = sub.id " +
                     "WHERE st.user_id = ? AND g.subject_id = ? " +
                     "ORDER BY g.grade_date, g.id";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(new Grade(
                            rs.getInt("id"),
                            null,
                            rs.getString("subject_name"),
                            rs.getDouble("grade")
                    ));
                }
            }
        }
        return grades;
    }

    public List<Grade> getGradesForTeacherSubject(int subjectId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.id, st.name AS student_name, sub.name AS subject_name, g.grade " +
                     "FROM grades g " +
                     "JOIN students st ON g.student_id = st.id " +
                     "JOIN subjects sub ON g.subject_id = sub.id " +
                     "WHERE g.subject_id = ? " +
                     "ORDER BY st.study_group, st.name, g.id";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(new Grade(
                            rs.getInt("id"),
                            rs.getString("student_name"),
                            rs.getString("subject_name"),
                            rs.getDouble("grade")
                    ));
                }
            }
        }
        return grades;
    }

    public boolean insertGrade(int studentId, int subjectId, double grade) throws SQLException {
        String sql = "INSERT INTO grades (student_id, subject_id, grade) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setDouble(3, grade);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteGradeById(int gradeId) throws SQLException {
        String sql = "DELETE FROM grades WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gradeId);
            return ps.executeUpdate() > 0;
        }
    }
}
