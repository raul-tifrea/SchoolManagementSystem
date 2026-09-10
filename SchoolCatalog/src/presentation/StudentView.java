package presentation;

import model.Absence;
import model.Grade;
import model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class StudentView extends JPanel {

    // Grades tab
    private JComboBox<Subject> subjectBox;
    private JTable gradesTable;
    private DefaultTableModel gradesModel;
    private JLabel gpaLabel;

    // Absences tab
    private JComboBox<Subject> absenceSubjectBox;
    private JTable absencesTable;
    private DefaultTableModel absencesModel;
    private JLabel absenceSummaryLabel;

    public StudentView() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(151, 21, 251));

        // ── Grades tab ────────────────────────────────────────────────────────
        JPanel gradesPanel = new JPanel(new BorderLayout());
        gradesPanel.setBackground(new Color(151, 21, 251));

        // GPA banner
        gpaLabel = new JLabel("GPA: —", JLabel.CENTER);
        gpaLabel.setFont(new Font("", Font.BOLD, 18));
        gpaLabel.setForeground(Color.WHITE);
        gpaLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        gpaLabel.setOpaque(true);
        gpaLabel.setBackground(new Color(100, 0, 180));
        gradesPanel.add(gpaLabel, BorderLayout.NORTH);

        gradesModel = new DefaultTableModel(new Object[]{"Grade"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        gradesTable = new JTable(gradesModel);
        styleTable(gradesTable);
        JScrollPane gradeScroll = new JScrollPane(gradesTable);
        gradeScroll.setBorder(BorderFactory.createEmptyBorder());
        gradesPanel.add(gradeScroll, BorderLayout.CENTER);

        JPanel gradeBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        gradeBottom.setBackground(new Color(151, 21, 251));
        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setFont(new Font("", Font.BOLD, 13));
        subjectLabel.setForeground(Color.WHITE);
        subjectBox = new JComboBox<>();
        subjectBox.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        subjectBox.setPreferredSize(new Dimension(180, 28));
        subjectBox.setFont(new Font("", Font.PLAIN, 13));
        gradeBottom.add(subjectLabel);
        gradeBottom.add(subjectBox);
        gradesPanel.add(gradeBottom, BorderLayout.SOUTH);

        // ── Absences tab ──────────────────────────────────────────────────────
        JPanel absencesPanel = new JPanel(new BorderLayout());
        absencesPanel.setBackground(new Color(151, 21, 251));

        absenceSummaryLabel = new JLabel("Total: 0  |  Motivated: 0  |  Unexcused: 0", JLabel.CENTER);
        absenceSummaryLabel.setFont(new Font("", Font.BOLD, 14));
        absenceSummaryLabel.setForeground(Color.WHITE);
        absenceSummaryLabel.setOpaque(true);
        absenceSummaryLabel.setBackground(new Color(100, 0, 180));
        absenceSummaryLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        absencesPanel.add(absenceSummaryLabel, BorderLayout.NORTH);

        absencesModel = new DefaultTableModel(new Object[]{"Date", "Motivated"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        absencesTable = new JTable(absencesModel);
        styleTable(absencesTable);
        absencesPanel.add(new JScrollPane(absencesTable), BorderLayout.CENTER);

        JPanel absenceBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        absenceBottom.setBackground(new Color(151, 21, 251));
        JLabel absSubjectLabel = new JLabel("Subject:");
        absSubjectLabel.setFont(new Font("", Font.BOLD, 13));
        absSubjectLabel.setForeground(Color.WHITE);
        absenceSubjectBox = new JComboBox<>();
        absenceSubjectBox.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        absenceSubjectBox.setPreferredSize(new Dimension(180, 28));
        absenceSubjectBox.setFont(new Font("", Font.PLAIN, 13));
        absenceBottom.add(absSubjectLabel);
        absenceBottom.add(absenceSubjectBox);
        absencesPanel.add(absenceBottom, BorderLayout.SOUTH);

        tabs.addTab("Grades", gradesPanel);
        tabs.addTab("Absences", absencesPanel);
        tabs.setBorder(BorderFactory.createEmptyBorder());
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

    // ── Subject combos ────────────────────────────────────────────────────────
    public void setSubjectsGrade(List<Subject> subjects) {
        subjectBox.removeAllItems();
        for (Subject s : subjects) subjectBox.addItem(s);
    }

    public void setAbsenceSubjects(List<Subject> subjects) {
        absenceSubjectBox.removeAllItems();
        for (Subject s : subjects) absenceSubjectBox.addItem(s);
    }

    public Subject getSelectedSubject()        { return (Subject) subjectBox.getSelectedItem(); }
    public Subject getSelectedAbsenceSubject() { return (Subject) absenceSubjectBox.getSelectedItem(); }
    public JComboBox<Subject> getSubjectBox()       { return subjectBox; }
    public JComboBox<Subject> getAbsenceSubjectBox(){ return absenceSubjectBox; }

    // ── Grades update ─────────────────────────────────────────────────────────
    public void updateGrades(List<Grade> grades) {
        gradesModel.setRowCount(0);
        double sum = 0;
        for (Grade g : grades) {
            sum += g.getValue();
            gradesModel.addRow(new Object[]{g.getValue()});
        }
        if (!grades.isEmpty()) {
            double avg = sum / grades.size();
            gpaLabel.setText(String.format("GPA: %.2f", avg));
        } else {
            gpaLabel.setText("GPA: —");
        }
    }

    // ── Absences update ───────────────────────────────────────────────────────
    public void updateAbsences(List<Absence> absences) {
        absencesModel.setRowCount(0);
        long motivated = absences.stream().filter(Absence::isMotivated).count();
        long unexcused = absences.size() - motivated;
        absenceSummaryLabel.setText(
                "Total: " + absences.size() + "  |  Motivated: " + motivated + "  |  Unexcused: " + unexcused);
        for (Absence a : absences) {
            absencesModel.addRow(new Object[]{a.getDate().toString(), a.isMotivated() ? "✓" : "✗"});
        }
    }
}
