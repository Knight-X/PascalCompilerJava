package wci.backend.interpreter;

import wci.backend.*;
import wci.intermediate.ICode;
import wci.intermediate.SymTabStack;
import wci.message.*;


import static wci.message.MessageType.INTERPRETER_SUMMARY;

public class Excutor extends Backend
{
  public void process(ICode iCode, SymTabStack symTabStack)
  throws Exception
  {
    long startTime = System.currentTimeMillis();
    float elapsedTime = (System.currentTimeMillis() - startTime) / 1000f;

    int executionCount = 0;
    int runtimeErrors = 0;

    sendMessage(new Message(INTERPRETER_SUMMARY,
                new Number[] {
                              executionCount,
			      runtimeErrors,
			      elapsedTime}));
   }
}
