package wci.backend.interpreter;

import wci.backend.*;
import wci.intermediate.icodeimpl.*;
import  wci.intermediate.*;
import wci.backend.interpreter.executors.*;
import wci.message.*;

import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.message.MessageType.INTERPRETER_SUMMARY;


public class Executor extends Backend
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

    long startTime = System.currentTimeMillis();

    ICodeNode rootNode = iCode.getRoot();
    StatementExecutor statementExecutor = new StatementExecutor(this);
    statementExecutor.execute(rootNode);

    float elapsedTime = (System.currentTimeMillis() - startTime) / 1000f;

    int runtimeErrors = errorHandler.getErrorCount();

    sendMessage(new Message(INTERPRETER_SUMMARY,
                new Number[] {executionCount,
                              runtimeErrors,
                              elapsedTime}));
   }
}

