
/**
 * Maintain an ArrayList of tableItems to build the check box panel
 * 
 * @author (Russ Louks) 
 * @version (v1.0)
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class IOTableArray
{
    // instance variables
	private String controllerName;
    private ArrayList<TableItem> iOTable = new ArrayList<TableItem>();

    /**
     * Constructor for objects of class IOTableArray
     */
    public IOTableArray()
    {
    	JSONParser parser = new JSONParser();
    	TableItem item;
		try
		{
			Object obj = parser.parse(new FileReader("/home/rwlouks/Desktop/ControlerConfig.json"));
			//Object obj = parser.parse(new FileReader("c:\\HomeIoTController\\ControlerConfig.json"));
			JSONObject jsonObject = (JSONObject) obj;
			
			controllerName = (String) jsonObject.get("ControlerName");
			JSONArray sensorList = (JSONArray) jsonObject.get("SensorList");
			
			String outString = new String();
			outString = "Name: " + controllerName;
			outString += "\nSensor List";
			Iterator<JSONObject> iterator = sensorList.iterator();
			while (iterator.hasNext())
			{
				JSONObject sensor = iterator.next();
				String sensorName = (String) sensor.get("SensorName");
				String displayName = (String) sensor.get("DisplayName");
				String displayObject = (String) sensor.get("DisplayObject");
				String type = (String) sensor.get("Type");
				outString += "\nSensor Name: "+ sensorName;
				outString += " Display Name: "+ displayName;
				outString += " Display Object: " + displayObject;
				outString += " Type: " + type;
				
				item = new TableItem(sensorName, displayName, displayObject);
				iOTable.add(item);
				//JOptionPane.showConfirmDialog(null, outString + "\nAdded");
				
			}
			//JOptionPane.showConfirmDialog(null, "iOTable Size = "+ iOTable.size());
					
		}
		
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }

    /**
     * class methods
     * 
     */
    public TableItem getTableItem(int index)
    {
        return (iOTable.get(index));
    }
    
    public int getSize()
    {
        return (iOTable.size());
    }
    public int findTableName(String name)
    {
        Boolean notFound = true;
        int i = 0;
        TableItem item;
        
        while (notFound && i<iOTable.size())
        {
           item = iOTable.get(i);
            if (name.equals(item.getTableName()))
            {
                notFound = false;
            }
            else
            {
                i++;
            }
        }
        if (notFound)
        {
            return (-1);
        }
        else
        {
            return (i);
        }
    }
    public void setStatus (String name, boolean status)
    {
        TableItem item;
        int i = findTableName(name);
        if (i>=0)
        {
        	item = iOTable.get(i);
            if (item.getCheckBox()!= null)
            {
            	item.setCheckBoxStatus(status);
            }
            else
            {
            	JOptionPane.showMessageDialog(null, "Not a JCheckBox");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, name +" not found");
        }
    }
    
    public void setStatus (String name, double value)
    {
        TableItem item;
        String strValue;
        int i = findTableName(name);
        if (i>=0)
        {
            item = iOTable.get(i);
            if (item.getTextBox()!= null)
            {
            	strValue = Double.toString(value);
            	item.setTextBox(strValue);
            }
            else
            {
            	JOptionPane.showMessageDialog(null, "Not a JTextField");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, name +" not found");
        }
    }
    
    public String getControllerName()
    {
    	return controllerName;
    }
}
