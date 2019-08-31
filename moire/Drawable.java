package moire;

import java.awt.Graphics;

// required methods to draw with GUI
public interface Drawable {			
	abstract void draw(Graphics g, int width, int height);
	abstract boolean needsRedraw();
	abstract void tick();
	abstract void tickSpeed(int ticksPerAction);
}
