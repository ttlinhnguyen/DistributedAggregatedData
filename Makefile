CLASSPATH := \
.:./lib/json-20230618.jar:./target/classes:./target/test-classes

TEST := src/test/java/*.java

CLASSES := client/*.java \
	clock/*.java \
	client/content/*.java \
	client/getclient/*.java \
	server/*.java \
	server/helpers/*.java \
	rest/*.java \
	rest/*.java

CLASSES := $(foreach c, $(CLASSES), src/main/java/$(c))

run: compile tests

unit:
	java -cp $(CLASSPATH) UnitTests
tests:
	java -cp $(CLASSPATH) ScenarioTests

server:
	java -cp $(CLASSPATH) server.AggregationServer

client:
	java -cp $(CLASSPATH) client.getclient.GETClient localhost:4567

content:
	java -cp $(CLASSPATH) client.content.ContentServer localhost:4567 data1.txt

compile: make_dir compile_class compile_test

make_dir:
	mkdir -p target
	mkdir -p target/classes
	mkdir -p target/test-classes

compile_class:
	javac -d ./target/classes/ -cp $(CLASSPATH) $(CLASSES)

compile_test:
	javac -d ./target/test-classes/ -cp $(CLASSPATH) $(TEST)


clean:
	rm *.class
