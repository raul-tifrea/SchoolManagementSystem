package dataaccess;

import connection.ConnectionFactory;
import model.User;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public int insertUser(User user, String name, String email,
                          Integer studyYear, String studyGroup) throws Exception {
        Connection connection = ConnectionFactory.getConnection();
        try {
            connection.setAutoCommit(false);


            int userId;
            String hashedPassword = PasswordUtil.hash(user.getPassword());
            String userSql = "INSERT INTO users (username, password, role) VALUES (?,?,?)";
            try (PreparedStatement ps = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, hashedPassword);
                ps.setString(3, user.getRole());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) userId = rs.getInt(1);
                else throw new Exception("Failed to insert user.");
            }


            if ("student".equalsIgnoreCase(user.getRole())) {
                String sql = "INSERT INTO students (user_id, name, email, study_year, study_group) VALUES (?,?,?,?,?)";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    ps.setString(2, name);
                    ps.setString(3, email);
                    ps.setInt(4, studyYear);
                    ps.setString(5, studyGroup);
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
        String sql = "SELECT u.id, u.username, u.password, u.role, sub.name AS subject_name, st.study_year, st.study_group " +
                     "FROM users u " +
                     "LEFT JOIN teachers t ON u.id = t.user_id " +
                     "LEFT JOIN subjects sub ON t.id = sub.teacher_id " +
                     "LEFT JOIN students st ON u.id = st.user_id " +
                     "ORDER BY u.role, u.username";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("role"));
                String extra = rs.getString("subject_name");
                if ("student".equalsIgnoreCase(user.getRole())) {
                    int year = rs.getInt("study_year");
                    String group = rs.getString("study_group");
                    if (!rs.wasNull()) {
                        extra = "Class " + year + group;
                    }
                }
                user.setSubject(extra);
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

    public boolean updateStudentYearGroup(int userId, int studyYear, String studyGroup) throws SQLException {
        String sql = "UPDATE students SET study_year = ?, study_group = ? WHERE user_id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studyYear);
            ps.setString(2, studyGroup);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int userId, String newHashedPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStudentDetails(int userId, String name, String email,
                                        int studyYear, String studyGroup) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, study_year = ?, study_group = ? WHERE user_id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, studyYear);
            ps.setString(4, studyGroup);
            ps.setInt(5, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateTeacherDetails(int userId, String name, String email) throws SQLException {
        String sql = "UPDATE teachers SET name = ?, email = ? WHERE user_id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public String[] getStudentDetails(int userId) throws SQLException {
        String sql = "SELECT name, email, study_year, study_group FROM students WHERE user_id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        rs.getString("name"),
                        rs.getString("email"),
                        String.valueOf(rs.getInt("study_year")),
                        rs.getString("study_group")
                    };
                }
            }
        }
        return null;
    }

    public String[] getTeacherDetails(int userId) throws SQLException {
        String sql = "SELECT name, email FROM teachers WHERE user_id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString("name"), rs.getString("email")};
                }
            }
        }
        return null;
    }

    public boolean updateUsername(int userId, String newUsername) throws SQLException {
        String sql = "UPDATE users SET username = ? WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newUsername);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<String[]> getAllStudentsDetailed() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT u.id, u.username, s.name, s.email, s.study_year, s.study_group " +
                     "FROM users u JOIN students s ON u.id = s.user_id " +
                     "ORDER BY s.study_year, s.study_group, s.name";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getString("email"),
                    String.valueOf(rs.getInt("study_year")),
                    rs.getString("study_group")
                });
            }
        }
        return rows;
    }

    public List<String[]> getAllTeachersDetailed() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT u.id, u.username, t.name, t.email, GROUP_CONCAT(sub.name SEPARATOR ', ') AS subjects " +
                     "FROM users u " +
                     "JOIN teachers t ON u.id = t.user_id " +
                     "LEFT JOIN subjects sub ON t.id = sub.teacher_id " +
                     "GROUP BY u.id, u.username, t.name, t.email " +
                     "ORDER BY t.name";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String subs = rs.getString("subjects");
                rows.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getString("email"),
                    subs != null ? subs : "None"
                });
            }
        }
        return rows;
    }

    public List<String[]> getAllSubjectsDetailed() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.study_year, t.name AS teacher_name " +
                     "FROM subjects s JOIN teachers t ON s.teacher_id = t.id " +
                     "ORDER BY s.study_year, s.name";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    String.valueOf(rs.getInt("study_year")),
                    rs.getString("teacher_name")
                });
            }
        }
        return rows;
    }

    public int[] getSubjectStats(int subjectId) throws SQLException {
        int[] stats = new int[3];
        String enrollSql = "SELECT COUNT(*) FROM enrollments WHERE subject_id = ?";
        String gradeSql  = "SELECT COUNT(*) FROM grades WHERE subject_id = ?";
        String absSql    = "SELECT COUNT(*) FROM absences WHERE subject_id = ?";
        try (Connection c = ConnectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(enrollSql)) {
                ps.setInt(1, subjectId); ResultSet rs = ps.executeQuery();
                if (rs.next()) stats[0] = rs.getInt(1);
            }
            try (PreparedStatement ps = c.prepareStatement(gradeSql)) {
                ps.setInt(1, subjectId); ResultSet rs = ps.executeQuery();
                if (rs.next()) stats[1] = rs.getInt(1);
            }
            try (PreparedStatement ps = c.prepareStatement(absSql)) {
                ps.setInt(1, subjectId); ResultSet rs = ps.executeQuery();
                if (rs.next()) stats[2] = rs.getInt(1);
            }
        }
        return stats;
    }

    public boolean deleteSubject(int subjectId) throws SQLException {
        String sql = "DELETE FROM subjects WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean reassignSubjectTeacher(int subjectId, int newTeacherUserId) throws SQLException {
        String getYearSql = "SELECT study_year FROM subjects WHERE id = ?";
        String checkSql   = "SELECT COUNT(*) FROM subjects s " +
                            "JOIN teachers t ON s.teacher_id = t.id " +
                            "WHERE t.user_id = ? AND s.study_year = ? AND s.id != ?";
        String updateSql  = "UPDATE subjects SET teacher_id = " +
                            "(SELECT id FROM teachers WHERE user_id = ?) WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection()) {
            int year;
            try (PreparedStatement ps = c.prepareStatement(getYearSql)) {
                ps.setInt(1, subjectId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) return false;
                year = rs.getInt("study_year");
            }
            try (PreparedStatement ps = c.prepareStatement(checkSql)) {
                ps.setInt(1, newTeacherUserId);
                ps.setInt(2, year);
                ps.setInt(3, subjectId);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Teacher already has a subject for year " + year + ".");
                }
            }
            try (PreparedStatement ps = c.prepareStatement(updateSql)) {
                ps.setInt(1, newTeacherUserId);
                ps.setInt(2, subjectId);
                return ps.executeUpdate() > 0;
            }
        }
    }
}
