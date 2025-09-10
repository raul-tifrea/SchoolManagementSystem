package model;

public class Grade {
    private int id;
    private String student_name;
    private String subject;
    private double grade;


    public Grade(int id, String student_name, String subject, double grade) {
        this.id = id;
        this.student_name = student_name;
        this.subject = subject;
        this.grade = grade;
    }


    public int getId() {
        return id;
    }

    public String getStudentName(){
        return student_name;
    }

    public double getValue(){
        return grade;
    }
}
