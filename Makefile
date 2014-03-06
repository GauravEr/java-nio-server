all: compile
	@echo -e '[INFO] Done!\n' 
clean:
	@echo -e '\n[INFO] Cleaning Up..'
	@-rm -rf cs455

compile: clean
	@echo -e '[INFO] Compiling the Source..'
	@javac -d . src/cs455/scale/**/*.java src/cs455/scale/**/**/*.java


