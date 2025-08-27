package model;

import java.time.LocalDate;

public class Absence {
    private int id;
    private String studentname;
    private String subjectname;
    private LocalDate date;

    public Absence(int id, String studentname, String subjectname, LocalDate date){
        this.id = id;
        this.studentname = studentname;
        this.subjectname = subjectname;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getSubjectname(){
        return subjectname;
    }

    public LocalDate getDate(){
        return date;
    }
}
