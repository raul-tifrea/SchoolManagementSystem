package dataaccess;

import connection.ConnectionFactory;
import model.Grade;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO{

    private final Connection connection;

    public StudentDAO(Connection connection){
        this.connection = connection;
    }

//    public List<Student> getStudents(int teacherid) throws SQLException {
//        List<Student> students = new ArrayList<>();
//        String query = "SELECT st.id, st.name FROM students st JOIN grades g ON st.id = g.student_id JOIN subjects sub ON g.subject_id = sub.id JOIN teachers t ON sub.teacher_id = t_id WHERE t.user_id = ? ORDER BY st.name";
//        try(PreparedStatement statement = connection.prepareStatement(query)){
//            statement.setInt(1,teacherid);
//            ResultSet result = statement.executeQuery();
//            while(result.next()){
//                students.add(new Student(result.getInt("id"),result.getString("name"));
//            }
//        }
//    }




}
