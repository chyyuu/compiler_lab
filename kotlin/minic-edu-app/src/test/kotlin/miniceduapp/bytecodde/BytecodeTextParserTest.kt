package miniceduapp.bytecodde

import org.junit.Test
import kotlin.test.*

class BytecodeTextParserTest {
    @Test
    fun parsesBytecodeText() {
/*
println("Hello world!");

int age = readInt();
if (age < 10) {
    exit();
}
double f = 8.0;
int i = 0;
while (i < 10) {
    while (true) {
        if (i % 2 == 0)
            break;
        i = i + 1;
    }
    i = i + 1;
}
*/
        val bytecode = """
L0
  LINENUMBER 1 L0
  GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
  LDC "Hello world!"
  INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
L1
  LINENUMBER 3 L1
  ALOAD 0
  GETFIELD MinicMain.scanner : Ljava/util/Scanner;
  INVOKEVIRTUAL java/util/Scanner.nextInt ()I
  ISTORE 1
L2
  LINENUMBER 4 L2
  ILOAD 1
  LDC 10
  IF_ICMPGE L3
  ICONST_1
  GOTO L4
L3
  ICONST_0
L4
  IFEQ L5
L6
  LINENUMBER 5 L6
  ICONST_0
  INVOKESTATIC java/lang/System.exit (I)V
L5
L7
  LINENUMBER 7 L7
  LDC 8.0
  DSTORE 2
L8
  LINENUMBER 8 L8
  LDC 0
  ISTORE 4
L9
  LINENUMBER 9 L9
L10
  ILOAD 4
  LDC 10
  IF_ICMPGE L11
  ICONST_1
  GOTO L12
L11
  ICONST_0
L12
  IFEQ L13
L14
  LINENUMBER 10 L14
L15
  LDC 1
  IFEQ L16
L17
  LINENUMBER 11 L17
  ILOAD 4
  LDC 2
  IREM
  LDC 0
  IF_ICMPNE L18
  ICONST_1
  GOTO L19
L18
  ICONST_0
L19
  IFEQ L20
L21
  LINENUMBER 12 L21
  GOTO L16
L20
L22
  LINENUMBER 13 L22
  ILOAD 4
  LDC 1
  IADD
  ISTORE 4
  GOTO L15
L16
L23
  LINENUMBER 15 L23
  ILOAD 4
  LDC 1
  IADD
  ISTORE 4
  GOTO L10
L13
  RETURN
  MAXSTACK = 100
  MAXLOCALS = 100
"""
        val expected = listOf<Instruction>(
                SimpleInstruction("GETSTATIC", "java/lang/System.out : Ljava/io/PrintStream;", line = 1),
                SimpleInstruction("LDC", "\"Hello world!\"", line = 1),
                SimpleInstruction("INVOKEVIRTUAL", "java/io/PrintStream.println (Ljava/lang/String;)V", line = 1),
                SimpleInstruction("ALOAD", "0", line = 3),
                SimpleInstruction("GETFIELD", "MinicMain.scanner : Ljava/util/Scanner;", line = 3),
                SimpleInstruction("INVOKEVIRTUAL", "java/util/Scanner.nextInt ()I", line = 3),
                SimpleInstruction("ISTORE", "1", line = 3),
                SimpleInstruction("ILOAD", "1", line = 4),
                SimpleInstruction("LDC", "10", line = 4),
                JumpInstruction("IF_ICMPGE", Label(0), line = 4),
                SimpleInstruction("ICONST_1", line = 4),
                JumpInstruction("GOTO", Label(1), line = 4),
                Label(0, line = 4),
                SimpleInstruction("ICONST_0", line = 4),
                Label(1, line = 4),
                JumpInstruction("IFEQ", Label(2), line = 4),
                SimpleInstruction("ICONST_0", line = 5),
                SimpleInstruction("INVOKESTATIC", "java/lang/System.exit (I)V", line = 5),
                Label(2, line = 5),
                SimpleInstruction("LDC", "8.0", line = 7),
                SimpleInstruction("DSTORE", "2", line = 7),
                SimpleInstruction("LDC", "0", line = 8),
                SimpleInstruction("ISTORE", "4", line = 8),
                Label(3, line = 9),
                SimpleInstruction("ILOAD", "4", line = 9),
                SimpleInstruction("LDC", "10", line = 9),
                JumpInstruction("IF_ICMPGE", Label(4), line = 9),
                SimpleInstruction("ICONST_1", line = 9),
                JumpInstruction("GOTO", Label(5), line = 9),
                Label(4, line = 9),
                SimpleInstruction("ICONST_0", line = 9),
                Label(5, line = 9),
                JumpInstruction("IFEQ", Label(6), line = 9),
                Label(7, line = 10),
                SimpleInstruction("LDC", "1", line = 10),
                JumpInstruction("IFEQ", Label(8), line = 10),
                SimpleInstruction("ILOAD", "4", line = 11),
                SimpleInstruction("LDC", "2", line = 11),
                SimpleInstruction("IREM", line = 11),
                SimpleInstruction("LDC", "0", line = 11),
                JumpInstruction("IF_ICMPNE", Label(9), line = 11),
                SimpleInstruction("ICONST_1", line = 11),
                JumpInstruction("GOTO", Label(10), line = 11),
                Label(9, line = 11),
                SimpleInstruction("ICONST_0", line = 11),
                Label(10, line = 11),
                JumpInstruction("IFEQ", Label(11), line = 11),
                JumpInstruction("GOTO", Label(8), line = 12),
                Label(11, line = 12),
                SimpleInstruction("ILOAD", "4", line = 13),
                SimpleInstruction("LDC", "1", line = 13),
                SimpleInstruction("IADD", line = 13),
                SimpleInstruction("ISTORE", "4", line = 13),
                JumpInstruction("GOTO", Label(7), line = 13),
                Label(8, line = 13),
                SimpleInstruction("ILOAD", "4", line = 15),
                SimpleInstruction("LDC", "1", line = 15),
                SimpleInstruction("IADD", line = 15),
                SimpleInstruction("ISTORE", "4", line = 15),
                JumpInstruction("GOTO", Label(3), line = 15),
                Label(6, line = 15),
                SimpleInstruction("RETURN", line = 15)
        )

        val result = BytecodeTextParser(bytecode).parse()

        assertEquals(expected, result)
    }
}