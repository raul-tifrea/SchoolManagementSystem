package presentation;

import model.Grade;
import model.Student;
import model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeacherView extends JPanel {
   private JTable table;
   private DefaultTableModel model;
   private JComboBox<String> studentcombo;
   private JComboBox<String> subjectcombo;
   private JTextField gradeField;
   private JButton addGradeButton;
   private JButton deleteGradeButton;

   public TeacherView(){
       setLayout(new BorderLayout());
       model = new DefaultTableModel(new Object[]{"Student ID","Subject","Grade"},0);
       table = new JTable(model);
       add(new JScrollPane(table), BorderLayout.CENTER);

       JPanel formPanel = new JPanel(new FlowLayout());
       studentcombo = new JComboBox<>();
       subjectcombo = new JComboBox<>();
       gradeField = new JTextField(8);
       addGradeButton = new JButton("Add Grade");
       deleteGradeButton = new JButton("Delete Grade");

       formPanel.add(new JLabel("Student"));
       formPanel.add(studentcombo);
       formPanel.add(new JLabel("Subject"));
       formPanel.add(subjectcombo);
       formPanel.add(new JLabel("Grade"));
       formPanel.add(gradeField);
       formPanel.add(addGradeButton);
       formPanel.add(deleteGradeButton);

       add(formPanel, BorderLayout.SOUTH);
   }

   public void setStudentCombo(List<Student> students){
       studentcombo.removeAllItems();
       for(Student s : students){
           studentcombo.addItem(String.valueOf(s));
       }
   }

   public void setSubjectCombo(List<Subject> subjects){
       subjectcombo.removeAllItems();
       for(Subject s : subjects){
           subjectcombo.addItem(s.toString());
       }
   }

   public Student getSelectedStudent(){
       return (Student) studentcombo.getSelectedItem();
   }

   public double getGrade(){
       return Double.parseDouble(gradeField.getText());
   }

   public int getSelectedGradeID(){
       int row = table.getSelectedRow();
       if(row == -1){
           return -1;
       }
       return (int) table.getValueAt(row, 0);
   }

   public void updateTable(List<Grade> grades){
       model.setRowCount(0);
       for(Grade g : grades){
            model.addRow(new Object[]{g.getId(),g.getStudentName(),g.getSubject(),g.getValue()});
       }
   }

   public Subject getSelectedSubject(){
       return (Subject) subjectcombo.getSelectedItem();
   }

   public JButton getAddGradeButton(){
       return addGradeButton;
   }

   public JButton getDeleteGradeButton(){
       return deleteGradeButton;
   }

    public void clearForm(){
       studentcombo.setSelectedIndex(0);
       subjectcombo.setSelectedIndex(0);
       gradeField.setText("");
    }




}
