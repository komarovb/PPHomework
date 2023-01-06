package problem2;

import java.util.Random;

public class Apprentice extends Thread {
	private Monitor control;
	
	public Apprentice(Monitor c){
		this.control=c;
	}
	public void run(){
		while(true){
			Random rnd = new Random();
			try {
				Thread.sleep(rnd.nextInt(2000+5000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			control.fullFillBowls();
		}
	}
}
