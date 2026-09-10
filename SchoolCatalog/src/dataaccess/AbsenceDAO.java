package dataaccess;

import connection.ConnectionFactory;
import model.Absence;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AbsenceDAO {

    public List<Absence> getAbsencesForStudent(int userId, int subjectId) throws Exception {
        List<Absence> absences = new ArrayList<>();
        String sql = "SELECT a.id, st.name AS student_name, sub.name AS subject_name, " +
                     "a.absence_date, a.motivated " +
                     "FROM absences a " +
                     "JOIN students st ON a.student_id = st.id " +
                     "JOIN subjects sub ON a.subject_id = sub.id " +
                     "WHERE st.user_id = ? AND a.subject_id = ? " +
                     "ORDER BY a.absence_date";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    absences.add(new Absence(
                            rs.getInt("id"),
                            rs.getString("student_name"),
                            rs.getString("subject_name"),
                            rs.getDate("absence_date").toLocalDate(),
                            rs.getBoolean("motivated")
                    ));
                }
            }
        }
        return absences;
    }

    public List<Absence> getAbsencesForStudentInSubject(int studentId, int subjectId) throws Exception {
        List<Absence> absences = new ArrayList<>();
        String sql = "SELECT a.id, st.name AS student_name, sub.name AS subject_name, " +
                     "a.absence_date, a.motivated " +
                     "FROM absences a " +
                     "JOIN students st ON a.student_id = st.id " +
                     "JOIN subjects sub ON a.subject_id = sub.id " +
                     "WHERE a.student_id = ? AND a.subject_id = ? " +
                     "ORDER BY a.absence_date";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    absences.add(new Absence(
                            rs.getInt("id"),
                            rs.getString("student_name"),
                            rs.getString("subject_name"),
                            rs.getDate("absence_date").toLocalDate(),
                            rs.getBoolean("motivated")
                    ));
                }
            }
        }
        return absences;
    }

    public List<Absence> getAbsencesForSubject(int subjectId) throws Exception {
        List<Absence> absences = new ArrayList<>();
        String sql = "SELECT a.id, st.name AS student_name, sub.name AS subject_name, " +
                     "a.absence_date, a.motivated " +
                     "FROM absences a " +
                     "JOIN students st ON a.student_id = st.id " +
                     "JOIN subjects sub ON a.subject_id = sub.id " +
                     "WHERE a.subject_id = ? " +
                     "ORDER BY st.study_group, st.name, a.absence_date";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    absences.add(new Absence(
                            rs.getInt("id"),
                            rs.getString("student_name"),
                            rs.getString("subject_name"),
                            rs.getDate("absence_date").toLocalDate(),
                            rs.getBoolean("motivated")
                    ));
                }
            }
        }
        return absences;
    }

    public boolean insertAbsence(int studentId, int subjectId, LocalDate date) throws Exception {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Absence date cannot be in the future.");
        }
        String sql = "INSERT INTO absences (student_id, subject_id, absence_date) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setDate(3, Date.valueOf(date));
            return ps.executeUpdate() > 0;
        }
    }

    public boolean motivateAbsence(int absenceId) throws Exception {
        String sql = "UPDATE absences SET motivated = TRUE WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, absenceId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteAbsenceById(int absenceId) throws Exception {
        String sql = "DELETE FROM absences WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, absenceId);
            return ps.executeUpdate() > 0;
        }
    }
}
