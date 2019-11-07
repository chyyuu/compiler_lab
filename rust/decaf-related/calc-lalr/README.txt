Please see contents in

https://mashplant.gitbook.io/decaf-doc/pa1a/lalr1-shi-yong-zhi-dao/yi-ge-wan-zheng-de-li-zi

Tested on rustc 1.40.0-nightly (1423bec54 2019-11-05) in ubuntu 19.10 x86-64

Prepare
 - 安装rust nightly
 - 安装 xdot `sudo apt install xdot`
 
Try
```
$ cargo build   //compiling
$ cargo run     //running
$ xdot dfa.dot  //show 词法的确定有限状态自动机
$ xdot fsm      //show LALR的语法有限状态机
$ less verbose.txt //show 产生式
```
