package wci.backend;

import wci.intermediate.ICode;
import wci.intermediate.SymTab;
import wci.message.*;

public abstract class Backend implements MessageProducer
{
  protected static SymTabStack symTabStack;
  protected static MessageHandler messageHandler;

  static {
    messageHandler = new MessageHandler();
  }

  protected ICode iCode;

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

  public abstract void process(ICode iCode, SymTab symTab)
    throws Exception;
  

  public void sendMessage(Message message) 
  {
    messageHandler.sendMessage(message);
  }

  public void addMessageListener(MessageListener listener)
  {
    messageHandler.addListener(listener);

  }

  public void removeMessageListener(MessageListener listener)
  {

    messageHandler.removeListener(listener);
  }
}

    

