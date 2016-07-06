package org.dream.university.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.dream.university.customValidators.IsGroupValid;
import org.springframework.stereotype.Component;

@Entity
@NamedQueries({
	@NamedQuery(name = "getStudentsOfGroup", query = "from Student student where student.studentGroup = :group")
	
})
@Table(name = "STUDENT_LIST")
@Component
public class Student {
	@Id
	@Column(name = "STUDENT_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int studentId;
	
	@Column(name = "STUDENT_NAME")
	private String studentName;
	
	@Column(name = "STUDENT_AVERAGE_MARK")
	private double studentAverageMark;
	
	@IsGroupValid
	@Column(name = "STUDENT_GROUP")
	private String studentGroup;
	
	public Student(){}
	public Student(int studentId, 
				   String studentName,
				   double studentAverageMark,
				   String studentGroup){
		this.studentId = studentId;
		this.studentName =studentName;
		this.studentGroup = studentGroup;
		this.studentAverageMark = studentAverageMark;
	}
	
	
	public String getStudentGroup() {
		return studentGroup;
	}

	public void setStudentGroup(String studentGroup) {
		this.studentGroup = studentGroup;
	}

	
	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public double getStudentAverageMark() {
		return studentAverageMark;
	}

	public void setStudentAverageMark(double studentAverageMark) {
		this.studentAverageMark = studentAverageMark;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}


}
