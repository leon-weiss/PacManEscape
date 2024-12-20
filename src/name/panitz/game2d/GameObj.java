package name.panitz.game2d;
public interface GameObj{
  Vertex pos();
  Vertex velocity();
  double width();
  double height();

  void paintTo(java.awt.Graphics g);

  default void move(){pos().add(velocity());}

  default boolean isAbove(double y){return pos().y+height()<y;}
  default boolean isAbove(GameObj that){return isAbove(that.pos().y);}

  default boolean isUnderneath(GameObj that){return that.isAbove(this);}

  default boolean isLeftOf(double x){return pos().x+width()<x;}
  default boolean isLeftOf(GameObj that){return isLeftOf(that.pos().x);}
  default boolean isRightOf(GameObj that){return that.isLeftOf(this);}
    default boolean isRightOf(double x){return pos().x>x;}

  default boolean touches(GameObj that){
    return 
     ! (    isAbove(that)  || isUnderneath(that)
         || isLeftOf(that) || isRightOf(that)    );
  }

  default boolean isStandingOnTopOf(GameObj that) {
	    return 
	    		 !(isLeftOf(that) || isRightOf(that)) 
	    		&& isAbove(that)
	            && pos().y + height() + velocity().y+1.5 > that.pos().y;
  }

    default boolean touches(Vertex point) {
        return point.x >= this.pos().x
                && point.x <= this.pos().x + width()
                && point.y >= this.pos().y
                && point.y <= this.pos().y + height();
    }

    static boolean overlaps(GameObj a, GameObj b) {
        return !( a.pos().x + a.width() < b.pos().x
                || a.pos().x > b.pos().x + b.width()
                || a.pos().y + a.height() < b.pos().y
                || a.pos().y > b.pos().y + b.height() );
    }

}

