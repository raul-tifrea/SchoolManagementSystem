package dataaccess;

import connection.ConnectionFactory;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public int insertUser(User user, String name, String email,
                          Integer studyYear, Integer studyGroup) throws Exception {
        Connection connection = ConnectionFactory.getConnection();
        try {
            connection.setAutoCommit(false);

            // 1. Insert into users
            int userId;
            String userSql = "INSERT INTO users (username, password, role) VALUES (?,?,?)";
            try (PreparedStatement ps = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getRole());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) userId = rs.getInt(1);
                else throw new Exception("Failed to insert user.");
            }

            // 2. Insert into role-specific table
            if ("student".equalsIgnoreCase(user.getRole())) {
                String sql = "INSERT INTO students (user_id, name, email, study_year, study_group) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    ps.setString(2, name);
                    ps.setString(3, email);
                    ps.setInt(4, studyYear);
                    ps.setInt(5, studyGroup);
                    ps.executeUpdate();
                }
            } else if ("teacher".equalsIgnoreCase(user.getRole())) {
                String sql = "INSERT INTO teachers (user_id, name, email) VALUES (?,?,?)";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    ps.setString(2, name);
                    ps.setString(3, email);
                    ps.executeUpdate();
                }
            }

            connection.commit();
            return userId;

        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.password, u.role, sub.name AS subject_name " +
                     "FROM users u " +
                     "LEFT JOIN teachers t ON u.id = t.user_id " +
                     "LEFT JOIN subjects sub ON t.id = sub.teacher_id " +
                     "ORDER BY u.role, u.username";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("role"));
                user.setSubject(rs.getString("subject_name"));
                users.add(user);
            }
        }
        return users;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"),
                            rs.getString("password"), rs.getString("role"));
                }
            }
        }
        return null;
    }

    public boolean assignSubjectToTeacher(int userId, String subjectName, int studyYear) throws SQLException {
        String sql = "INSERT INTO subjects (name, teacher_id, study_year) " +
                     "SELECT ?, t.id, ? FROM teachers t WHERE t.user_id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, subjectName);
            ps.setInt(2, studyYear);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean enrollStudent(int studentId, int subjectId) throws SQLException {
        String sql = "INSERT IGNORE INTO enrollments (student_id, subject_id) VALUES (?, ?)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            return ps.executeUpdate() > 0;
        }
    }

    public int enrollAllStudentsInYear(int subjectId, int studyYear) throws SQLException {
        String sql = "INSERT IGNORE INTO enrollments (student_id, subject_id) " +
                     "SELECT s.id, ? FROM students s WHERE s.study_year = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ps.setInt(2, studyYear);
            return ps.executeUpdate();
        }
    }

    public boolean removeEnrollment(int studentId, int subjectId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND subject_id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStudentYearGroup(int userId, int studyYear, int studyGroup) throws SQLException {
        String sql = "UPDATE students SET study_year = ?, study_group = ? WHERE user_id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studyYear);
            ps.setInt(2, studyGroup);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }
}
