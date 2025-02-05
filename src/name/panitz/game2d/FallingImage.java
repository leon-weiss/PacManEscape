package name.panitz.game2d;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
public class FallingImage extends AbstractGameObj {
	static double G = 9.81;
	double v0;
	int t = 0;
	Image image;

	private final double PLAYER_MOVE_SPEED = 1.4;

	public boolean isJumping = false;

	public FallingImage(String imageFileName, Vertex corner, Vertex movement) {
		super(corner,  movement, 0 ,0);
	    var iIcon 
	     = new ImageIcon(getClass().getClassLoader().getResource(imageFileName));
	    width = iIcon.getIconWidth();
	    height=iIcon.getIconHeight();
	    image = iIcon.getImage();
	}

	public void stop() {
		double oldX = velocity().x;
		pos().add(velocity().mult(-1.1));
		velocity().x = oldX;
		velocity().y = 0;
		isJumping = false;
	}

	public void restart() {
		double oldX = velocity().x;
		pos().add(velocity().mult(-1.1));
		velocity().x = oldX;
		velocity().y = 0;
		isJumping = false;
	}

	
	public void left() {
		velocity().x = -PLAYER_MOVE_SPEED;
	}

	public void right() {
		velocity().x = PLAYER_MOVE_SPEED;
	}

	public void jump() {
		if (!isJumping) {
			startJump(-4);
		}
	}

	public void startJump(double v0) {
		isJumping = true;
		this.v0 = v0;
		t = 0;
	}

	@Override
	public void move() {
		if (isJumping) {
			t++;
            velocity().y = v0 + G * t / 200;
		}
		super.move();
	}

	@Override
	public void paintTo(Graphics g) {
	    g.drawImage(image, (int)pos.x, (int)pos.y, null);		
	}

	public void changeImg(String imageFileName) {
		var iIcon
				= new ImageIcon(getClass().getClassLoader().getResource(imageFileName));
		image = iIcon.getImage();
	}
}