## Run a specific data set across all configurations

import subprocess
import os
import sys

build = './builds/extended-bgp.jar'

data = sys.argv[1]
runs = int(sys.argv[2])

for run in xrange(runs):
    print 'Run: ' + str(run)
    for props in os.listdir('./props'):
        if props.endswith('python') and run >= 5:
            pass
        elif not props.startswith('.'):
            print 'Props: ' + props

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
