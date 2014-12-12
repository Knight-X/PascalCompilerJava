package wci.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.PrintStream;

import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;


public class ParseTreePrinter
{
  private static final int INDENT_WIDTH = 4;
  private static final int LINE_WIDTH = 80;

  private PrintStream ps;
  private int length;
  private String indent;
  private String indentation;
  private StringBuilder line;

  public ParseTreePrinter(PrintStream ps)
  {
    this.ps = ps;
    this.length = 0;
    this.indentation = "";

    this.line = new StringBuilder();
    this.indent = "";

    for (int i = 0; i < INDENT_WIDTH; ++i) {
      this.indent += " ";
    }
  
  }

  public void print(ICode iCode)
  {
    ps.println("\n====== INTERMEDIATE CODE ======\n");

    printNode((ICodeNodeImpl) iCode.getRoot());  
    printLine();
  }

  private void printNode(ICodeNodeImpl node)
  {
    append(indentation);
    append("<" + node.toString());

    printAttributes(node);
    printTypeSpec(node);

    ArrayList<ICodeNode> childNodes = node.getChildren();

    if ((childNodes != null) && (childNodes.size() > 0)) {
      append(">");
      printLine();
 
      printChildNodes(childNodes);
      append(indentation);
      append("</" + node.toString() + ">");

    }

    else {
      append(" ");
      append("/>");
    }

    printLine();
  }


  private void printAttributes(ICodeNodeImpl node)
  {
    String saveIndentation = indentation;

    identation += ident;

    Set<Map.Entry<ICodeKey, Object> attributes = node.entrySet();

    Iterator<Map.Entry<ICodeKey, Object>> it = attributes.iterator();

    while (it.hasNext()) {
      Map.Entry<ICodeKey, Object> attribute = it.next();
      printAttribute(attribute.getKey().toString(), attribute.getValue());
    }

    identation = saveIndentation;
  }

  
