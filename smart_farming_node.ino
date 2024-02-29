#include "EspMQTTClient.h"  // Facilita la conexión y comunicación con el cliente MQTT.

#define ANALOG_PIN A0
#define PUMP_PIN 12   //RGB GREEN
#define VALVE_PIN 14   //PIN RED BUT COLOR BLUE
#define ENABLE_PIN 4   //GREEN LED 

#define SWITCH_PIN 5


/***************************** VARIABLES *********************************/
int wl_upper_limit = 100;
int wl_mid_upper_limit = 70;
int wl_mid_lower_limit = 50;
int wl_lower_limit = 20;
float  water_level = 60.0;

float ph_level = 6.0;
int ph_upper_limit = 8;
int ph_lower_limit = 4;

float conductivity = 10;
int con_upper_limit = 20;
int con_lower_limit = 0;

int valve_state = 0;
int pump_state = 0;
int system_state = 1;

int mqtt_is_connected = 0;

float ph_avg = 0.0;
float con_avg = 0.0;
int counter = 0;


unsigned long lastMsg = 0;
unsigned long lastMsg2 = 0;

/* Inicialización del cliente MQTT */
EspMQTTClient client(
  "OF_iPhone",
  "12345678",
  "test.mosquitto.org",  // MQTT Broker server ip
  "of11",        // Client name that uniquely identify your device
  1883             // The MQTT port, default to 1883. this line can be omitted
);

void onMessageReceived(const String& topic, const String& message);
void check_water_level(void);
void check_water_quality(void);
/******************** FUNCIÓN INCIAL DE CONFIGURACIÓN ************************/

void setup_mqtt(void) {

  client.enableDebuggingMessages();  // Esta función habilita los mensajes de depuración que se envían a la salida serial (puerto serie) de Arduino,
                                     // el cliente MQTT imprimirá información adicional sobre su estado y actividad en el puerto serie

  client.enableHTTPWebUpdater();  // Esta función habilita la actualización del firmware a través de una interfaz web (HTTP) en el dispositivo Arduino.
                                  // Cuando está habilitada, el cliente MQTT puede recibir comandos o actualizaciones de firmware a través de una conexión HTTP.
                                  // Por defecto, se utilizan los valores de MQTTUsername y MQTTPassword como nombre de usuario y contraseña para acceder a la interfaz web,
                                  // pero también es posible proporcionar valores personalizados mediante la sobrecarga de la función, como en enableHTTPWebUpdater("user", "password").


  client.enableOTA();  // Esta función habilita las actualizaciones OTA (Over The Air) en el dispositivo Arduino.
                       // Las actualizaciones OTA permiten actualizar el firmware del dispositivo de forma inalámbrica a través de la red,
                       // en lugar de tener que conectar físicamente el Arduino al ordenador para cargar un nuevo programa.
                       // Por defecto, se utiliza el valor de MQTTPassword (de la funcion enableHTTPWebUpdater) como contraseña para acceder a las actualizaciones OTA.
                       // También es posible especificar una contraseña personalizada y un puerto personalizado mediante la sobrecarga de la función, como en enableOTA("password", port).


}

void setup() {
  analogReference(DEFAULT);
  Serial.begin(9600);
  setup_mqtt();
  pinMode(PUMP_PIN, OUTPUT);
  pinMode(VALVE_PIN, OUTPUT);
  pinMode(ENABLE_PIN, OUTPUT);
  pinMode(SWITCH_PIN, INPUT);
  digitalWrite(PUMP_PIN, LOW);
  digitalWrite(VALVE_PIN, LOW);
  digitalWrite(ENABLE_PIN, HIGH);

}

/* 
 *  Esta función se llama una vez después de que todo se haya conectado (Wifi y MQTT) y es obligatorio implementarla con EspMQTTClient.
 *  Se suscribe a todos los topics necesarios.
 */
void onConnectionEstablished (void) {
   Serial.println("Connection established ");

  mqtt_is_connected = 1;
  client.subscribe("1/water_level/UPPER", onMessageReceived);
  client.subscribe("1/water_level/MidUPPER", onMessageReceived);
  client.subscribe("1/water_level/MidLOWER", onMessageReceived);
  client.subscribe("1/water_level/LOWER", onMessageReceived);

  client.subscribe("1/ph/UPPER", onMessageReceived);
  client.subscribe("1/ph/LOWER", onMessageReceived);

  client.subscribe("1/conductivity/UPPER", onMessageReceived);
  client.subscribe("1/conductivity/LOWER", onMessageReceived);


  client.subscribe("1/valve/enable", onMessageReceived);
  client.subscribe("1/pump/enable", onMessageReceived);
  client.subscribe("1/enable", onMessageReceived);
}


/************************ FUNCIÓN DE BUCLE REPETITIVO ****************************/
void loop() {

  client.loop();

  if(mqtt_is_connected && system_state){
    

    unsigned long now = millis();
    
    if (now - lastMsg > 2000) { //Each 5 minutes (300000) --> To test 5 seconds
      lastMsg = now;
      check_water_level();
      check_water_quality();
    }

    if (now - lastMsg2 > 5000) { //Each 5 minutes (300000) --> To test 5 seconds
      lastMsg2 = now;
      String msg = String(ph_level) + "," + String(water_level) + "," + String(conductivity) + "," + String(system_state) + "," + String(pump_state) + "," + String(valve_state);
      client.publish("trough1/measures", msg, 1);
      
    }
  }
}

void onMessageReceived(const String& topic, const String& message) 
{
  Serial.println("Message received from " + topic + ": " + message);
  Serial.println(" ");
  
  if(topic == "1/water_level/UPPER"){
    wl_upper_limit = message.toInt();
  }
  else if(topic == "1/water_level/MidUPPER"){
    wl_mid_upper_limit = message.toInt();
  }
  else if(topic == "1/water_level/MidLOWER"){
    wl_mid_lower_limit = message.toInt();
  }
  else if(topic == "1/water_level/LOWER"){
    wl_lower_limit = message.toInt();
  }
  else if(topic == "1/ph/UPPER"){
    ph_upper_limit = message.toInt();
  }
  else if(topic == "1/ph/LOWER"){
    ph_lower_limit = message.toInt();
  }
  else if(topic == "1/conductivity/UPPER"){
    con_upper_limit = message.toInt();
  }
  else if(topic == "1/conductivity/LOWER"){
    con_lower_limit = message.toInt();
  }
  else if(topic == "1/valve/enable"){
    valve_state = message.toInt();
    if(valve_state == 1){
      digitalWrite(VALVE_PIN, HIGH);
      Serial.println("Valve OPEN");
    }
    else{
      digitalWrite(VALVE_PIN, LOW);
      Serial.println("Valve CLOSED");
    }
  }
  else if(topic == "1/pump/enable"){
    pump_state = message.toInt();
    if(pump_state == 1){
      digitalWrite(PUMP_PIN, HIGH);
      Serial.println("Pump ON");
    }
    else{
      digitalWrite(PUMP_PIN, LOW);
      Serial.println("Pump OFF");
    }
  }
  else if(topic == "1/enable"){
    system_state = message.toInt();
    if(system_state == 0){
      digitalWrite(VALVE_PIN, LOW);
      digitalWrite(PUMP_PIN, LOW);
      digitalWrite(ENABLE_PIN, LOW);
      valve_state = 0;
      String msg = String(ph_level) + "," + String(water_level) + "," + String(conductivity) + "," + String(system_state) + "," + String(pump_state) + "," + String(valve_state);
      client.publish("trough1/measures", msg, 1);
      Serial.println("System OFF");
    }
    else{
      digitalWrite(ENABLE_PIN, HIGH);
      Serial.println("System ON");
    }
  }


}

void check_water_level(void)
{

  if(digitalRead(SWITCH_PIN)){
    water_level = analogRead(ANALOG_PIN);
    water_level = (water_level/1024)*100;
  }

  if(water_level < wl_mid_lower_limit){
    pump_state = 1;
    digitalWrite(PUMP_PIN, HIGH);
  }
  else if(water_level > wl_mid_upper_limit){
    pump_state = 0;
    digitalWrite(PUMP_PIN, LOW);
  }

  Serial.print("Water Level: ");
  Serial.println(water_level);
  Serial.print("Water Level Limits: ");
  Serial.print(wl_lower_limit);
  Serial.print(", ");
  Serial.print(wl_mid_lower_limit);
  Serial.print(", ");
  Serial.print(wl_mid_upper_limit);
  Serial.print(", ");
  Serial.println(wl_upper_limit);
  Serial.println(" ");

}

void check_water_quality(void)
{

  if(!digitalRead(SWITCH_PIN)){
    ph_level = analogRead(ANALOG_PIN);
    ph_level = (ph_level/1024)*100;
    ph_level = map(ph_level, 0, 100, 1, 12);

    conductivity = analogRead(ANALOG_PIN);
    conductivity = (conductivity/1024)*80;
    conductivity = map(ph_level, 0, 100, 0, 50);
  }
  

  if(counter == 0){
    ph_avg = ph_level;
    con_avg = conductivity;
  }
  else{
    ph_avg = (ph_avg+ph_level)/2;
    con_avg = (con_avg+conductivity)/2;
  }

  if(counter > 20){
    int ph_st = (ph_avg < ph_lower_limit) || (ph_avg > ph_upper_limit);
    int con_st = (con_avg < con_lower_limit) || (con_avg > con_upper_limit);
    if(ph_st || con_st){
      valve_state = 1;
      digitalWrite(VALVE_PIN, HIGH);
      ph_avg = ph_level;
      con_avg = conductivity;
    }
    else{
      valve_state = 0;
      digitalWrite(VALVE_PIN, LOW);
      ph_avg = ph_level;
      con_avg = conductivity;
    }
    counter=0;
  }

  counter++;

  Serial.print("pH Level Average: ");
  Serial.println(ph_avg);
  Serial.print("pH Level Limits: ");
  Serial.print(ph_lower_limit);
  Serial.print(", ");
  Serial.println(ph_upper_limit);
  Serial.println(" ");

  Serial.print("Conductivity Average: ");
  Serial.println(con_avg);
  Serial.print("Conductivity Limits: ");
  Serial.print(con_lower_limit);
  Serial.print(", ");
  Serial.println(con_upper_limit);
  Serial.println(" ");
}


