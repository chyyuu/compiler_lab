from collections import deque

class SSMStack(deque):
  push = deque.append  #push to stack
  pop = deque.pop      #pop

  def peek(self):
    return self[-1]   # peek top element