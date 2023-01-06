#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#define UP    0
#define DOWN  1
#define LEFT  2
#define RIGHT 3

#define LENGTH(x) (sizeof(x)/sizeof((x)[0]))

main(int argc, char *argv[])  {

  //printf("AGRUMENTS - %d",argc);
  if(argc<2){
    printf("Do not forget about arguments!");
    return;
}
  int numtasks, rank, dest, i,j,tag=1, 
    nbrs[4], dims[2]={0,0}, 
    time,periods[2]={0,0}, reorder=0, coords[2],size,status;
  
  MPI_Comm cartcomm;
  
  MPI_Init(&argc,&argv);
  MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
  FILE *fp;
  int n,m,read;
  char line[5];
  int length=LENGTH(line);

  fp=fopen(argv[1],"r");
	
  if( fgets( line, 5, fp ) != NULL )
    n=atoi(line);
  if( fgets( line, 5, fp ) != NULL )
	m=atoi(line);
  //printf("Value of n is - %d\n",n);
  //printf("Value of m is - %d\n",m);
  dims[0]=n;
  dims[1]=m;
  size=n*m;
  fgets(line,5,fp);
  int allx[size],ally[size],count=0,ptr=0,
      protex[1],protey[1],deadx[1],deady[1];
  protex[0]=-100;
  protey[0]=-100;
  deadx[0]=-100;
  deady[0]=-100;
  while ( fgets( line, 5, fp ) != NULL ){ 
    int i,j,pos,x,y;
    int found = 0;
    char xcoord[5],ycoord[5];
    for(i=0;i<length;i++){
      if(line[i]==','){
	found=1;  
        pos=i;
	//Determining X coordinate
        for(j=0;j<pos;j++){
	  xcoord[j]=line[j]; 
	}
	x=atoi(xcoord);
	allx[count]=x;
	//printf("X coordinate is - %d\n",x);
	//Determining Y coordinate
	pos=length-(length-pos)+1;
	int l=0;
        for(j=pos;j<length;j++){
	  ycoord[l]=line[j];
	  l++;
	  if(l==5)
	    break; 
	}
	y=atoi(ycoord);
	ally[count]=y;
	//printf("Y coordinate is - %d\n",y);
	count++;
      }
    }
    if(!found){
      allx[count]=-2;
      ally[count]=-2;
      ptr=count;
      count++;	
    }
  }
  allx[count]=-5;
  ally[count]=-5;
  count++;
  fclose(fp); 
 
  if(numtasks == size) {
    MPI_Cart_create(MPI_COMM_WORLD, 2, dims, periods, reorder, &cartcomm);
    MPI_Comm_rank(cartcomm, &rank);
    MPI_Cart_coords(cartcomm, rank, 2, coords);
    MPI_Cart_shift(cartcomm, 0, 1, &nbrs[UP], &nbrs[DOWN]);
    MPI_Cart_shift(cartcomm, 1, 1, &nbrs[LEFT], &nbrs[RIGHT]);
    
    /*File processing testing block
    if(rank==0){
      int k;
      for(k=0;k<count+1;k++){
	printf("Coordinate X - %d\n",allx[k]);	
	printf("Coordinate Y - %d\n",ally[k]);
      }	
    }
    -----------------------------*/
    status=0;
    int good=1;
    for(i=0;i<count;i++){
	if(allx[i]==-2&&ally[i]==-2){
	  good=-1;
	}	
	if(allx[i]==-5&&ally[i]==-5){
	  break;
	}
	if(coords[0]==allx[i]&&coords[1]==ally[i]){
	 status=good; 
	}
    } 
    printf("rank= %d coords= %d %d status= %d  neighbors(u,d,l,r)= %d %d %d %d\n",
           rank,coords[0],coords[1],status,nbrs[UP],nbrs[DOWN],nbrs[LEFT],
                                                            nbrs[RIGHT]);
    srand(rank);
    while(1){
	if(status==1){
	//Producing action  
	  time = rand() % 10;
	  usleep(time * 1000000);
    	  for (i=0; i<4; i++){
      	    dest = nbrs[i];
	    printf("[%d]Going to send my msg to %d\n",rank,nbrs[i]);
      	    MPI_Send(&status, 1, MPI_INT, dest, tag, 
                    MPI_COMM_WORLD);
    	  }
	  //printf("All messages were sent!\n");
	  status=2;
	  printf("Adding my point [%d,%d] to the list of protected\n",coords[0],coords[1]);
	  protex[0]=coords[0];
	  protey[0]=coords[1];
	  break; 
	}
	else if(status==-1){
	//Contagious actions
	  //printf("I am a bad guy!\n"); 
	  time = rand() % 10;
	  usleep(time * 1000000);
    	  for (i=0; i<4; i++){
      	    dest = nbrs[i];
	    printf("[%d]Going to send my msg to %d\n",rank,nbrs[i]);
      	    MPI_Send(&status, 1, MPI_INT, dest, tag, 
                    MPI_COMM_WORLD);
    	  }
	  //printf("All messages were sent!\n");
	  status=-2;
	  printf("Adding my point [%d,%d] to the list of dead\n",coords[0],coords[1]);
	  deadx[0]=coords[0];
	  deady[0]=coords[1];
	  break; 
	}
	else{
	//Standart actions
	  int newStatus=0;	
	  MPI_Recv(&newStatus,1,MPI_INT,MPI_ANY_SOURCE,tag,MPI_COMM_WORLD,MPI_STATUS_IGNORE);  
	  printf("I am process with id: %d and my new status is %d\n",rank,newStatus);
	  status=newStatus;
	}
    }
    MPI_Barrier(MPI_COMM_WORLD);
    int resultx[size],resulty[size],deadresx[size],deadresy[size];
    MPI_Gather(&protex,1,MPI_INT,resultx,1,MPI_INT,0,MPI_COMM_WORLD);
    MPI_Gather(&protey,1,MPI_INT,resulty,1,MPI_INT,0,MPI_COMM_WORLD);
    MPI_Gather(&deadx,1,MPI_INT,deadresx,1,MPI_INT,0,MPI_COMM_WORLD);
    MPI_Gather(&deady,1,MPI_INT,deadresy,1,MPI_INT,0,MPI_COMM_WORLD);
    if(rank==0){
      printf("Protected:");
      j=0;
      for(i=0;i<size;i++){
	if(resultx[i]!=-100&&resulty[i]!=-100){
    	  printf(" [%d,%d]",resultx[i],resulty[i]);
	  j++;
    	}
      }
      printf("\nNumbers of protected cities: %d\n",j);    		
      printf("Dead:");
      j=0; 
      for(i=0;i<size;i++){
    	if(deadresx[i]!=-100&&deadresy[i]!=-100){
          printf(" [%d,%d]",deadresx[i],deadresy[i]);
          j++;
	}
      }    		 
      printf("\nNumbers of dead cities: %d\n",j);    		
    }
  }
  else{
    printf("Must specify %d processors. Terminating.\n",size);
  }
  MPI_Finalize();
}
