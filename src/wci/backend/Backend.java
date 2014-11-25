package wci.backend;

import wci.intermediate.ICode;
import wci.intermediate.SymTab;
import wci.message.*;

public abstract class Backend implements MessageProducer
{
  protected static MessageHandler messageHandler;

  static {
    messageHandle = new MessageHandler();
  }

  protected SymTab symTab;
  protected ICode iCode;

  public ICode getICode()
  {
    return iCode;
  }

  public SymTab getSymTab()
  {
    return symTab;
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

    
    
