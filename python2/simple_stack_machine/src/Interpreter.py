from SSM_Stack import SSMStack
import sys

class SSM_Interpreter:

  def __init__(self):
    self.machineStack = SSMStack()   # main data stack
    self.machineStore = {}           # main store memory
    self.pc = 0                      # program counter

    self.label_map = {}              # look up table for labels, store pc of label as value

    self.code_map = {}               # store each line of code in this dictionary
                                     # keys to this map will be the program counter
    
    self.referenced_labels = []      # keep all labels that are referenced in jump instruction in here
                                     # this will be only used for checking if referenced labels actually exist somewhere in code
                                     
    self.instruct_map = {            # map instruction set to interpreter functions
      "ildc": self.push,
      "iadd": self.add,
      "isub": self.sub,
      "imul": self.mul,
      "idiv": self.div,
      "imod": self.mod,
      "pop": self.pop,
      "dup": self.dup,
      "swap": self.swap,
      "jz": self.jz,
      "jnz": self.jnz,
      "jmp": self.jump,
      "load": self.load,
      "store": self.store
    }

  # exit program if there are errors in execution
  def terminate(self):
    print("Exiting program due to errors!!")
    sys.exit()

  # use this function whenever you want to ensure the top most element is an integer
  # otherwise it will print an error and terminate program
  def safePop(self):
    try:
      number = int(self.machineStack.pop())
      return number
    except IndexError:
      print("Error: Empty Stack: IndexError occurred while execution.")
      self.terminate()
    except ValueError:
      print("Error: The elements you tried to pop weren't integers!")
      self.terminate()


  # ildc num: push the given integer num on to the stack.
  def push(self, args):
    #check if num is an integer
    try:
      if isinstance(args, list):
        num = int(args[1])
      else:
        num = args
      self.machineStack.push(num)
    except ValueError:
      print("Error: You can only push an integer into stack using ildc")
      self.terminate()


  # iadd: pop the top two elements of the stack, add them, and push their sum on to the stack.
  def add(self, args):
    num1 = self.safePop()
    num2 = self.safePop()
    self.machineStack.push( num1 + num2 )


  # subtract the top-most element on stack from the second-to-top element; 
  # pop the top two elements of the stack, and push the result of the subtraction on to the stack.
  def sub(self, args):
    num1 = self.safePop()
    num2 = self.safePop()
    self.machineStack.push( num2 - num1 )


  # pop the top two elements of the stack, multiply them, and push their product on to the stack.
  def mul(self, args):
    num1 = self.safePop()
    num2 = self.safePop()
    self.machineStack.push( num1 * num2 )

  # divide the second-to-top element on the stack by the top-most element; 
  # pop the top two elements of the stack, and push the result of the division (the quotient) on to the stack.
  def div(self, args):
    num1 = self.safePop()
    num2 = self.safePop()
    self.machineStack.push( int(num2 / num1))



  # divide the second-to-top element on the stack by the top-most element; 
  # pop the top two elements of the stack, and push the result of the division (the remainder) on to the stack.
  def mod(self, args):
    num1 = self.safePop()
    num2 = self.safePop()
    self.machineStack.push( num2 % num1 )


  # pop the top-most element of the stack.
  def pop(self, args):
    self.safePop()

  # push the value on the top of stack on to the stack (i.e. duplicate the top-most entry in the stack).
  def dup(self, args):
    peekValue = self.machineStack.peek()
    self.push(peekValue)

  # swap the top two values on the stack. That is, if n is the top-most value on the stack, 
  # and m is immediately below it, make m the top most value of the stack with n immediately below it.
  def swap(self, args):
    num1 = self.safePop()
    num2 = self.safePop()
    self.machineStack.push(num1)
    self.machineStack.push(num2)

  def jumpHelper(self, jumpTo):
    code = -1
    # Check if jumpTo exists in code_map
    if jumpTo in self.code_map:
      self.executeCode(jumpTo)
      return 0

    # support nested Labels
    elif jumpTo in self.label_map.values():
      jumpTo += 1
      code = 0
      return self.jumpHelper(jumpTo)

    else:
      print("Error: There was an error jumping to referenced label.")
      print("This is probably because there's no code to execute after label")
      self.terminate()


  # jz label : pop the top most value from the stack; 
  # if it is zero, jump to the instruction labeled with the given label;
  #  otherwise go to the next instruction. --> this is handled by executeCode()
  def jz(self, args):
    topElement = self.safePop()
    if topElement == 0:
      # we check before execution that labels actually exists so no need to check again
      label = args[1]
      jumpTo = self.label_map[label] + 1
      result = self.jumpHelper(jumpTo)  # Now jump
      return result
    return 
      

  # jnz label: pop the top most value from the stack; 
  # if it is not zero, jump to the instruction labeled with the given label; 
  # otherwise go to the next instruction.
  def jnz(self, args):
    topElement = self.safePop()
    if topElement != 0:
      label = args[1]
      jumpTo = self.label_map[label] + 1
      result = self.jumpHelper(jumpTo) # Now jump with the helper, also handles nested labels
      return result
    return


  # jmp label: jump to the instruction labeled with the given label.
  def jump(self, args):
    label = args[1]
    jumpTo = self.label_map[label] + 1
    result = self.jumpHelper(jumpTo)
    return result


  #  the top-most element of the stack is the address in store, say a. 
  #  This instruction pops the top-most element, and pushes the value at address a in store.
  def load(self, args):
    address = self.safePop()
    try:
      self.machineStack.push( self.machineStore[address] )  # push the value at address to stack
    except KeyError:
      print("Error: AddressErr: There is no value at address: %d" % address)
      self.terminate()



  # Treat the second-to-top element on the stack as an address a, and the top-most element as an integer i. Pop the top two elements from stack. The cell at address a in the store is updated with integer i.
  def store(self, args):
    value = self.safePop()
    address = self.safePop()
    # now update the dictionary (store memory)
    self.machineStore[address] = value


  # This function will execute the code after its been error checked
  # It will utilize the code_map dictionary to go through each instruction sequentially
  # Then it executes each instruction using the methods in this class
  def executeCode(self, start):
    for pc in xrange(start, len(self.code_map) + len(self.label_map)):
      if pc in self.code_map:
        instruction = self.code_map[pc] # get each instruction
        opcode = instruction[0]         # get the opcode 

        # Now execute code depending on what instruction it was
        if opcode in self.instruct_map:
          returnCode = self.instruct_map[opcode](instruction)
          
          # returnCode will ONLY return 0 if jump instruction results in jumping to a label 
          # we wanna break out of this main loop 
          # jump instruction will handle continuing execution after the referenced label
          if returnCode == 0:
            break


