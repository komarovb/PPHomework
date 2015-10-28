package problem;

import java.util.Random;

//Class that represents a single mine representative!

public class Representative extends Thread {
	
	Mines currentMine;//Getting parent Minery
	
	private Random timeFactor;
	private int rNum; //Representative unique number
	protected Boolean working; //Trigger that is looking for a representative work
	
	public Representative(int rNum, Mines currentMine){
		this.timeFactor=new Random();
		this.rNum=rNum;
		this.currentMine=currentMine;
		this.working=true;
		System.out.println("Representative "+rNum+" from Mine number "+currentMine.mineId+" is awake");
	}
	
	public void run(){
		try {
			while(working){
				currentMine.s_empty.acquire(); //Decreasing s_empty value (default is 2)
				System.out.println("Representative "+rNum+" from Mine number "+currentMine.mineId+" has come to a marketplace!");
				currentMine.slots.add(1); //Adding an element in a single slot in the marketplace
				System.out.println("Representative "+rNum+" from Mine number "+currentMine.mineId+" has left some gold on a marketplace-----");
				currentMine.s_full.release();  //Informing our clients that one more element is available	
			}
			Thread.sleep(timeFactor.nextInt(500) + 200); //Our long and dangerous way to a marketplace
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}