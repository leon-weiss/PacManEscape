package name.panitz.game2d.klaus;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import name.panitz.game2d.FallingImage;
import name.panitz.game2d.Game;
import name.panitz.game2d.GameObj;
import name.panitz.game2d.ImageObject;
import name.panitz.game2d.TextObject;
import name.panitz.game2d.Vertex;

public record HeartsOfKlaus(FallingImage player, int width, int height, List<List<? extends GameObj>> goss,
		List<ImageObject> walls, List<ImageObject> hearts, List<FallingImage> barrels, 
		List<TextObject> texts,int[] energyAndHearts)
		implements Game {

	static ImageObject newHeart(Vertex corner) {
		return new ImageObject(corner, new Vertex(0, 0), "heart.png");
	}

	static ImageObject newWall(Vertex corner) {
		return new ImageObject(corner, new Vertex(0, 0), "wall.png");
	}

	static final int GRID_WIDTH = 34;

	static String level1 = """
			w                 f  w
			whf       wwh    www w
			wwwww       ww       w
			w        h           w
			w       ww         h w
			w            ww      w
			w      hf          fhw
			w    wwww        wwwww
			w                    w
			wh             h     w
			wwwww       wwww     w
			w                    w
			w p    h            hw
			w   wwww        wwwwww
			w                    w
			w                   gw
			wwwwwwww  wwwwwwwwwwww""";

	public HeartsOfKlaus() {
		this(new Klaus(new Vertex(0, 0)), 22 * GRID_WIDTH, 17 * GRID_WIDTH
				,new ArrayList<>(),new ArrayList<>(),new ArrayList<>()
				,new ArrayList<>(),new ArrayList<>(),new int[] {0,0});
		init();
		
	}

	private void decreaseEnergy() {
		energyAndHearts[0]--;
		initTexts();
	}
	private void playerBarrelCollision() {
		for (var b : barrels()) {
			if (b.touches(player)) {
				decreaseEnergy();
				b.pos().moveTo(new Vertex(Math.random()*(width() - 2*40) + 40, -40));

			}
			if (b.pos().y > height()) {
				b.pos().moveTo(
				        new Vertex(Math.random()*(width() - 2*40) + 40, -40));
			}
		}
	}

	private void fallingBarrel() {
		for (var b : barrels) {
			for (var wall : walls) {
				if (!b.isAbove(wall)&&!b.isUnderneath(wall)
					&& (
						b.isLeftOf(wall)&&b.pos().x+b.width()+b.velocity().x+1>wall.pos().x
						||
						b.isRightOf(wall)&&b.pos().x+b.velocity().x-1<wall.pos().x+wall.width()
						)) {
					b.velocity().x*=-1;
					
				}
			}
			boolean isStandingOnTop = false;
			for (var wall : walls) {

				if (b.isStandingOnTopOf(wall)) {
					isStandingOnTop = true;
					break;
				}
			}
			if (!isStandingOnTop && !b.isJumping) {
				b.startJump(0.1);
			}else if (isStandingOnTop){
				b.restart();
			}
		}

	}

	private void checkPlayerWallCollsions() {
		boolean isStandingOnTop = false;
		for (var wall : walls) {
			if (player.touches(wall)) {
				player.stop();
				return;
			}
			if (player.isStandingOnTopOf(wall)) {
				isStandingOnTop = true;
			}
		}

		if (!isStandingOnTop && !player.isJumping)
			player.startJump(0.05);
	}

	private void collectHearts() {
		ImageObject removeMe = null;
		for (var heart : hearts) {
			if (player().touches(heart)) {
				removeMe = heart;
				energyAndHearts[1]++;
				initTexts();
				break;
			}
		}
		if (removeMe != null)
			hearts.remove(removeMe);
	}

	@Override
	public void doChecks() {
		collectHearts();
		checkPlayerWallCollsions();
		fallingBarrel();
		playerBarrelCollision();
		if (player().pos().y > height()) {
			player().pos().moveTo(new Vertex(2*GRID_WIDTH, height() - 80));
		}
	}

	public boolean lost() {
		return energyAndHearts[0] <= 0;
	}

	public boolean won() {
		return hearts.isEmpty();
	}

	@Override
	public void init() {
		barrels.clear();
		hearts.clear();
		walls.clear();
		
		readLevel();
		initTexts();

		goss().add(barrels);
		goss().add(walls);
		goss().add(hearts);
		goss().add(texts);
		energyAndHearts[0]=10;
		energyAndHearts[1]=0;

	}

	private void readLevel() {
		int l = 0;
		var lines = level1.split("\\n");
		for (String line : lines) {
			int col = 0;
			for (char c : line.toCharArray()) {
				switch (c) {
				case 'w'->walls.add(newWall(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
				case 'h'->hearts.add(newHeart(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH)));
				case 'f'->barrels.add(new Barrel(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH-1)));
				case 'p'->player().pos().moveTo(new Vertex(col * GRID_WIDTH, l * GRID_WIDTH - 2));
				}
				col++;
			}
			l++;
		}
	}

	private void initTexts() {
		texts.clear();
		texts.add(new TextObject(new Vertex(50, 20),"Energy: " + energyAndHearts[0] ));
		texts.add(new TextObject(new Vertex(50, 50),"Hearts: " + energyAndHearts[1] ));
	}

	@Override
	public void keyPressedReaction(KeyEvent keyEvent) {
		switch (keyEvent.getKeyCode()) {
		case VK_RIGHT -> player().right();
		case VK_LEFT -> player().left();
		case VK_DOWN -> player().stop();
		case VK_UP -> player().jump();
		}

	}

	public static void main(String... args) {
		new HeartsOfKlaus().play();
	}



}
