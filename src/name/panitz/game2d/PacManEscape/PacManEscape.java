package name.panitz.game2d.PacManEscape;

import name.panitz.game2d.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.awt.event.KeyEvent.*;

record PacManEscape(FallingImage player, List<GameObj> hintergrund, List<GameObj> block, List<GameObj> floor, List<GameObj> clouds, List<List<? extends GameObj>> goss) implements Game {
    public static void main(String[] args) {
        new PacManEscape().play();
    }

    static final int GRID_WIDTH = 50;
    static final int NUM_OF_CLOUDS = 3; //NICHT HÖHER ALS 10 (sonst StackOverflow)
    static int currentLevel;

    public PacManEscape() {
        this(new PacManPlayer(new Vertex(100, 400)), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        init();
    }

    static String level1 = """
    #
    #
    #
    #          bbbb
    #
    #  bb
    #
    #      bbb
    # bbb       b
    #
    gggggggggggggggg
    dddddddddddddddd
    """;

    static String level2 = """
    #
    #
    #
    #
    #
    #
    #
    #
    #
    #
    gggggggggggggggg
    dddddddddddddddd
    """;

    static String level3 = """
    #
    #
    #
    #
    #
    #
    #
    #
    #
    #
    gggggggggggggggg
    dddddddddddddddd
    """;

    static final String[] levels = {level1, level2, level3};

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

        currentLevel = 0;
        nextLevel();

        goss().add(hintergrund());
        hintergrund().add(new ImageObject("hintergrund.png"));

        goss.add(clouds());
        for(int i = 0; i < NUM_OF_CLOUDS; i++) {clouds.add(newCloud(cloudSpawn()));}

        goss().add(floor());
        goss().add(block());
    }

    @Override
    public void doChecks() {
        for (var c:clouds()) {
            if (c.isLeftOf(0)) {c.pos().x = width();}
            if (c.isRightOf(width())) {c.pos().x = -120;}
        }

        checkPlayerWallCollsions();
        if (player.pos().x > width()) {
            nextLevel();
        }

        boundaries();
    }

    //Player verlässt Spielfeld/Bildschirm nicht, außer es gibt ein nächstes Level
    void boundaries() {
            if (player.pos().x < 0) {player.pos().x = 0;}
        if (player.pos().x > width() - 50 && currentLevel >= levels.length) {player.pos().x = width()-50;}
    }

    void nextLevel() {
        if (currentLevel == levels.length) {
            if (!won()) {return;};
        } else {
            currentLevel++;
        }

        readLevel(levels[currentLevel-1]);
        player.pos().x = 0;
    }

    void checkPlayerWallCollsions() {
        boolean isStandingOnTop = false;
        for (var block : floor()) {
            if (player.touches(block)) {
                player.stop();
                return;
            }
            if (player.isStandingOnTopOf(block)) {
                isStandingOnTop = true;
            }
        }

        for (var block : block()) {
            if (player.touches(block)) {
                player.stop();
                return;
            }
            if (player.isStandingOnTopOf(block)) {
                isStandingOnTop = true;
            }
        }

        if (!isStandingOnTop && !player.isJumping)
            player.startJump(0.05);
    }

    @Override
    public void keyPressedReaction(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case VK_RIGHT:
                player().right();
                player().changeImg("pacman.gif");
                break;
            case VK_LEFT:
                player().left();
                player().changeImg("pacman_inverted.gif");
                break;
            case VK_SPACE:
                player().jump();
                break;
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

    private void readLevel(String level) {
        block().clear();

        int l = 0;
        var lines = level.split("\\n");
        for (String line : lines) {
            int col = 0;
            for (char c : line.toCharArray()) {
                switch (c) {
                    case 'd'->floor.add(newDirtblock(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
                    case 'g'->floor.add(newGrassblock(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
                    case 'b'->block.add(newBrickblock(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
                }
                col++;
            }
            l++;
        }
    }

    static ImageObject newBrickblock(Vertex corner) {
        return new ImageObject(corner, new Vertex(0, 0), "brick.png");
    }

    static ImageObject newGrassblock(Vertex corner) {
        return new ImageObject(corner, new Vertex(0, 0), "grassblock.png");
    }

    static ImageObject newDirtblock(Vertex corner) {
        return new ImageObject(corner, new Vertex(0, 0), "dirtblock.png");
    }

    static ImageObject newCloud(Vertex corner) {
        return new ImageObject(corner, new Vertex(randomCloudVelocity(), 0), "cloud.png");
    }

    //berechnet zufällige Zahl zwischen -0.3 und -0.2 oder 0.2 und 0.3, damit die Wolken unterschiedliche Geschwindigkeiten haben
    static double randomCloudVelocity() {
        double velocity = new Random().nextDouble(0.2, 0.3);
        return new Random().nextFloat(0, 1) >= 0.5 ? velocity : velocity + (-1);
    }


    //berechnet Vertex zum spawnen von Wolken-Objekten auf vorbestimmten Ebenen
    Vertex cloudSpawn() {
        Vertex spawn = new Vertex(new Random().nextInt(0, 800), new Random().nextInt(20, 100));
        ImageObject testCloud = newCloud(spawn);

        for (var cloud : clouds()) {
            if (GameObj.overlaps(testCloud, cloud)) {
                return cloudSpawn();
            }
        }

        return spawn;
    }

    //leert alle Listen der goss-Liste und die goss-Liste selbst, um sicherzustellen, dass das Spiel korrekt initialisiert wird
    static void resetAll(List<List<? extends GameObj>> goss) {
        for (var gos : goss) {
            gos.clear();
        }

        goss.clear();
    }
}

