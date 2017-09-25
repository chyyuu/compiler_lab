echo "build jar packageof simple calculator"
mvn package
echo "run simple calculator"
java -jar target/SimpleCUPCalculator-0.1-jar-with-dependencies.jar <input.test
echo "test simple calculator"
mvn test
