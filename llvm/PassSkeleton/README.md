# Pass Skeleton

Tested in ubuntu 17.10 x86-64, llvm-5.0

A completely useless LLVM pass.

Build:

    $ mkdir build
    $ cd build
    $ cmake ..
    $ make 

Run:

    $ clang -Xclang -load -Xclang SkeletonPass.so ../tests/test.c
