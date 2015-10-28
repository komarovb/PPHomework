package problem;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class GuildA extends Alchemists{
	private int id;
	private Mines lead;
	private Mines mercury;
	private Mines sulfur;
	
	public GuildA(int type, Mines lead, Mines mercury, Mines sulfur){
		this.lead=lead;
		this.mercury=mercury;
		this.sulfur=sulfur;
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
		if(!(lead.slots.isEmpty())&&!(mercury.slots.isEmpty())){
			try {
//				Main.slots_access.wait();
				lead.s_full.acquire();
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
				Main.marketscannerA.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			proceed();
			Main.done=true;
//			Main.slots_access.release();
		}
		System.out.println("Alchemist from guild A with the id:"+this.id+" has came back home");
	}
	public void proceed(){
		this.backpack.put("lead", 1);
		lead.slots.poll();
		System.out.println("Alchemist got a lead!");
		this.backpack.put("mercury", 1);
		mercury.slots.poll();
		System.out.println("Alchemist got a mercury!");
		lead.s_empty.release();
		mercury.s_empty.release();
	}
}
