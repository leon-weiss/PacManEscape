package name.panitz.game2d.PacManEscape;

import name.panitz.game2d.FallingImage;
import name.panitz.game2d.Vertex;

public class PacManPlayer extends FallingImage {
    public PacManPlayer(Vertex corner) {
        super("pacman.gif", corner, new Vertex(0, 0));
    }
}
