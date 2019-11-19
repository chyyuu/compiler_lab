# Machine

The PL/0 virtual machine is a stack based execution environment. It starts executing at offset 0, and stops executing when the offset becomes invalid, typically using the `HALT` opcode.

## Example

In this example, we demonstrate assembling some code to print the number 25, and then we feed this directly to the virtual machine for execution.

```
-- Generate some machine code to print out the number 25
$ echo "PUSH 25 PRINT" | ./pl0_assembler.py
[10, 25, 50]

-- Execute the machine code
$ echo "[10, 25, 50]" | ./pl0_machine.py
25
-- Machine State --
Sequence: [10, 25, 50]
Stack: [25]
Offset: 3
```

