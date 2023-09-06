CLASSPATH = \
.:./lib/json-20230618.jar:target/classes:target/test-classes

build:
	java -cp $(CLASSPATH) Tests
