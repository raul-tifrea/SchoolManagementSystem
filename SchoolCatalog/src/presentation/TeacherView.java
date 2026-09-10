package presentation;

import model.Absence;
import model.Grade;
import model.Student;
import model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TeacherView extends JPanel {

    // Subject selector (at the top, shared across tabs)
    private JComboBox<Subject> subjectCombo;

    // Grades tab
    private JTable gradesTable;
    private DefaultTableModel gradesModel;
    private JComboBox<Student> studentCombo;
    private JTextField gradeField;
    private JButton addGradeButton;
    private Map<String, List<Integer>> gradeIdMap = new HashMap<>();

    // Absences tab
    private JTable absencesTable;
    private DefaultTableModel absencesModel;
    private JComboBox<Student> absenceStudentCombo;
    private JButton addAbsenceButton;
    private JButton deleteAbsenceButton;
    private JButton motivateAbsenceButton;

    public TeacherView() {
        setLayout(new BorderLayout());

        // ── Subject selector bar (top) ────────────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        topBar.setBackground(new Color(55, 0, 110));
        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setFont(new Font("", Font.BOLD, 14));
        subjectLabel.setForeground(Color.WHITE);
        subjectCombo = new JComboBox<>();
        subjectCombo.setPreferredSize(new Dimension(200, 30));
        subjectCombo.setFont(new Font("", Font.PLAIN, 13));
        topBar.add(subjectLabel);
        topBar.add(subjectCombo);
        add(topBar, BorderLayout.NORTH);

        // ── Tabs ──────────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();

        // ── Grades tab ────────────────────────────────────────────────────────
        JPanel gradesPanel = new JPanel(new BorderLayout());
        gradesModel = new DefaultTableModel(new Object[]{"Student", "Group", "Grades", "Average"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        gradesTable = new JTable(gradesModel);
        styleTable(gradesTable);
        gradesPanel.add(new JScrollPane(gradesTable), BorderLayout.CENTER);

        JPanel gradesForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        gradesForm.setBackground(new Color(75, 2, 133));
        studentCombo = new JComboBox<>();
        studentCombo.setPreferredSize(new Dimension(170, 28));
        studentCombo.setFont(new Font("", Font.PLAIN, 13));
        gradeField = new JTextField(6);
        gradeField.setFont(new Font("", Font.PLAIN, 13));
        gradeField.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        addGradeButton = makeButton("Add Grade");

        gradesForm.add(makeLabel("Student")); gradesForm.add(studentCombo);
        gradesForm.add(makeLabel("Grade (1-10)")); gradesForm.add(gradeField);
        gradesForm.add(addGradeButton);
        gradesPanel.add(gradesForm, BorderLayout.SOUTH);

        // ── Absences tab ──────────────────────────────────────────────────────
        JPanel absencesPanel = new JPanel(new BorderLayout());
        absencesModel = new DefaultTableModel(new Object[]{"Student", "Group", "Date", "Motivated"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        absencesTable = new JTable(absencesModel);
        styleTable(absencesTable);
        absencesPanel.add(new JScrollPane(absencesTable), BorderLayout.CENTER);

        JPanel absenceTopBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        absenceStudentCombo = new JComboBox<>();
        absenceStudentCombo.setPreferredSize(new Dimension(170, 28));
        absenceStudentCombo.setFont(new Font("", Font.PLAIN, 13));
        absenceTopBar.add(makeLabel("Filter by student:"));
        absenceTopBar.add(absenceStudentCombo);
        absencesPanel.add(absenceTopBar, BorderLayout.NORTH);

        JPanel absenceForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        absenceForm.setBackground(new Color(75, 2, 133));
        addAbsenceButton    = makeButton("Add Absence");
        deleteAbsenceButton = makeButton("Delete Absence");
        motivateAbsenceButton = makeButton("Mark Motivated");
        absenceForm.add(addAbsenceButton);
        absenceForm.add(deleteAbsenceButton);
        absenceForm.add(motivateAbsenceButton);
        absencesPanel.add(absenceForm, BorderLayout.SOUTH);

        tabs.addTab("Grades", gradesPanel);
        tabs.addTab("Absences", absencesPanel);
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

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("", Font.BOLD, 13));
        l.setForeground(Color.WHITE);
        return l;
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("", Font.PLAIN, 13));
        b.setPreferredSize(new Dimension(145, 34));
        b.setBorder(BorderFactory.createSoftBevelBorder(0));
        return b;
    }

    // ── Subject combo ─────────────────────────────────────────────────────────
    public void setSubjectCombo(List<Subject> subjects) {
        subjectCombo.removeAllItems();
        for (Subject s : subjects) subjectCombo.addItem(s);
    }
    public Subject getSelectedSubject()  { return (Subject) subjectCombo.getSelectedItem(); }
    public JComboBox<Subject> getSubjectCombo() { return subjectCombo; }

    // ── Student combos ────────────────────────────────────────────────────────
    public void setStudentCombos(List<Student> students) {
        studentCombo.removeAllItems();
        absenceStudentCombo.removeAllItems();
        for (Student s : students) {
            studentCombo.addItem(s);
            absenceStudentCombo.addItem(s);
        }
    }
    public Student getSelectedStudent()        { return (Student) studentCombo.getSelectedItem(); }
    public Student getSelectedAbsenceStudent() { return (Student) absenceStudentCombo.getSelectedItem(); }
    public JComboBox<Student> getAbsenceStudentCombo() { return absenceStudentCombo; }

    // ── Grade field ───────────────────────────────────────────────────────────
    public double getGrade() { return Double.parseDouble(gradeField.getText().trim()); }
    public void clearGradeForm() {
        if (studentCombo.getItemCount() > 0) studentCombo.setSelectedIndex(0);
        gradeField.setText("");
    }

    // ── Grades table ──────────────────────────────────────────────────────────
    public void updateGradesTable(List<Grade> grades) {
        gradesModel.setRowCount(0);
        gradeIdMap.clear();

        // Group grades by student name
        Map<String, StringBuilder> gradesStr = new LinkedHashMap<>();
        Map<String, String>        groupStr  = new LinkedHashMap<>();
        Map<String, List<Double>>  avgMap    = new LinkedHashMap<>();

        for (Grade g : grades) {
            String key = g.getStudentName();
            gradesStr.computeIfAbsent(key, k -> new StringBuilder()).append(g.getValue()).append(", ");
            gradeIdMap.computeIfAbsent(key, k -> new ArrayList<>()).add(g.getId());
            avgMap.computeIfAbsent(key, k -> new ArrayList<>()).add(g.getValue());
        }

        for (Map.Entry<String, StringBuilder> e : gradesStr.entrySet()) {
            String name    = e.getKey();
            String allGrades = e.getValue().toString().replaceAll(", $", "");
            double avg = avgMap.get(name).stream().mapToDouble(Double::doubleValue).average().orElse(0);
            gradesModel.addRow(new Object[]{name, "-", allGrades, String.format("%.2f", avg)});
        }
    }

    public JTable getGradesTable()               { return gradesTable; }
    public Map<String, List<Integer>> getGradeIdMap() { return gradeIdMap; }

    // ── Absences table ────────────────────────────────────────────────────────
    public void updateAbsencesTable(List<Absence> absences) {
        absencesModel.setRowCount(0);
        for (Absence a : absences) {
            absencesModel.addRow(new Object[]{
                    a.getStudentName(),
                    "-",
                    a.getDate().toString(),
                    a.isMotivated() ? "✓" : "✗"
            });
        }
    }
    public JTable getAbsencesTable()     { return absencesTable; }

    // ── Buttons ───────────────────────────────────────────────────────────────
    public JButton getAddGradeButton()        { return addGradeButton; }
    public JButton getAddAbsenceButton()      { return addAbsenceButton; }
    public JButton getDeleteAbsenceButton()   { return deleteAbsenceButton; }
    public JButton getMotivateAbsenceButton() { return motivateAbsenceButton; }
}
