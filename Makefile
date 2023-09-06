CLASSPATH := \
.:./lib/json-20230618.jar:./target/classes:./target/test-classes

TEST := src/test/java/Tests.java

CLASSES := client/*.java \
	clock/*.java \
	content/*.java \
	server/*.java \
	rest/*.java \
	rest/*.java

CLASSES := $(foreach c, $(CLASSES), src/main/java/$(c))

run: compile build

build:
	java -cp $(CLASSPATH) Tests

compile: compile_class compile_test

compile_class:
	javac -d ./target/classes/ -cp $(CLASSPATH) $(CLASSES)

compile_test:
	javac -d ./target/test-classes/ -cp $(CLASSPATH) $(TEST)

clean:
	rm *.class
