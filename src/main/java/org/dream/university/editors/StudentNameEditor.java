package org.dream.university.editors;

import java.beans.PropertyEditorSupport;

/**
 * This class has one method, 
 * which converts to uppercase name of student's group, 
 * retrieved from searchStudents.jsp
 * @author Phediv Yura
 *
 */
public class StudentNameEditor extends PropertyEditorSupport {
	
	@Override
	public void setAsText(String studentGroup) throws IllegalArgumentException{
		studentGroup = studentGroup.toUpperCase();
		setValue(studentGroup);
	}

}
