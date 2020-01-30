import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JRadioButton;

/*
 * Created on Tevet 5770 
 */

/**
 * @author לויאן
 */


public class MyActionListener implements ActionListener
{
	Event64[] evPressButt;
	Event64 evToShabbat, evToChol, evRegel;
	
	public MyActionListener(Event64 evToShabbat2, Event64 evToChol2, Event64 evRegel2) {
		evToShabbat=evToShabbat2;
		evToChol=evToChol2;
		evRegel=evRegel2;
	}
	
//	public MyActionListener(Event64[] evPressButt2) {
//		evPressButt=evPressButt2;
//	}

	public void actionPerformed(ActionEvent e) 
	{
		JRadioButton butt=(JRadioButton)e.getSource();
		
		//evPressButt[Integer.parseInt(butt.getName())-4].sendEvent();
		
		if(butt.getName().equals("16"))
		{
			if(butt.isSelected())
				evToShabbat.sendEvent();
			else
				evToChol.sendEvent();
		}
		else
			evRegel.sendEvent(butt.getName());
		
		System.out.println(butt.getName());
		//		butt.setEnabled(false);
		//		butt.setSelected(false);
	}

}
