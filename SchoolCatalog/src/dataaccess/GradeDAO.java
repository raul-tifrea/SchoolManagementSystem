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

    public List<Grade> getGradesForStudent(int studentId, int subjectId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String query = "SELECT g.id, g.grade FROM grades g JOIN students st ON g.student_id = st.id WHERE st.user_id = ? AND g.subject_id = ? ORDER BY g.id";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, studentId);
            statement.setInt(2, subjectId);
            try(ResultSet result = statement.executeQuery()){
                while(result.next()){
                    grades.add(new Grade(result.getInt("id"), null, null, result.getDouble("grade")));
                }
            }
        }
        return grades;
    }


    public List<Grade> getGradesForTeacher(int teacherId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String query = "SELECT g.id, st.name AS student_name, g.grade FROM grades g JOIN students st ON g.student_id = st.id JOIN subjects sub ON g.subject_id = sub.id JOIN teachers t ON sub.teacher_id = t.id WHERE t.user_id = ? ORDER BY st.name, g.id";
        try(Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1,teacherId);
            try(ResultSet result = statement.executeQuery()){
                while(result.next()){
                    grades.add(new Grade(
                            result.getInt("id"),
                            result.getString("student_name"),
                            null,
                            result.getDouble("grade")));
                }
            }

        }
        return grades;
    }


    public boolean insertGrade(int teacherUserId, int studentId, double grade) throws SQLException {
        String sql = " INSERT INTO grades (student_id, subject_id, grade) SELECT ?, sub.id, ? FROM subjects sub JOIN teachers t ON sub.teacher_id = t.id WHERE t.user_id = ? LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDouble(2  , grade);
            ps.setInt(3, teacherUserId);
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
