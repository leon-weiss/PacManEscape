package name.panitz.game2d.PacManEscape;

import name.panitz.game2d.FallingImage;
import name.panitz.game2d.Vertex;

import java.util.Random;

public class PacManGhost extends FallingImage {
    public PacManGhost(Vertex corner, char color) {
        super(getGhostImage(color), corner, new Vertex(getRandomVelocity(), 0));
    }

    @Override
    public void move() {
        super.move();
        // Simuliert die Schwerkraft bei Bewegung
        velocity().y += 0.05; // Schwerkraft
    }

    private static String getGhostImage(char color) {
        return switch (color) {
            case 'p' -> "ghost_pink.gif";
            case 'y' -> "ghost_yellow.gif";
            case 'n' -> "ghost_cyan.gif";
            default -> throw new IllegalStateException("Unexpected value: " + color);
        };
    }

    private static double getRandomVelocity() {
        double velocity = new Random().nextDouble(0.5, 1.0);
        return new Random().nextBoolean() ? velocity : -velocity;
    }
}