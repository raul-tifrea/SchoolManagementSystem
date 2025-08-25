package dataaccess;

import connection.ConnectionFactory;

import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public int insertUser(User user) throws Exception{
        String query = "INSERT INTO users (username, password, role) VALUES (?,?,?)";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if(rs.next())
                return rs.getInt(1);
            else
                throw new Exception("Error inserting user");
        }

    }


    public void insertStudent(int userId, String name, String email) throws Exception{
        String query = "INSERT INTO students (user_id, name, email) VALUES (?,?,?)";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, name);
            statement.setString(3, email);
            statement.executeUpdate();
        }

    }


    public void insertTeacher(int userId, String name, String email) throws SQLException{
        String query = "INSERT INTO teachers (user_id, name, email) VALUES (?,?,?)";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, name);
            statement.setString(3, email);
            statement.executeUpdate();
            }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.id, u.username, u.password, u.role,sub.name AS subject_name FROM users u LEFT JOIN teachers t ON u.id = t.user_id LEFT JOIN subjects sub ON t.id = sub.teacher_id\n";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery()) {
            while(result.next()){
                User user = new User(result.getInt("id"),result.getString("username"),result.getString("password"),result.getString("role"));
                user.setSubject(result.getString("subject_name"));
                users.add(user);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return users;

    }

    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();
            if(result.next()){
                return new User(result.getInt("id"),result.getString("username"),result.getString("password"),result.getString("role"));

            }
        }
        return null;
    }

    public boolean SubjecttoTeacher(int userId, String subjectname) throws SQLException{
        String query = "INSERT INTO subjects (name, teacher_id) SELECT ?, t.id FROM teachers t WHERE t.user_id = ? AND NOT EXISTS (SELECT 1 FROM subjects WHERE name = ?)";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, subjectname);
            statement.setInt(2, userId);
            statement.setString(3, subjectname);
            return statement.executeUpdate() > 0;
        }
    }



}
