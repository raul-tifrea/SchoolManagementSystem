package presentation;

import model.Student;
import model.Subject;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class AdminView extends JPanel {

    // --- Users table ---
    private JTable usersTable;
    private DefaultTableModel usersModel;

    // --- Add user form ---
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> roleBox;
    private JSpinner yearSpinner;
    private JSpinner groupSpinner;
    private JLabel yearLabel;
    private JLabel groupLabel;
    private JButton addButton;
    private JButton deleteButton;

    // --- Subject assignment ---
    private JButton setSubjectTeacher;

    // --- Enrollment tab ---
    private JComboBox<Subject> enrollSubjectBox;
    private JComboBox<Student> enrollStudentBox;
    private JButton enrollButton;
    private JButton enrollAllButton;
    private JButton removeEnrollButton;
    private JTable enrollmentsTable;
    private DefaultTableModel enrollmentsModel;

    public AdminView() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        // ── Tab 1: Users ──────────────────────────────────────────────────────
        JPanel usersPanel = new JPanel(new BorderLayout());

        Object[] cols = {"ID", "Username", "Role", "Subject / Year-Group"};
        usersModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        usersTable = new JTable(usersModel);
        styleTable(usersTable);
        usersTable.getColumnModel().getColumn(0).setMinWidth(0);
        usersTable.getColumnModel().getColumn(0).setMaxWidth(0); // hide ID column
        usersPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        // Add-user form
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        formPanel.setBackground(new Color(98, 0, 205));

        usernameField = new JTextField(8); styleField(usernameField);
        passwordField = new JTextField(8); styleField(passwordField);
        nameField     = new JTextField(8); styleField(nameField);
        emailField    = new JTextField(8); styleField(emailField);

        roleBox = new JComboBox<>(new String[]{"student", "teacher"});
        roleBox.setFont(new Font("", Font.PLAIN, 13));
        roleBox.setPreferredSize(new Dimension(90, 28));

        yearSpinner  = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        groupSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        yearSpinner.setPreferredSize(new Dimension(50, 28));
        groupSpinner.setPreferredSize(new Dimension(50, 28));

        yearLabel  = makeLabel("Year");
        groupLabel = makeLabel("Group");

        addButton    = makeButton("Add User");
        deleteButton = makeButton("Delete User");
        setSubjectTeacher = makeButton("Assign Subject");

        formPanel.add(makeLabel("Username")); formPanel.add(usernameField);
        formPanel.add(makeLabel("Password")); formPanel.add(passwordField);
        formPanel.add(makeLabel("Name"));     formPanel.add(nameField);
        formPanel.add(makeLabel("Email"));    formPanel.add(emailField);
        formPanel.add(makeLabel("Role"));     formPanel.add(roleBox);
        formPanel.add(yearLabel);             formPanel.add(yearSpinner);
        formPanel.add(groupLabel);            formPanel.add(groupSpinner);
        formPanel.add(addButton);
        formPanel.add(deleteButton);
        formPanel.add(setSubjectTeacher);

        usersPanel.add(formPanel, BorderLayout.SOUTH);

        // Show/hide year+group spinners based on role
        roleBox.addActionListener(e -> {
            boolean isStudent = "student".equals(roleBox.getSelectedItem());
            yearLabel.setVisible(isStudent);
            yearSpinner.setVisible(isStudent);
            groupLabel.setVisible(isStudent);
            groupSpinner.setVisible(isStudent);
        });
        // Initial state: student selected by default
        yearLabel.setVisible(true); yearSpinner.setVisible(true);
        groupLabel.setVisible(true); groupSpinner.setVisible(true);

        // ── Tab 2: Enrollments ────────────────────────────────────────────────
        JPanel enrollPanel = new JPanel(new BorderLayout());

        Object[] eCols = {"Student", "Subject"};
        enrollmentsModel = new DefaultTableModel(eCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        enrollmentsTable = new JTable(enrollmentsModel);
        styleTable(enrollmentsTable);
        enrollPanel.add(new JScrollPane(enrollmentsTable), BorderLayout.CENTER);

        JPanel enrollFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        enrollFormPanel.setBackground(new Color(98, 0, 205));

        enrollSubjectBox = new JComboBox<>();
        enrollSubjectBox.setPreferredSize(new Dimension(160, 28));
        enrollSubjectBox.setFont(new Font("", Font.PLAIN, 13));

        enrollStudentBox = new JComboBox<>();
        enrollStudentBox.setPreferredSize(new Dimension(160, 28));
        enrollStudentBox.setFont(new Font("", Font.PLAIN, 13));

        enrollButton       = makeButton("Enroll Student");
        enrollAllButton    = makeButton("Enroll All (Year)");
        removeEnrollButton = makeButton("Remove Enrollment");

        enrollFormPanel.add(makeLabel("Subject")); enrollFormPanel.add(enrollSubjectBox);
        enrollFormPanel.add(makeLabel("Student")); enrollFormPanel.add(enrollStudentBox);
        enrollFormPanel.add(enrollButton);
        enrollFormPanel.add(enrollAllButton);
        enrollFormPanel.add(removeEnrollButton);
        enrollPanel.add(enrollFormPanel, BorderLayout.SOUTH);

        tabs.addTab("Users", usersPanel);
        tabs.addTab("Enrollments", enrollPanel);
        add(tabs, BorderLayout.CENTER);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void styleTable(JTable t) {
        t.setFont(new Font("", Font.PLAIN, 13));
        t.setRowHeight(28);
        t.setShowGrid(false);
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("", Font.BOLD, 14));
        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < t.getColumnCount(); i++) t.getColumnModel().getColumn(i).setCellRenderer(cr);
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("", Font.BOLD, 13));
        l.setForeground(Color.WHITE);
        return l;
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("", Font.PLAIN, 13));
        b.setPreferredSize(new Dimension(150, 36));
        b.setBorder(BorderFactory.createSoftBevelBorder(0));
        return b;
    }

    // ── Public API ────────────────────────────────────────────────────────────
    public void updateUsersTable(List<User> users) {
        usersModel.setRowCount(0);
        for (User u : users) {
            String extra = "-";
            if ("teacher".equalsIgnoreCase(u.getRole()) && u.getSubject() != null)
                extra = u.getSubject();
            usersModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole(), extra});
        }
    }

    /** Returns the user ID from the hidden column 0 of the selected row, or -1. */
    public int getSelectedUserId() {
        int row = usersTable.getSelectedRow();
        return (row == -1) ? -1 : (int) usersTable.getValueAt(row, 0);
    }

    public void updateEnrollmentsTable(List<String[]> rows) {
        enrollmentsModel.setRowCount(0);
        for (String[] r : rows) enrollmentsModel.addRow(r);
    }

    public void populateSubjectBox(List<Subject> subjects) {
        enrollSubjectBox.removeAllItems();
        for (Subject s : subjects) enrollSubjectBox.addItem(s);
    }

    public void populateStudentBox(List<Student> students) {
        enrollStudentBox.removeAllItems();
        for (Student s : students) enrollStudentBox.addItem(s);
    }

    public Subject getSelectedEnrollSubject() { return (Subject) enrollSubjectBox.getSelectedItem(); }
    public Student getSelectedEnrollStudent() { return (Student) enrollStudentBox.getSelectedItem(); }

    public void clearForm() {
        usernameField.setText(""); passwordField.setText("");
        nameField.setText("");     emailField.setText("");
        roleBox.setSelectedIndex(0);
        yearSpinner.setValue(1);   groupSpinner.setValue(1);
    }

    // Getters for controller
    public JButton getAddButton()              { return addButton; }
    public JButton getDeleteButton()           { return deleteButton; }
    public JButton getSetSubjectTeacherButton(){ return setSubjectTeacher; }
    public JButton getEnrollButton()           { return enrollButton; }
    public JButton getEnrollAllButton()        { return enrollAllButton; }
    public JButton getRemoveEnrollButton()     { return removeEnrollButton; }
    public JComboBox<Subject> getEnrollSubjectBox() { return enrollSubjectBox; }

    public String getUsername() { return usernameField.getText().trim(); }
    public String getPassword() { return passwordField.getText().trim(); }
    public String getName()     { return nameField.getText().trim(); }
    public String getEmail()    { return emailField.getText().trim(); }
    public String getRole()     { return (String) roleBox.getSelectedItem(); }
    public int    getYear()     { return (int) yearSpinner.getValue(); }
    public int    getGroup()    { return (int) groupSpinner.getValue(); }
}
