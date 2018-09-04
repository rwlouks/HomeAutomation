import javax.swing.JOptionPane;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class TestSensorController {

	public static void main(String[] args) {
	
		MqttClient localClient = null;
		// Create the local MQTT Client
        try
            {
                localClient = new MqttClient("tcp://localhost", MqttClient.generateClientId());
            }
            catch (Exception MqttException)
            {
                JOptionPane.showMessageDialog(null, "new localMqttClient error");
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
        
		
		String topic = "DenTemp";
		String payload = "{\"ControllerName\" : \"DenTemp\", \"SensorValues\" :"
							+ "[{\"SensorName\" : \"DenTemp\", \"SensorValue\":45},"
							+ " {\"SensorName\" : \"DenHumid\", \"SensorValue\":60}]}";
		MqttMessage jsonPayload = new MqttMessage(payload.getBytes());
		JOptionPane.showMessageDialog(null, payload);
		try {
			   localClient.publish(topic, jsonPayload);
			   localClient.disconnect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	}


