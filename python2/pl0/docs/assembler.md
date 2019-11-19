# Assembler

An assembler converts symbolic opcodes and labels into executable machine code.

Symbolic opcodes such as `LOAD` or `ADD` cannot be executed directly, the need to be converted to machine executable bytecode. These get converted by using a lookup table.

Labels need to be converted to memory addresses. Because symbolic assembly code may have a non-specific location, using labels makes it easier to produce output from compiler and by hand.

The PL/0 assembler is very simple. It supports all the pl0 symbolic opcodes, and direct labels.

## Example Usage

The following program is a program that generates the fibonacci sequence.

```
# Firstly we JMP to main, because the execution starts at offset 0
	JMP main
# This contains f[n]
m:
	1
# This contains f[n+1]
n:
	1

# The number of iterations to calculate
count:
	20

# The main function which is a loop to generate the next Fibonacci number.
main:
	LOAD m
	PRINT
	LOAD n
	ADD
	LOAD n
	SAVE m
	SAVE n

# Decrement the counter
	PUSH 1
	LOAD count
	SUB
	DUP
	SAVE count
	JLT main
```

This code can be compiled to machine executable code:

```
-- Assemble the above fibonacci program
$ ./pl0_assembler.py < examples/fibonacci.pl0a
[16, 5, 1, 1, 20, 6, 2, 50, 6, 3, 45, 6, 3, 7, 2, 7, 3, 10, 1, 6, 4, 46, 12, 7, 4, 20, 5]

-- Feed the machine code to the virtual machine
$ ./pl0_assembler.py < examples/fibonacci.pl0a | ./pl0_machine.py
1
1
2
3
5
8
13
21
34
55
89
144
233
377
610
987
1597
2584
4181
6765
-- Machine State --
Sequence: [16, 5, 10946, 17711, 0, 6, 2, 50, 6, 3, 45, 6, 3, 7, 2, 7, 3, 10, 1, 6, 4, 46, 12, 7, 4, 20, 5]
Stack: []
Offset: 27
```