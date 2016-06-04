# PDI SDMX Plugin
 This is a [Pentaho Data Integration](http://community.pentaho.com/projects/data-integration/) input step plugin 
 that allows the user to consume webservices provided by various istitutions that disseminate statistical timeseries 
 based on [SDMX](https://sdmx.org/) technical standard.
 
 The plugin is actually a wrapper around a connector java library, available [here](https://github.com/amattioc/SDMX/tree/master/JAVA)
 as stand-alone project. It helps user to both explore the dataflows containing the data and build a query,
 in SDMX format, setting the right codes for each dimension. These dimensions, along with the time of observation
 and the relevant numeric value, are then picked up as fields in the step output stream, as we are accustomed to do in
 a pdi scenario.
 
## BUILD INSTRUCTIONS
 This is a maven project than can be built from a parent pom using two alternative profiles:
 
 * with `mvn clean package -P dist` you will obtain the two artifacts, the actual plugin and the sdmx dependency jar
  in the conventional target build folder of maven
 * with `mvn clean package -P dev` you will install directly the plugin into pdi folder. To do so, adjust the property
  `<kettle.folder>`, contained in parent pom, according to your environment.