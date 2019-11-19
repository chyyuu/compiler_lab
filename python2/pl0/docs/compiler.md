# Compiler

The PL/0 compiler processes PL/0 source code and produces PL/0 assembly language.

## Example

Here is an example PL/0 program to generate the fibonacci sequence:

```
VAR m, n, k, count;

BEGIN
	m := 1;
	n := 1;
	count := 0;
	
	WHILE count <= 20 DO
	BEGIN
		k := n;
		n := m + n;
		m := k;
		
		! k;
		
		count := count + 1
	END
END.
```

Using the PL/0 compiler we can convert this program to PL/0 assembly language:

```
$ ./pl0_compiler.py  examples/fibonacci.pl0
```

The assembly code generated:

```
JMP main
t_var_m_1:
	0
t_var_n_2:
	0
t_var_k_3:
	0
t_var_count_4:
	0
main:
	PUSH 1
	SAVE t_var_m_1
	PUSH 1
	SAVE t_var_n_2
	PUSH 1
	SAVE t_var_k_3
	PUSH 0
	SAVE t_var_count_4
t_while_start_5:
	LOAD t_var_count_4
	PUSH 20
	CMPLTE
	JE t_while_end_6
	LOAD t_var_k_3
	PRINT
	POP
	LOAD t_var_n_2
	SAVE t_var_k_3
	LOAD t_var_m_1
	LOAD t_var_n_2
	ADD
	SAVE t_var_n_2
	LOAD t_var_k_3
	SAVE t_var_m_1
	LOAD t_var_count_4
	PUSH 1
	ADD
	SAVE t_var_count_4
	JMP t_while_start_5
t_while_end_6:
	HALT
```