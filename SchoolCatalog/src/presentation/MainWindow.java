package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import dataaccess.UserDAO;
import model.User;
import presentation.AdminView;
import presentation.StudentView;
import presentation.TeacherView;
import presentation.AdminController;

public class MainWindow extends JFrame {

    private JTabbedPane MainPane;
    private AdminView AdminView;
    private StudentView studentView;
    private TeacherView teacherView;

    private JPanel loginPanel;
    private JComboBox<String> roleBox;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public MainWindow(){
        setTitle("School Catalog");
        setSize(1600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        loginPanel = createNewLoginPanel();
        loginPanel.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        add(mainPanel);


    }

    private JPanel createNewLoginPanel(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(48, 24, 78));

        JPanel topBar = new JPanel();
        topBar.setBackground(new Color(102, 0, 153));
        topBar.setPreferredSize(new Dimension(1600, 40));
        panel.add(topBar, BorderLayout.NORTH);

        JPanel leftBar = new JPanel();
        leftBar.setBackground(new Color(153, 0, 204));
        leftBar.setPreferredSize(new Dimension(90, 800));
        panel.add(leftBar, BorderLayout.WEST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(241, 241, 241));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 30);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("School Catalog");
        titleLabel.setFont(new Font("Lucida Bright", Font.BOLD, 70));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, c);

        c.gridwidth = 1;

        c.gridy = 1;
        c.gridx = 1;
        c.anchor = GridBagConstraints.CENTER;
        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font("", Font.PLAIN, 14));
        formPanel.add(roleLabel, c);
        c.gridy = 2;

        roleBox = new JComboBox<>(new String[]{"Admin", "Teacher", "Student"});
        roleBox.setPreferredSize(new Dimension(260, 30));
        roleBox.setFont(new Font("", Font.PLAIN, 14));
        roleBox.setBorder(BorderFactory.createSoftBevelBorder(1));

        formPanel.add(roleBox, c);
        c.gridy = 3;

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("", Font.PLAIN, 14));
        formPanel.add(usernameLabel, c);
        c.gridy = 4;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(260, 30));
        usernameField.setBorder(BorderFactory.createSoftBevelBorder(1));
        formPanel.add(usernameField, c);

        c.gridy = 5;

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("", Font.PLAIN, 14));
        formPanel.add(passwordLabel, c);
        c.gridy = 6;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(260, 30));
        passwordField.setBorder(BorderFactory.createSoftBevelBorder(1));
        formPanel.add(passwordField, c);

        c.gridy = 7;
        c.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("", Font.PLAIN, 14));
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.setBorder(BorderFactory.createSoftBevelBorder(0));
        formPanel.add(loginButton, c);

        panel.add(formPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e){
                try {
                    login();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        usernameField.setFont(new Font("", Font.PLAIN, 14));
        passwordField.setFont(new Font("", Font.PLAIN, 14));
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    login();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        return panel;
    }

    private void login() throws SQLException {
        String role = (String) roleBox.getSelectedItem();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if(username.isEmpty() || password.isEmpty()){

            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        try {
            User user = userDAO.login(username, password, role);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Login successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                roleview(role.toLowerCase(),user.getId());
            }
        } catch (HeadlessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void roleview(String role, int userId) throws SQLException {
        getContentPane().removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder());
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(new Color(48, 24, 78));
        logoutPanel.setBorder(BorderFactory.createEmptyBorder());
        JButton logoutButton = new JButton("Logout");
        logoutPanel.add(logoutButton);
        mainPanel.add(logoutPanel, BorderLayout.SOUTH);

        MainPane = new JTabbedPane();
        MainPane.setBorder(BorderFactory.createEmptyBorder());


        if(role.equals("admin")){
            if(AdminView == null){
                AdminView = new AdminView();
                new AdminController(AdminView);


            }
            MainPane.addTab("Admin", AdminView);
        } else if(role.equals("teacher")){
            if(teacherView == null){
                teacherView = new TeacherView();
                 new TeacherController(teacherView,userId);

            }
            MainPane.addTab("Teacher", teacherView);
        } else if(role.equals("student")){
            if(studentView == null){
                studentView = new StudentView();
                new StudentController(studentView,userId);
            }
            MainPane.addTab("Student", studentView);
        }

        mainPanel.add(MainPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        revalidate();
        repaint();


    }

    private void logout(){
        getContentPane().removeAll();
        JPanel mainPanel = new JPanel(new BorderLayout());

        loginPanel = createNewLoginPanel();
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        add(mainPanel);

        AdminView = null;
        studentView = null;
        teacherView = null;

        revalidate();
        repaint();

    }
}
