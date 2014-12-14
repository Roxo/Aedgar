import csv
import os
import sys

######### Escribir datos en el csv #################
if len(sys.argv) >= 2:
	subcarpeta=sys.argv[1]
	c = csv.writer(open(subcarpeta+"/resumenes.csv", "wb"))
	c.writerow(["Etiqueta","DelCol","Llamada","Nodos","Poblacion","Representados","Semilla","Topologia","C","Epocas","minutos","T_Entrenamiento","Evaluaciones","Eval*datos","Comunicaciones","Reglas","R_5x2","R_Dat_entr","%aciertoC1","%aciertoC2"])
	
	ficheros = os.listdir(subcarpeta)
	lista = []
	for i in ficheros:
		if "resumen" in i:
			lista.append(i);

	for i in lista:
		if i != "resumenes.csv":
			f = open(subcarpeta+"/"+i)
			datos = f.read()
			f.close()
			datos = datos.split(",")
			datos[0] = datos[0].replace(datos[1],"")
			datos[len(datos)-1]=datos[len(datos)-1].replace("\n","")
			c.writerow(datos)
else:
	print "Necesita introducir la subcarpeta donde se encuentran los datos"
	
