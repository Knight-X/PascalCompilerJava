public enum HH{
  AND,
  OR,
  GG,
  PLUS("+"),
  MINUS("-");

  private String text;

  HH()
  {
    this.text = this.toString();
  }

  HH(String text)
  {
    this.text = text;
  }

  public String getText()
  {
    return text;
  }

  public static void main(String[] args) {
  
  for (HH p : HH.values()){
    System.out.printf("%s\n", p.getText());
  }
 }
}
