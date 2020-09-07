#!/usr/bin/python
# -*- coding: latin-1 -*-

import serial
import time
import json

ser=serial.Serial('/dev/ttyACM0', 9600)
last_msg=""
pos=1
n=0
data = [0.0, 0.0, 0.0]

while 1:
	print("##################Start ListenArdu######################")
	msg=ser.readline()	#lecture de la ligne du fichier
	
	#conversion byte to string
	new_msg=msg.decode(encoding="utf-8")
	print("message:  ", new_msg)
	
	
	
	#on ecrit dans le fichier si la donnée recue est differente de la precedente
	if new_msg != last_msg:
		fichier = open("data.txt", "w")
		#création d'un dict
		myMsg = {
        'gas1': float(data[0]),
        'gas2': float(data[1]),
        'gas3': float(data[2])}
		result=json.dumps(myMsg)
		print(result)
		fichier.write(result)
		fichier.close
		last_msg=new_msg
	else:
		print("fichier pas modifie")
		
		
		
	#extraction des data de la chaine, construction d'une sous chaine pour chaque valeur	
	while pos!=(-1):
		pos=new_msg.find(",")
		data[n] = new_msg[0: pos:]
		new_msg = new_msg[pos + 1 ::]
		n +=1
		
	data[n-1] = new_msg[0:-2]
	pos=1
	n=0
	
	print("##################End ListenArdu######################")
	time.sleep(1)
