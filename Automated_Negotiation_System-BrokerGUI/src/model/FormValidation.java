package model;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FormValidation {
	
	public static boolean textFieldNotEmpty(TextField t)
	{
		boolean r = false;
		if(t.getText() != null && !t.getText().isEmpty())
		{
			r = true;
		}
		return r;
	}
	
	public static boolean textFieldNotEmpty(TextField t, Label l, String validationString)
	{
		boolean r = true;
		String s = null;
		if(!textFieldNotEmpty(t))
		{
			r = false;
			s = validationString;
		}
		l.setText(s);
		return r;
	}
	
	public static boolean comboBoxNotSelected(ComboBox<String> c, Label l, String validationString)
	{
		boolean r = false;
		String s = null;
		if(c.getValue() != null && c.getValue() != "No Select" && c.getValue() != "")
		{
			r = true;
		}
		else {
			r = false;
			s = validationString;
		}
		l.setText(s);
		return r;
	}
}
