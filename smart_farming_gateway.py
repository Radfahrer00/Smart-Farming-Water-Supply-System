import json
import time
from paho.mqtt import client as mqtt_client
import ssl

water_level = 0.0
pH = 0.0
conductivity = 0.0

def on_connect_upm(client, userdata, flags, rc):
    print(f"Client connected to UPM Server with result code {rc}")

def on_publish_upm(client, userdata, mid):
    print(f"Message published with mid: {mid}")

def on_disconnect_upm(client, userdata, rc):
    print(f"Client disconnected with result code {rc}")
    
def on_message_upm(client, userdata, msg):
    json_msg = json.loads(msg.payload.decode())
    print(f"Received `{msg.payload.decode()}` from `{msg.topic}` topic")
    send_message_to_node(json_msg)
    
def on_connect_mto(client, userdata, flags, rc):
    print(f"SUBSCRIBER MTO Connected with result code {rc}")

def on_publish_mto(client, userdata, mid):
    print(f"Message published with mid: {mid}")

def on_disconnect_mto(client, userdata, rc):
    print(f"SUBSCRIBER Disconnected with result code {rc}")
    
def on_message_mto(client, userdata, msg):
    print(f"Received `{msg.payload.decode()}` from `{msg.topic}` topic")
    send_message_to_server(msg)

def send_message_to_server(msg):
    measurements = msg.payload.decode().split(",")
    pH = float(measurements[0])
    water_level = float(measurements[1])
    conductivity = float(measurements[2])
    device = int(measurements[3])
    pump = int(measurements[4])
    valve = int(measurements[5])

    json_msg = json.dumps({"ph" : pH, 
                           "water level" : water_level, 
                           "conductivity" : conductivity,
                           "device" : device,
                           "pump" : pump,
                           "valve" : valve})

    if msg.topic == "trough1/measures":
        upm_client.publish(pub_tb_topic, json_msg)
        print("Message sent from node 1: " + json_msg + " to " + pub_tb_topic)
    else:
        upm_client2.publish(pub_tb_topic, json_msg)
        print("Message sent from node 2: " + json_msg + " to " + pub_tb_topic)

def send_message_to_node(json_msg):
    try:
        node = json_msg["params"]["nodeID"]
        method = json_msg["method"] 
        if method == "changeDeviceState":
            topic = str(node)+"/enable"
            variable = json_msg["params"]["state"]
            str_msg = 1 if variable == True else 0
        elif method == "changeValveState":
            topic = str(node)+"/valve/enable"
            variable = json_msg["params"]["state"]
            str_msg = 1 if variable == True else 0
        elif method == "changePumpState":
            topic = str(node)+"/pump/enable"
            variable = json_msg["params"]["state"]
            str_msg = 1 if variable == True else 0
        else:
            variable = json_msg["params"]["variable"]
            if variable == "water level" : variable = "water_level"
            limitType = json_msg["params"]["limitType"]
            topic = str(node)+"/"+variable+"/"+limitType
            str_msg = json_msg["params"]["limitValue"] # DECIRLE A TONY QUE CORRIJA EL LIMTI
        print("Message sent: " + str(str_msg) + " to " + topic)
        mto_client.publish(topic, str_msg)
    except:
        print("[ERROR] Not able to decode message from server")

# UPM client definition
host_upm = "srv-iot.diatel.upm.es"
port_upm = 8883
client_id_pub = "group6asasiasjdidas"
pub_tb_topic = "v1/devices/me/telemetry"
sub_tb_topic = "v1/devices/me/rpc/request/+"
access_token = "gWUf7BaZMAHPKgLR3MqG"
upm_client = mqtt_client.Client(client_id_pub)
upm_client.tls_set(ca_certs=None, certfile=None, keyfile=None, cert_reqs=ssl.CERT_REQUIRED, tls_version=ssl.PROTOCOL_TLSv1_2)
upm_client.username_pw_set(access_token)
upm_client.on_connect = on_connect_upm
upm_client.on_publish = on_publish_upm
upm_client.on_disconnect = on_disconnect_upm
upm_client.on_message = on_message_upm
upm_client.connect(host_upm, port_upm)
upm_client.subscribe(sub_tb_topic)
upm_client.loop_start()

# UPM client 2 definition
host_upm = "srv-iot.diatel.upm.es"
port_upm = 8883
client_id_pub = "group6_1"
pub_tb_topic = "v1/devices/me/telemetry"
sub_tb_topic = "v1/devices/me/rpc/request/+"
access_token = "D4Hq807rOop3UecQ4YG8"
upm_client2 = mqtt_client.Client(client_id_pub)
upm_client2.tls_set(ca_certs=None, certfile=None, keyfile=None, cert_reqs=ssl.CERT_REQUIRED, tls_version=ssl.PROTOCOL_TLSv1_2)
upm_client2.username_pw_set(access_token)
upm_client2.on_connect = on_connect_upm
upm_client2.on_publish = on_publish_upm
upm_client2.on_disconnect = on_disconnect_upm
upm_client2.on_message = on_message_upm
upm_client2.connect(host_upm, port_upm)
upm_client2.subscribe(sub_tb_topic)
upm_client2.loop_start()

# Mosquitto client definition
host_mosquitto = 'test.mosquitto.org'
port_mosquitto = 1883
client_id_pub = "group6_2"
sub_phy_topic = "trough1/measures"
mto_client = mqtt_client.Client(client_id_pub)
mto_client.on_connect = on_connect_mto
mto_client.on_publish = on_publish_mto
mto_client.on_disconnect = on_disconnect_mto
mto_client.on_message = on_message_mto
mto_client.connect(host_mosquitto, port_mosquitto)
mto_client.subscribe("trough1/measures")
mto_client.subscribe("trough2/measures")
mto_client.loop_start()

while(1):
    print("Waiting for messages")
    time.sleep(10)

