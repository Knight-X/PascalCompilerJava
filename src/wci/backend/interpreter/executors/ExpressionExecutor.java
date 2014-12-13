package wci.backend.interpreter.executors;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;
import wci.backend.interpreter.*;

import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.backend.interpreter.RuntimeErrorCode.*;

public class ExpressionExecutor extends StatementExecutor
{
  public ExpressionExecutor(Executor parent)
  {
    super(parent); 
  }

  public Objeect execute(ICodeNode node)
  {
    ICodeNodeTypeImpl nodeType = (ICodeNodeTypeImpl) node.getType();

    switch (nodeType) {
      case VARIABLE: {
        SymTabEntry entry = (SymTabEntry) node.getAttribute(ID);
        return entry.getAttribute(DATA_VALUE);
      }

      case INTEGER_CONSTANT: {
        return (Integer) node.getAttribute(VALUE);
      }

      case REAL_CONSTANT: {
        return (Float) node.getAttribute(VALUE);
      }

      case STRING_CONSTANT: {
        return (String) node.getAttribute(VALUE);
      }

      case NEGATE: {
       
        ArrayList<ICodeNode> children = node.getChildren();
        ICodeNode expressionNode = children.get(0);

        Object value = execute(expressionNode);

        if (value instanceof Integer) {
          return -((Integer) value);
        } else {
          return -((Float) value);
        }
      }

      case NOT: {
         ArrayList<ICodeNode> children = node.getChildren();

         ICodeNode expressionNode = children.get(0);

         boolean value = (Boolean) execute(expressionNode);

         return !value;
      }

      default: return executeBinaryOperator(node, nodeType);
    }
  }

  private static final EnumSet<ICodeNodeTypeImpl> ARITH_OPS = EnumSet.of(ADD, SUBSTRACT, MULTIPLY, FLOAT_DIVIDE, INTEGER_DIVIDE, MOD);

  private Object executeBinaryOperator(ICodeNode node, ICodeNodeTypeIMpl nodeType)
  {
    ArrayList<ICodeNode> children = node.getChildren();
    ICodeNode operandNode1 = children.get(0);
    ICodeNode operandNode2 = children.get(1);


      
