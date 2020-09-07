import time
import json

from random import seed
from random import randint
from datetime import datetime

import paho.mqtt.client as mqtt
from gpiozero import LED
from time import sleep
import RPi.GPIO as GPIO

from sense_emu import SenseHat	#emu pour utilisation de emulateur

sense = SenseHat()
broker="farmer.cloudmqtt.com"
port=12425
user="trapccpv"
pwd="9710kh6VgsVe"

rmv = [10, 8]

class Wearer:
	nom=""
	
class dataStruct:
    gas1=0.0
    gas2=0.0
    gas3=0.0
    
my_dataStruct=dataStruct();
my_wearer=Wearer();

#initialisation du nm du porteur (sauvegarde)
f_read_wearer = open("porteur_NAME.txt", "r")
my_wearer.nom = f_read_wearer.read()
f_read_wearer.close

print(my_wearer.nom)
print("@ du nom de la classe à l'origine: ", id (my_wearer.nom))




#callbacks
def on_connect (client, userdata, flags, rc):
    print("Connected with code: " +str(rc))
    #subscribe topic
    client.subscribe("jacket1")

    
def on_message (client, userdata, msg):
    print ("Topic: "+ msg.topic+"\nMessage recu: "+str(msg.payload))
    received_msg = json.loads(msg.payload)
    #modification du nom du porteur si celui-ci est different du precedent
    if received_msg["porteurNom"] != my_wearer.nom:
        my_wearer.nom= received_msg["porteurNom"]
        f_write_wearer = open("porteur_NAME.txt", "w")
        f_write_wearer.write(my_wearer.nom)
        f_write_wearer.close
    else:
        print("pas de changement du nom du porteur")
		


    
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.username_pw_set(user, pwd)
client.connect(broker, port, 60)


#client.loop_forever()
client.loop_start()
time.sleep(1)
while True:
	
	#lecture depuis le senseHat de la temp, pression et humidity
    #hum=randint(40, 80)
    hum = sense.get_humidity()
    #tempC=randint(15, 60)
    tempC=sense.get_temperature()
    #pressure=randint(15, 60)
    pressure=sense.get_pressure()
    
	#lecture des capteurs de gas depuis le fichie data
    f = open("data.txt", "r")
    read_msg = f.read()
    f.close
    print(read_msg)
    json_msg = json.loads(read_msg)
    my_dataStruct.gas1= json_msg["gas1"]
    my_dataStruct.gas2= json_msg["gas2"]
    my_dataStruct.gas3= json_msg["gas3"]
    '''
    vargas1 = randint(0, 10)
    vargas2= randint(0, 10)
    vargas3 = randint(0, 10)
    '''
    
    heure=str(datetime.now())
    heure = heure[rmv[0] + 1 ::]
    heure = heure[0: rmv[1]:]
    print(heure)
    
    print ("Nom de porteur actuel: ", my_wearer.nom)
    print("@ du nom envoyé: ", id (my_wearer.nom))
    
    ########dictionnaire#######
    myPubmsg = {
        'porteurNom': my_wearer.nom,
        'humidity': hum,
        'temperature': tempC,
        'pressure': pressure,
        'gas1': my_dataStruct.gas1,
        'gas2': my_dataStruct.gas2,
        'gas3': my_dataStruct.gas3,
        'time': heure
    }
    send_msg=json.dumps(myPubmsg) #conversion en json
    client.publish("jacket1", send_msg)
    print("###############################################################################")
    time.sleep(2)
    
client.loop_stop()
client.disconnect() 
    
    
