### October 1st, 2018
* Input validation properly works now, used the incorrect dependencies before
* Exception handling improved
* Fixed some NullPointerException sources
* Removed `broker_name` and `authentication_info` columns from `broker` database table, unique constraint also deleted
* Added [Swagger UI](https://swagger.io/tools/swagger-ui/) to each core system, to help the discoverability of the API
* Merged the `feature/dev_sys_reg` branch into the `develop` branch, which contains the updated System Registry core system

### September 24th, 2018
* `README.md` now has a detailed guide on how to setup an arrowhead cloud from source
* The Gatekeeper AccessControlFilter had a critical bug fixed
* Added a 2nd relay test certificate
* Basic continuous integration tooling added to the project
* Core systems now wait 10 second and retry with the service registration 3 times, when the SR is not available at start (makes core system 
deployment easier) 

### September 12th, 2018
* Changed the way config files (properties file before) are processed by the core systems. Now there is a `default.conf` for each core system, 
containing default values, and these values can be overridden with key-value pairs placed in a `app.conf` file, which only has to contain the 
properties with the new values.

### September 7th, 2018
* Certificate Authority core system is ready for use now. It uses the Bouncy Castle library. There is a certificate requester client inside the 
client-java repository, which connects to this core system, and creates a usable keystore from the response. 

### September 5th, 2018
* AccessControlFilters now have an abstract parent class to avoid duplicate codes
* Added 2 different certificate signing method to the CA module, both of which still need thorough testing

### August 27th, 2018
* Fixed the registered packages on web server startup
* ArrowheadCloud and ArrowheadSystem names now allow for the following special characters too: _ - :
* Added custom exception to certificate validation path related errors, when sending request to other systems
* ServiceRegistryEntry contains a String metadata field again (With the current security metadata, this is needed to be able to provide a service 
in secure and insecure mode at the same time)
* Fixed a bug in Gatekeeper AccessControlFilter, preventing GSD/ICN process in secure mode
* Secure and insecure mode of the full framework can now work at the same time with the same database, if the ArrowheadCloud (and OwnCloud + 
NeighborCloud) table contains both Gatekeeper versions.

### August 13th, 2018
* Our complicated custom input validation solution replaced with a clean, easy to use annotation based validation library (Hibernate validator)
* Management API refactorings, to provide ID-field based resource query, update and delete operations
* Common resources (services, systems, clouds, devices) have their management REST endpoints in the common module now, so each module can provide their own endpoints for their database
* Complex entity classes now use Hibernate's @OnDelete annotation to delete child entities, when a parent entity gets deleted
* Created new common resource: ArrowheadDevice
* Service metadata is moved back to ArrowheadService, and the port field is moved back to ArrowheadSystem (from ServiceRegistryEntry)