package wci.backend.interpreter.executors;

import java.util.ArrayList;

import wci.intermediate.*;
import wci.backend.interpreter.*;

public class CompoundEexcutor extends StatementExcutor
{

  public CompoundExecutor(Executor parent)
  {

    super(parent);
  }

  public Object execute(ICodeNode node)
  {
    StatementExecutor statementExecutor = new StatementExecutor(this);
    ArrayList<ICodeNode> children = node.getChildren();

    for (ICodeNode child : children) {
      statementExecutor.execute(child);
    }

    return null;
  }
}
