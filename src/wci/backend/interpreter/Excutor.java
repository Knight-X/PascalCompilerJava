package wci.backend.interpreter;

import wci.backend.*;
import wci.intermediate.ICode;
import wci.intermediate.SymTabStack;
import wci.message.*;


import static wci.message.MessageType.INTERPRETER_SUMMARY;

public class Excutor extends Backend
{

  protected static int executionCount;
  protected static RuntimeErrorHandler errorHandler;

  static {
    executionCount = 0;
    errorHandler = new RuntimeErrorHandler();
  }

  public Executor() {}

  public Executor(Executor parent)
  { 
    super();
  }

  public RuntimeErrorHandler getErrorHandler()
  {
    return errorHandler;
  }

  public void process(ICode iCode, SymTabStack symTabStack)
    throws Exception
  {
    this.symTabStack = symTabStack;
    this.iCode = iCode;

    ICodeNode rootNode = iCode.getRoot();
    StatementExecutor statementExecutor = new StatementExecutor();
    statementExecutor.executor(rootNode);

    float elapsedTime = (System.currentTimeMillis() - startTime) / 1000f;

    int runtimeErrors = errorHandler.getErrorCount();

    sendMessage(new Message(INTERPRETER_SUMMARY,
                new Number[] {executionCount,
                              runtimeErrors,
                              elapsedTime}));
   }
}
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
