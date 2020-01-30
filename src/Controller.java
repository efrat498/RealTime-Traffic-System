import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.awt.Color;

import javax.swing.JPanel;


public class Controller extends Thread 
{
	Event64[] evToRed, evToGreen, evOnRed, evToShabbat, evToChol;//, evPressButt;
	Event64 evShabbatPressed, evCholPressed, evRegelPressed;
	
	//ArrayList<Set<Integer>> groups=new ArrayList<Set<Integer>>();
	Set<Integer> g0;//=new HashSet<Integer>(16);
	Set<Integer> g1;
	Set<Integer> g2;
	
	Timer75 timer;
	Event64 evTimer;
	final int timeReplaceGroups = 1000;
	final int timeDelayGreenToRed=500;
	
	boolean flag;//save if one of the buttons was pressed
	
	enum OutState{ON_SHABBAT, ON_CHOL};
	OutState outState;
	enum InState{G0_TO_GREEN, G0_TO_RED, G0_IN_RED, G1_TO_GREEN, G1_TO_RED, G1_IN_RED, G2_TO_GREEN, G2_TO_RED, G2_IN_RED}
	InState inState;
	
	public Controller(Event64[] evToRed2, Event64[] evToGreen2, Event64[] evOnRed2, Event64[] evToShabbat2, Event64[] evToChol2,
			Event64 evShabbatPressed, Event64 evCholPressed, Event64 evRegelPressed) {
		super();
		this.evToRed = evToRed2;
		this.evToGreen = evToGreen2;
		this.evOnRed = evOnRed2;
		this.evToShabbat = evToShabbat2;
		this.evToChol = evToChol2;
		//this.evPressButt = evPressButt2;
		this.evShabbatPressed=evShabbatPressed;
		this.evCholPressed=evCholPressed;
		this.evRegelPressed=evRegelPressed;
		
		g0=new HashSet<Integer>(Arrays.asList(0,6,7,9,10,12,13));
		g1=new HashSet<Integer>(Arrays.asList(1,4,5,6,7,9,10,12,13));
		g2=new HashSet<Integer>(Arrays.asList(2,3,4,5,8,11));
		
		start();
	}
	
	public void run() {
		try
		{
			System.out.println("Started run");
			setG0Green();
			evTimer=new Event64();
			timer=new Timer75(timeReplaceGroups, evTimer);
			inState=InState.G0_TO_GREEN;
			outState=OutState.ON_CHOL;
			
			while(true)
			{
				switch(outState)
				{
				case ON_CHOL:
//					evTimer=new Event64();
//					timer=new Timer75(timeReplaceGroups, evTimer);
					while(outState==OutState.ON_CHOL)
					{
						switch (inState) {
						case G0_TO_GREEN:
							System.out.println("G0_TO_GREEN");
							while(true)
							{
								//System.out.println("jojo");
								if(evTimer.arrivedEvent())//not enter into it
								{
									//System.out.println("timer arrived");
									evTimer.waitEvent();
									setG0Red();
									inState=InState.G0_TO_RED;
									break;
								}
								else if(evShabbatPressed.arrivedEvent())//shabbat pressed
								{
									evShabbatPressed.waitEvent();
									setShabbat();
									outState=OutState.ON_SHABBAT;
									break;
								}
								else
									yield();
							}
							break;
						case G0_TO_RED:
							System.out.println("G0_TO_RED");

							while(true)
							{
								if(isG0OnRed())
								{
									//System.out.println("isG0OnRed is true");
									inState=InState.G0_IN_RED;
									evTimer=new Event64();
									timer=new Timer75(timeDelayGreenToRed, evTimer);
									break;
								}
								else if(evShabbatPressed.arrivedEvent())//shabbat pressed
								{
									evShabbatPressed.waitEvent();
									setShabbat();
									outState=OutState.ON_SHABBAT;
									break;
								}
								else
									yield();
							}
							break;
						case G0_IN_RED:
							System.out.println("G0_IN_RED");
							while(inState==InState.G0_IN_RED)
							{
								if(evTimer.arrivedEvent())
								{
									GoStateG1ToGreen();
									break;
								}
								else if(evShabbatPressed.arrivedEvent())//shabbat pressed
								{
									evShabbatPressed.waitEvent();
									setShabbat();
									outState=OutState.ON_SHABBAT;
									break;
								}
								else if (evRegelPressed.arrivedEvent()) 
								{
									//evRegelPressed.waitEvent();
									//int numRamzor = Integer.parseInt((String) (evRegelPressed.waitEvent()));
									Object obj = evRegelPressed.waitEvent();
									Integer numRamzor =Integer.parseInt(obj.toString());
									if (g1.contains(numRamzor)) 
									{
										GoStateG1ToGreen();
										break;
									} 
									else if (g2.contains(numRamzor)) 
									{
										GoStateG2ToGreen();
										break;
									} 
									else if (g0.contains(numRamzor)) 
									{
										GoStateG0ToGreen();
										break;
									}

								} 
								else
									yield();
							}
							break;
						case G1_TO_GREEN:
							System.out.println("G1_TO_GREEN");
							while(true)
							{
								if(evTimer.arrivedEvent())
								{
									evTimer.waitEvent();
									setG1Red();
									inState=InState.G1_TO_RED;
									break;
								}
								else if(evShabbatPressed.arrivedEvent())//shabbat pressed
								{
									evShabbatPressed.waitEvent();
									setShabbat();
									outState=OutState.ON_SHABBAT;
									break;
								}
								else
									yield();
							}
							break;
						case G1_TO_RED:
							System.out.println("G1_TO_RED");
							while(true)
							{
								if(isG1OnRed())
								{
									inState=InState.G1_IN_RED;
									evTimer=new Event64();
									timer=new Timer75(timeDelayGreenToRed, evTimer);									
									break;
								}
								else if(evShabbatPressed.arrivedEvent())//shabbat pressed
								{
									evShabbatPressed.waitEvent();
									setShabbat();
									outState=OutState.ON_SHABBAT;
									break;
								}
								else
									yield();
							}
							break;
						case G1_IN_RED:
							System.out.println("G1_IN_RED");
							while(inState==InState.G1_IN_RED)
							{
								if(evTimer.arrivedEvent())
								{
									GoStateG2ToGreen();
									break;
								}
								else if(evShabbatPressed.arrivedEvent())//shabbat pressed
								{
									evShabbatPressed.waitEvent();
									setShabbat();
									outState=OutState.ON_SHABBAT;
									break;
								}
								else if(evRegelPressed.arrivedEvent())
								{
									//evRegelPressed.waitEvent();
									//int numRamzor = Integer.parseInt((String) (evRegelPressed.waitEvent()));
									Object obj = evRegelPressed.waitEvent();
									Integer numRamzor =Integer.parseInt(obj.toString());
									System.out.println("pressed "+numRamzor);
									if (g1.contains(numRamzor)) {
										GoStateG1ToGreen();
										break;
									} else if (g2.contains(numRamzor)) {
										GoStateG2ToGreen();
										break;
									} else if (g0.contains(numRamzor)) {
										GoStateG0ToGreen();
										break;
									}
								}
//									for (int i = 0; i < evPressButt.length-1; i++) 
//									{
//										if(evPressButt[i].arrivedEvent())
//										{
//											flag=true;
//											evPressButt[i].waitEvent();
//											if(g2.contains(i))
//											{
//												GoStateG2ToGreen();
//												break;
//											}
//											else if(g0.contains(i))
//											{
//												GoStateG0ToGreen();
//												break;
//											}
//											else if(g1.contains(i))
//											{
//												GoStateG1ToGreen();
//												break;
//											}
//										}
//									}
									//if(!flag)
								else
										yield();
								
							}
							break;
						case G2_TO_GREEN:
							System.out.println("G2_TO_GREEN");
							while(true)
							{
								if(evTimer.arrivedEvent())
								{
									evTimer.waitEvent();
									setG2Red();
									inState=InState.G2_TO_RED;
									break;
								}
								else if(evShabbatPressed.arrivedEvent())//shabbat pressed
								{
									evShabbatPressed.waitEvent();
									setShabbat();
									outState=OutState.ON_SHABBAT;
									break;
								}
								else
									yield();
							}
							break;
						case G2_TO_RED:
							System.out.println("G2_TO_RED");
							while(true)
							{
								if(isG2OnRed())
								{
									inState=InState.G2_IN_RED;
									evTimer=new Event64();
									timer=new Timer75(timeDelayGreenToRed, evTimer);
									break;
								}
								else if(evShabbatPressed.arrivedEvent())//shabbat pressed
								{
									evShabbatPressed.waitEvent();
									setShabbat();
									outState=OutState.ON_SHABBAT;
									break;
								}
								else
									yield();
							}
							break;
						case G2_IN_RED:
							System.out.println("G2_IN_RED");
							while(inState==InState.G2_IN_RED)
							{
								if(evTimer.arrivedEvent())
								{
									GoStateG0ToGreen();
									break;
								}
								else if(evShabbatPressed.arrivedEvent())//shabbat pressed
								{
									evShabbatPressed.waitEvent();
									setShabbat();
									outState=OutState.ON_SHABBAT;
									break;
								}
								else if (evRegelPressed.arrivedEvent()) 
								{
									//evRegelPressed.waitEvent();
									
									
									System.out.println("regel was pressed");
									
									Object obj=evRegelPressed.waitEvent();
									//System.out.println(Integer.parseInt(obj.toString()));
									//System.out.println(obj);
									
									Integer numRamzor =Integer.parseInt(obj.toString());
									
									
									//System.out.println("opopo");
									System.out.println("bye"+numRamzor);
									
									if (g1.contains(numRamzor)) 
									{
										GoStateG1ToGreen();
										break;
									} 
									else if (g2.contains(numRamzor)) 
									{
										GoStateG2ToGreen();
										break;
									} 
									else if (g0.contains(numRamzor)) 
									{
										GoStateG0ToGreen();
										break;
									}

								} 
								else
									yield();
							}
							break;
						}
					}
					break;
				case ON_SHABBAT:
					evCholPressed.waitEvent();
					setChol();
					setG0Green();
					evTimer=new Event64();
					timer=new Timer75(timeReplaceGroups, evTimer);
					outState = OutState.ON_CHOL;
					inState = InState.G0_TO_GREEN;

					break;
				}
			}
		}
		catch(Exception ex){}
	}

	private void GoStateG1ToGreen() {
		setG1Green();
		inState=InState.G1_TO_GREEN;
		evTimer=new Event64();
		timer=new Timer75(timeReplaceGroups, evTimer);
	}
	private void GoStateG2ToGreen() {
		setG2Green();
		inState=InState.G2_TO_GREEN;
		evTimer=new Event64();
		timer=new Timer75(timeReplaceGroups, evTimer);
		
	}
	private void GoStateG0ToGreen() {
		setG0Green();
		inState=InState.G0_TO_GREEN;
		evTimer=new Event64();
		timer=new Timer75(timeReplaceGroups, evTimer);
		
	}
	
	private boolean isG0OnRed() {
		boolean flag = true;

		for (Integer num : g0) 
		{
			flag = flag && evOnRed[num].arrivedEvent();
			//System.out.println("lololo "+num+" "+flag);
		}
		//System.out.println("lololo "+flag);
		if (flag)
			for (Integer num : g0) {
				evOnRed[num].waitEvent();
			}
		return flag;
	}
	private boolean isG1OnRed() 
		{
			boolean flag=true;
			boolean tempFlag;
			for (Integer num : g1) {
				flag = flag && evOnRed[num].arrivedEvent();
			}
			if(flag)
				for (Integer num : g1) {
					evOnRed[num].waitEvent();
				}
			return flag;
		}
	private boolean isG2OnRed() 
		{
			boolean flag=true;
			boolean tempFlag;
			for (Integer num : g2) {
				flag = flag && evOnRed[num].arrivedEvent();
			}
			if(flag)
				for (Integer num : g2) {
					evOnRed[num].waitEvent();
				}
			return flag;
		}
	
	private void setShabbat() {
		for (int i = 0; i < evToShabbat.length; i++) {
			evToShabbat[i].sendEvent();
		}
	}
	private void setChol() {
		for (int i = 0; i < evToChol.length; i++) {
			evToChol[i].sendEvent();
		}
	}

	private void setG0Green() {
		for (Integer num : g0) {
			//System.out.println("set ramzor "+num+" green");
			evToGreen[num].sendEvent();
		}
	}
	private void setG1Green() {
		for (Integer num : g1) {
			evToGreen[num].sendEvent();
		}
	}
	private void setG2Green() {
		for (Integer num : g2) {
			evToGreen[num].sendEvent();
		}
	}
	
	private void setG0Red() {
		for (Integer num : g0) {
			evToRed[num].sendEvent();
		}
	}
	private void setG1Red() {
		for (Integer num : g1) {
			evToRed[num].sendEvent();
		}
	}
	private void setG2Red() {
		for (Integer num : g2) {
			evToRed[num].sendEvent();
		}
	}
	

}
