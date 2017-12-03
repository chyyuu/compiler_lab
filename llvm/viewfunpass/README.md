viewfunpass
==========
Tested in ubuntu 17.10 x86-64, llvm-5.0

This is a trivial LLVM pass, which can be extended to analyse or transform LLVM
IR.  To build:

	$ mkdir Build
	$ cd Build
	$ cmake ..
	$ make

        
This will produce a library, called ViewfunctionPass.so.  This library can then 
be loaded by clang and will automatically insert itself at the end of the 
optimisation pipeline.

To use it, run this command from the build directory:

	$ clang -Xclang -load -Xclang ./ViewfunctionPass.so -c ../tests/t.c

The pass will be invoked at any optimisation level.

