package problem;
import java.util.concurrent.ConcurrentHashMap;

public class Alchemists extends Thread{
	
	protected int type;
	protected ConcurrentHashMap<String, Integer> backpack;
	
	public Alchemists(){
	}
//	private boolean checkBackPack(){
//		int sum;
//		boolean result=false;
//		if(this.type==1){
//			sum=backpack.get("lead")+backpack.get("mercury");
//		}
//		else if(this.type==2){
//			sum=backpack.get("sulfur")+backpack.get("mercury");
//		}
//		else{
//			sum=backpack.get("lead")+backpack.get("mercury")+backpack.get("sulfur");
//			if(sum==3){
//				result = true;
//				return result;
//			}
//		}
//		if(sum==2){
//			result = true;
//			return result;
//		}
//		return result;
//	}
}
