package name.panitz.game2d.klaus;

import name.panitz.game2d.FallingImage;
import name.panitz.game2d.Vertex;

public class Klaus extends FallingImage {
  public Klaus(Vertex corner) {
    super("player.png", corner, new Vertex(0, 0));
  }
}
