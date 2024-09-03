# JSD-Datawave-Authorization-Plugin
A wrapper for the Datawave Authorization Service. It provides authentication services for connection to a specifically designed postgresql database.

## Build
In order to build this package, you need to use maven. the command to run is `maven clean verify -Pexec -Pdocker`. The `-Pdocker` requires Docker to be running
in the background to build the image. It was initially built within Eclipse.
