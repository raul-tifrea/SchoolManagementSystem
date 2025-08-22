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
        String query = "SELECT * FROM users";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery()) {
            while(result.next()){
                users.add(new User(result.getInt("id"),result.getString("username"),result.getString("password"),result.getString("role")));
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

    public void SubjecttoTeacher(int userId, String subjectname) throws SQLException{
        String query = "INSERT INTO subjects (name, teacher_id) SELECT ?, t.id FROM teachers t WHERE t.user_id = ?";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, subjectname);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    public List<String[]> getUsersWithDetails() throws SQLException {
        List<String[]> users = new ArrayList<>();
        String query = "SELECT u.id, u.username, u.role, " +
                "s.name AS student_name, s.email, " +
                "t.name AS teacher_name " +
                "FROM users u " +
                "LEFT JOIN students s ON u.id = s.user_id " +
                "LEFT JOIN teachers t ON u.id = t.user_id";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("id"));
                String username = rs.getString("username");
                String role = rs.getString("role");
                String name = (rs.getString("student_name") != null)
                        ? rs.getString("student_name")
                        : rs.getString("teacher_name");
                String email = rs.getString("email") != null ? rs.getString("email") : "-";

                users.add(new String[]{id, username, role, name, email});
            }
        }
        return users;
    }


}
