package presentation;

import model.Subject;
import model.User;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class AdminView extends JPanel{

    private JTable table;
    private DefaultTableModel model;

    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField nameField;
    private JTextField emailField;
    private JButton addButton;
    private JButton deleteButton;
    private JComboBox<String> roleBox;
    private JButton setSubjectTeacher;

    public AdminView(){
        setLayout(new BorderLayout());
        Object[] cols = new Object[]{"Username","Role","Subject"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        table.setBackground(new Color(255, 255, 255));
        table.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
        table.setFont(new Font("", Font.PLAIN, 14));
        table.setRowHeight(30);
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("", Font.BOLD, 16));
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.setBackground(new Color(98, 0, 205));
        usernameField = new JTextField(8);
        usernameField.setFont(new Font("", Font.PLAIN, 14));

        usernameField.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));

        passwordField = new JTextField(8);
        passwordField.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));
        passwordField.setFont(new Font("", Font.PLAIN, 14));


        nameField = new JTextField(8);
        nameField.setFont(new Font("", Font.PLAIN, 14));

        nameField.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));

        emailField = new JTextField(8);
        emailField.setFont(new Font("", Font.PLAIN, 14));

        emailField.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));

        roleBox = new JComboBox<>(new String[]{"student","teacher"});
        roleBox.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));
        roleBox.setPreferredSize(new Dimension(100, 20));
        roleBox.setFont(new Font("", Font.PLAIN, 14));

        addButton = new JButton("Add User");
        addButton.setBorder(BorderFactory.createSoftBevelBorder(0));
        addButton.setPreferredSize(new Dimension(130, 40));
        addButton.setFont(new Font("", Font.PLAIN, 14));


        deleteButton = new JButton("Delete User");
        deleteButton.setBorder(BorderFactory.createSoftBevelBorder(0));
        deleteButton.setPreferredSize(new Dimension(130, 40));
        deleteButton.setFont(new Font("", Font.PLAIN, 14));

        setSubjectTeacher = new JButton("Set Subject Teacher");
        setSubjectTeacher.setBorder(BorderFactory.createSoftBevelBorder(0));
        setSubjectTeacher.setPreferredSize(new Dimension(150, 40));
        setSubjectTeacher.setFont(new Font("", Font.PLAIN, 14));

        JLabel username = new JLabel("Username");
        username.setFont(new Font("", Font.BOLD, 14));
        username.setForeground(Color.WHITE);
        formPanel.add(username);
        formPanel.add(usernameField);

        JLabel password = new JLabel("Password");
        password.setFont(new Font("", Font.BOLD, 14));
        password.setForeground(Color.WHITE);
        formPanel.add(password);
        formPanel.add(passwordField);

        JLabel name = new JLabel("Name");
        name.setFont(new Font("", Font.BOLD, 14));
        name.setForeground(Color.WHITE);
        formPanel.add(name);
        formPanel.add(nameField);

        JLabel email = new JLabel("Email");
        email.setFont(new Font("", Font.BOLD, 14));
        email.setForeground(Color.WHITE);
        formPanel.add(email);
        formPanel.add(emailField);

        JLabel role = new JLabel("Role");
        role.setFont(new Font("", Font.BOLD, 14));
        role.setForeground(Color.WHITE);
        formPanel.add(role);
        formPanel.add(roleBox);
        formPanel.add(addButton);
        formPanel.add(deleteButton);
        formPanel.add(setSubjectTeacher);

        add(formPanel, BorderLayout.SOUTH);

    }

    public JButton getSetSubjectTeacherButton(){
        return setSubjectTeacher;
    }

    public int getSelectedUserId(){
        int row = table.getSelectedRow();
        if(row == -1){
            return -1;
        }
        return (int) table.getValueAt(row, 0);
    }

    public void updateTable(List<User> users) {
        model.setRowCount(0);
        for (User u : users) {
            String subject = (u.getSubject() != null) ? u.getSubject() : "-";
            model.addRow(new Object[]{u.getUsername(), u.getRole(), subject});
        }
    }

    public void clearForm(){
        usernameField.setText("");
        passwordField.setText("");
        nameField.setText("");
        emailField.setText("");
        roleBox.setSelectedIndex(0);
    }
    public JButton getAddButton() {
        return addButton;
    }
    public JButton getDeleteButton() {
        return deleteButton;
    }

    public String getUsername() {
        return usernameField.getText();
    }
    public String getPassword() {
        return passwordField.getText();
    }

    public String getName() {
        return nameField.getText();
    }
    public String getEmail() {
        return emailField.getText();
    }
    public String getRole() {
        return (String) roleBox.getSelectedItem();
    }

}
