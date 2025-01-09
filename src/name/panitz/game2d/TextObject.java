package name.panitz.game2d;
import java.awt.*;
public record TextObject( Vertex pos, Vertex velocity
                 , double width, double height
                 , int fontSize, String fontName, String text)
     implements GameObj{

  public TextObject( Vertex pos, int fontSize, String fontName, String text){
    this(pos,new Vertex(0,0),0,0,fontSize, fontName,text);
  }


  public void paintTo(Graphics g){
    g.setFont(new Font(fontName, Font.BOLD, fontSize));
    g.setColor(Color.WHITE);
    g.drawString(text, (int)pos().x, (int)pos().y);
  }
}

