#include "llvm/Pass.h"
#include "llvm/Support/raw_ostream.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Instructions.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/InstVisitor.h"
#include "llvm/IR/LegacyPassManager.h"
#include "llvm/Transforms/IPO/PassManagerBuilder.h"



using namespace llvm;

// LLVM passes are normally defined in the anonymous namespace, as they should
// only ever be exposed via their superclass interface
namespace {
/// SimplePass is a very simple example of an LLVM pass.  This runs on every
/// function and so can not change global module state.  If you want to create
/// or modify globals, then inherit from ModulePass instead.  
///
struct SimplePass : FunctionPass, InstVisitor<SimplePass>
{
  /// The module that we're currently working on
  Module *M = 0;
  /// The data layout of the current module.
  const DataLayout *DL = 0;
  /// Unique value.  Its address is used to identify this class.
  static char ID;
  /// Call the superclass constructor with the unique identifier as the
  /// (by-reference) argument.
  SimplePass() : FunctionPass(ID) {}

  /// Return the name of the pass, for debugging.
  StringRef getPassName() const override {
    return "Simple example pass";
  }

  /// doInitialization - called when the pass manager begins running this
  /// pass on a module.  A single instance of the pass may be run on multiple
  /// modules in sequence.
  bool doInitialization(Module &Mod) override {
    M = &Mod;
    DL = &Mod.getDataLayout();
    // Return false on success.
    return false;
  }

  /// doFinalization - called when the pass manager has finished running this
  /// pass on a module.  It is possible that the pass will be used again on
  /// another module, so reset it to its initial state.
  bool doFinalization(Module &Mod) override {
    assert(&Mod == M);
    M = nullptr;
    DL = nullptr;
    // Return false on success.
    return false;
  }

  /// A vector where we'll collect the alloca instructions that we've visited.
  /// Note that, unlike std::vector<>, this takes a second template argument
  /// indicating the amount of space that will be allocated inside the object
  /// for storage.  If we have fewer than 16 objects, then this won't need to
  /// call `new` or `delete` to dynamically allocate memory.
  llvm::SmallVector<AllocaInst*, 16> visitedAllocas;

  /// Example visit method.  This is called once for each alloca instruction
  /// in the function.  Implement methods like this to inspect different
  /// instructions.
  void visitAllocaInst(AllocaInst &AI) {
    // Log the alloca to the standard error
    llvm::errs() << AI << '\n';
    visitedAllocas.push_back(&AI);
  }

  bool runOnFunction(Function &F) override {
    visitedAllocas.clear();
    // The visit method is inherited by InstVisitor.  This will call each
    // of the visit*() methods, allowing individual functions to be inspected.
    visit(F);
    llvm::SmallVector<AllocaInst*, 16> foundAllocas;
    // Alternatively, we can loop over each basic block and then over each
    // instruction and inspect them individually:
    for (auto &BB : F) {
      for (auto &I : BB) {
        if (AllocaInst *AI = dyn_cast<AllocaInst>(&I)) {
          // Log the alloca to the standard error
          llvm::errs() << *AI << '\n';
          foundAllocas.push_back(AI);
        }
      }
    }
    // Note that we *must not* modify the IR in either of the previous methods,
    // because doing so will invalidate the iterators that we're using to
    // explore it.  After we've finished using the iterators, it is safe to do
    // the modifications.
    assert(foundAllocas.size() == visitedAllocas.size());
    // We have not modified this function.
    return false;
  }
};
char SimplePass::ID;

/// This function is called by the PassManagerBuilder to add the pass to the
/// pass manager.  You can query the builder for the optimisation level and so
/// on to decide whether to insert the pass.
void addSimplePass(const PassManagerBuilder &Builder, legacy::PassManagerBase &PM) {
  PM.add(new SimplePass());
}

/// Register the pass with the pass manager builder.  This instructs the
/// builder to call the `addSimplePass` function at the end of adding other
/// optimisations, so that we can insert the pass.  See the
/// `PassManagerBuilder` documentation for other extension points.
RegisterStandardPasses SOpt(PassManagerBuilder::EP_OptimizerLast,
                         addSimplePass);
/// Register the pass to run at -O0.  This is useful for debugging the pass,
/// though modifications to this pass will typically want to disable this, as
/// most passes don't make sense to run at -O0.
RegisterStandardPasses S(PassManagerBuilder::EP_EnabledOnOptLevel0,
                         addSimplePass);
} // anonymous namespace


