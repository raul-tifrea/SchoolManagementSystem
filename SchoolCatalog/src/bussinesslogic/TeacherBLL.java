package bussinesslogic;

import dataaccess.GradeDAO;
import dataaccess.StudentDAO;

import java.sql.SQLException;

public class TeacherBLL {
    private static GradeDAO gradeDAO;

    public TeacherBLL() {
        this.gradeDAO = new GradeDAO();
    }

    public boolean addGrades(int teacherId, int studentId, double grade) throws SQLException {
        if(grade < 1 || grade > 10)
            throw new IllegalArgumentException("Grade must be between 1 and 10");
        return gradeDAO.insertGrade(teacherId,studentId,grade);
    }

}
