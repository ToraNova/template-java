# Makefile for a java project
# I fucking love makefiles why not.


.PHONY: all build clean test jar run

#TODO please change name of main file
mainclass = "edu.mmu.idcrypt.idsign.PrimaryCLI"

jarfiles = $(wildcard dist/*.jar lib/*.jar)
javacpnix = $(shell echo ${jarfiles} | sed -e 's/ /:/g')
javacpwin = $(shell echo ${jarfiles} | sed -e 's/ /;/g')

all: build nixrun.sh winrun.bat
	@echo Done

build:
	ant

clean:
	rm nixrun.sh
	rm winrun.bat
	ant clean

test: build
	ant junit

jar: build
	ant jar

run: build
	java -cp $(javacpnix) $(mainclass)

nixrun.sh: build
	@echo "#/bin/sh" > nixrun.sh
	@echo "java -cp $(javacpnix) $(mainclass) \"\$$@\"" >> nixrun.sh
	@chmod u+x nixrun.sh

winrun.bat: build
	@echo "@echo off" > winrun.bat
	@echo "java -cp $(javacpwin) $(mainclass)" >> winrun.bat
