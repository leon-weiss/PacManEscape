package name.panitz.game2d.PacManEscape;

import name.panitz.game2d.*;
import name.panitz.game2d.klaus.Barrel;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.awt.event.KeyEvent.*;

record PacManEscape(FallingImage player, List<GameObj> hintergrund, List<GameObj> floor, List<GameObj> clouds, List<List<? extends GameObj>> goss) implements Game {
    public static void main(String[] args) {
        new PacManEscape().play();
    }

    static final int GRID_WIDTH = 50;
    static final int NUM_OF_CLOUDS = 5;

    public PacManEscape() {
        this(new PacManPlayer(new Vertex(100, 400)), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        init();
    }

    static String level1 = """
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

        readLevel();

        goss().add(hintergrund());
        hintergrund().add(new ImageObject("hintergrund.png"));

        goss.add(clouds());
        for(int i = 0; i < NUM_OF_CLOUDS; i++) {clouds.add(newCloud(cloudSpawn()));}

        goss().add(floor());
    }

    @Override
    public void doChecks() {
        for (var c:clouds()) {
            if (c.isLeftOf(0)) {c.pos().x = width();}
            if (c.isRightOf(width())) {c.pos().x = -120;}
        }

        checkPlayerWallCollsions();
    }

    private void checkPlayerWallCollsions() {
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

    private void readLevel() {
        int l = 0;
        var lines = level1.split("\\n");
        for (String line : lines) {
            int col = 0;
            for (char c : line.toCharArray()) {
                switch (c) {
                    case 'd'->floor.add(newDirtblock(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
                    case 'g'->floor.add(newGrassblock(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
                }
                col++;
            }
            l++;
        }
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
        System.out.println(velocity);
        return new Random().nextFloat(0, 1) >= 0.5 ? velocity : velocity + (-1);
    }


    //berechnet Vertex zum spawnen von Wolken-Objekten auf vorbestimmten Ebenen
    Vertex cloudSpawn() {
        return new Vertex(new Random().nextInt(100, 700), new Random().nextInt(50, 150));
    }


    //leert alle Listen der goss-Liste und die goss-Liste selbst, um sicherzustellen, dass das Spiel korrekt initialisiert wird
    static void resetAll(List<List<? extends GameObj>> goss) {
        for (var gos : goss) {
            gos.clear();
        }

        goss.clear();
    }
}

