package problem2;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
	final Lock lock = new ReentrantLock();
	final Condition[] PhiQ;
	
	private int potN;
	private int bowlN;
	private boolean w;
	private int n;
	private Queue<Integer> ingW;
	
	public Monitor(int n){
		this.PhiQ=new Condition[n];
		for (int i = 0; i < PhiQ.length; i++) {
			PhiQ[i]=lock.newCondition();
		}
		this.ingW=new LinkedList<Integer>();
		this.w=false;
		this.n=n;
	}
	public void fullFillBowls(){
		lock.lock();
		try{
			int id;
			System.out.println("------!!!!Apprentice in da house!");
			System.out.println("Queue size - "+ingW.size());
			System.out.println("Queue - "+ingW.toString());
			if(!ingW.isEmpty()){
				for (int i = 0; i < n/2; i++) {
					Main.ingredients[i]=5;
				}
				id=ingW.poll();
				System.out.println("Apprentice is going to signal to "+id);
				ingW.clear();
				PhiQ[id].signal();
			}
		}
		finally{
			lock.unlock();
		}
	}
	/**
	 * @param id
	 * @throws InterruptedException
	 * Method that allows Alchemist to take a pot and a bowl or
	 * wait if they are not acceptable right now
	 */
	public void beginProduction(int id) throws InterruptedException{
		lock.lock();
		try{
			getPotBowlN(id);
			if(Main.pot[potN]==1||Main.bowl[bowlN]==1||Main.ingredients[bowlN]<1){
				Main.waiting[id]=true;
				getPotBowlN(id);
				if(Main.ingredients[bowlN]<1){
					System.out.println("1AAAAdding alch with id "+id+" to a queue");
					ingW.add(id);
				}
				System.out.println("Alchemist with id - "+id+" is going to wait!");
				PhiQ[id].await();
				System.out.println("Alch with ID-"+id+" is AWAKEN!");
				Main.waiting[id]=false;
			}
			getPotBowlN(id);
			System.out.println("Bowl - "+bowlN+"::Pot - "+potN);
			Main.pot[potN]=1;
			Main.bowl[bowlN]=1;
			Main.ingredients[bowlN]--;
			System.out.println("Ingredients in bowl "+bowlN+" left: "+Main.ingredients[bowlN]);
			System.out.println("Alchemist with id - "+id+" took a pot and an ingredient from a bowl");
			if(w){
				w=false;
				PhiQ[getNewID(id,-1,2,1)].signal();
			}
//			if(!ingW.isEmpty()&&Main.ingredients[bowlN]>=1){
//				int num;
//				num=ingW.poll();
//				System.out.println("Alch with id"+id+" is going to signal to, who is still waiting "+num);
//				PhiQ[num].signal();
//			}
		}
		finally{
			lock.unlock();
		}	
	}
	/**
	 * @param id
	 * @throws InterruptedException
	 * Method is responsible for Alchemist to put down pot and bowl
	 * id - Current Alchemist id
	 */
	public void stopProduction(int id){
		lock.lock();
		try{
			getPotBowlN(id);
			Main.pot[potN]=0;
			Main.bowl[bowlN]=0;
			System.out.println("Alchemist with id - "+id+" produced 1 gold");
			if(Main.waiting[getNewID(id,-1,1,1)]==true&&Main.pot[getNewID(potN,-1,1,2)]==0&&Main.ingredients[bowlN]>=1){
				w=true;
				System.out.println("Prev - permissinon");
			}
			else{
				if(Main.ingredients[bowlN]<1){
					System.out.println(id+" - 2AAAAdding alch with id "+getNewID(id,-1,1,1)+" to a queue");
					ingW.add(getNewID(id,-1,1,1));
				}
			}
			if(Main.waiting[getNewID(id,1,1,1)]==true&&Main.bowl[getNewID(bowlN,1,1,2)]==0&&Main.ingredients[getNewID(bowlN,1,1,2)]>=1){
				System.out.println("1Alch with id"+id+" signalling to "+getNewID(id,1,1,1));
				PhiQ[getNewID(id,1,1,1)].signal();
			}
			else{
				if(Main.ingredients[getNewID(bowlN,1,1,2)]<1){
					System.out.println(id+" - 3AAAAdding alch with id "+getNewID(id,1,1,1)+" to a queue");
					ingW.add(getNewID(id,1,1,1));
				}
			}
			if(w){
				w=false;
				System.out.println("2Alch with id"+id+" Signal to "+getNewID(id,-1,1,1));
				PhiQ[getNewID(id,-1,1,1)].signal();
			}
		}
		finally{
			lock.unlock();
		}	
	}
	private void  getPotBowlN(int id){
		if(id%2==0){
			this.potN=id/2;
			this.bowlN=id/2;
		}
		else{
			this.potN=(id+1)/2;
			this.bowlN=(id-1)/2;
		}
		if(potN>((n/2)-1)){
			potN=0;
		}
		if(bowlN>((n/2)-1)){
			bowlN=0;
		}
	}
	
	/**
	 * @param id - for Alchemist id/Initial value
	 * @param operation - +/- (plus(1) or minus(-1))
	 * @param amount - number to add/subtract
	 * @param type - Alchemists(1), bowl/pot(2)
	 * @return
	 * 
	 */
	private int getNewID(int id,int operation,int amount,int type){
		int newID = 0;
		int tNe = n-1; //Total number of elements in collection;
		if(type==2){
			tNe=(n/2)-1;
		}
		switch (operation) {
		case 1:
			newID=id+amount;
			if(newID>tNe){
				newID=0;
			}
			break;
		case -1:
			newID=id-amount;
			if(newID<0){
				newID=tNe;
			}
			break;
		default:
			System.out.println("Something was really WRONG in getNewID function in Monitor class!");
			break;
		}
		return newID;
	}
}
