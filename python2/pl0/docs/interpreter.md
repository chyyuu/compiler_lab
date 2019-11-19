# Interpreter

The PL/0 interpreter uses a tree traversal approach to execute code. This means that it directly executes the tree data structure produced by the parser.

## Example

We can execute the multiply program using the interpreter:

```
$ ./pl0_interpreter.py < examples/multiply.pl0
200
-- Stack Frame --
Constants: {}
Variables: {'y': 20, 'x': 10, 'z': 200}
Procedures: {}
```

