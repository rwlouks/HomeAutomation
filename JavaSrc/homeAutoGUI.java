
/**
 * Write a description of class myGUIExample here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;

import javax.swing.*;
import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

public class homeAutoGUI extends JFrame
{
    // instance variables
    private static final long serialVersionUID = 1L;
    private JButton inputButton = new JButton("Send GetStatus");
    private JButton exitButton = new JButton("Exit");
    private JTextField receivedMsg = new JTextField(50);
    private JTextField errorMsg = new JTextField(50);
    private Timestamp mqttTimeStamp;
    private JPanel buttonPanel = new JPanel();
    private JPanel cbPanel = new JPanel();
    private JPanel textPanel = new JPanel();
    private MqttClient localClient = null;
    

	
    private String gDoorTopic = "GDoorIn";
    private String serverTopic = "ServerIn";
    private String getStatusCommand = "{\"Command\":\"GetStatus\",\"IOTableName\":\"GD1Sensor\"}";
    private JSONObject cmdOut = new JSONObject();
    private IOTableArray serverIOTable = new IOTableArray();
   
    
    /**
     * Constructor for objects of class myGUIExample
     */
    public homeAutoGUI()
    {
        setSize(800,600);
        setLayout(new BorderLayout());
        Font font1 = new Font("SansSerif", Font.BOLD, 30);
        textPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Den Temp"));
        
        inputButton.addActionListener(new inputListener());
        exitButton.addActionListener(new exitListener());
        buttonPanel.add(errorMsg);
        buttonPanel.add(inputButton);
        buttonPanel.add(exitButton);
        // Loop through the IOTable array list and add the checkboxes to the cbPanel
        TableItem myItem;
        for (int i=0; i<serverIOTable.getSize(); i++)
        {
            myItem = serverIOTable.getTableItem(i);
            if (myItem.getCheckBox() != null)
            {
            	cbPanel.add(myItem.getCheckBox());
            }
            else
            {
            	myItem.getTextLabel().setFont(font1);
            	myItem.getTextBox().setFont(font1);
            	textPanel.add(myItem.getTextLabel());
            	textPanel.add(myItem.getTextBox());
            }
            
        }
        add(receivedMsg, BorderLayout.NORTH); 
        add(cbPanel, BorderLayout.EAST);
        add(textPanel, BorderLayout.WEST);
        add(buttonPanel, BorderLayout.SOUTH);
        
       

       initMQTT(); 
       setVisible(true);
 
    } 
      
	private void initMQTT()
	{
		
				
			// Create the local MQTT Client
	        try
	            {
	                localClient = new MqttClient("tcp://localhost", MqttClient.generateClientId());
	            }
	            catch (Exception MqttException)
	            {
	                JOptionPane.showMessageDialog(null, "new localMqttClient error");
	            }
	        // Set the call back functions
	        try
	           {
	               localClient.setCallback((MqttCallback) new LocalMqttCallback());
	               
	           }
	           catch(Exception MqttException)
	           {
	               JOptionPane.showMessageDialog(null, "Call Back Error");
	           }
	       
	        // Try to connect    
	        try
	            {
	                localClient.connect();
	            }
	            catch (Exception MqttException)
	            {
	                JOptionPane.showMessageDialog(null, "Connection error");
	            }
	        
	        // Subscribe to the topic
	        
	        try
	            {
	                localClient.subscribe(serverIOTable.getControllerName());
	            }
	            catch (Exception MqttException)
	            {
	                JOptionPane.showMessageDialog(null, "Subscribe error");
	            }
	            
		}


	private class inputListener implements ActionListener
    {
  
		public void actionPerformed(ActionEvent e)
        {
           MqttMessage myMessage = new MqttMessage();
           byte[] bytPayload;
           cmdOut.put("Command","GetStatus");  // build the JSON command message
           cmdOut.put("IOTableName","GD1Sensor");           
           String cmdString = JSONValue.toJSONString(cmdOut);  // convert the JSON command message to a string
           //JOptionPane.showMessageDialog(null, cmdString);
           bytPayload = cmdString.getBytes(); //  convert the JSON string to a byte array for Mqtt
           myMessage.setPayload(bytPayload); // set the Mqtt message payload
          
            try
            {
                localClient.publish(gDoorTopic, myMessage);
            }
            catch (Exception MqttException)
            {
                JOptionPane.showMessageDialog(null, "Publish error");
            }
            
            //JOptionPane.showMessageDialog(null,"Clicked");          
        }
    }
    private class exitListener implements ActionListener
    {
        public exitListener() {
			// TODO Auto-generated constructor stub
		}

		public void actionPerformed(ActionEvent e)
        {
            try
            {
              localClient.unsubscribe(serverIOTable.getControllerName());
            }
            catch (Exception MqttException)
            {
              JOptionPane.showMessageDialog(null, "Unsubscribe Error");
            }
            try
            {
              localClient.disconnect();

            }
            catch (Exception MqttException)
            {
              JOptionPane.showMessageDialog(null, "Disconnect Error");
            }
           
            System.exit(0);
        }
    }
    
	 private class LocalMqttCallback implements MqttCallback
	 {
		public LocalMqttCallback() {}
	
		public void connectionLost(Throwable cause)
		 {
			initMQTT(); 
			mqttTimeStamp = new Timestamp(System.currentTimeMillis());
	     	errorMsg.setText(mqttTimeStamp + " " + cause.getMessage()+" " + cause.toString());
			//JOptionPane.showMessageDialog(null, "Connection lost "+cause);
		 }
		 
        public void messageArrived(String topic, MqttMessage message)
         {
     	    //JOptionPane.showMessageDialog(null, "Local Message Received");
            String controllerName = "";
            //String outString;
            JSONParser parser = new JSONParser();
            JSONObject cmdJSON;
     	   //JOptionPane.showMessageDialog(null, topic);
     	   String msg = new String(message.getPayload());
     	   mqttTimeStamp = new Timestamp(System.currentTimeMillis());
     	   receivedMsg.setText(mqttTimeStamp + " " + msg);
     	   //JOptionPane.showMessageDialog(null, msg);
            
            // convert the Mqtt message payload into a JSON format
            try
            {
				cmdJSON = (JSONObject) parser.parse(msg);
				controllerName = (String) cmdJSON.get("ControllerName");
				//JSONArray sensorValues = (JSONArray) cmdJSON.get("SensorValues");
				//outString = "Name: " + controllerName;
				//outString += "\nSensor Values";
				//JOptionPane.showConfirmDialog(null, outString + "\nAdded");
				// Got rid of the JSON embedded array of sensor values
				// The JSON message now just has elements that correspond to the tableName values
				// in the IOTableArray
				//Iterator<JSONObject> iterator = sensorValues.iterator();
				int sensorNum = serverIOTable.getSize();
				for (int i=0; i< sensorNum; i++)
				{
					//JSONObject sensor = iterator.next();
					TableItem sensorObj = serverIOTable.getTableItem(i);
					//Get the sensor name from the TableItem object
					String sensorName = sensorObj.getTableName();
					//Need to determine the type of display object before setting the value
					//Hard code for now to match the TestSensorController app.
					
					String sensorValue = Long.toString((long) cmdJSON.get(sensorName));
					//
					//outString += "\nSensor Name: "+ sensorName;
					//outString += " Sensor Value: "+ sensorValue;									
					
					//JOptionPane.showConfirmDialog(null, outString + "\nAdded");
					
					//Need to determine the type of display object before setting the value
					//Hard code for now to match the TestSensorController app.
					serverIOTable.getTableItem(i).getTextBox().setText(sensorValue);
					
				}

					
			}
            catch (ParseException e)
            {
					// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "ParseException Handler");
            	mqttTimeStamp = new Timestamp(System.currentTimeMillis());
    	     	errorMsg.setText(mqttTimeStamp + "Parse Exceptoin " + e.toString());
    	     	JOptionPane.showMessageDialog(null, "ParseException Handler");
			}
            
            
            
            
        }
        public void deliveryComplete(IMqttDeliveryToken token) {}
		
	}
}
