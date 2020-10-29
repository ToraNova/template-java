# Makefile for a java project
# I fucking love makefiles why not.


.PHONY: all build clean test jar run

#TODO please change name of main file
mainclass = "edu.mmu.idcrypt.idsign.PrimaryCLI"

jarfiles = $(wildcard dist/*.jar lib/*.jar)
javacpnix = $(shell echo ${jarfiles} | sed -e 's/ /:/g')
javacpwin = $(shell echo ${jarfiles} | sed -e 's/ /;/g')

#TODO ensure script files have the right name
all: build nixrun.sh winrun.bat
	@echo Done

#TODO ensure script files have the right name
clean:
	rm -f nixrun.sh
	rm -f winrun.bat
	ant clean

build:
	ant


test: build
	ant junit

jar: build
	ant jar

run: build
	java -cp $(javacpnix) $(mainclass)

# TODO ensure script files have the right name
nixrun.sh: build
	@echo "#/bin/sh" > $@
	@echo "java -cp $(javacpnix) $(mainclass) \"\$$@\"" >> $@
	@chmod u+x nixrun.sh

winrun.bat: build
	@echo "@echo off" > $@
	@echo "java -cp $(javacpwin) $(mainclass)" >> $@
