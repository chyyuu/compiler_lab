# Parser

A parser converts a sequence of tokens into a tree data structure. This tree can then be used for analyzing the program.

## Example

Here is the PL/0 syntax tree for the multiply program:

```
$ ./pl0_parser.py examples/multiply.pl0
PROGRAM
  BLOCK
    VARIABLES
      ('NAME', 'x')
      ('NAME', 'y')
      ('NAME', 'z')
    BEGIN
      SET
        ('NAME', 'x')
        EXPRESSION
          TERM
            ('NUMBER', 10)
      SET
        ('NAME', 'y')
        EXPRESSION
          TERM
            ('NUMBER', 20)
      SET
        ('NAME', 'z')
        EXPRESSION
          TERM
            ('NAME', 'x')
            TIMES
              ('NAME', 'y')
      PRINT
        EXPRESSION
          TERM
            ('NAME', 'z')
```

### Source Code Visualisation

We can also visualise programs using graphviz:

```
-- You will need to install graphviz in order to generate graphs from source code files.
$ ./pl0_graphviz.py  examples/multiply.pl0
Generating Graph...
dot - graphviz version 2.26.3 (20100126.1600)
-- Snip --
Opening Graph...
//show the graph 
$xdot graph.dot   //OR  $okular graph.pdf
```