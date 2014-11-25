package wci.backend.compiler;

import wci.backend.*;
import wci.intermediate.ICode;
import wci.intermediate.SymTab;
import wci.message.*;

import static wci.message.MessageType.COMPILER_SUMMARY;

public class CodeGenerator extends Backend
{
  public void process(ICode iCode, SymTab symTab)
    throws Exception
  {
    long startTime = System.currentTimeMills();
    float elapsedTime = (System.currentTimeMills() - startTime) / 1000f;
   
    int instructionCount = 0;

     sendMessage(new Message(COMPILER_SUMMARY, 
                 new Number[] {instructionCount,
				elapsedTime}));
  }
}
