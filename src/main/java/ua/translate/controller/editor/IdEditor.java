package ua.translate.controller.editor;

import java.beans.PropertyEditorSupport;

public class IdEditor extends PropertyEditorSupport {
	
	@Override
	public void setAsText(String id) throws IllegalArgumentException{
		id = id.replace(",", "");
		setValue(new Long(id));
	}
}
