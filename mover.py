import csv
import os
import shutil

NDirectorio = "Fuzzy"
	
directoryPath = r'./resultados/'+NDirectorio
if not os.path.exists(directoryPath):
	os.mkdir(directoryPath)

for fichero in os.listdir(r'./resultados'):
		if fichero is not os.path.isdir(fichero):
			shutil.move("./resultados/"+fichero,directoryPath)
