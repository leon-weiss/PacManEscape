import name.panitz.game2d.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

record PacManEscape(FallingImage player, List<List<? extends GameObj>> goss) implements Game {
    public static void main(String[] args) {
        new PacManEscape().play();
    }

    public PacManEscape() {
        this(new PacManPlayer(new Vertex(100, 100)), new ArrayList<>());
        init();
    }

    static String level1 = """
    
    
    ggggg
    ddddd
    """;

    @Override
    public int width() {
        return 800;
    }

    @Override
    public int height() {
        return 600;
    }

    @Override
    public FallingImage player() {
        return null;
    }

    @Override
    public List<List<? extends GameObj>> goss() {
        return List.of();
    }

    @Override
    public void init() {

    }

    @Override
    public void doChecks() {

    }

    @Override
    public void keyPressedReaction(KeyEvent keyEvent) {

    }

    @Override
    public boolean won() {
        return false;
    }

    @Override
    public boolean lost() {
        return false;
    }
}

