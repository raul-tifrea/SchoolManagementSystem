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
   private JTable gradestable;
   private DefaultTableModel gradesmodel;
   private JTable absencestable;
   private DefaultTableModel absencesmodel;
   private JComboBox<Student> studentcombo;
   private JComboBox<Student> absencestudcombo;
   private JTextField gradeField;
   private JButton addGradeButton;
   private JButton addAbsenceButton;
   private JButton deleteAbsenceButton;

   private Map<String, List<Integer>> gradeIdMap = new HashMap<>();

   public TeacherView(){
       setLayout(new BorderLayout());
       JTabbedPane tabs = new JTabbedPane();

       JPanel gradesPanel = new JPanel(new BorderLayout());
       gradesmodel = new DefaultTableModel(new Object[]{"Student Name","Grade","Average"},0){
           @Override
           public boolean isCellEditable(int row, int column) {
               return false;
           }
       };
       gradestable = new JTable(gradesmodel);
       gradestable.setBackground(new Color(255, 255, 255));
       gradestable.setShowGrid(false);
       gradestable.setFont(new Font("", Font.PLAIN, 14));
       gradestable.setRowHeight(30);
       JTableHeader tableHeader = gradestable.getTableHeader();
       tableHeader.setFont(new Font("", Font.BOLD, 16));
       DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
       headerRenderer.setHorizontalAlignment(JLabel.CENTER);
       tableHeader.setDefaultRenderer(headerRenderer);
       DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
       centerRenderer.setHorizontalAlignment(JLabel.CENTER);
       gradestable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
       gradestable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
       gradestable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
       gradesPanel.add(new JScrollPane(gradestable), BorderLayout.CENTER);

       JPanel formPanel = new JPanel(new FlowLayout());
       formPanel.setBackground(new Color(75, 2, 133));
       studentcombo = new JComboBox<>();
       studentcombo.setPreferredSize(new Dimension(100, 30));
       studentcombo.setFont(new Font("", Font.PLAIN, 14));
       studentcombo.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));

       gradeField = new JTextField(8);
       gradeField.setPreferredSize(new Dimension(100, 30));
       gradeField.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));
       gradeField.setFont(new Font("", Font.PLAIN, 14));
       addGradeButton = new JButton("Add Grade");
       addGradeButton.setBorder(BorderFactory.createSoftBevelBorder(0));
       addGradeButton.setPreferredSize(new Dimension(130, 40));
       addGradeButton.setFont(new Font("", Font.PLAIN, 14));


       JLabel studentLabel = new JLabel("Select Student");
       studentLabel.setFont(new Font("", Font.BOLD, 14));
       studentLabel.setForeground(Color.WHITE);
       formPanel.add(studentLabel);
       formPanel.add(studentcombo);
       JLabel gradeLabel = new JLabel("Grade");
       gradeLabel.setFont(new Font("", Font.BOLD, 14));
       gradeLabel.setForeground(Color.WHITE);
       formPanel.add(gradeLabel);
       formPanel.add(gradeField);
       formPanel.add(addGradeButton);

       gradesPanel.add(formPanel, BorderLayout.SOUTH);

       JPanel absencesPanel = new JPanel(new BorderLayout());
       absencesmodel = new DefaultTableModel(new Object[]{"Absence"},0){
           @Override
           public boolean isCellEditable(int row, int column) {
               return false;
           }
       };
       absencestable = new JTable(absencesmodel);
       absencestable.setBackground(new Color(255, 255, 255));
       absencestable.setShowGrid(false);
       absencestable.setFont(new Font("", Font.PLAIN, 14));
       absencestable.setRowHeight(30);
       JTableHeader absencesTableHeader = absencestable.getTableHeader();
       absencesTableHeader.setFont(new Font("", Font.BOLD, 16));
       DefaultTableCellRenderer absencesHeaderRenderer = (DefaultTableCellRenderer) absencesTableHeader.getDefaultRenderer();
       absencesHeaderRenderer.setHorizontalAlignment(JLabel.CENTER);
       absencesTableHeader.setDefaultRenderer(absencesHeaderRenderer);
       DefaultTableCellRenderer centerAbsencesRenderer = new DefaultTableCellRenderer();
       centerAbsencesRenderer.setHorizontalAlignment(JLabel.CENTER);
       absencestable.getColumnModel().getColumn(0).setCellRenderer(centerAbsencesRenderer);

       JPanel absenceFormPanel = new JPanel(new FlowLayout());
       absencestudcombo = new JComboBox<>();
       absencestudcombo.setPreferredSize(new Dimension(100, 30));
       absencestudcombo.setFont(new Font("", Font.PLAIN, 14));
       absencestudcombo.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));
       JLabel absenceLabel = new JLabel("Select Absence Student");
       absenceLabel.setFont(new Font("", Font.BOLD, 14));
       absenceLabel.setForeground(Color.BLACK);
       absenceFormPanel.add(absenceLabel);
       absenceFormPanel.add(absencestudcombo);
       absencesPanel.add(absenceFormPanel, BorderLayout.NORTH);

       JPanel absencesFormPanel = new JPanel(new FlowLayout());
       absencesFormPanel.setBackground(new Color(75, 2, 133));
       absenceFormPanel.setBorder(BorderFactory.createEmptyBorder());
       addAbsenceButton = new JButton("Add Absence");
       addAbsenceButton.setBorder(BorderFactory.createSoftBevelBorder(0));
       deleteAbsenceButton = new JButton("Delete Absence");
       deleteAbsenceButton.setBorder(BorderFactory.createSoftBevelBorder(0));
       addAbsenceButton.setPreferredSize(new Dimension(130, 40));
       addAbsenceButton.setFont(new Font("", Font.PLAIN, 14));
       deleteAbsenceButton.setPreferredSize(new Dimension(130, 40));
       deleteAbsenceButton.setFont(new Font("", Font.PLAIN, 14));
       absencesFormPanel.add(addAbsenceButton);
       absencesFormPanel.add(deleteAbsenceButton);
       absencesPanel.add(new JScrollPane(absencestable), BorderLayout.CENTER);
       absencesPanel.add(absencesFormPanel, BorderLayout.SOUTH);

       tabs.addTab("Grades", gradesPanel);
       tabs.addTab("Absences", absencesPanel);
       add(tabs, BorderLayout.CENTER);

   }

   public void setStudentCombo(List<Student> students){
       studentcombo.removeAllItems();
       for(Student s : students){
           studentcombo.addItem(s);
       }
   }


   public Student getSelectedStudent(){
       return (Student) studentcombo.getSelectedItem();
   }


   public double getGrade(){
       return Double.parseDouble(gradeField.getText());
   }

   public void updateGradesTable(List<Grade> grades){
       gradesmodel.setRowCount(0);
       gradeIdMap.clear();
       Map<String, StringBuilder> map = new LinkedHashMap<>();
       Map<String, List<Double>> averageMap = new HashMap<>();
       for(Grade g : grades) {
           map.computeIfAbsent(g.getStudentName(), k -> new StringBuilder()).append(g.getValue()).append(", ");
           gradeIdMap.computeIfAbsent(g.getStudentName(), k -> new ArrayList<>()).add(g.getId());
           averageMap.computeIfAbsent(g.getStudentName(), k -> new ArrayList<>()).add(g.getValue());
       }
       for(Map.Entry<String, StringBuilder> e : map.entrySet()){
           String allGrade = e.getValue().toString().replaceAll(", $", "");
           List<Double> average = averageMap.get(e.getKey());
           double averageGrade = average.stream().mapToDouble(Double::doubleValue).average().orElse(0);
           gradesmodel.addRow(new Object[]{e.getKey(), allGrade, String.format("%.2f", averageGrade)});
       }
   }

   public Map<String, List<Integer>> getGradeIdMap(){
       return gradeIdMap;
   }


   public JButton getAddGradeButton(){
       return addGradeButton;
   }


    public void clearForm(){
       studentcombo.setSelectedIndex(0);
       gradeField.setText("");
    }


    public JTable getGradesTable() {
        return gradestable;
    }

    public JButton getAddAbsenceButton() {
       return addAbsenceButton;
    }

    public JButton getDeleteAbsenceButton() {
       return deleteAbsenceButton;
    }
    public JTable getAbsencesTable() {
        return absencestable;
    }


    public void updateAbsencesTable(List<Absence> absences){
       absencesmodel.setRowCount(0);
       for(Absence a : absences){
           absencesmodel.addRow(new Object[]{a.getDate().toString()});
       }
    }

    public JComboBox<Student> getAbsenceStudentCombo() {
        return absencestudcombo;
    }

    public Student getSelectedAbsenceStudent(){
       return (Student) absencestudcombo.getSelectedItem();
    }

    public void setAbsenceStudentCombo(List<Student> students){
       absencestudcombo.removeAllItems();
       for(Student s : students){
           absencestudcombo.addItem(s);
       }
    }


}
