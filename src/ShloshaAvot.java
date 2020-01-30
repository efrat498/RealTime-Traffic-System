import java.awt.Color;

import javax.swing.JPanel;

//import ShneyLuchot.InState;
//import ShneyLuchot.OutState;

/*
 * Created on Mimuna 5767  upDate on Tevet 5770 
 */

/**
 * @author לויאן
 */

public class ShloshaAvot extends Thread {
	Ramzor ramzor;
	JPanel panel;
	Timer75 timer;
	Event64 evTimer;
	// define all events
	Event64 evToRed, evToGreen, evOnRed, evToShabbat, evToChol;
	final int y = 3;
	final int timeRedYellowToGreen = 500;
	final int timeGreenToOff = 100;
	final int timeYellowToRed = 500;
	final int timeYellowToOff = 100;
	
	int key;
	
	int count = y;

	enum OutState {
		ON_CHOL, ON_SHABBAT
	};

	OutState outState;

	enum InCholState {
		ON_RED, ON_REDYELLOW, ON_GREEN, ON_YELLOW, ON_BLINKEDGREEN
	};

	InCholState inCholState;

	enum InBlinkedGreenState {
		ON_GREEN, OFF
	};

	InBlinkedGreenState inBlinkedGreenState;

	enum InShabbatState {
		ON_YELLOW, OFF
	};

	InShabbatState inShabbatState;

	private boolean stop = true;

	public ShloshaAvot(Ramzor ramzor, JPanel panel, int key, Event64 evToGreen, Event64 evToRed, Event64 evOnRed,
			Event64 evToShabbat, Event64 evToChol) {
		this.ramzor = ramzor;
		this.panel = panel;
		this.key=key;

		// Initialize all events
		this.evToGreen = evToGreen;
		this.evToRed = evToRed;
		this.evOnRed = evOnRed;
		this.evToShabbat = evToShabbat;
		this.evToChol = evToChol;

		// new CarsMaker(panel,this,key);
		start();
	}

	public void run() {
		SetRed();
		outState = OutState.ON_CHOL;
		inCholState = InCholState.ON_RED;
		try {
			while (true) {
				switch (outState) {
				case ON_CHOL:
					while (outState == OutState.ON_CHOL) {
						switch (inCholState) {
						case ON_RED:
							while (true) {
								if (evToGreen.arrivedEvent()) {
									evToGreen.waitEvent();
									SetRedYellow();
									inCholState = InCholState.ON_REDYELLOW;
									//Initialize timer
									evTimer=new Event64();
									timer=new Timer75(timeRedYellowToGreen, evTimer);
									break;
								} else if (evToShabbat.arrivedEvent()) {
									evToShabbat.waitEvent();
									SetYellow();
									outState = OutState.ON_SHABBAT;
									break;
								} else
									yield();
							}
							break;
						case ON_REDYELLOW:
							while (true) {
								if (evTimer.arrivedEvent()) {
									evTimer.waitEvent();
									SetGreen();
									inCholState = InCholState.ON_GREEN;
									break;
								} else if (evToShabbat.arrivedEvent()) {
									evToShabbat.waitEvent();
									SetYellow();
									outState = OutState.ON_SHABBAT;
									break;
								} else
									yield();
							}
							break;
						case ON_GREEN:
							while (true) {
								if (evToRed.arrivedEvent()) {
									evToRed.waitEvent();
									//System.out.println("shlosha avot out of green");
									count = y;
									// Initialize tm
									evTimer=new Event64();									
									timer=new Timer75(timeGreenToOff, evTimer);
									inBlinkedGreenState = InBlinkedGreenState.ON_GREEN;
									inCholState = InCholState.ON_BLINKEDGREEN;
									break;
								} else if (evToShabbat.arrivedEvent()) {
									evToShabbat.waitEvent();
									SetYellow();
									outState = OutState.ON_SHABBAT;
									break;
								} else
									yield();
							}
							break;
						case ON_YELLOW:
							while (true) {
								if (evTimer.arrivedEvent()) {
									evTimer.waitEvent();
									SetRed();
									//System.out.println("on yellow to red");
									evOnRed.sendEvent();
									//System.out.println("on yellow to red2");
									inCholState = InCholState.ON_RED;
									break;
								} else if (evToShabbat.arrivedEvent()) {
									evToShabbat.waitEvent();
									SetYellow();
									outState = OutState.ON_SHABBAT;
									break;
								} else
									yield();
							}
							break;
						case ON_BLINKEDGREEN:
							while (inCholState == InCholState.ON_BLINKEDGREEN) {
								switch (inBlinkedGreenState) {
								case ON_GREEN:
									while (true) 
									{
										if (count == 0) 
										{
											SetYellow();
											evTimer=new Event64();
											timer=new Timer75(timeYellowToRed, evTimer);
											inCholState = InCholState.ON_YELLOW;
											break;
										} 
										else if (evTimer.arrivedEvent()) 
										{
											evTimer.waitEvent();
											SetOff();
											evTimer=new Event64();
											timer=new Timer75(timeGreenToOff, evTimer);
											inBlinkedGreenState = InBlinkedGreenState.OFF;
											break;
										} 
										else if (evToShabbat.arrivedEvent()) 
										{
											evToShabbat.waitEvent();
											SetYellow();
											outState = OutState.ON_SHABBAT;
											break;
										} 
										else
											yield();
									}
									break;
								case OFF:
									while (true) 
									{
										if (count == 0) 
										{
											SetYellow();
											inCholState = InCholState.ON_YELLOW;
											break;
										} 
										else if (evTimer.arrivedEvent()) 
										{
											evTimer.waitEvent();
											SetGreen();
											count--;
											inBlinkedGreenState = InBlinkedGreenState.ON_GREEN;
											evTimer=new Event64();
											timer=new Timer75(timeGreenToOff, evTimer);
											break;
										} 
										else if (evToShabbat.arrivedEvent()) 
										{
											evToShabbat.waitEvent();
											SetYellow();
											outState = OutState.ON_SHABBAT;
											break;
										} 
										else
											yield();
									}
									break;
								}
							}
							break;
						}
					}

					break;

				case ON_SHABBAT:
					inShabbatState = InShabbatState.ON_YELLOW;
					evTimer=new Event64();
					timer=new Timer75(timeYellowToOff, evTimer);
					while (outState == OutState.ON_SHABBAT) {
						switch (inShabbatState) {
						case ON_YELLOW:
							while (true) {
								if (evTimer.arrivedEvent()) {
									//System.out.println("yellow to off blink!");
									evTimer.waitEvent();
									SetOff();
									inShabbatState = InShabbatState.OFF;
									evTimer=new Event64();
									timer=new Timer75(timeYellowToOff, evTimer);
									break;
								} 
								else if (evToChol.arrivedEvent()) {
									evToChol.waitEvent();
									SetRed();
									outState = OutState.ON_CHOL;
									inCholState=InCholState.ON_RED;
									break;
								} else
									yield();
							}
							break;
						case OFF:
							while (true) {
								if (evTimer.arrivedEvent()) {
									evTimer.waitEvent();
									SetYellow();
									inShabbatState = InShabbatState.ON_YELLOW;
									evTimer=new Event64();
									timer=new Timer75(timeYellowToOff, evTimer);
									break;
								} else if (evToChol.arrivedEvent()) {
									evToChol.waitEvent();
									SetRed();
									outState = OutState.ON_CHOL;
									inCholState=InCholState.ON_RED;
									break;
								} else
									yield();
							}
							break;
						}
					}
					break;
				}
			}
		} catch (Exception ex) {}

//		try {
//			while (true) {
//				sleep(1000);
//				setLight(2, Color.YELLOW);
//				sleep(1000);
//				SetGreen();
//				stop = false;
//				sleep(3000);
//				stop = true;
//				SetYellow();
//				sleep(1000);
//				SetRed();
//			}
//		} catch (InterruptedException e) {
//		}

	}

	private void SetOff() {
		setLight(1, Color.LIGHT_GRAY);
		setLight(2, Color.LIGHT_GRAY);
		setLight(3, Color.LIGHT_GRAY);

	}

	private void SetRed() {
		setLight(1, Color.RED);
		setLight(2, Color.LIGHT_GRAY);
		setLight(3, Color.LIGHT_GRAY);
	}

	private void SetYellow() {
		setLight(1, Color.LIGHT_GRAY);
		setLight(2, Color.YELLOW);
		setLight(3, Color.LIGHT_GRAY);
	}

	private void SetGreen() {
		setLight(1, Color.LIGHT_GRAY);
		setLight(2, Color.LIGHT_GRAY);
		setLight(3, Color.GREEN);
	}

	private void SetRedYellow() {
		setLight(1, Color.RED);
		setLight(2, Color.YELLOW);
		setLight(3, Color.LIGHT_GRAY);
	}

	public void setLight(int place, Color color) {
		ramzor.colorLight[place - 1] = color;
		panel.repaint();
	}

	public boolean isStop() {
		return stop;
	}
}
