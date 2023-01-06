package problem;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class GuildA extends Alchemists{
	private int id;
	private Mines lead;
	private Mines mercury;
	
	public GuildA(int type, Mines lead, Mines mercury){
		this.lead=lead;
		this.mercury=mercury;
		backpack = new ConcurrentHashMap<String, Integer>();
		backpack.put("lead", 0);
		backpack.put("mercury", 0);
		this.type=1;
		Random rnd = new Random();
		this.id=rnd.nextInt(1000);
		System.out.println("Alchemist from guid A with id: "+this.id+" is awake!");
	}
	public void run(){
		try {
			Thread.sleep(1000);
			Main.marketslot.acquire();
			System.out.println("Alchemist with id: "+this.id+" on a marketslot!");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if(!(lead.slots.isEmpty())&&!(mercury.slots.isEmpty())){
			proceed();
			Main.marketslot.release();
			System.out.println("Alchemist from guild A with the id:"+this.id+" has came back home without waiting");
		}
		else{
			Main.marketslot.release();
			System.out.println("Alchemist with id: "+this.id+" started waiting!");
			Control.addAlchToaQueue(1);
			try {
				Control.marketscannerA.acquire();
				System.out.println("Alchemist with id: "+this.id+" on a marketslot again!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			proceed();
			Main.marketslot.release();
			System.out.println("Alchemist from guild A with the id:"+this.id+" has came back home with some waiting");
		}
	}
	public void proceed(){
		try {
			lead.s_full.acquire();
			mercury.s_full.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.backpack.put("lead", 1);
		lead.slots.poll();
		System.out.println("Alchemist with id: "+this.id+" got a lead!");
		this.backpack.put("mercury", 1);
		mercury.slots.poll();
		System.out.println("Alchemist with id: "+this.id+" got a mercury!");
		lead.s_empty.release();
		mercury.s_empty.release();
	}
}
