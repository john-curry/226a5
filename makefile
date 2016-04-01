CC=javac
FLAGS=-g
SRC=$(wildcard *.java)
TARGETS=$(SRC:.java=.class)
OUT=Maze
ARGS=3 3
CLASSPATH=.

all: $(TARGETS)

%.class: %.java
	$(CC) -cp $(CLASSPATH) $(FLAGS) $<

run:
	java -classpath $(CLASSPATH) -ea $(OUT) $(ARGS)

debug:$(TARGETS)
	jdb $(OUT) $(ARGS)

clean:
	rm *.class
