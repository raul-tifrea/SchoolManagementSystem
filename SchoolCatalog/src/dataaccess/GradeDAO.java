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
        String query = "SELECT g.id, st.name AS student_name, sub.name AS subject, g.grade " +
                "FROM grades g JOIN students st ON g.student_id = st.id JOIN subjects sub ON g.subject_id = sub.id JOIN teachers t ON sub.teacher_id = t.id WHERE t.user_id = ? ORDER BY st.name, sub.name";
        try(Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1,teacherId);
            try(ResultSet result = statement.executeQuery()){
                while(result.next()){
                    grades.add(new Grade(
                            result.getInt("id"),
                            result.getString("student_name"),
                            result.getString("subject"),
                            result.getDouble("grade")));
                }
            }

        }
        return grades;
    }


    public boolean insertGradeForTeacher(int teacherId, int studentId, int subjectId,double grade) throws SQLException {
        String query = "INSERT INTO grades (student_id, subject_id, grade) SELECT ?, ?, ? WHERE EXISTS ( SELECT 1 FROM subjects sub JOIN teachers t ON sub.teacher_id = t.id WHERE sub.id = ? AND t.user_id = ? )";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);
            statement.setInt(2, subjectId);
            statement.setDouble(3, grade);
            statement.setInt(4, subjectId);
            statement.setInt(5, teacherId);
            return statement.executeUpdate() > 0;

        }
    }

    public boolean deleteGradeForTeacher(int teacherId, int gradeId) throws SQLException {
        String query = "DELETE g FROM grades g JOIN subjects sub ON g.subject_id = sub.id JOIN teachers t ON sub.teacher_id = t.id WHERE g.id = ? AND t.user_id = ?";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, gradeId);
            statement.setInt(2, teacherId);
            return statement.executeUpdate() > 0;
        }
    }



}
