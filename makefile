# Makefile for a java project
# I fucking love makefiles why not.


.PHONY: all clean test jar

all:
	# Build project
	ant main

clean:
	ant clean

# TODO please fix this
test:
	ant junit

jar:
	ant jar

run:
	#TODO please change to the jar file name
	java -jar dist/*.jar

