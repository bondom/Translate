package ua.translate.controller.editor;

import java.beans.PropertyEditorSupport;

/**
 * This editor has one method, which removes any leading and trailing whitespaces 
 * of retrieved String
 * @author Yuriy Phediv
 *
 */
public class CommentTextEditor extends PropertyEditorSupport {
	
	@Override
	public void setAsText(String commentText) throws IllegalArgumentException{
		commentText = commentText.trim();
		setValue(commentText);
	}
}
