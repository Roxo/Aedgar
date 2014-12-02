import csv
import os
from subprocess import Popen

ProgramCall = "java -jar Aedgar.jar"
Nodes = "5"
Represented = "2" 
TPob = "50"
Seed = "1234567"
Top = "1"
Obj = "-1"
GnWCom = "20"
Seasons = "50"
DstFolder = "datos/ecoli-0_vs_1"

c = csv.writer(open("ejemplo.csv", "wb"))
f = open('ejemplo.bat','w')

ficheros = os.listdir(r'./'+DstFolder)
lista = []
for i in ficheros:
	if "tra.dat" in i:
		archivo = i.split("tra.dat")[0]
		lista.append(archivo)

c.writerow(["echo","Etiqueta","Nodos","T_Pob","Representados","Semilla","Topologia","Objetivo","GenSinCom","epocas","Subcarpeta"])
f.write("echo Etiqueta Nodos T_Pob Representados Semilla Topologia Objetivo GenSinCom epocas Subcarpeta")

for i in lista:
	c.writerow([ProgramCall,i,Nodes,TPob,Represented,Seed,Top,Obj,GnWCom,Seasons,DstFolder])
	f.write(ProgramCall+" "+i+" "+Nodes+" "+TPob+" "+Represented+" "+Seed+" "+Top+" "+Obj+" "+GnWCom+" "+Seasons+" "+DstFolder+"\n")

f.close()

p = Popen("ejemplo.bat", cwd=r"./")
stdout, stderr = p.communicate()



c = csv.writer(open("resumenes.csv", "wb"))
c.writerow(["Etiqueta","DelCol","Llamada","Nodos","Poblacion","Representados","Semilla","Topologia","C","Epocas","minutos","T_Entrenamiento","Evaluaciones","Eval*datos","Comunicaciones","Reglas","R_5x2","R_Dat_entr","%aciertoC1","%aciertoC2"])

ficheros = os.listdir(r'./resultados')
lista = []
for i in ficheros:
	if "resumen" in i:
		lista.append(i);

print lista 
for i in lista:
	f = open("./resultados/"+i)
	datos = f.read()
	f.close()
	datos = datos.split(",")
	c.writerow(datos)
