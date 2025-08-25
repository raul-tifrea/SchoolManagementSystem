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

    public List<Absence> getAbsencesForStudent(int studentId,int subjecttId) throws Exception{
        List<Absence> absences = new ArrayList<>();
        String query = "SELECT a.id, st.name AS student_name, sub.name AS subject_name, a.absence_date\n" +
                "        FROM absences a\n" +
                "        JOIN students st ON a.student_id = st.id\n" +
                "        JOIN subjects sub ON a.subject_id = sub.id\n" +
                "        WHERE st.user_id = ? AND sub.id = ?\n" +
                "        ORDER BY a.absence_date\n";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1,studentId);
            statement.setInt(2,subjecttId);
            try(ResultSet result = statement.executeQuery()){
                while(result.next()){
                   absences.add(new Absence(result.getInt("id"),result.getString("student_name"),
                           result.getString("subject_name"),
                           result.getDate("absence_date").toLocalDate()));
                }
            }
        }
        return absences;
    }

    public List<Absence> getAbsencesForTeacher(int teacherUserId) throws Exception{
        List<Absence> absences = new ArrayList<>();
        String query = "SELECT a.id, st.name AS student_name, sub.name AS subject_name, a.absence_date FROM absences a JOIN students st ON a.student_id = st.id JOIN subjects sub ON a.subject_id = sub.id JOIN teachers t ON sub.teacher_id = t.id WHERE t.user_id = ? ORDER BY st.name, a.absence_date";
        try(Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1,teacherUserId);
            try(ResultSet result = statement.executeQuery()){
                while(result.next()){
                    absences.add(new Absence(result.getInt("id"),
                    result.getString("student_name"),
                    result.getString("subject_name"),
                    result.getDate("absence_date").toLocalDate()));
                }
            }
        }
        return absences;
    }

    public boolean insertAbsence(int teacherId, int studentId, LocalDate date) throws Exception{
        String query = "INSERT INTO absences (student_id, subject_id, absence_date) SELECT ?, sub.id, ? FROM subjects sub JOIN teachers t ON sub.teacher_id = t.id WHERE t.user_id = ? ";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, studentId);
            statement.setDate(2, Date.valueOf(date));
            statement.setInt(3, teacherId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteAbsenceById(int absenceId) throws Exception{
        String query = "DELETE FROM absences WHERE id = ?";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, absenceId);
            return statement.executeUpdate() > 0;
            }
    }


}
