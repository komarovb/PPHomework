package problem2;

import java.util.Random;

public class Alchemists extends Thread {
	private int numOfGoldProduced;
	private int maxGold;
	private int myID;
	private Monitor control;
	
	public Alchemists(int id,int max,Monitor control){
		this.numOfGoldProduced=0;
		this.maxGold=max;
		this.myID=id;
		this.control = control;
	}
	public void run(){
		while(numOfGoldProduced!=maxGold){
			Random rnd = new Random();
			try {
				if(numOfGoldProduced>=1){
					Thread.sleep(rnd.nextInt(500+1500));
				}
				control.beginProduction(myID);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(1000);
				numOfGoldProduced++;
			} catch (InterruptedException e) {
				e.printStackTrace();}
			control.stopProduction(myID);
		}
		System.out.println("Alchemist with id - "+myID+" HAS a LUNCH");
	}
}
