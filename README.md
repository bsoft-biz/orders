<a href="http://bsoft.biz/">![Business Software, Ltd](src/main/resources/poweredby.png)</a>
# Orders application

## How to use

### Before using you must rename and configure these files:
* persistence.properties.sample to persistence.properties
* email.properties.sample to email.properties
* provide settings for email and database

### To use oracle database you need to install oracle jdbc into your local maven repository
#### Get Oracle jdbc driver
Download it from oracle.com or get it from `{ORACLE_HOME}\jdbc\lib\`

#### Install Oracle driver
`mvn install:install-file -Dfile={Path/to/your/ojdbc.jar} -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0.2 -Dpackaging=jar`

[Wiki page] (https://github.com/bsoft-biz/orders/wiki/How-to-add-Oracle-JDBC-driver-in-your-Maven-local-repository)

### Install fronted utils, package managers and load packages
[Install node.js] (https://nodejs.org/en/download/), then run:
```
npm install
npm install -g grunt-cli
grunt copy
```


## Testing
```
npm run protractor
```
