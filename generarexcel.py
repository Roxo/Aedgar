import csv
import os


######### Escribir datos en el csv #################

c = csv.writer(open("./resultados/resumenes.csv", "wb"))
c.writerow(["Etiqueta","DelCol","Llamada","Nodos","Poblacion","Representados","Semilla","Topologia","C","Epocas","minutos","T_Entrenamiento","Evaluaciones","Eval*datos","Comunicaciones","Reglas","R_5x2","R_Dat_entr","%aciertoC1","%aciertoC2"])

ficheros = os.listdir(r'./resultados')
lista = []
for i in ficheros:
	if "resumen" in i:
		lista.append(i);

print lista
for i in lista:
	if i != "resumenes.csv":
		f = open("./resultados/"+i)
		datos = f.read()
		f.close()
		datos = datos.split(",")
		datos[0] = datos[0].replace(datos[1],"")
		datos[len(datos)-1]=datos[len(datos)-1].replace("\n","")
		c.writerow(datos)
	
	
