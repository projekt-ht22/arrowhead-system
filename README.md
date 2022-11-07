# The arrowhead system for the snowblower project
This is the arrowhead project for the snowblower.

## The different systems
- Hello: Is a simple hello example that can be used as a template for new systems it has both a provider and consumer.

## Getting started
Here is a guide to test get started and test that everything works using the Hello systems.
1. Set up a linux vm if you run on windows and continue in the vm.
2. Install the core system from [this repo](https://github.com/projekt-ht22/arrowhead-core).
2. Start core systems.
2. Compile the systems by `mvn install`.
3. Run the updatedb-before.sql script (`sudo mysql < scripts/updatedb-before.sql`).
4. Start hello-provider by in new terminal running:
```
cd hello/hello-provider/target
java -jar hello-provider-0.0.1.jar
```
5. Run the updatedb-before.sql script again (`sudo mysql < scripts/updatedb-before.sql`).
5. Stop provider by pressing `CTRL + C` in the terminal running it.
5. Run the updatedb-after.sql script again (`sudo mysql < scripts/updatedb-after.sql`).
6. Start the provider again.
7. Start the consumer by running in a new terminal:
```
cd hello/hello-consumer/target
java -jar hello-consumer-0.0.1.jar
```
8. Make sure the appropriate output is shown.

After this the system is set up correctly and hello can be used as a template to add new systems and looking into the sql scripts to update the core database to know about the systems and the connections between them.

## Adding a new system
Putt it in its own folder as in hello.
Remember to generate a certificate for each system using the instructions from [here](https://github.com/eclipse-arrowhead/core-java-spring/blob/master/documentation/certificates/create_client_certificate.pdf) using the cloud certificate in the certificate folder in the arrowhead-core repo.
Add the system to the sql scripts so that the authorization system knows what systems are allowed to consume what services.
