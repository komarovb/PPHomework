package problem;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class GuildB extends Alchemists{
	private int id;
	private Mines sulfur;
	private Mines mercury;
	
	public GuildB(int type, Mines sulfur, Mines mercury){
		this.sulfur=sulfur;
		this.mercury=mercury;
		backpack = new ConcurrentHashMap<String, Integer>();
		backpack.put("sulfur", 0);
		backpack.put("mercury", 0);
		this.type=2;
		Random rnd = new Random();
		this.id=rnd.nextInt(1000);
		System.out.println("Alchemist from guid B with id: "+this.id+" is awake!");
	}
	public void run(){
		try {
			Thread.sleep(1000);
			Main.marketslot.acquire();
			System.out.println("Alchemist with id: "+this.id+" on a marketslot!");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if(!(sulfur.slots.isEmpty())&&!(mercury.slots.isEmpty())){
			proceed();
			Main.marketslot.release();
			System.out.println("Alchemist from guild B with the id:"+this.id+" has came back home without waiting");
		}
		else{
			Main.marketslot.release();
			System.out.println("Alchemist with id: "+this.id+" started waiting!");
			Control.addAlchToaQueue(2);
			try {
				Control.marketscannerB.acquire();
				Main.marketslot.acquire();
				System.out.println("Alchemist with id: "+this.id+" on a marketslot again!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			proceed();
			Main.marketslot.release();
			System.out.println("Alchemist from guild B with the id:"+this.id+" has came back home with some waiting");
		}
	}
	public void proceed(){
		try {
			sulfur.s_full.acquire();
			mercury.s_full.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.backpack.put("sulfur", 1);
		sulfur.slots.poll();
		System.out.println("Alchemist with id: "+this.id+" got a sulfur!");
		this.backpack.put("mercury", 1);
		mercury.slots.poll();
		System.out.println("Alchemist with id: "+this.id+" got a mercury!");
		sulfur.s_empty.release();
		mercury.s_empty.release();
	}
}
