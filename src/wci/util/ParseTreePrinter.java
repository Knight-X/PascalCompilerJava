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
