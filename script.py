import csv
import os
import subprocess
import sys

if len(sys.argv) <= 3:

	retcode = subprocess.call ('python generarexcel.py '+sys.argv[1])
	retcode = subprocess.call ('python mover.py '+sys.argv[1]+" "+sys.argv[2])
else:
	print "Error en la llamada. Se necesitan carpetas origen y destino"
	print "ejemplo: python mover.py c_origen c_destino"