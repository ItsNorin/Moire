package moire;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MoirePatternDrawable implements Drawable {

	private final int squareSizePX;
	private String id0;

	// rotation in radians for every ticksPerRotation ticks
	private final double rotationAmountRad;
	// how many ticks it takes to rotate rotationAmountRad
	private int ticksPerRotation;

	// how far checker is currently rotated
	private double currentRotationRad;
	private int ticksSinceLastRotation;
	
	// true if needs to rotate
	private boolean needsRedraw;
	
	// hella RAM nommer
	private Map<String, BufferedImage> cache;
	
	private Color color;

	public MoirePatternDrawable(int squareSizePX, double rotationDeg, Color color) {
		this(squareSizePX, rotationDeg, 0, -1, color);
	}

	public MoirePatternDrawable(
			int squareSizePX, 
			double startRotationDeg, 
			int rotationAmountDeg,
			int updatesPerRotation,
			Color color) 
	{
		this.squareSizePX = squareSizePX;
		this.currentRotationRad = Math.toRadians(startRotationDeg);
		this.rotationAmountRad = Math.toRadians(rotationAmountDeg);
		if (updatesPerRotation < 0)
			updatesPerRotation = -1;
		this.ticksPerRotation = updatesPerRotation;
		this.color = color;

		cache = new HashMap<String, BufferedImage>();
		id0 = null;
		ticksSinceLastRotation = 0;
		needsRedraw = true;
	}
	
	
	private static String getID(int size, int angleDeg) {
		return Integer.toString(size) + "." + Integer.toString(angleDeg);
	}
	
	private String getID(int size, double angleRad) {
		return getID(size, (int)Math.floor(Math.toDegrees(angleRad)));
	}
	
	private void generateBase(int newSize) {
		BufferedImage base = new BufferedImage(newSize, newSize, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = base.createGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(color);
		
		for (int y = 0; y < newSize; y += squareSizePX) {
			for (int x = 0; x < newSize; x += squareSizePX) {
				if (((x + y) / squareSizePX) % 2 == 0) {
					g2.fillRect(x, y, squareSizePX, squareSizePX);
				}
			}
		}
		cache.put(getID(newSize,0), base);
		g2.dispose();
	}
	
	@Override
	public void draw(Graphics g, int width, int height) {
		// since board will be rotated, make it 2x the size
		int newSize = Math.max(width, height);
		newSize = 10 * (int) Math.ceil(newSize / 5); 
		
		String id = getID(newSize, currentRotationRad);
		id0 = getID(newSize, 0);
		
		// generate base rotation 
		if(!cache.containsKey(id0)) {
			generateBase(newSize);
		}
		// clear cache if re-sized
		if(cache.get(id0).getWidth() != newSize) {
			cache.clear();
			generateBase(newSize);
		}
		
		// generate if image has not been cached
		if (!cache.containsKey(id)) {
			// rotate checker
			AffineTransform rotate = AffineTransform.getRotateInstance(currentRotationRad, newSize / 2, newSize / 2);
			AffineTransformOp opr = new AffineTransformOp(rotate, AffineTransformOp.TYPE_BICUBIC);
			BufferedImage workingBoard = opr.filter(cache.get(id0), null);

			cache.put(id, workingBoard);
		}

		if (needsRedraw) {
			ticksSinceLastRotation = 0;
			needsRedraw = false;
		}
		
		g.drawImage(cache.get(id), -newSize/4, -newSize/4, null);
	}

	@Override
	public boolean needsRedraw() {
		return needsRedraw;
	}

	@Override
	public void tick() {
		if (ticksPerRotation != -1) {
			ticksSinceLastRotation++;
			if (ticksSinceLastRotation >= ticksPerRotation) {
				currentRotationRad += rotationAmountRad;
				
				if (currentRotationRad > Math.toRadians(90))
					currentRotationRad -= Math.toRadians(89);
				
				needsRedraw = true;
			}
		}
	}

	@Override
	public void tickSpeed(int ticksPerAction) {
		this.ticksPerRotation = ticksPerAction;
	}
	
	

}
