CC=javac
FLAGS=-g
SRC=$(wildcard *.java)
TARGETS=$(SRC:.java=.class)
OUT=Maze
ARGS=10 10
CLASSPATH=.

all: $(TARGETS)
	java -classpath $(CLASSPATH) -ea $(OUT) $(ARGS)

%.class: %.java
	$(CC) -cp $(CLASSPATH) $(FLAGS) $<

run:
	java -classpath $(CLASSPATH) -ea $(OUT) $(ARGS)

debug:$(TARGETS)
	jdb -classpath $(CLASSPATH) $(OUT) $(ARGS)

clean:
	rm *.class
