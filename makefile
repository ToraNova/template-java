# Makefile for a java project
# I fucking love makefiles why not.


.PHONY: all clean test jar

all:
	# Build project
	ant main

clean:
	ant clean

test:
	ant junit

jar:
	ant jar

# TODO please change the jar file name
run:
	java -jar dist/output.jar

