package model;

import java.time.LocalDate;

public class Absence {
    private int id;
    private String studentName;
    private String subjectName;
    private LocalDate date;
    private boolean motivated;

    public Absence(int id, String studentName, String subjectName, LocalDate date, boolean motivated) {
        this.id = id;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.date = date;
        this.motivated = motivated;
    }

    public int getId()              { return id; }
    public String getStudentName()  { return studentName; }
    public String getSubjectName()  { return subjectName; }
    public LocalDate getDate()      { return date; }
    public boolean isMotivated()    { return motivated; }
}
