# Makefile for PDF_Generator

JAR=build/libs/PDF_Generator-1.0-SNAPSHOT.jar
WRAPPER=dummy-pdf-sizegen
INSTALL_DIR=/usr/local/bin

.PHONY: build install uninstall test clean

build:
	./gradlew build

install: build
	install -m 0755 $(WRAPPER) $(INSTALL_DIR)/$(WRAPPER)

uninstall:
	rm -f $(INSTALL_DIR)/$(WRAPPER)

test:
	./dummy-pdf-sizegen 1

clean:
	./gradlew clean
