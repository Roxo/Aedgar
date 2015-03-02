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
DstFolder = ["datos/ecoli-0_vs_1","datos/ecoli-0_vs_1Discretizado",
"datos/glass-0-1-2-3_vs_4-5-6","datos/glass-0-1-2-3_vs_4-5-6Discretizado",
"datos/habermanImb","datos/habermanImbDiscretizado","datos/iris0",
"datos/iris0Discretizado","datos/new-thyroid1","datos/new-thyroid1Discretizado"]
CarpetaResultados = "resultados/Triangular_Aprox/"
depuracion = "0"
cobertura = "2"
aproximativo = "1"
############## Creamos ficheros bat y csv ####################
c = csv.writer(open("ejecucionTr_I.csv", "wb"))
f = open('ejecucionTr_I.bat','w')
c.writerow(["echo","Etiqueta","Nodos","T_Pob","Representados","Semilla","Topologia","Objetivo","GenSinCom","epocas","Subcarpeta","Cobertura","Aproximativo","Carpeta Resultados","Depuracion"])
f.write("echo Etiqueta Nodos T_Pob Representados Semilla Topologia Objetivo GenSinCom epocas Subcarpeta Cobertura Aproximativo Carpeta_Resultados Depuracion ")

#################### Buscamos los ficheros que queremos y les quitamos el tra.dat ###################
for j in DstFolder:
	ficheros = os.listdir(r'./'+j)
	lista = []
	for i in ficheros:
		if "tra.dat" in i:
			archivo = i.split("tra.dat")[0]
			lista.append(archivo)
	for i in lista:
		c.writerow([ProgramCall,i,Nodes,TPob,Represented,Seed,Top,Obj,GnWCom,Seasons,j,cobertura,aproximativo,CarpetaResultados,depuracion])
		f.write(ProgramCall+" "+i+" "+Nodes+" "+TPob+" "+Represented+" "+Seed+" "+Top+" "+Obj+" "+GnWCom+" "+Seasons+" "+j+ " "+cobertura+" "+aproximativo+" "+CarpetaResultados+" "+depuracion+" "+"\n")

f.close()
