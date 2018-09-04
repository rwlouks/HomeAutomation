//Home Automation Project
//Temperature and Humidity Sensor
// Uses:
//    DHT Temperature & Humidity Sensor
//    Adafruit Unified Sensor Library
//    Adafruit MQTT Library


// Depends on the following Arduino libraries:
// - Adafruit Unified Sensor Library: https://github.com/adafruit/Adafruit_Sensor
// - DHT Sensor Library: https://github.com/adafruit/DHT-sensor-library
#include <ArduinoJson.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <ESP8266WiFi.h>

#include "Adafruit_MQTT.h"
#include "Adafruit_MQTT_Client.h"

/************************* WiFi Access Point *********************************/

#define WLAN_SSID       "adsup"
#define WLAN_PASS       "7346121744"

/************************* MQTT Server Setup *********************************/

#define HA_SERVER      "192.168.1.6"
#define HA_SERVERPORT  1883                   // use 8883 for SSL
//#define HA_USERNAME    "...your MQTT Server Username..."
//#define HA_PASSWORD         "...your MQTT Server Password..."

/************ MQTT Publish and Subscribe Topics ******************************/

// MQTT Publish Topics
#define HA_SENSOR_CONTROLLER "DenTemp"
#define HA_DENTEMP "/DenTemp"
#define HA_DENHUMID "/DenHumid"

// MQTT Subscribe Topics


/************ Global State (you don't need to change this!) ******************/

// Create an ESP8266 WiFiClient class to connect to the MQTT server.
WiFiClient client;
// or... use WiFiFlientSecure for SSL
//WiFiClientSecure client;

// Setup the MQTT client class by passing in the WiFi client and MQTT server and login details.
//Adafruit_MQTT_Client mqtt(&client, HA_SERVER, HA_SERVERPORT, HA_USERNAME, HA_PASSWORD);
Adafruit_MQTT_Client mqtt(&client, HA_SERVER, HA_SERVERPORT);
/****************************** Feeds ***************************************/

// Setup a feed called 'photocell' for publishing.
// Notice MQTT paths for AIO follow the form: <username>/feeds/<feedname>
Adafruit_MQTT_Publish controller = Adafruit_MQTT_Publish(&mqtt, HA_SENSOR_CONTROLLER);


#define DHTPIN            2         // Pin which is connected to the DHT sensor.

// Uncomment the type of sensor in use:
//#define DHTTYPE           DHT11     // DHT 11 
#define DHTTYPE           DHT22     // DHT 22 (AM2302)
//#define DHTTYPE           DHT21     // DHT 21 (AM2301)

// See guide for details on sensor wiring and usage:
//   https://learn.adafruit.com/dht/overview

DHT_Unified dht(DHTPIN, DHTTYPE);

uint32_t delayMS;
uint32_t x=0;
double currHumid = 0;
double currTemp = 0;


void setup() {
  Serial.begin(9600); 

  // Connect to WiFi access point.
  Serial.println(); Serial.println();
  Serial.print("Connecting to ");
  Serial.println(WLAN_SSID);

  WiFi.begin(WLAN_SSID, WLAN_PASS);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();

  Serial.println("WiFi connected");
  Serial.println("IP address: "); Serial.println(WiFi.localIP());
  // Initialize DHT Sensor.
  dht.begin();
  Serial.println("DHTxx Unified Sensor Init");
  // Print temperature sensor details.
  sensor_t sensor;
  dht.temperature().getSensor(&sensor);
  Serial.println("------------------------------------");
  Serial.println("Temperature");
  Serial.print  ("Sensor:       "); Serial.println(sensor.name);
  Serial.print  ("Driver Ver:   "); Serial.println(sensor.version);
  Serial.print  ("Unique ID:    "); Serial.println(sensor.sensor_id);
  Serial.print  ("Max Value:    "); Serial.print(sensor.max_value); Serial.println(" *C");
  Serial.print  ("Min Value:    "); Serial.print(sensor.min_value); Serial.println(" *C");
  Serial.print  ("Resolution:   "); Serial.print(sensor.resolution); Serial.println(" *C");  
  Serial.println("------------------------------------");
  // Print humidity sensor details.
  dht.humidity().getSensor(&sensor);
  Serial.println("------------------------------------");
  Serial.println("Humidity");
  Serial.print  ("Sensor:       "); Serial.println(sensor.name);
  Serial.print  ("Driver Ver:   "); Serial.println(sensor.version);
  Serial.print  ("Unique ID:    "); Serial.println(sensor.sensor_id);
  Serial.print  ("Max Value:    "); Serial.print(sensor.max_value); Serial.println("%");
  Serial.print  ("Min Value:    "); Serial.print(sensor.min_value); Serial.println("%");
  Serial.print  ("Resolution:   "); Serial.print(sensor.resolution); Serial.println("%");  
  Serial.println("------------------------------------");
  // Set delay between sensor readings based on sensor details.
  delayMS = 100000;
 

  
}

void loop() {
  // Ensure the connection to the MQTT server is alive (this will make the first
  // connection and automatically reconnect when disconnected).  See the MQTT_connect
  // function definition further below.
  MQTT_connect();

  // Delay between measurements.
  delay(delayMS);
   
  // Get temperature event and print its value.
  sensors_event_t event;  
  dht.temperature().getEvent(&event);
  if (isnan(event.temperature)) {
    Serial.println("Error reading temperature!");
  }
  else {
    currTemp = event.temperature;
    // C to F conversion
    currTemp = (currTemp * 9)/5 + 32;
    Serial.print("Temperature: ");
    Serial.print(currTemp);
    Serial.println(" *F");
  }
  // Get humidity event and print its value.
  dht.humidity().getEvent(&event);
  if (isnan(event.relative_humidity)) {
    Serial.println("Error reading humidity!");
  }
  else {
    currHumid = event.relative_humidity;
    Serial.print("Humidity: ");
    Serial.print(event.relative_humidity);
    Serial.println("%");
    
  }
  sendMessage(currTemp, currHumid);
}

// Function to connect and reconnect as necessary to the MQTT server.
// Should be called in the loop function and it will take care if connecting.
void MQTT_connect() {
  int8_t ret;

  // Stop if already connected.
  if (mqtt.connected()) {
    return;
  }

  Serial.print("Connecting to MQTT... ");

  uint8_t retries = 3;
  while ((ret = mqtt.connect()) != 0) { // connect will return 0 for connected
       Serial.println(mqtt.connectErrorString(ret));
       Serial.println("Retrying MQTT connection in 5 seconds...");
       mqtt.disconnect();
       delay(5000);  // wait 5 seconds
       retries--;
       if (retries == 0) {
         // basically die and wait for WDT to reset me
         while (1);
       }
  }
  Serial.println("MQTT Connected!");
}

// Function to serialize the JSON format to publish to the Home Automation MQTT Server
// JSON fomrat is based on the Home Automation JSON config file format
void sendMessage(double t, double h)
{
  // Serialize the message into a JSON format
  const size_t bufferSize = JSON_ARRAY_SIZE(2) + 3*JSON_OBJECT_SIZE(2);
  DynamicJsonBuffer jsonBuffer(bufferSize);

  JsonObject& root = jsonBuffer.createObject();
  root["ControllerName"] = "DenTemp";
  // Eliminate the nested array in the MQTT message because AWS IoT Rules can't use them
  //JsonArray& SensorValues = root.createNestedArray("SensorValues");
  //JsonObject& SensorValues_0 = SensorValues.createNestedObject();
  //SensorValues_0["SensorName"] = "DenTemp";
  
  //Round off temp and convert to integer
  t = t+.5;
  int intTemp = (int)t;
  root["DenTemp"] = intTemp;

  //JsonObject& SensorValues_1 = SensorValues.createNestedObject();
  //SensorValues_1["SensorName"] = "DenHumid";
  //Round off Humidity and convert to integer
  h = h + .5;
  int intHumid = (int) h;
  root["DenHumid"] = intHumid;

  // Create a string from the JSON and send it to the console
  String MQTTMessage;
  root.printTo(MQTTMessage);
  Serial.println(MQTTMessage);

  // Convert the JSON to a char[] for sending via MQTT
  int str_len = MQTTMessage.length() + 1;
  char char_array[str_len];
  MQTTMessage.toCharArray(char_array, str_len);

  // Publish the message to the MQTT Server
  if (!controller.publish(char_array))
  {
    Serial.println("Publish Failed");
  }
  
  return;
}

