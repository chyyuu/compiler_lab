# LL-1-Parsing-Table-Calculator
derived from https://github.com/Acejoy/LL-1-Parsing-Table-Calculator

```
g++  FIRST_FOLLOW_TABLE.cpp

$./a.out
Enter the filename wgich contains the grammar rules: p1
Enter the start symbol: E

the non terminals are:{ E  E'  F  T  T'  }.

the terminals are:{ (  )  *  +  e  id  }.


 N_TERMINAL  |  PRODUCTIONS
-----------------------------------
|           E|T E',
|          E'|+ T E',e,
|           F|( E ),id,
|           T|F T',
|          T'|* F T',e,




-------------CALC FIRST SET----------------
....

```
