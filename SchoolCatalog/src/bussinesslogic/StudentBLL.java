package bussinesslogic;

import dataaccess.GradeDAO;
import dataaccess.SubjectDAO;
import model.Grade;
import model.Subject;

import java.sql.SQLException;
import java.util.List;

public class StudentBLL {
    private static GradeDAO gradeDAO;
    private static SubjectDAO subjectDAO;

    public StudentBLL() {
        this.gradeDAO = new GradeDAO();
        this.subjectDAO = new SubjectDAO();
    }

    public static List<Grade> loadGrades(int studentId, int subjectId) throws SQLException {
        return gradeDAO.getGradesForStudent(studentId,subjectId);
    }
}
