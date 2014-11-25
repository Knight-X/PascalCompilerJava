package wci.backend.interpreter;

import wci.backend.*;
import wci.intermediate.ICode;
import wci.intermeidate.SymTab;
import wci.message.*;


import static wci.message.MessageType.INTERPETER_SUMMARY;

public class Excutor extends Backend
{
  public void process(ICode iCode, SymTab symTab)
  throws Exception
  {
    long startTime = System.currentTimeMillis();
    float elapsedTime = (System.currentTimeMillis() - startTime) / 1000f;

    int executionCount = 0;
    int runtimeErros = 0;

    sendMessage(new Message(INTERPRETER_SUMMARY,
                new Number[] {
                              executionCount,
			      runtimeError,
			      elapsedTime}));
   }
}
