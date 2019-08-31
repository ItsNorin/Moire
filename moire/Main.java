package moire;

import javax.swing.SwingUtilities;

public class Main {

	private static final int PREF_W = 400;
	private static final int PREF_H = PREF_W;

	public static void main(String[] args) {
		Gui test = new Gui("Moire - Scroll for Speed").setWindowSize(PREF_W, PREF_H);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				test.start();
			}
		});
	}

}
