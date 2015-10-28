package problem;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class GuildB extends Alchemists{
	private int id;
	private Mines mercury;
	private Mines sulfur;
	private Mines lead;
	
	public GuildB(int type,Mines lead, Mines mercury, Mines sulfur){
		this.sulfur=sulfur;
		this.mercury=mercury;
		this.lead=lead;
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
		if(!(sulfur.slots.isEmpty())&&!(mercury.slots.isEmpty())){
			try {
//				Main.slots_access.wait();
				sulfur.s_full.acquire();
				mercury.s_full.acquire();
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
				Main.marketscannerB.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			proceed();
			Main.done=true;
//			Main.slots_access.release();
		}
		System.out.println("Alchemist from guild B with the id:"+this.id+" has came back home");
	}
	public void proceed(){
		this.backpack.put("sulfur", 1);
		sulfur.slots.poll();
		System.out.println("Alchemist got a sulfur!");
		this.backpack.put("mercury", 1);
		mercury.slots.poll();
		System.out.println("Alchemist got a mercury!");
		sulfur.s_empty.release();
		mercury.s_empty.release();
	}
}
