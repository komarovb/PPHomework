package problem;

import java.util.concurrent.Semaphore;

public class Main {
	public static Semaphore marketslot;
	public static Semaphore marketscannerA;
	public static Semaphore marketscannerB;
	public static Semaphore marketscannerC;
	public static Semaphore slots_access;
	public static volatile boolean done;
	public static void relMarketslot(){
		marketslot.release();
	}
	public static void main(String[] args){
		
		marketslot = new Semaphore(1);
		marketscannerA= new Semaphore(0);
		marketscannerB= new Semaphore(0);
		marketscannerC= new Semaphore(0);
		slots_access = new Semaphore(1);
		done=false;
		//Step 1 mines initialization and processing
		Mines lead = new Mines(2,3,1);
		Mines mercury = new Mines(2,3,2);
		Mines sulfur = new Mines(2,3,3);
		//---------------------------
		Alchemists a1 = new GuildA(1,lead, mercury,sulfur);
		Alchemists a2 = new GuildB(2,lead, mercury,sulfur);
		Alchemists a3 = new GuildC(3,lead, mercury,sulfur);
		Alchemists a4 = new GuildA(1,lead, mercury,sulfur);
		Alchemists a5 = new GuildB(2,lead, mercury,sulfur);
		Alchemists a6 = new GuildA(1,lead, mercury,sulfur);
		
		lead.goToMarket();
		mercury.goToMarket();
		sulfur.goToMarket();
		a1.start();
		a2.start();
		a3.start();
		a4.start();
		a5.start();
		a6.start();
		
		try {
			a1.join();
			a2.join();
			a3.join();
			a4.join();
			a5.join();
			a6.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("All Alchemist came back home!");
		
	}
}
/*Mine types:
 * Lead - 1;
 * Mercury - 2;
 * Sulfur - 3;
 * 
 * */

/*Question list:
 * 1) Which type of protection is better to use for semaphores?
 * 2) Are all representatives coming to the marketplace infinitely?
 * 3) A loop in producer/consumer stuff - is it a busy waiting?
 * 4) How to make semaphores parallel call and to proceed if one success?
 * 5)...
 * IDEA:Rewrite classes and create subclasses for each Alchemist type
 * */