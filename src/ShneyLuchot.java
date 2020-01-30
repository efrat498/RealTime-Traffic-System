import java.awt.Color;

import javax.swing.JPanel;

/*
 * Created on Mimuna 5767  upDate on Tevet 5770 
 */

/**
 * @author לויאן
 */

class ShneyLuchot extends Thread {
	Ramzor ramzor;
	JPanel panel;

	Event64 evToRed, evToGreen, evOnRed, evToShabbat, evToChol;

	enum OutState {
		ON_CHOL, ON_SHABBAT
	};

	OutState outState;

	enum InState {
		ON_RED, ON_GREEN
	};

	InState inState;

	public ShneyLuchot(Ramzor ramzor, TrafficLightPanel panel, int key, Event64 evToGreen, Event64 evToRed,Event64 evOnRed,
			Event64 evToShabbat, Event64 evToChol) {
		super();
		
		this.ramzor = ramzor;
		this.panel = panel;
		this.evToRed = evToRed;
		this.evToGreen = evToGreen;
		this.evOnRed = evOnRed;
		this.evToShabbat = evToShabbat;
		this.evToChol = evToChol;
		start();
	}

//	public ShneyLuchot(Ramzor ramzor, JPanel panel) {
//		this.ramzor = ramzor;
//		this.panel = panel;
//		start();
//	}


	public void run() {
		outState = OutState.ON_CHOL;
		inState = InState.ON_RED;
		SetRed();
		try {
			while (true) {
				switch (outState) {
				case ON_CHOL:
					while (outState == OutState.ON_CHOL) {
						switch (inState) {
						case ON_RED:
							while (true) {
								if (evToGreen.arrivedEvent()) {
									//System.out.println("set green");
									evToGreen.waitEvent();
									SetGreen();
									inState = InState.ON_GREEN;
									break;
								} else if (evToShabbat.arrivedEvent()) {
									evToShabbat.waitEvent();
									SetOff();
									outState = OutState.ON_SHABBAT;
									break;
								} else
									yield();
							}
							break;
						case ON_GREEN:
							while (true) 
							{
								if (evToRed.arrivedEvent()) 
								{
									evToRed.waitEvent();
									SetRed();
									evOnRed.sendEvent();
									inState = InState.ON_RED;
									break;
								} else if (evToShabbat.arrivedEvent()) 
								{
									evToShabbat.waitEvent();
									SetOff();
									outState = OutState.ON_SHABBAT;
									break;
								} else
									yield();
							}
							break;
						}
					}
					break;
				case ON_SHABBAT:
					evToChol.waitEvent();
					SetRed();
					outState = OutState.ON_CHOL;
					inState = InState.ON_RED;

					break;

				}
			}
		} 
		catch (Exception e){}

//		
//		try 
//		{
//			while (true)
//			{
//				sleep(1000);
//				SetGreen();
//				sleep(1000);
//				SetRed();
//			}
//		} catch (InterruptedException e) {}
	}

	private void SetOff() {
		setLight(1, Color.GRAY);
		setLight(2, Color.GRAY);

	}

	private void SetGreen() {
		setLight(1, Color.GRAY);
		setLight(2, Color.GREEN);
	}

	private void SetRed() {
		setLight(1, Color.RED);
		setLight(2, Color.GRAY);
	}

	public void setLight(int place, Color color) {
		ramzor.colorLight[place - 1] = color;
		panel.repaint();
	}
}
