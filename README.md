# Projet de veste connectée

Ce projet vise en l'élaboration d'une veste munie de plusieurs capteurs (gaz, accéléromètre, luminosité...)
afin de garantir la sécurité du laborantin qui la porte.

Le système fait appel à plusieurs technologies telles que arduino, raspberry, Androïd. 


La prise d'informations de certains capteurs est effectuée via une arduino (pour des raisons d'évoltion) 
et envoyé à la raspberry qui elle même redirigera ces infos sur un serveur distant. 
Ensuite, ces données sont rendues visible via un smatphone qui écoute les données recu sur le serveur. 
Depuis l'application mobile, il est possible d'interagir avec la veste.  Pour la communication rasp-to-server 
et server-to-samrtphone, le protocole MQTT a été utilisé.
