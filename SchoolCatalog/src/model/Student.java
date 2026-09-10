package model;

public class Student {
    private int id;
    private String name;
    private String email;
    private int studyYear;   // 9-12
    private String studyGroup;  // A, B, C

    public Student(int id, String name, String email, int studyYear, String studyGroup) {
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
    public String getStudyGroup()  { return studyGroup; }

    @Override
    public String toString() {
        return name + " (" + studyYear + studyGroup + ")";
    }
}
