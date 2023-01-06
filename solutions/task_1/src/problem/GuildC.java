package problem;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class GuildC extends Alchemists{
	private int id;
	private Mines lead;
	private Mines mercury;
	private Mines sulfur;
	
	public GuildC(int type,Mines lead, Mines mercury,Mines sulfur){
		this.lead=lead;
		this.mercury=mercury;
		this.sulfur=sulfur;
		backpack = new ConcurrentHashMap<String, Integer>();
		backpack.put("lead", 0);
		backpack.put("mercury", 0);
		backpack.put("sulfur", 0);
		this.type=3;
		Random rnd = new Random();
		this.id=rnd.nextInt(1000);
		System.out.println("Alchemist from guid C with id: "+this.id+" is awake!");
	}
	public void run(){
		try {
			Thread.sleep(1000);
			Main.marketslot.acquire();
			System.out.println("Alchemist with id: "+this.id+" on a marketslot!");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if(!(lead.slots.isEmpty())&&!(sulfur.slots.isEmpty())&&!(mercury.slots.isEmpty())){
			proceed();
			Main.marketslot.release();
			System.out.println("Alchemist from guild C with the id:"+this.id+" has came back home without waiting");
		}
		else{
			Main.marketslot.release();
			System.out.println("Alchemist with id: "+this.id+" started waiting!");
			Control.addAlchToaQueue(3);
			try {
				Control.marketscannerC.acquire();
				Main.marketslot.acquire();
				System.out.println("Alchemist with id: "+this.id+" on a marketslot again!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			proceed();
			Main.marketslot.release();
			System.out.println("Alchemist from guild C with the id:"+this.id+" has came back home with some waiting");
		}
	}
	public void proceed(){
		try {
			lead.s_full.acquire();
			mercury.s_full.acquire();
			sulfur.s_full.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.backpack.put("lead", 1);
		lead.slots.poll();
		System.out.println("Alchemist with the id:"+this.id+" got a lead!");
		this.backpack.put("mercury", 1);
		mercury.slots.poll();
		System.out.println("Alchemist with the id:"+this.id+" got a mercury!");
		this.backpack.put("sulfur", 1);
		sulfur.slots.poll();
		System.out.println("Alchemist with the id:"+this.id+" got a sulfur!");
		lead.s_empty.release();
		mercury.s_empty.release();
		sulfur.s_empty.release();
	}
}
