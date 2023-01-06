package problem;

import java.util.concurrent.Semaphore;

public class Control {
	
	public static int WaA=0;
	public static int WaB=0;
	public static int WaC=0;
	public static Semaphore marketscannerA;
	public static Semaphore marketscannerB;
	public static Semaphore marketscannerC;
	public static Semaphore WaAControl;
	public static Semaphore WaBControl;
	public static Semaphore WaCControl;
	public static Semaphore ControlAccess;
	private static boolean done = false;
	
	public static void permitAlchToGoBuy(int type){
//		System.out.println("Permit called!");
		if(type==1){
				try {
					WaAControl.acquire();
//					System.out.println("Queue A:"+WaA);
					if(WaA>0){
//						System.out.println("Number of waiting alch 1: "+WaA);
						done=true;
						marketscannerA.release();
						ControlAccess.release();
						WaAControl.release();
						removeAlchFromaQueue(type);
					}
					else{
						Main.marketslot.release();
						ControlAccess.release();
						WaAControl.release();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		else if(type==2){
			try {
				WaBControl.acquire();
//				System.out.println("Queue B:"+WaB);
				if(WaB>0){
//					System.out.println("Number of waiting alch 2: "+WaB);
					done=true;
					marketscannerB.release();
					ControlAccess.release();
					WaBControl.release();
					removeAlchFromaQueue(type);
				}
				else{
					Main.marketslot.release();
					ControlAccess.release();
					WaBControl.release();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if(type==3){
			try {
				WaCControl.acquire();
//				System.out.println("Queue C:"+WaC);
				if(WaC>0){
//					System.out.println("Number of waiting alch 3: "+WaC);
					done=true;
					marketscannerC.release();
					ControlAccess.release();
					WaCControl.release();
					removeAlchFromaQueue(type);
				}
				else{
					Main.marketslot.release();
					ControlAccess.release();
					WaCControl.release();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else{
			System.out.println("Unknown type!");
		}
//		System.out.println("Nobody is waiting");
		done=false;
	}
	public static void addAlchToaQueue(int type){
		if(type==1){
			try {
				WaAControl.acquire();
				WaA++;
				System.out.println("Alch A added to a queue!");
				WaAControl.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
		else if(type==2){
			try {
				WaBControl.acquire();
				WaB++;
				System.out.println("Alch B added to a queue!");
				WaBControl.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if(type==3){
			try {
				WaCControl.acquire();
				WaC++;
				System.out.println("Alch C added to a queue!");
				WaCControl.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else{
			System.out.println("Unknown type!");
		}
	}
	public static void removeAlchFromaQueue(int type){
		if(type==1){
			try {
				WaAControl.acquire();
				WaA--;
				WaAControl.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
		else if(type==2){
			try {
				WaBControl.acquire();
				WaB--;
				WaBControl.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else if(type==3){
			try {
				WaCControl.acquire();
				WaC--;
				WaCControl.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else{
			System.out.println("Unknown type!");
		}
	}
	public static void checkForGoods(){
//		System.out.println("Checking!....");
//		System.out.println(Main.lead.slots+" "+Main.sulfur.slots+" "+Main.mercury.slots+" ");
		try {
			ControlAccess.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(!(Main.lead.slots.isEmpty())&&!(Main.mercury.slots.isEmpty())&&!(Main.sulfur.slots.isEmpty())){
			permitAlchToGoBuy(3);
			if(done==true){
//				System.out.println("End of checking");
				return;
			}
		}
		if(!(Main.lead.slots.isEmpty())&&!(Main.mercury.slots.isEmpty())&&done==false){
			permitAlchToGoBuy(1);
			if(done==true){
//				System.out.println("End of checking");
				return;
			}
		}
		if(!(Main.sulfur.slots.isEmpty())&&!(Main.mercury.slots.isEmpty())&&done==false){
			permitAlchToGoBuy(2);
			if(done==true){
//				System.out.println("End of checking");
				return;
			}
		}
//		System.out.println(done);
//		System.out.println("End of checking");
		Main.marketslot.release();
		ControlAccess.release();
	}
	
	public Control(){}
}
