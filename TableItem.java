
/**
 * Structure to provide a relationship between the IOTableName of an IO Point and the on screen
 * check box
 * @author (Russ Louks) 
 * @version (v1.0)
 */
import javax.swing.*;

public class TableItem
{
    // instance variables - replace the example below with your own
    private JCheckBox checkBox;
    private JTextField textBox;
    private JLabel textLabel;
    private String tableName;
    private String displayObjectType;
    

    /**
     * Constructor for objects of class tableItem
     */
    public TableItem(String tbName, String displayLabel, String dObjectType)
    {
        // Initialize instance variables
    	displayObjectType = dObjectType;
    	tableName = tbName;
    	if (dObjectType.equals("JCheckBox"))
    	{
    		checkBox = new JCheckBox(displayLabel);
    		textBox = null;
    		textLabel = null;
    		
    	}
    	else
    	{
    		checkBox = null;
    		textBox = new JTextField(4);
    		textLabel = new JLabel(displayLabel);  		
    	}
    }

    /**
     * Getters and Setters
     *  
     */
    public String getTableName()
    {
        return (tableName);
    }
    
    public String getDisplayObjectType()
    {
    	return(displayObjectType);
    }
    
    public JCheckBox getCheckBox ()
    {
        return(checkBox);
    }
    public JTextField getTextBox()
    {
    	return(textBox);
    }
    public JLabel getTextLabel()
    {
    	return(textLabel);
    }
    
    public void setTableName(String tbName)
    {
        tableName = tbName;
    }
    
    public void setCheckBoxStatus (Boolean status)
    {
        checkBox.setSelected(status);
    }
    
    public void setDisplayLabel(String label)
    {
    	textLabel.setText(label);
    }
    
    public void setTextBox(String value)
    {
    	textBox.setText(value);
    }
    
}
