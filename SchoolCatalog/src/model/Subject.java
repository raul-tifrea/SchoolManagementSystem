package model;

public class Subject {
    private int id;
    private String name;
    private int studyYear;

    public Subject(int id, String name, int studyYear) {
        this.id = id;
        this.name = name;
        this.studyYear = studyYear;
    }

    public int getId()         { return id; }
    public int getStudyYear()  { return studyYear; }

    @Override
    public String toString() {
        return name + " (Year " + studyYear + ")";
    }
}
