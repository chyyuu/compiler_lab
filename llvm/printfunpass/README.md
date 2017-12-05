viewfunpass
==========
Tested in ubuntu 17.10 x86-64, llvm-5.0

This is a trivial LLVM pass, which can be extended to analyse or transform LLVM
IR.  To build:

	$ mkdir build
	$ cd build
	$ cmake ..
	$ make
	$ cd ..

        
This will produce a library, called FnNamePrint.so.  This library can then 
be loaded by opt.

To use it, run this command from the build directory:

	$ opt -load ./build/FnNamePrint.so -funcnameprint test.ll

Show more pass info
        $ opt -load ./build/FnNamePrint.so  -debug-pass=Structure -funcnameprint test.ll

