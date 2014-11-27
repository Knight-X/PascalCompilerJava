package wci.frontend;

import java.io.BufferReader;
import java.io.IOException;

import wci.message.*;
import static wci.message.MessageType.SOURCE_LINE;

public class Source implements MessageProducer
{
  public sataic findal char EOL = '\n';
  public static final char EOF = (char) 0;

  private BufferReader reader;
  private String line;

  private int lineNum;
  private int currentPos;

  private MessageHandler messageHandler;


  public Source(BufferReader reader)
    throws IOException
  {
    this.lineNum = 0;
    this.currentPos = -2;
    this.reader = reader;

    this.messageHandler = new MessageHandler();

  }

  public int getLineNum()
  {
    return lineNum;
  }

  public int getPosition()
  {
    return currentPos;
  }

  public char currentChar()
    throws Excpetion
  {
    if (currentPos == -2) {
        readLine();
        return nextChar();
    }

    else if (line == null){
      return EOF;
    }

    else if ((currentPos == -1) || (currentPos == line.length())){
      return EOL;
    }

    else if (currentPos > line.length()){
      readLine();
      return nextChar();
    }

    else {
      return line.charAt(currentPos);
    }
  }

  public char nextChar()
    throws Exception
  {
    ++currentPos;
    return currentChar();
  }

  public char peekChar()
    throws Exception
  {
    currentChar();
    if (line == null) {
      return EOF;
    }
  
    int nextPos = currentPos + 1;

    return nextPos < line.length() ? line.charAt(nextPos) : EOL;
  }


  private void readLine()
    throws IOException
  {
    line = reader.readLine();

    currentPos = -1;

    if (line != null) {
      ++lineNum;

    }

    if (line != null) {
       sendMessage(new Message(SOURCE_LINE, 
		new Object[] {lineNum, line}));
    }

 }


  public void close()
    throws Exception
  {
    if (reader != null) {
      try {
        reader.close();
      }
      catch (IOException ex) {
         ex.printStackTrace();
         throw ex;
       }
    }
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
 }
