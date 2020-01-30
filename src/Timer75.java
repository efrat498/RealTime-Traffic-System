
/**
 *
 * @author levian
 *
 */
 
public class Timer75 extends Thread
{
    private final long time;
    private final Event64 evTime;
	private boolean cancel=false;

    public Timer75(long time,Event64 evTime)
    {
        this.time=time;
        this.evTime=evTime;
        setDaemon(true);
        start();
    }
	
	public void cancel()
	{
		cancel=true;
	}

    public void run()
    {
        try 
		{
        	//System.out.println("timer started "+time);
            sleep(time);
            //System.out.println("timer finished "+time);
        } catch (InterruptedException ex) {}
        //System.out.println(cancel);
        if (!cancel)
			evTime.sendEvent();
    }

}