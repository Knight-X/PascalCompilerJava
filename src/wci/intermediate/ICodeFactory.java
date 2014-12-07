package wci.intermediate;

import wci.intermediate.icodeimpl.ICodeImpl;
import wci.intermedidate.icodeimpl.ICodeNodeImpl;

public class ICodeFactory
{

  public static ICode createCode()
  {

    return new ICodeImpl();
  }

  public static ICodeNode createICodeNode(ICodeNodeType type)
  {
    return new ICodeNodeImpl(type);
  }

}
