package problem;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Mines{
	
	protected int mineId;
	public Queue<Integer> slots;
	private Representative[] representatives;
	protected Semaphore s_empty;
	protected Semaphore s_full;
	
	public Mines(int numberOfSlots, int numberOfRepresentatives, int id)
	{
		this.mineId = id;
		this.slots = new LinkedList<Integer>();
		this.representatives = new Representative[numberOfRepresentatives];
		for (int i = 0; i < representatives.length; i++) {
			representatives[i]=new Representative(i+1,this);
		}
		this.s_empty = new Semaphore(numberOfSlots);
		this.s_full = new Semaphore(0);
	}
	public Mines()
	{	
	}
	public void goToMarket()
	{
		for (int i = 0; i < representatives.length; i++) {
			representatives[i].start();
		}
	}
}
