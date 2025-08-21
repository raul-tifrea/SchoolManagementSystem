package model;

public class Grade {
    private int id;
    private int studentid;
    private int subjectid;
    private double grade;

    public Grade(){}

    public Grade(int id, int studentid, int subjectid, double grade) {
        this.id = id;
        this.studentid = studentid;
        this.subjectid = subjectid;
        this.grade = grade;
    }
}
