package dataaccess;

import connection.ConnectionFactory;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<Student> getStudentsForSubject(int subjectId) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT st.id, st.name, st.email, st.study_year, st.study_group " +
                     "FROM students st " +
                     "JOIN enrollments e ON e.student_id = st.id " +
                     "WHERE e.subject_id = ? " +
                     "ORDER BY st.study_group, st.name";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getInt("study_year"),
                            rs.getString("study_group")
                    ));
                }
            }
        }
        return students;
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, name, email, study_year, study_group FROM students ORDER BY study_year, study_group, name";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("study_year"),
                        rs.getString("study_group")
                ));
            }
        }
        return students;
    }
}
