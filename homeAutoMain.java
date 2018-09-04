import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

/**
 * Home Automation Main
 * simply loads the GUI class
 * 
 * @author Russ Louks
 * @version 5/12/2017
 */
public class homeAutoMain
{
	
    public static void main(String[] args)
    {
        // Initialize the MQTT connections
    	new homeAutoGUI();
        
    }
    

	
}


	

