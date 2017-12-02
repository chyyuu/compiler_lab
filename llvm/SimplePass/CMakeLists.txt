cmake_minimum_required(VERSION 3.3)

find_program(LLVM_CONFIG NAMES llvm-config DOC "Path to llvm-config utility")
if (${LLVM_CONFIG} STREQUAL "LLVM_CONFIG-NOTFOUND")
	message(SEND_ERROR "llvm-config not found, please manually set path with -DLLVM_CONFIG")
endif()
message(STATUS "Using llvm-config: ${LLVM_CONFIG}")

exec_program(${LLVM_CONFIG} ARGS --prefix OUTPUT_VARIABLE LLVM_PREFIX)
exec_program(${LLVM_CONFIG} ARGS --cxxflags OUTPUT_VARIABLE LLVM_CONF_DEFINITIONS)
exec_program(${LLVM_CONFIG} ARGS --includedir OUTPUT_VARIABLE LLVM_INCLUDE_DIRS)
exec_program(${LLVM_CONFIG} ARGS --libdir   OUTPUT_VARIABLE LLVM_LIBRARY_DIRS)

set(CMAKE_MODULE_PATH "${LLVM_PREFIX}/lib/cmake/llvm")

set(LLVM_RUNTIME_OUTPUT_INTDIR ".")
set(LLVM_LIBRARY_OUTPUT_INTDIR ".")
include(HandleLLVMOptions)
include(AddLLVM)

add_definitions(${LLVM_CONF_DEFINITIONS})
include_directories(${LLVM_INCLUDE_DIRS})
link_directories(${LLVM_LIBRARY_DIRS})

add_llvm_loadable_module(SimplePass 
	SimplePass.cc
)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

