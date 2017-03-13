# Extensions to Behavioral Genetic Programming

This work builds off of the work done by Krawiec et al. in their paper entitled 'Behavioral GP: A Broader and More Detailed Take on Semantic GP'. It entails an implementation of Behavioral Genetic Programming (BGP) which is introduced in their work, with the addition of several extensions to the BGP paradigm.

## Quick Start

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. Simply download the zip, or clone the repository to your local machine. To build the application, in the root folder of the repository, simply run 

```
ant -buildfile build.xml
```

This will create an ```out``` directory.  To run conventional genetic programming on the Keijzer1 data set, simply run

```
java -jar out/artifacts/BGP_steven_fine_jar/BGP_steven_fine.jar -train ./data/keijzer1 -minutes 1 -properties ./props/gp
```

## Data sets and Propery files

There are a variety of data sets in the ```data``` directory.  Any of these data sets can be used by replacing the argument for ```-train``` with a different data file. To use your own data set it must be provided in csv format where each line corresponds to a single data point and the target values are placed in the last column.

To run different configurations of genetic programming, a property file must be specified. The ```props``` directory contains a large number of different configurations for different genetic programming runs.  The three BGP configurations presented by Krawiec et al. can be run by using the files ```./props/bp2a_reptree```, ```./props/bp4_reptree```, and ```./props/bp4a_reptree``` respectively.  To use any configuration file simple replace the argument for ```-properties``` with a different propery file.

## Example

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
