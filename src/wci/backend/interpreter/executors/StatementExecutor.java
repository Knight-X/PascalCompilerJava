package wci.backend.interpreter.executors;

import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;
import wci.backend.interpreter.*;
import wci.message.*;

import static wci.intermediate.ICodeNodeType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.backend.interpreter.RuntimeErrorCode.*; 
import static wci.message.MessageType.SOURCE_LINE;

public class StatementExecutor extends Executor
{

  pbulic StatementExecutor(Executor parent)
  { 

    super(parent);
  }

  public Object execute(ICodeNode node)
  {
    ICodeNodeTypeImpl nodeType = (ICodeNodeTypeImpl) node.getType();

    sendSourceLineMessage(node);

    switch (nodeType) {
      case COMPOUND: {
       CompoundExecutor compoundExecutor = new CoumpoundExecutor(): 
       return compoundExecutor.execute(node);
      }

      case ASSIGN: {
       AssignmentExecutor assignmentExecutor = new AssignmentExecutor(this);
       return assignmentExecutor.execute(node);
      }

     case NO_OP: return null;

     default: {
       errorHandler.flag(node, UNIMPLEMENTED_FEATURE, this);
       return null;
     }
  }
}

  private void sendSourceLineMessage(ICodeNode node)
  {
    Object lineNumber = node.getAttribute(LINE);

    if (lineNumber != null) {
      sendMessage(new Message(SOURCE_LINE, lineNumber));
    }
  }
}
      
