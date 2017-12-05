#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/Support/raw_ostream.h"

using namespace llvm;

namespace {
	struct FnNamePrint: public FunctionPass {
		static char ID;
		FnNamePrint () : FunctionPass(ID) {}
		bool runOnFunction(Function &F) override {
			errs() << "Function " << F.getName() << '\n';
			return false;
		}
	};
}

char FnNamePrint::ID = 0;
static RegisterPass< FnNamePrint > X("funcnameprint",
"Function Name Print", false, false);
