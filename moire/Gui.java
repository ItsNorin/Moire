package moire;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Gui extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	protected String title_;

	protected JFrame frame_;
	protected List<Drawable> layers_;
	
	protected Timer tm;

	protected int ticksPerAction;
	
	public Gui() {
		this("Moire Pattern - RAM Eater");
	}

	public Gui(String title) {
		this.title_ = title;

		this.frame_ = new JFrame(title);
		this.frame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame_.addComponentListener(new FrameListener());
		this.frame_.addMouseWheelListener(new MouseWheel());
		
		this.layers_ = new ArrayList<Drawable>();
		this.layers_.add(new MoirePatternDrawable(4,0,Color.BLUE));
		this.layers_.add(new MoirePatternDrawable(4,0,1,30,Color.BLUE));
		
		this.tm = new Timer(1,this);
		this.ticksPerAction = 30;
	}

	
	
	public Gui setWindowSize(int width, int height) {
		frame_.setPreferredSize(new Dimension(width, height));
		frame_.setSize(width, height);
		return this;
	}
	

	public void start() {
		frame_.getContentPane().add(this);
		frame_.pack();
		frame_.setLocationRelativeTo(null);
		frame_.setVisible(true);
		
		tm.start();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Drawable i : layers_) {
			i.draw(g, this.getWidth(), this.getHeight());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println(e.toString());
		
		boolean needsRedraw = false;
		
		for(Drawable i : layers_) {
			i.tick();
			if(i.needsRedraw())
				needsRedraw = true;
		}
		
		if(needsRedraw)
			repaint();
	}
	
	private class FrameListener implements ComponentListener{
        public void componentHidden(ComponentEvent arg0) {
        }
        public void componentMoved(ComponentEvent arg0) {   
        }
        public void componentResized(ComponentEvent arg0) {
        	repaint();
        }
        public void componentShown(ComponentEvent arg0) {

        }
    }
	
	private class MouseWheel implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent mwe) {
			ticksPerAction += mwe.getWheelRotation();
			if(ticksPerAction < 3)
				ticksPerAction = 3;
			if(ticksPerAction > 30)
				ticksPerAction = 30;
			for(Drawable i : layers_) {
				i.tickSpeed(ticksPerAction);
			}
		}
	}
}
