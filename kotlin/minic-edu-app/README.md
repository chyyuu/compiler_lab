
#Very basic IDE and vizualization/simulation of Mini-C compiler

From

- https://github.com/AlexP11223/minic-edu-app
- https://github.com/AlexP11223/minic

## prepare on ubuntu 17.04 64bit
```
sudo apt install openjfx openjdk-8-jdk openjdk-8-jre
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
```
## build on ubuntu 17.04 64bit
```
mvn install -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip -B -V

```
## run on ubuntu 17.04 64bit
```
java -jar ./dist/minic-edu-app-dist/minic-edu-app.jar
```

------

## original info
[![Build Status](https://travis-ci.org/AlexP11223/minic-edu-app.svg?branch=master)](https://travis-ci.org/AlexP11223/minic-edu-app)


Very basic IDE and vizualization/simulation of Mini-C compiler (https://github.com/AlexP11223/minic).

Allows to see output for each compilation phase, such as tokens, AST image, generated JVM bytecode and to execute bytecode step-by-step simulating JVM operand stack and variables changes.

Implemented in Kotlin, using JavaFX ([TornadoFX](https://github.com/edvin/tornadofx)) for GUI, [RichTextFX](https://github.com/TomasMikula/RichTextFX) text editor.

![](http://i.imgur.com/JH78kBw.png)

![](http://i.imgur.com/XCCLbFH.png)

![](http://i.imgur.com/57O4khA.png)

![](http://i.imgur.com/LGi5RWx.png)

![](http://i.imgur.com/WS6hgoQ.png)

![](http://i.imgur.com/RcyQyRq.png)
  
# How to build

Requirements:
- JDK 8+.
- Maven 3+.

Run Maven **package** phase. This will download all dependencies, run JUnit tests and build JAR file + native application/bundle (such as .exe for Windows). Check Maven output to see if all tests and build steps are completed successfully.

(Maven is included in popular Java IDEs such as IntelliJ Idea or Eclipse. You can run it either via your IDE Maven plugin or from command line in separate [Maven installation](https://maven.apache.org/install.html): `mvn package`.)

`dist/` folder will contain JAR file and native bundles for the target OS, such as .exe for Windows, as well as sample source code files.
 
Some of the tests (as well the application itself) launch `java`, using path from `System.getProperty("java.home")`. Fallbacks to `java` (from PATH environment variable) if it is not found.

