package wci.frontend;

import wci.intermediate.*;
import wci.message.*;

public abstract class Parser implements MessageProducer
{
  protected static SymTabStack symTabStack;
  protected static MessageHandler messageHandler;

  static{
	symTabStack = SymTabFactory.createSymTabStack();
	messageHandler = new MessageHandler();
  }

  protected Scanner scanner;


  protected Parser(Scanner scanner)
  {
    this.scanner = scanner;
    this.iCode = null;

   }

  public Scanner getScanner()
  {
    return scanner;
  }

  public ICode getICode()
  {
    return iCode;
  }

  public SymTabStack getSymTabStack()
  {
    return symTabStack;
  }

  public MessageHandler getMessageHandler()
  {
    return messageHandler;
  }

  public abstract void parse()
    throws Exception;

  public abstract int getErrorCount();


  public Token currentToken()
  {
    return scanner.currentToken();
  }

  public Token nextToken()
    throws Exception
  {
    return scanner.nextToken();
  }

  public void addMessageListener(MessageListener listener)
  {
    messageHandler.addListener(listener);
  }

  public void removeMessageListener(MessageListener listener)
  {
    messageHandler.removeListener(listener);
  }

  public void sendMessage(Message message)
  {
    messageHandler.sendMessage(message);
  }
}
