import sys
from string import ascii_letters
from string import digits
from Interpreter import SSM_Interpreter


def terminate():
  print("Exiting program due to errors!!")
  sys.exit()

def isletter(char):
  return char in ascii_letters

def isNum(char):
  return char in digits

def skipWhitespace():
  global cursor
  while cursor < len(inputProg) and inputProg[cursor] in " \t\n":
    cursor += 1

def validLabel(label):
  # first character must be a letter
  if label[0] not in ascii_letters:
    print("Error: Invalid label name: %s" %label)
    print("Labels must start with an alphabetic character")
    terminate()

  else:
    for char in label:
      if char != '_' and not char.isalnum():
        print("Error: Invalid label name: %s" %label)
        print("Labels can only include alphanumeric or '_' characters")
        terminate()

  return True



def findIntArg(opcode):
  global cursor
  skipWhitespace()

  number = ""
  while cursor < len(inputProg) and inputProg[cursor] not in " \t\n#":
    number += inputProg[cursor]
    cursor += 1

  try:
    intNumber = int(number)
    # save the instruction since it's valid
    ssmInterpreter.code_map[ssmInterpreter.pc] = [opcode, intNumber]
    ssmInterpreter.pc +=1   # always increment program counter
  except ValueError: 
    print("Error: You must have an integer argument! ")
    terminate()

  #Skip any subsequent whitespace
  skipWhitespace()
  #Read the next instruction
  readInstr()


def findJumpArg(opcode):
  global cursor
  skipWhitespace()

  labelArg = ""
  while cursor < len(inputProg) and inputProg[cursor] not in " \t\n#":
    labelArg += inputProg[cursor]
    cursor += 1

  # check if the label referenced has a proper name
  if validLabel(labelArg):
    ssmInterpreter.code_map[ssmInterpreter.pc] = [opcode, labelArg] # add instr to code_map
    ssmInterpreter.referenced_labels.append(labelArg)   # add it to referenced_labels
    ssmInterpreter.pc += 1 # always increment the program counter

  skipWhitespace()  # Skip any subsequent whitespace
  readInstr()       # Read the next instruction


def handleWord(currentStr): 
  global cursor
  # Check if currentStr is a label
  temp = cursor
  while temp < len(inputProg) and inputProg[temp] in " \t\n":
    temp += 1

  if temp < len(inputProg ) and inputProg[temp] == ':':
    cursor = temp + 1

    # check if it's a valid label
    # Method handles prog termination if it's not
    if validLabel(currentStr):
      #Save the label jump location
      ssmInterpreter.label_map[currentStr] = ssmInterpreter.pc
      ssmInterpreter.pc += 1
      #Skip unnecessary whitespace after label
      skipWhitespace()
      readInstr()

  # Not a label, check for instruction arguments
  elif currentStr == "ildc":
    findIntArg(currentStr)
  elif currentStr in jumpInstrs:
    findJumpArg(currentStr)
  elif currentStr in otherInstrs:
    skipWhitespace()
    ssmInterpreter.code_map[ssmInterpreter.pc] = [currentStr]
    ssmInterpreter.pc += 1
    readInstr()
  else:
    print("Error: Invalid instruction. %s is not a valid instruction" %currentStr)
    terminate()


# Read the next instruction
def readInstr():
  #Save the beginning index in case this is a label
  global cursor
  beginning = cursor

  #Save the current label/instruction
  currentStr = ""
  #Analyze each char in new instruction
  while cursor < len(inputProg):
    #Continue on letter
    if isletter(inputProg[cursor]):
      currentStr += inputProg[cursor]

    #Continue on non-initial number
    elif isNum(inputProg[cursor]) and cursor != beginning:
      currentStr += inputProg[cursor]

    #Continue on non-initial underscore
    elif inputProg[cursor] == '_' and cursor != beginning:
      currentStr += inputProg[cursor]

    #Check for the end of a label
    elif inputProg[cursor] == ':' and cursor != beginning:
      cursor += 1
      #Skip unnecessary whitespace after label
      skipWhitespace()

      # check if it's a valid label
      # Method handles prog termination if it's not
      if validLabel(currentStr):
        #Save next instr/label location in dictionary
        ssmInterpreter.label_map[currentStr] = ssmInterpreter.pc
        ssmInterpreter.pc += 1
        #Restart procedure and read the next instruction
        readInstr()

    # Reached the end of the instr
    elif inputProg[cursor] in " \t\n":
      handleWord(currentStr)

    #If this is a comment, skip to next line
    elif inputProg[cursor] == '#':
      cursor = inputProg.find('\n', cursor)
      if currentStr:
        handleWord(currentStr)
      else:
        skipWhitespace()
        readInstr()

    else:
      #Invalid character, exit with an error
      print("Syntax Error!")
      terminate()

    cursor += 1


# Before Executing any code, we ensure that all labels referenced by jump instructions actually exist
# This is possible by looking up to see if all referenced_labels actually exist in label_map
def checkLabelsExist():
  for referenced in ssmInterpreter.referenced_labels:
    if referenced not in ssmInterpreter.label_map:
      print("Error: You tried to reference a label that's not defined anywhere")
      print("The referenced label that caused the error was: %s" %referenced)
      terminate()


# will output the top most element in machine stack 
def output():
  try:
    topElement = ssmInterpreter.machineStack.peek()
    print("This is the integer on top of stack after code was executed: %d" %topElement)
  except IndexError:
    print("Stack is Empty after execution of your input!")


#----- BEGIN MAIN SCRIPT ----
if __name__ == "__main__":
  jumpInstrs = ("jz", "jnz", "jmp")
  otherInstrs = ("iadd", "isub", "imul", "idiv", "pop", "dup", "swap", "load", "imod", "store")
  global cursor #Global cursor for current read pos
  cursor = 0    # init global cursor

  if len(sys.argv) > 1 and sys.argv[1]:
    inputProg = open(sys.argv[1]).read()
  else:
    inputProg = sys.stdin.read()

  ssmInterpreter = SSM_Interpreter()

  #Skip whitespaces before reading instructions
  skipWhitespace()

  #Begin reading instructions
  readInstr()

  # Ensure all referenced labels exist somewhere in code
  checkLabelsExist()

  # Now run the code from beginning
  ssmInterpreter.executeCode(0)

  # Now print the top of the stack
  output()


