import java.awt.Color;

import javax.swing.JPanel;

//import ShloshaAvot.InShabbatState;
//import ShloshaAvot.OutState;

/*
 * Created on Mimuna 5767  upDate on Tevet 5770 
 */

/**
 * @author לויאן
 */
class Echad extends Thread {
	Ramzor ramzor;
	JPanel panel;
	Timer75 timer;
	Event64 evTimer;
	final int timeOnToOff = 1000;

	enum OutState {
		ON, OFF
	};

	OutState outState;

	public Echad(Ramzor ramzor, JPanel panel) {
		this.ramzor = ramzor;
		this.panel = panel;

		start();
	}

	public void run()
	{
		try 
		{
			while (true)
			{
					switch (outState) {
					case ON:
						while (true) {
							if (evTimer.arrivedEvent()) {
								evTimer.waitEvent();
								SetOff();
								outState = OutState.OFF;
								evTimer=new Event64();
								timer=new Timer75(timeOnToOff, evTimer);
								break;
							} 
							else
								yield();
						}
						break;
					case OFF:
						while (true) {
							if (evTimer.arrivedEvent()) {
								evTimer.waitEvent();
								SetOn();
								outState = OutState.ON;
								evTimer=new Event64();
								timer=new Timer75(timeOnToOff, evTimer);
								break;
							} 
							else
								yield();
						}
						break;
					}
				}
		
		}
		catch(Exception ex){}
		
	}

	private void SetOn() {
		setLight(1, Color.YELLOW);
	}

	private void SetOff() {
		setLight(1, Color.GRAY);
	}

	public void setLight(int place, Color color) {
		ramzor.colorLight[place - 1] = color;
		panel.repaint();
	}
}
