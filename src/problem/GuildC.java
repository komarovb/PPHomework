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
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//Trick-------------------
		if(!(lead.slots.isEmpty())&&!(mercury.slots.isEmpty())&&!(sulfur.slots.isEmpty())){
			Main.marketscannerC.release();
			if(Main.done==false){
				Main.marketscannerA.release();
			}
			if(Main.done==false){
				Main.marketscannerB.release();
			}
		}
		else if(!(lead.slots.isEmpty())&&!(mercury.slots.isEmpty())&&Main.done==false){
			Main.marketscannerA.release();
		}
		else if(!(sulfur.slots.isEmpty())&&!(mercury.slots.isEmpty())&&Main.done==false){
			Main.marketscannerB.release();
		}
		//-------------Not sure
		if(!(lead.slots.isEmpty())&&!(mercury.slots.isEmpty())&&!(sulfur.slots.isEmpty())){
			try {
//				Main.slots_access.wait();
				lead.s_full.acquire();
				mercury.s_full.acquire();
				sulfur.s_full.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			proceed();
//			Main.slots_access.release();
			Main.marketslot.release();
		}
		else{
			Main.marketslot.release();
			try {
//				Main.slots_access.wait();
				Main.marketscannerC.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			proceed();
			Main.done=true;
//			Main.slots_access.release();
		}
		System.out.println("Alchemist from guild C with the id:"+this.id+" has came back home");
	}
	public void proceed(){
		this.backpack.put("lead", 1);
		lead.slots.poll();
		System.out.println("Alchemist got a lead!");
		this.backpack.put("mercury", 1);
		mercury.slots.poll();
		System.out.println("Alchemist got a mercury!");
		this.backpack.put("sulfur", 1);
		sulfur.slots.poll();
		System.out.println("Alchemist got a sulfur!");
		lead.s_empty.release();
		mercury.s_empty.release();
		sulfur.s_empty.release();
	}
}
