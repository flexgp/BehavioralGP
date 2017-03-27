# Extensions to Behavioral Genetic Programming

This work builds off of the work done by Krawiec et al. in their paper entitled 'Behavioral GP: A Broader and More Detailed Take on Semantic GP'. It entails an implementation of Behavioral Genetic Programming (BGP) which is introduced in their work, with the addition of several extensions to the BGP paradigm.

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

## Quick Start

The following works on Ubuntu 14.04.5 LTS. Simply download the zip, or clone the repository to your local machine. (If you are using a different system, you may need to change the value of ```jdk.home.1.8``` to point to the location of your JDK compiler.)

To build the application, in the root folder of the repository, simply run 

```bash
ant -f build.xml
```

This will create an `out` directory.

To run conventional genetic programming on the Keijzer1 data set for one minute, simply run

```bash
java -jar out/artifacts/BGP_steven_fine_jar/BGP_steven_fine.jar -train ./data/keijzer1 -minutes 1 -properties ./props/gp
```
This will create a file called `mostAccurate.txt`, which contains the most accurate model from the run. It shows the fitness and an S-expression of the model, e.g.
```bash
$ cat mostAccurate.txt
-0.22959479982773615,0.08131685328492509,(mylog (cos (* (sin (sin (+ (mylog (cos (sin (sin (+ (mylog (cos (* (sin (+ (mylog (cos (sin (sin (+ (mylog (mylog (cos (sin (sin (+ (mylog (cos (* (sin (sin (+ (mylog (cos (sin (sin (sin (sin (mylog (mylog X1)))))))) (mylog (sin (mylog X1)))))) X1))) (mylog (mylog X1)))))))) X1))))) (mylog (mylog X1)))) (mydivide (sin X1) X1)))) (mylog (mylog X1))))))) (mylog (mylog X1))))) X1)))
```

## Data sets

There are a variety of data sets in the `data` directory.  Any of these data sets can be used by replacing the argument for `-train` with a different data file. E.g. `-train ./data/nguyen9`

To use your own data set it must be provided in csv format where each line corresponds to a single data point and the target values are placed in the last column.

## Propery files

To run different configurations of genetic programming, a property file must be specified. The `props` directory contains a large number of different configurations for different genetic programming runs.  

The three BGP configurations presented by Krawiec et al. can be run by using the files `./props/bp2a_reptree`, `./props/bp4_reptree`, and `./props/bp4a_reptree` respectively.  To use any configuration file simple replace the argument for `-properties` with a different propery file, E.g. `-properties ./props/bp4_reptree`.

# Documentation

See `docs` folder.
