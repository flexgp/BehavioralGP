## Run a specific configuration across all datasets

import subprocess
import os
import sys

build = './builds/extended-bgp.jar'

props = sys.argv[1]
runs = int(sys.argv[2])

for run in xrange(runs):
    print 'Run: ' + str(run)
    for data in os.listdir('./data'):
        if not data.startswith('.'):
            print 'Data: ' + data
            
            args = ['java', '-jar', build, '-run']
            args.append('./data/' + data)
            args.append('-properties')
            args.append('./props/' + props)
            code = -1
            while code != 0:
                code = subprocess.call(args)
            fr = open('temp_output', 'r')
            line = fr.read().split('\n')[0]
            fr.close()
            fw = open('./output/' + props + '_' + data, 'a')
            fw.write(line)
            fw.write('\n')
            fw.close()
