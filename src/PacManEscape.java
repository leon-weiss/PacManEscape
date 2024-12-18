import name.panitz.game2d.Game;
import name.panitz.game2d.GameObj;

import java.awt.event.KeyEvent;
import java.util.List;

record PacManEscape() implements Game {


        public static void main(String[] args) {
            System.out.println("Hello, World!");
        }

    @Override
    public int width() {
        return 0;
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public GameObj player() {
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

