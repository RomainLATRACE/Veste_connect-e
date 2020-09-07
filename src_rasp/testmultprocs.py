-#!/usr/bin/python
# -*- coding: latin-1 -*-


import time
import json
import subprocess
import os
from multiprocessing import Pool

processes = ('ListenArdu.py','Read_file.py')

def run_process(process):
	os.system('python {}'.format(process))


pool = Pool(processes=2)
pool.map(run_process, processes)
