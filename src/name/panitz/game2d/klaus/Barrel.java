package name.panitz.game2d.klaus;

import name.panitz.game2d.FallingImage;
import name.panitz.game2d.Vertex;

public class Barrel extends FallingImage {
  public Barrel(Vertex corner) {
    super("fass.gif", corner, new Vertex(1, 0.01));
  }

  public void fromTop(double wi) {
    pos().moveTo(
        new Vertex(Math.random()*(wi - 2*40) + 40, -40));
  }
}
