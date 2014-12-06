package wci.backend.compiler;


import wci.backend.*;
import wci.intermediate.ICode;
import wci.intermediate.SymTabStack;
import wci.message.*;

import static wci.message.MessageType.COMPILER_SUMMARY;

public class CodeGenerator extends Backend
{
  public void process(ICode iCode, SymTabStack symTabStack)
    throws Exception
  {
    long startTime = System.currentTimeMillis();
    float elapsedTime = (System.currentTimeMillis() - startTime) / 1000f;
   
    int instructionCount = 0;

     sendMessage(new Message(COMPILER_SUMMARY, 
                 new Number[] {instructionCount,
				elapsedTime}));
  }
}
