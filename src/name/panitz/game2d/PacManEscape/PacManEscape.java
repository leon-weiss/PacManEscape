package name.panitz.game2d.PacManEscape;

import name.panitz.game2d.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.awt.event.KeyEvent.*;

record PacManEscape(FallingImage player, List<ImageObject> hintergrund, List<GameObj> blocks, List<GameObj> floor, List<GameObj> clouds, List<GameObj> coins, List<FallingImage> ghosts, List<TextObject> texts, List<List<? extends GameObj>> goss) implements Game {
    public static void main(String[] args) {
        new PacManEscape().play();
    }

    static final int GRID_WIDTH = 50;
    static final int NUM_OF_CLOUDS = 4;
    static int currentLevel;
    static int coinsLeft;
    static int lives = 5;

    public PacManEscape() {
        this(new PacManPlayer(new Vertex(100, 400)), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        init();
    }

    /* === Zeichenlegende ===
     * g -> Grassblock
     * d -> Erdblock
     * b -> Backsteinblock
     * c -> Coin
     * p -> pinker Geist
     * y -> gelber Geist
     * n -> blauer Geist
     * */

    static String level1 = """
    #
    #
    #cc         nc
    #bb     c  bbbbb
    #       b
    #  bb
    #      ccc
    #  y   bbb
    # bbb       b
    #           p
    gggggggggggggggg
    dddddddddddddddd
    """;

    static String level2 = """
    #
    #
    ccccc        c
    bbbbb       bbbb
    #
    #      bbb   c
    #           cbbb
    # ccc    cccb
    # bbb  b bbb
    #n y p
    gggggggggggggggg
    dddddddddddddddd
    """;

    static String level3 = """
    #
    #
    #
    b
    #
    #
    bbbb
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
        hintergrund().add(new ImageObject(new Vertex(350, 175), new Vertex(0,0),"castle.png"));

        goss().add(floor());
        goss().add(blocks());
        goss().add(coins());
        goss().add(ghosts());
        goss().add(texts());

        goss.add(clouds());
        for (int i = 0; i < NUM_OF_CLOUDS; i++) {
            clouds.add(newCloud(cloudSpawn())); // Erstellt Wolken an zufälligen Positionen
        }
    }

    @Override
    public void doChecks() {
        // Überprüft, ob Wolken den Bildschirm verlassen und setzt sie zurück
        for (var c : clouds()) {
            if (c.isLeftOf(0)) {
                c.pos().x = width();
            }
            if (c.isRightOf(width())) {
                c.pos().x = -80;
            }
        }

        // Überprüft, ob der Spieler Münzen berührt
        for (int i = 0; i < coins().size(); i++) {
            if (player().touches(coins().get(i))) {
                coins().remove(coins().get(i));
                coinsLeft--;
            }
        }

        // Kollisionserkennung für Spieler und Geister
        checkPlayerWallCollision();
        checkPlayerGhostCollision();
        checkGhostWallCollision();
        boundaries();

        // Überprüft, ob das Level beendet ist
        if (player.pos().x > width() && coinsLeft == 0) {
            nextLevel();
        }

    }

    // Begrenzung der Bewegungen für Spieler und Geister
    void boundaries() {
        if (player.pos().x < 0) {
            player.pos().x = 0;
        }
        if ((player.pos().x > width() - player.width() && coinsLeft != 0) || (currentLevel >= levels.length && player.pos().x > width() - player.width())) {
            player.pos().x = width() - player.width();
        }

        for (var ghost : ghosts()) {
            if (ghost.pos().x < 0 || ghost.pos().x + ghost.width() > width()) {
                ghost.velocity().x *= -1; // Geister ändern Richtung bei Kollision mit Bildschirmrand
            }
        }
    }

    // Überprüfung von Kollisionen der Geister mit Wänden und Plattformen
    void checkGhostWallCollision() {
        for (var ghost : ghosts()) {
            boolean isStandingOnTop = false;
            for (var block : floor()) {
                if (ghost.touches(block)) {
                    ghost.stop();
                    return;
                }
                if (ghost.isStandingOnTopOf(block)) {
                    isStandingOnTop = true;
                }
            }

            for (var block : blocks()) {
                if (ghost.touches(block)) {
                    ghost.velocity().x *= -1;
                    return;
                }
                if (ghost.isStandingOnTopOf(block)) {
                    isStandingOnTop = true;
                }
            }

            if (!isStandingOnTop) {
                ghost.velocity().x *= -1; // Simuliert die Schwerkraft
            } else {
                ghost.velocity().y = 0; // Setzt vertikale Geschwindigkeit zurück
            }
        }
    }

    // Lädt das nächste Level
    void nextLevel() {
        if (currentLevel == levels.length) {
            if (!won()) {
                return;
            }
        } else {
            currentLevel++;
            texts.clear();
            texts.add(new TextObject(new Vertex(50, 560), 28, "Arial", "Leben: " + lives));
            texts.add(new TextObject(new Vertex(650, 560), 28,"Arial", "Level: " + currentLevel));
        }

        readLevel(levels[currentLevel - 1]);
        player.pos().x = 0;
    }

    // Überprüft Kollisionen zwischen Spieler und Geistern
    void checkPlayerGhostCollision() {
        for (int i = 0; i < ghosts().size(); i++) {
            var ghost = ghosts().get(i);
            if (player.touches(ghost)) {
                ghosts().remove(i);
                i--;
                deductLive();
                if (lost()) {
                    break;
                }
            }
        }
    }

    // Reduziert die Lebensanzahl des Spielers und Aktualisiert die Textobjekte
    void deductLive() {
        lives--;
        texts.clear();
        texts.add(new TextObject(new Vertex(50, 560),28, "Arial", "Leben: " + lives));
        texts.add(new TextObject(new Vertex(650, 560),28, "Arial", "Level: " + currentLevel));
    }

    // Überprüft Kollisionen des Spielers mit Wänden und Plattformen
    void checkPlayerWallCollision() {
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

        for (var block : blocks()) {
            if (player.touches(block)) {
                player.stop();
                return;
            }
            if (player.isStandingOnTopOf(block)) {
                isStandingOnTop = true;
            }
        }

        if (!isStandingOnTop && !player.isJumping) {
            player.startJump(0.05);
        }
    }

    //Spielsteuerung
    @Override
    public void keyPressedReaction(KeyEvent keyEvent) {
        if (!lost()) {
            switch (keyEvent.getKeyCode()) {
                case VK_RIGHT -> {
                    player().right();
                    player().changeImg("pacman.gif");
                }
                case VK_LEFT -> {
                    player().left();
                    player().changeImg("pacman_inverted.gif");
                }
                case VK_SPACE -> player().jump();
            }
        }
    }

    @Override
    public boolean won() {
        return false;
    }

    @Override
    public boolean lost() {
        if (lives > 0) {
            return false;
        } else {
            resetAll(goss());
            hintergrund().add(new ImageObject("hintergrund_red.png"));
            goss().add(hintergrund());
            goss().add(texts());
            texts.add(new TextObject(new Vertex(180, 300),40, "Arial", "DU HAST VERLORE"));
            return true;
        }
    }

    // Liest die Leveldefinition und erstellt die Objekte
    private void readLevel(String level) {
        coinsLeft = 0;
        blocks().clear();
        ghosts().clear();

        int l = 0;
        var lines = level.split("\n");
        for (String line : lines) {
            int col = 0;
            for (char c : line.toCharArray()) {
                switch (c) {
                    case 'd' -> floor.add(newDirtblock(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
                    case 'g' -> floor.add(newGrassblock(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
                    case 'b' -> blocks.add(newBrickblock(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
                    case 'c' -> coins.add(newCoin(new Vertex(col * GRID_WIDTH + 15, l * GRID_WIDTH + 15)));
                    case 'p', 'y', 'n' -> ghosts.add(new PacManGhost(new Vertex(col * GRID_WIDTH + 15, l * GRID_WIDTH + 15), c));
                }
                col++;
            }
            l++;
        }
    }

    // Hilfsmethoden zum Erstellen von Spielobjekten
    static ImageObject newCoin(Vertex corner) {
        coinsLeft++;
        return new ImageObject(corner, new Vertex(0, 0), "coin.gif");
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

    static double randomCloudVelocity() {
        double velocity = new Random().nextDouble(0.2, 0.3);
        return new Random().nextBoolean() ? velocity : -velocity;
    }

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

    // Setzt alle Objektlisten zurück
    static void resetAll(List<List<? extends GameObj>> goss) {
        for (var gos : goss) {
            gos.clear();
        }

        goss.clear();
    }
}
