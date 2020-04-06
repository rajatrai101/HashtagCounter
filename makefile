JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASS = hashtagcounter.java

default: class

class: $(CLASS:.java=.class)

clean:
	$(RM) *.class

