libgdx-util
===========

Utilities for working with libgdx projects

### Upgrading libgdx version
In build.gradle, update both allprojects.version and allprojects.ext.gdxVersion to the same value.

# Utils library
## Eclipse
Generate the necessary Eclipse project files.

`./gradlew eclipse`

In Eclipse, go to File -> Import... -> Existing Projects into Workspace.

## Publishing the libgdx-util jar
Make sure the Google Drive client is installed and you have the ~/Google Drive/mvn-repo directory.

`./gradlew publish`

# Destructable Map Preprocessor

This desktop application will calculate the surface normals of an image.

`./gradlew destructable-map-preprocessor-desktop:run`

