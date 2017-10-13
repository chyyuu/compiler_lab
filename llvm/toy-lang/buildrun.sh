clang++ -g toy_lang_compiler.cpp `llvm-config --cxxflags --ldflags --system-libs --libs core mcjit native` -O3 -o toy_lang_compiler
./toy_lang_compiler example1
