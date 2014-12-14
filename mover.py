import csv
import os
import shutil
import sys

if len(sys.argv) <= 3:

	f_origen = sys.argv[1]
	f_destino = sys. argv[2]
	
	directoryPath = f_origen+'/'+f_destino
	if not os.path.exists(directoryPath):
		os.mkdir(directoryPath)
	
	for fichero in os.listdir(f_origen):
			if not os.path.isdir(fichero):
				shutil.move(f_origen+"/"+fichero,directoryPath)
else:
	print "Error en la llamada. Se necesitan carpetas origen y destino"
	print "ejemplo: python mover.py c_origen c_destino"