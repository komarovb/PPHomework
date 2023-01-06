package problem2;

import java.util.Scanner;

public class Main {
	static int[] pot;
	static int[] bowl;
	static boolean[] waiting;
	static int[] ingredients;
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		pot = new int[n/2];
		bowl = new int[n/2];
		ingredients = new int[n/2];
		waiting = new boolean[n];
		Monitor controller = new Monitor(n);
		Apprentice apprentice = new Apprentice(controller);
		Alchemists[] alchs =new Alchemists[n];
		/*
		 * if pot(bowl)[i]=0 - it is free
		 * if pot(bowl)[i]=1 - it is busy
		 * waiting[i]=false - doesn't wait
		 * waiting[i]=true - is waiting for dish
		 * */
		for(int i=0;i<n;i++){
			if(i<n/2){
				pot[i]=0;
				bowl[i]=0;
				ingredients[i]=5;
			}
			waiting[i] = false;
			alchs[i]=new Alchemists(i,10,controller);
			alchs[i].start();
		}
		apprentice.start();
		for(int i=0;i<n;i++){
			try {
				alchs[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("All alchemists now have LUNCH time!");
		sc.close();
	}

}

/*
 * 1)Loops staff
 * 2)Question about code from slide а что если между
 * 
 * */

