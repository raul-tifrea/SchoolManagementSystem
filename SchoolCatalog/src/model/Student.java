package model;

public class Student {
    private int id;
    private String name;
    private String email;
    private int studyYear;   // 1-4
    private int studyGroup;  // 1-5

    public Student(int id, String name, String email, int studyYear, int studyGroup) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.studyYear = studyYear;
        this.studyGroup = studyGroup;
    }

    public int getId()          { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public int getStudyYear()   { return studyYear; }
    public int getStudyGroup()  { return studyGroup; }

    @Override
    public String toString() {
        return name + " (Yr" + studyYear + " Gr" + studyGroup + ")";
    }
}
