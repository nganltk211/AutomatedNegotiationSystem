package gui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Class supporting the validation of GUI-Element
 */
public class FormValidation {
	
	/**
	 * Checks whether a text field is not empty
	 * @param t : checked text field
	 * @return true if the text field is not empty
	 */
	public static boolean textFieldNotEmpty(TextField t)
	{
		boolean r = false;
		if(t.getText() != null && !t.getText().isEmpty())
		{
			r = true;
		}
		return r;
	}
	
	/**
	 * When the text field is empty, a warning message will be shown on GUI 
	 * @param t : the text field
	 * @param l : label to show message
	 * @param validationString : validation message
	 * @return
	 */
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
	
	/**
	 * Checks whether a comboxbox value is selected
	 * @param c : the combobox
	 * @param l : label to show a suitable validation message to users
	 * @param validationString : validation message
	 * @return false if the no value is selected
	 */
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
