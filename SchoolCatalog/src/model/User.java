package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String Subject;
    public User(){}

    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return  username;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String Subject) {
        this.Subject = Subject;
    }

    public String getPassword() {
        return password;
    }
    public String getRole() {
        return role;
    }
    public int getId() {
        return id;
    }

}
