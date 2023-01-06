package problem;

import java.util.concurrent.Semaphore;

public class Main {
	public static Semaphore marketslot;
	public static Mines lead;
	public static Mines mercury;
	public static Mines sulfur;
	public static void relMarketslot(){
		marketslot.release();
	}
	public static void main(String[] args){
		
		marketslot = new Semaphore(1);
		Control.marketscannerA= new Semaphore(0);
		Control.marketscannerB= new Semaphore(0);
		Control.marketscannerC= new Semaphore(0);
		Control.WaAControl=new Semaphore(1);
		Control.WaBControl=new Semaphore(1);
		Control.WaCControl=new Semaphore(1);
		Control.ControlAccess=new Semaphore(1);
		//Step 1 mines initialization and processing
		lead = new Mines(2,3,1);
		mercury = new Mines(2,3,2);
		sulfur = new Mines(2,3,3);
		//---------------------------
		lead.goToMarket();
		mercury.goToMarket();
		sulfur.goToMarket();
		//---------------------------
		Alchemists[] gA = new GuildA[30];
		for(int i=0;i<gA.length;i++){
			gA[i]=new GuildA(1,lead, mercury);
			gA[i].start();
		}
		Alchemists[] gB = new GuildA[30];
		for(int i=0;i<gB.length;i++){
			gB[i]=new GuildA(1,lead, mercury);
			gB[i].start();
		}
		Alchemists[] gC = new GuildA[30];
		for(int i=0;i<gC.length;i++){
			gC[i]=new GuildA(1,lead, mercury);
			gC[i].start();
		}
		//---------------------------
//		Alchemists a1 = new GuildA(1,lead, mercury);
//		Alchemists a4 = new GuildA(1,lead, mercury);
//		Alchemists a6 = new GuildA(1,lead, mercury);
//		Alchemists a7 = new GuildA(1,lead, mercury);
//		Alchemists a8 = new GuildA(1,lead, mercury);
//		Alchemists a9 = new GuildA(1,lead, mercury);
//		
//		Alchemists a2 = new GuildB(2,sulfur, mercury);
//		Alchemists a5 = new GuildB(2,sulfur, mercury);
//		Alchemists a10 = new GuildB(2,sulfur, mercury);
//		Alchemists a11 = new GuildB(2,sulfur, mercury);
//		
//		Alchemists a12 = new GuildC(3,lead, mercury,sulfur);
//		Alchemists a13 = new GuildC(3,lead, mercury,sulfur);
//		Alchemists a3 = new GuildC(3,lead, mercury,sulfur);
//		Alchemists a14 = new GuildC(3,lead, mercury,sulfur);
		
//		a1.start();
//		a2.start();
//		a3.start();
//		a4.start();
//		a5.start();
//		a6.start();
//		a7.start();
//		a8.start();
//		a9.start();
//		a10.start();
//		a11.start();
//		a12.start();
//		a13.start();
//		a14.start();
		
//		try {
//			a1.join();
//			a2.join();
//			a3.join();
//			a4.join();
//			a5.join();
//			a6.join();
//			a7.join();
//			a8.join();
//			a9.join();
//			a10.join();
//			a11.join();
//			a12.join();
//			a13.join();
//			a14.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		for(int i=0;i<gA.length;i++){
			try {
				gA[i].join();
				gB[i].join();
				gC[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("All ALCHEMISTS CAME BACK HOME!");
		
	}
}
/*Mine types:
 * Lead - 1;
 * Mercury - 2;
 * Sulfur - 3;
 * 
 * */

/*Question list:
 * 
 * */