public class OpponentEntity extends Entity {
	
	private double moveSpeed = 75;
	private Game game;
	
	public OpponentEntity(Game game,String ref,int x,int y) {
		
		super(ref,x,y);	
		this.game = game;
		dx = -moveSpeed;
	}
	
	public void move(long delta) {
		
		if ((dx < 0) && (x < 10)) {
			game.updateLogic();
		}
		if ((dx > 0) && (x > 775)) {
			game.updateLogic();
		}
		super.move(delta);
	}
	
	public void doLogic() {
		
		dx = -dx;
		y += 10;
		
		if (y > 570) {
			game.notifyDeath();
		}
	}
	
	public void collidedWith(Entity other) {
	}
}