all: group compiler

group:
	echo "Vikram Manja : vmanja, Alex Lagerman : alagerma"

compiler: helloworld

helloworld: helloworld.c
	$(CC) -o $@ $<

clean:
	rm helloworld