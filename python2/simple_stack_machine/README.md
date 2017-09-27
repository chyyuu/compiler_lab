# SSM-Interpreter

This is a very basic simple stack machine interpreter.
The program will parse an input file or stdin that includes SSM instructions.
If instructions are valid:
- it will execute the code in test file
- print the integer on top of stack after execution

### How to Run
1. Use this command to run the interpreter with a file:
`python src/main.py <test_file>`

2. You can also run using stdin if you don't supply any test files
`python src/main.py`

Note: If using stdin, you can end input and execute with `ctrl+z` or `ctrl+d`
