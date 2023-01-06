#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#define LENGTH(x) (sizeof(x)/sizeof((x)[0]))
int minRank(int neighR[],int n);
int main(int argc, char *argv[])  {

  //printf("AGRUMENTS - %d",argc);
  if(argc<2){
    printf("Do not forget about arguments!\n");
    return -1;
}
  int numtasks, rank,x,y,status=0; 
  
  MPI_Init(&argc,&argv);
  MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
  FILE *fp;
  int n;
  char line[5];
  int length=LENGTH(line);

  fp=fopen(argv[1],"r");
	
  if( fgets( line, 5, fp ) != NULL )
    n=atoi(line);
  printf("Value of n is - %d\n",n);
  int allx[n],ally[n],neighX[n-1],neighY[n-1],neighS[n-1],neighR[n-1],count=0;
	MPI_Request req[n];
	MPI_Status stat[n];
  while ( fgets( line, 5, fp ) != NULL ){ 
    int i,found=0;
    char xcoord[5],ycoord[5];
    for(i=0;i<length;i++){
      if(line[i]!=' '&&found==0){
				//Determining X coordinate
				//printf("I am here and my char is: %c\n",line[i]);
	  		xcoord[i]=line[i]; 
      }
			else if(line[i]==' '){
				found=i+1;	
			}
			else{	
				//Determining Y coordinate
				//printf("I am here and my char is: %d\n",line[i]);
				int l=i-found;
				if(line[i]==0)
					break;
	  		ycoord[l]=line[i];
			}
    }
	//printf("\n");
	x=atoi(xcoord);
	y=atoi(ycoord);
	//printf("X coordinate is - %d\n",x);
	//printf("Y coordinate is - %d\n",y);
	allx[count]=x;
	ally[count]=y;
	count++;
  }
  fclose(fp); 
  if(numtasks == n) {
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
		//Finding all neighbours
		int i;
		count=0;
		x=allx[rank];
		y=ally[rank];
		for(i=0;i<n;i++){
			int rX,uY,lX,dY,dist=3;
			rX=x+dist;
			lX=x-dist;
			uY=y+dist;
			dY=y-dist;
			if(i!=rank&&allx[i]>=lX&&allx[i]<=rX&&ally[i]>=dY&&ally[i]<=uY){
				neighX[count]=allx[i];
				neighY[count]=ally[i];
				neighR[count]=i;
				count++;
			}
		}
		if(count<n){
			neighX[count]=-100;
			neighY[count]=-100;
			neighR[count]=-100;
		}			
    printf("My rank is - %d and my coords are: (%d;%d)\n",rank,x,y);
		for(i=0;i<n;i++){
			if(neighX[i]!=-100)
				printf("Neighbours: rank - %d,coords - (%d;%d)\n",neighR[i],neighX[i],neighY[i]);
			else
				break;
		}
		//---------------------
		//NeighR sorting
		for(i=2;i<n-1;i++){
			int j=i-1,key=neighR[i];
			if(key==-100)
				break;
			while(j>0&&neighR[j]>key){
				neighR[j+1]=neighR[j];
				j--;
			}
			neighR[j+1]=key;
		}
			
		//-----------
		/*
 		* Statuses system:
 		* 0 - nothing happening
 		* 1 - waiting
 		* 2 - playing
 		* 3 - done	
 		* */
	  //usleep(2 * 1000000);
		//while(status!=3){
			if(status==0){
				int min=-100;
					min=neighR[0];
					if(min>rank)
						min=rank;
					printf("Min for me(%d) %d\n",rank,min);
					if(rank==min){
						status=2;
						for(i=0;i<n-1;i++){
							if(neighX[i]==-100)
								break;
							MPI_Isend(&status,1,MPI_INT,neighR[i],1,MPI_COMM_WORLD,&req[i]);
							printf("Sending(%d) msg to %d\n",rank,neighR[i]);
						}
						MPI_Waitall(i,req,stat);
						MPI_Allgather(&status,1,MPI_INT,neighS,1,MPI_INT,MPI_COMM_WORLD);
					}
					else{
						int lStatus=-1;
						//printf("I am going to recevive from: %d\n",min);
						MPI_Irecv(&lStatus,1,MPI_INT,min,1,MPI_COMM_WORLD,&req[n-1]);
						MPI_Wait(&req[n-1],&stat[n-1]);	
						printf("Recv(%d) msg from %d, received status is:%d\n",rank,min,lStatus);	
						if(lStatus==2){
							status=1;
							for(i=0;i<n-1;i++){
								if(neighX[i]==-100)
									break;
								MPI_Isend(&status,1,MPI_INT,neighR[i],1,MPI_COMM_WORLD,&req[i]);
								printf("Sending(%d) msg to %d\n",rank,neighR[i]);
							}
							MPI_Waitall(i,req,stat);
							MPI_Allgather(&status,1,MPI_INT,neighS,1,MPI_INT,MPI_COMM_WORLD);
						}
						else if(lStatus==1){
							//printf("Going to Gather!\n");
							MPI_Allgather(&status,1,MPI_INT,neighS,1,MPI_INT,MPI_COMM_WORLD);
							//printf("Gathered!");	
							//for(i=0;i<n;i++)
								//printf("Statuses: %d ",neighS[i]);
							//printf("\n");
							for(i=1;i<n-1;i++){
								if(neighS[neighR[i]]==0){
									if(neighR[i]<rank){
										status=1;
										break;
									}
									else{
										status=2;
										break;
									}
								}			
							}
						}
					}		
				//Find out if I am a neighbourhood leader
				//Change my status to 2 if yes
				//change my status to 1 if no	
			}
			else if(status==1){
				//Waiting to receive signal to start playing
				//Check If all my neighbours are not singing
				//Send an answer if I started playing or not
				//If I started change my status to 2
			}
			else if(status==2){
				//1.Sleep for two seconds
				//2.Change my status to 3
				//3.Find next leader in my group
				//4.Send him a proposition to start
				//5.Receive an answer from him
				//6.If answer is positive (process started playing) terminate process
				//7.If answer is negative find another candidate in your neighbouhood
				//8.Repeat steps 3 - 7 unil no positive reply or lack of neighbours
			}
			
			//for(i=0;i<n;i++)
				//printf("Statuses: %d ",neighS[i]);
			//printf("\n");
		//}
		MPI_Barrier(MPI_COMM_WORLD);
		printf("My rank is -  %d and my status is %d\n",rank,status);
  }
  else{
    printf("Error in hosts file!");
  }
  MPI_Finalize();
  return 0;
}
int minRank(int neighR[],int n){
	int i,min=neighR[0];
	for(i=0;i<n;i++){
		if(neighR[i]==-100)
			break;
		if(neighR[i]<min)
			min=neighR[i];
	}
	return min;	
}
