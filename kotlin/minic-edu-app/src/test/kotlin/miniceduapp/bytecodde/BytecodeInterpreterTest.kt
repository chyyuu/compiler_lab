package miniceduapp.bytecodde

import minic.Compiler
import org.junit.Test
import kotlin.test.*

class BytecodeInterpreterTest {
    @Test
    fun handlesValueSizes() {
        val bytecode = BytecodeTextParser("""
  LDC 42
  ISTORE 1
  LDC 8.0
  LDC 6.0
  DADD
  DSTORE 2
  LDC "Hello"
  ASTORE 4
  DLOAD 2
  ILOAD 1
  ALOAD 4
  RETURN
""").parse()

        with(BytecodeInterpreter(bytecode)) {
            start()
            executeNextInstruction()

            assertEquals(listOf(
                IntValue(42)
            ), operandStack)
            assertNull(variables[1])

            executeNextInstruction()

            assertEquals(emptyList(), operandStack)
            assertEquals(IntValue(42), variables[1])
            assertNull(variables[2])

            executeNextInstruction()

            assertEquals(listOf(
                    DoubleValue(8.0),
                    WideValuePart()
            ), operandStack)
            assertEquals(IntValue(42), variables[1])
            assertNull(variables[2])
            assertNull(variables[2])

            executeNextInstruction()

            assertEquals(listOf(
                    DoubleValue(6.0),
                    WideValuePart(),
                    DoubleValue(8.0),
                    WideValuePart()
            ), operandStack)

            executeNextInstruction()

            assertEquals(listOf(
                    DoubleValue(14.0),
                    WideValuePart()
            ), operandStack)
            assertNull(variables[2])

            executeNextInstruction()

            assertEquals(emptyList(), operandStack)
            assertEquals(listOf(
                    IntValue(42),
                    DoubleValue(14.0),
                    WideValuePart()
            ), variables.subList(1, 4))

            executeNextInstruction()

            assertEquals(listOf(
                    StringValue("Hello")
            ), operandStack)
            assertEquals(listOf(
                    IntValue(42),
                    DoubleValue(14.0),
                    WideValuePart()
            ), variables.subList(1, 4))
            assertNull(variables[4])

            executeNextInstruction()

            assertEquals(emptyList(), operandStack)
            assertEquals(listOf(
                    IntValue(42),
                    DoubleValue(14.0),
                    WideValuePart(),
                    StringValue("Hello")
            ), variables.subList(1, 5))

            executeNextInstruction()

            assertEquals(listOf(
                    DoubleValue(14.0),
                    WideValuePart()
            ), operandStack)
            assertEquals(listOf(
                    IntValue(42),
                    DoubleValue(14.0),
                    WideValuePart(),
                    StringValue("Hello")
            ), variables.subList(1, 5))

            executeNextInstruction()

            assertEquals(listOf(
                    IntValue(42),
                    DoubleValue(14.0),
                    WideValuePart()
            ), operandStack)
            assertEquals(listOf(
                    IntValue(42),
                    DoubleValue(14.0),
                    WideValuePart(),
                    StringValue("Hello")
            ), variables.subList(1, 5))

            executeNextInstruction()

            assertEquals(listOf(
                    StringValue("Hello"),
                    IntValue(42),
                    DoubleValue(14.0),
                    WideValuePart()
            ), operandStack)
            assertEquals(listOf(
                    IntValue(42),
                    DoubleValue(14.0),
                    WideValuePart(),
                    StringValue("Hello")
            ), variables.subList(1, 5))
        }
    }

    @Test
    fun canExit() {
        val bytecode = BytecodeTextParser(Compiler("""
if (true) {
    println("Exit");
    exit();
}
println("NotExit");
""").bytecodeText()).parse()
        val expectedOutput = """
Exit
""".trimStart()

        val sb = StringBuilder()
        BytecodeInterpreter(bytecode, onOutput = {
            sb.append(it)
        }).execute()

        assertEquals(expectedOutput, sb.toString())
    }

    @Test
    fun executes() {
        val bytecode = BytecodeTextParser(Compiler("""
print("Hello");
println("");
string name = "John";
string str = "Hello " + name + "!";
println(str);
double h = 8;
int x = 42;
println("x = " + toString(x));
println(toString(h));
int y = x + 1 - 2 + 0 + 1 - 1;
println(toString(y));
y = y * 2 + (8 / 4) + 1021 % 2;
println(toString(y));
int neg = -1;
println(toString(neg));
double dneg = -1.0;
println(toString(dneg));
double r = (1.0 + 2.0 - 0.5) * (8.0 / 2.0) + 7.0 % 2;
println(toString(r));
if (false) {
    println("n");
}
if (true) {
    println("t");
}
bool flag = true;
println(toString(flag));
println(toString(!flag));

println("");
println("eq");
int i1 = 42; int i2 = 43; int i3 = 42;
double f1 = 42.5; double f2 = 42.0; double f3 = 42.5;
bool b1 = true; bool b2 = false; bool b3 = true;
string s1 = "Hello"; string s2 = "not hello"; string s3 = "Hello";
println(toString( i1 == i1 ) + " " + toString( i1 == i2 ) + " " + toString( i2 == i1 ) + " " + toString( i1 == i3 ) + " " + toString( i3 == i1 ));
println(toString( i1 != i1 ) + " " + toString( i1 != i2 ) + " " + toString( i2 != i1 ) + " " + toString( i1 != i3 ) + " " + toString( i3 != i1 ));
println(toString( f1 == f1 ) + " " + toString( f1 == f2 ) + " " + toString( f2 == f1 ) + " " + toString( f1 == f3 ) + " " + toString( f3 == f1 ));
println(toString( f1 != f1 ) + " " + toString( f1 != f2 ) + " " + toString( f2 != f1 ) + " " + toString( f1 != f3 ) + " " + toString( f3 != f1 ));
println(toString( b1 == b1 ) + " " + toString( b1 == b2 ) + " " + toString( b2 == b1 ) + " " + toString( b1 == b3 ) + " " + toString( b3 == b1 ));
println(toString( b1 != b1 ) + " " + toString( b1 != b2 ) + " " + toString( b2 != b1 ) + " " + toString( b1 != b3 ) + " " + toString( b3 != b1 ));
println(toString( s1 == s1 ) + " " + toString( s1 == s2 ) + " " + toString( s2 == s1 ) + " " + toString( s1 == s3 ) + " " + toString( s3 == s1 ));
println(toString( s1 != s1 ) + " " + toString( s1 != s2 ) + " " + toString( s2 != s1 ) + " " + toString( s1 != s3 ) + " " + toString( s3 != s1 ));
println(toString( i1 == f2 ) + " " + toString( f2 == i1 ));
println(toString( i1 != f2 ) + " " + toString( f2 != i1 ));

println("");
println("cmp");
i1 = 42; i2 = 43; i3 = 42;
f1 = 42.5; f2 = 42.6; f3 = 42.5;
println(toString( i1 < i1 ) + " " + toString( i1 < i2 ) + " " + toString( i2 < i1 ) + " " + toString( i1 < i3 ) + " " + toString( i3 < i1 ));
println(toString( i1 > i1 ) + " " + toString( i1 > i2 ) + " " + toString( i2 > i1 ) + " " + toString( i1 > i3 ) + " " + toString( i3 > i1 ));
println(toString( i1 <= i1 ) + " " + toString( i1 <= i2 ) + " " + toString( i2 <= i1 ) + " " + toString( i1 <= i3 ) + " " + toString( i3 <= i1 ));
println(toString( i1 >= i1 ) + " " + toString( i1 >= i2 ) + " " + toString( i2 >= i1 ) + " " + toString( i1 >= i3 ) + " " + toString( i3 >= i1 ));
println(toString( f1 < f1 ) + " " + toString( f1 < f2 ) + " " + toString( f2 < f1 ) + " " + toString( f1 < f3 ) + " " + toString( f3 < f1 ));
println(toString( f1 > f1 ) + " " + toString( f1 > f2 ) + " " + toString( f2 > f1 ) + " " + toString( f1 > f3 ) + " " + toString( f3 > f1 ));
println(toString( f1 <= f1 ) + " " + toString( f1 <= f2 ) + " " + toString( f2 <= f1 ) + " " + toString( f1 <= f3 ) + " " + toString( f3 <= f1 ));
println(toString( f1 >= f1 ) + " " + toString( f1 >= f2 ) + " " + toString( f2 >= f1 ) + " " + toString( f1 >= f3 ) + " " + toString( f3 >= f1 ));
println(toString( i1 < f2 ) + " " + toString( f2 < i1 ));
println(toString( i1 > f2 ) + " " + toString( f2 > i1 ));
println(toString( i1 <= f2 ) + " " + toString( f2 <= i1 ));
println(toString( i1 >= f2 ) + " " + toString( f2 >= i1 ));

println("");
println("and or");
{
    bool a = true;
    bool b = false;
    println(toString( a && a ) + " " + toString( a && b ) + " " + toString( b && a ) + " " + toString( b && b ));
    println(toString( a || a ) + " " + toString( a || b ) + " " + toString( b || a ) + " " + toString( b || b ));
    println(toString( !a ) + " " + toString( !b ) + " " + toString( !!a ));
    println(toString( a && (a || b) ));
    println(toString( a && (!a || b) ));
}

println("");
println("vars");
{
    int a = 0;
    if (true)
    {
        double b = 1.0;
        int c = 2;
        string d = "hello";
        {
            double e = 4.5;
            int f = 5;

            println(toString(b));
            println(toString(c));
            println(d);
            println(toString(e));
            println(toString(f));
        }
        c = 42;
        int e = a + 6;

        println(toString(e));
        println(toString(c));
    }
    int b = 7;
    double c = 8;
    {
        int d = 9;
        println(toString(d));
    }
    bool e = true;

    println(toString(a));
    println(toString(b));
    println(toString(c));
    println(toString(e));
}

println("");
println("if");
flag = true;
if (flag)
    println("if1");
if (flag) {
    println("if2");
    if (!flag)
        println("if3");
    else {
        println("else1");
    }
} else {
    println("else2");
}

println("");
println("while");
flag = true;
int c = 0;
while (c < 3) {
    c = c + 1;
    println(toString(c));
}
while (!flag) {
    println("unreachable");
}
flag = true;
c = 0;
while (true) {
    c = c + 1;
    println(toString(c));
    if (c >= 3)
        break;
}
while (!flag) {
    while (true) {
        flag = false;
        break;
        println("unreachable");
    }
}
""").bytecodeText()).parse()
        val expectedOutput = """
Hello
Hello John!
x = 42
8.0
41
85
-1
-1.0
11.0
t
true
false

eq
true false false true true
false true true false false
true false false true true
false true true false false
true false false true true
false true true false false
true false false true true
false true true false false
true true
false false

cmp
false true false false false
false false true false false
true true false true true
true false true true true
false true false false false
false false true false false
true true false true true
true false true true true
true false
false true
true false
false true

and or
true false false false
true true true false
false true true
true
false

vars
1.0
2
hello
4.5
5
6
42
9
0
7
8.0
true

if
if1
if2
else1

while
1
2
3
1
2
3
""".trimStart()

        val sb = StringBuilder()
        BytecodeInterpreter(bytecode, onOutput = {
            sb.append(it)
        }).execute()

        assertEquals(expectedOutput, sb.toString())
    }
}
