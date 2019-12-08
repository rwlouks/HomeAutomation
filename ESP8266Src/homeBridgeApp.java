import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;



public class homeBridgeApp
{
	private MqttClient remoteClient = null;
    private AWSIotMqttClient awsClient = null;
    private MqttClient localClient = null;
    private IOTableArray serverIOTable = new IOTableArray();
    
    
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		
		
	    
	    

	}
	
private void initMQTT() {
		
		//read the configuration file
			JSONParser parserMQTT = new JSONParser();
			String endpoint = "";
			String clientId = "";
			String certificateFile = "";
			String privateKeyFile = "";
			
			
			
			try
			{
				Object obj = parserMQTT.parse(new FileReader("C:\\HomeIoTController\\ControllerMQTTConfig.json"));
				JSONObject jsonObject = (JSONObject) obj;
				
				endpoint = (String) jsonObject.get("AWSclientEndpoint");
				clientId = (String) jsonObject.get("AWSclientId");
				certificateFile = (String) jsonObject.get("AWScertificateFile");
				privateKeyFile = (String) jsonObject.get("AWSprivateKeyFile");
				
				String outString = new String();
				outString = "Endpoint: " + endpoint;
				outString += "\nClient ID: " + clientId;
				outString += "\nCertificate File: " + certificateFile;
				outString += "\nPrivate Key File: " + privateKeyFile;
				
				JOptionPane.showConfirmDialog(null, outString);
						
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
			
			// Connect to the AWS IoT space.
			
			KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
			System.out.println(pair.keyPassword);
			System.out.println(pair.keyStore);
			awsClient = new AWSIotMqttClient(endpoint, clientId, pair.keyStore, pair.keyPassword);
			
			try 
			{
				awsClient.connect();
			} 
			catch (AWSIotException e) {
				// TODO Auto-generated catch block
				System.out.println("Error code: " + e.getErrorCode());
				System.out.println("Error message: " + e.getMessage());
				System.exit(23);
			
			}

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

}
