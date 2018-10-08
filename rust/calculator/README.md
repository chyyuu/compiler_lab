# calculator in rust
Derived From https://github.com/asmoaesl/rsc

**RSC is a handwritten scientific calculator for turning an equation inside a string into a result.** 

The abbreviation can be interpreted in two majorly different ways:
* Rust Scientific Calculations library
* Rust Scientific Calculator

## Installation
First, you should nstall rustup, cargo, rustc, etc. for basic rust development.

### Compile from Source Using Cargo
You can access option information by starting RSC with the option "help", such as:
 `cargo run -- help`
OR just try with AST
 `cargo run -- ast`
OR just try 
 `cargo run `

### Use
RSC will open to a single input dialog with an arrow pointing to the right. Here you just enter a mathematical expression and it will attempt to calculate it and print the result. The expression can include spaces.
```
>2 + 2
4
>3(3)
9
>sqrt 4
2
>2+3*4
14
>2+3(4)
14
>(3)(4)
12
>sqrt 4
2
>sqrt(4)
2
>sqrt 4 + 2
4
>sin(6.5/9.7)
0.6210667900937665
>sin cos tan 2
-0.5449592372801408
```
Similarly, you can exit RSC by entering "exit" or "quit" at the dialog.
RSC can be run with the `ast` flag and show the internal expression that was created by the parser. This is most commonly used for entertainment purposes 😛.
```
cargo run -- ast
>pi*2^2
BinOp(
    Star,
    Constant(
        3.141592653589793
    ),
    Pow(
        Constant(
            2.0
        ),
        Constant(
            2.0
        )
    )
)
12.566370614359172
```
### Errors
```
cargo run
>oops
Lexer error: InvalidIdentifier("oops")
>3.3.1
Lexer error: InvalidNumber("3.3.1")
>2+
Parser error: ExpectedFactor(None)
>2(3
Parser error: ExpectedClosingParenthesis
```

## syntax accepted for mathematical expressions in RSC.

### Numbers
RSC is pretty lenient when it comes to a number and how you represent it. For example, `-0.1` is the same as `-.1`.

The following is a list of accepted examples of numbers.
* `2`
* `256`
* `-256`
* `.451`
* `-.451`

The above is valid, but the following is not.
* `-.`
* `2.1.3`
* `2.`

### Exponentials
RSC supports exponentials using the caret (`^`). You can enter any term, to the power of, any term.

Expressions like `2^3` and `3+2^3` work as you expect them to. But the expression `2^2+1` probably doesn't do what you think it does. For example:
```
>2^3
8
>2^2+1
5
```
You can also chain exponentials together: `2^2^(1+1)`, which is interpreted as `(2^2)^(1+1)`.

### Constants & Functions
#### Available Constants & Functions
| Constants | Functions |
|-----------|-----------|
| E         | SQRT      |
| PI        | SIN       |
|           | COS       |
|           | TAN       |
|           | LOG       |

Constants and functions can be in any case you choose, be it uppercase, lowercase, or mixed case.

#### Constants in Expressions
A constant is interpreted as an ordinary number. For example, when you enter `pi`, it is just placing the value of PI at that exact location. Expressions like `3pi` are not valid, and are read as `3` and `3.14159...`. Instead, use `3(pi)` or similar.

#### Functions in Expressions
A function takes the next term immediately following as its operand. For example, `sqrt 2`, `sqrt (2)`, and `sqrt(1+1)` are all the same; though `sqrt 1+1` is interpreted as `sqrt(1) + 1`. This makes using the functions simpler. You can say `sin cos tan 2` instead of `sin(cos(tan(2)))`.

### Precedence
Precedence in RSC is a little weird. The following table can explain more than I ever could in words.

| Operation                     | first:last; 0:4 |
|:------------------------------|----------------:|
| Functions, Constants, Numbers |               0 |
| Parenthesis                   |               1 |
| Exponents                     |               2 |
| Multiplication & Division     |               3 |
| Addition & Subtraction        |               4 |

Two things to note:
1. Functions and constants are considered to be terms themselves.
2. Multiplication & division, like addition & subtraction, are on the same level. **`2/3*4` calculates the `2/3` first, not the `3*4`.** Same with `2-3+4`. The reason for this is because most people wouldn't trust the calculator this much and just put parenthesis like `2/(3*4)` instead, and secondly, because it would be another hundred lines of code to solve and complicate the code further. Fork if it matters to you. Sorry for any inconveniences.

## License
RSC is MIT licensed. RSC will always remain free to modify and use without attribution.
