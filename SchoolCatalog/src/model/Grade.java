package model;

public class Grade {
    private int id;
    private String studentName;
    private String subjectName;
    private double grade;
    private String gradeDate;

    public Grade(int id, String studentName, String subjectName, double grade, String gradeDate) {
        this.id = id;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.grade = grade;
        this.gradeDate = gradeDate;
    }

    public int getId()              { return id; }
    public String getStudentName()  { return studentName; }
    public String getSubjectName()  { return subjectName; }
    public double getValue()        { return grade; }
    public String getGradeDate()    { return gradeDate != null ? gradeDate : ""; }
}
