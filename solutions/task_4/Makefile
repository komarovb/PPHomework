CC=mpicc
C_FLAGS=-Wall  -g
L_FLAGS=-lm -lrt
TARGET=mpitutorial
FILES=main.o
SOURCE=main.c
HOSTFILE=hosts

${TARGET}: ${FILES}
	${CC} -o ${TARGET} ${FILES} ${L_FLAGS}

${FILES}: ${SOURCE}
	${CC} -o ${FILES} -c ${SOURCE} ${C_FLAGS}

.PHONY: clean run

run:
	mpirun --hostfile ${HOSTFILE} ./${TARGET} ${FNAME}
	
clean:
	-rm -f ${FILES} ${TARGET}
 
