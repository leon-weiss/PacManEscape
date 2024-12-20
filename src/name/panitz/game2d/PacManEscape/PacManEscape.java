package name.panitz.game2d.PacManEscape;

import name.panitz.game2d.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static java.awt.event.KeyEvent.*;

record PacManEscape(FallingImage player, List<GameObj> hintergrund, List<GameObj> floor, List<GameObj> clouds, List<List<? extends GameObj>> goss) implements Game {
    public static void main(String[] args) {
        new PacManEscape().play();
    }

    static final int GRID_WIDTH = 34;
    static final int NUM_OF_CLOUDS = 1;

    public PacManEscape() {
        this(new PacManPlayer(new Vertex(100, 500)), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
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
    public void init() {
        resetAll(goss);
        goss().add(hintergrund());
        hintergrund().add(new ImageObject("hintergrund.png"));

        goss.add(clouds());
        for(int i = 0; i < NUM_OF_CLOUDS; i++) {clouds.add(newCloud(new Vertex(randomNum(10.0, 500.0), randomNum(10.0, 200.0))));}
    }

    @Override
    public void doChecks() {

    }

    @Override
    public void keyPressedReaction(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case VK_RIGHT -> player().right();
            case VK_LEFT -> player().left();
            case VK_SPACE -> player().jump();
        }
    }

    @Override
    public boolean won() {
        return false;
    }

    @Override
    public boolean lost() {
        return false;
    }

    private void readLevel() {
        int l = 0;
        var lines = level1.split("\\n");
        for (String line : lines) {
            int col = 0;
            for (char c : line.toCharArray()) {
                switch (c) {
                    case 'g'->floor.add(newGrassblock(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
                    case 'd'->floor.add(newDirtblock(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
                }
                col++;
            }
            l++;
        }
    }

    static ImageObject newGrassblock(Vertex corner) {
        return new ImageObject(corner, new Vertex(0, 0), "grassblock.webp");
    }

    static ImageObject newDirtblock(Vertex corner) {
        return new ImageObject(corner, new Vertex(0, 0), "dirtblock.webp");
    }

    static ImageObject newCloud(Vertex corner) {
        return new ImageObject(corner, new Vertex(randomNum(-0.5, 0.5), 0), "cloud.png");
    }

    static double randomNum(double min, double max) {
        return ((Math.random() * (max - min)) + min);
    }

    static void resetAll(List<List<? extends GameObj>> goss) {
        for (var gos : goss) {
            gos.clear();
        }

        goss.clear();
    }
}

