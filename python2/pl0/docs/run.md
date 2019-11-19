# How to run the examples

## Using the interpreter

From the main project directory, simply execute the interpreter and feed the source code via standard input:

```
$ ./pl0_interpreter.py < examples/fibonacci.pl0
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
10946
-- Stack Frame --
Constants: {'K': 20}
Variables: {'count': 21, 'k': 17711, 'm': 17711, 'n': 28657}
Procedures: {}
```

## Using the compiler, assembler and virtual machine

From the main project directory, execute the compiler and feed the source code via standard input. Pass this to the assembler, and then to the virtual machine.

```
$ ./pl0_compiler.py < examples/fibonacci.pl0 | ./pl0_assembler.py | ./pl0_machine.py
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
10946
-- Machine State --
Sequence: [16, 6, 17711, 28657, 17711, 21, 10, 1, 7, 2, 10, 1, 7, 3, 10, 1, 7, 4, 10, 0, 7, 5, 6, 5, 10, 20, 31, 22, 57, 6, 4, 50, 11, 6, 3, 7, 4, 6, 2, 6, 3, 45, 7, 3, 6, 4, 7, 2, 6, 5, 10, 1, 45, 7, 5, 16, 22, 1]
Stack: []
Offset: -1
```

