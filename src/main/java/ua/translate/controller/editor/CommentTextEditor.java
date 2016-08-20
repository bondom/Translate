package ua.translate.controller.editor;

import java.beans.PropertyEditorSupport;

public class CommentTextEditor extends PropertyEditorSupport {
	
	@Override
	public void setAsText(String commentText) throws IllegalArgumentException{
		commentText = commentText.trim();
		setValue(commentText);
	}
}
