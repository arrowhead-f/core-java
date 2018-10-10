[![Build Status](https://api.travis-ci.com/arrowhead-f/core-java.svg?branch=develop)](https://travis-ci.com/arrowhead-f/core-java)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?colorB=green)](https://github.com/arrowhead-f/core-java/blob/master/LICENSE)

# Arrowhead Framework 4.2

[Arrowhead](http://www.arrowhead.eu/) (and its continuation, [Productive4.0](https://productive40.eu/)) is an ambitious holistic innovation project,
 meant to open the doors to the potentials of Digital Industry and to maintain a leadership position of the industries in Europe. All partners involved will work on creating the capability to efficiently design and integrate hardware and software of Internet of Things (IoT) devices. Linking the real with the digital world takes more than just adding software to the hardware.
 
### Building and running this project from source

The project has the following dependencies:
* JRE/JDK 8
* Maven 3
* MySQL server 5.7 (other SQL databases can work with Hibernate ORM, but the `common module pom.xml` has to include the appropriate connector 
dependency to use them)

**NOTE:** MySQL server 8 is not supported yet! Be careful, this is the default one downloaded from the MySQL site.

Each core system module has a `config` folder, and inside this folder a `default.conf` file, containing default values for the environment 
variables needed to run the core system, such as database username/password, web-server address, logging settings and much more.

Config files are processed the following way:
* The active working directory is searched for `default.conf`
  * If found, the key-value pairs are loaded in from the file.
  * If not found, the program looks for `config/default.conf` and load those values.
  * If this is also not found, the program throws an error, and the core system wil fail to start.
* The active working directory is searched for `app.conf`
  * If found, the key-value pairs inside this file override the values from `default.conf` (if the default value for a key is fine, the `app.conf` 
  does not need to contain it)
  * If not found, the program looks for `config/app.conf` and use those values.
  * If this is also not found, then all the `default.conf` values will remain.
  
All the `app.conf` files are in `.gitignore`, so local environment variables do not get pushed to the Github repository. **The recommended way to 
configure a local installation is to create `app.conf` files with the values that need to change for each core system.**

After the config files are inline with the local environment, **use `mvn install` inside the root folder of the repository, to build all the core 
system JARs.** The build will create a `target` folder inside every module, where there will be the copied `config` folder, a `lib` folder containing
 all the dependencies, and the actual core system JAR.
 
Each core system looks for the following 3 command line argument (others are ignored):
* **`-d`**: stands for debug mode. When this is passed at startup, the core system will print every incoming HTTP request (with the payload, if 
there is any), and the corresponding HTTP response to the console. If the console output is forwarded to a file at startup, this is additional 
logging, that can be useful to follow operation.
* **`-daemon`**: this argument starts the core system as a daemon (background) process on UNIX systems. This means it will listen to kill signals,
 and can shutdown gracefully (for example deregister its service from the Service Registry).
* **`-tls`**: starts the core system in "secure" mode. Certificate file related configs are used to start a HTTPS web-server with a proper SSL 
context in this mode (and to provide system identity), and core systems use the TLS protocol to communicate with each other. A secure and insecure 
version of the same core system can run at the same time on different ports, but an insecure core system can not communicate with a secure core 
system.

The Orchestrator core system also has a **`-nogk`** argument. When used, the Orchestrator will start in "no Gatekeeper" mode, where it won't look
 for the Gatekeeper services in the Service Registry, but can only do intra-cloud orchestration (with the help of the Authorization and Service 
 Registry core systems).

Startup bash scripts are provided in the `scripts` folder:
* `start_insecure_coresystems.sh`: starts the core systems without using certificates, with plain HTTP
* `start_secure_coresystems.sh`: starts the core systems using certificates, with HTTPS
* `stop_coresystems.sh`: stops all running core systems

When the core systems are running, they will log to 2 different places, if the default logging configuration is unchanged:
* All core systems will log to the same `logs` table inside a `log` database. This log source will contain log 
messages from all the core systems 
in one place.
* Each core system will log to a file in its active working directory called `log4j_log.txt`. These text files are separate for each core system, 
meaning one text file only contains the log messages of one core system.

The databases schema(s) have to be created before starting the core systems. The `logs` table also has to be manually created, because that is 
only used by the logging library, and the ORM library does not know about it. The ORM library can create all the other arrowhead tables, if they do 
not exist yet. An SQL script can be found at `common/config/create_arrowhead_logs.sql` to create the `logs` table.

The project can also be run from an IDE for testing purposes. Just import the multi-module project as a maven project, and the IDE should find all 
the `pom.xml` files necessary to download the dependencies and start the core systems.