    import java.util.*;
	
	public class ai extends AIModule{
	private int ply; 
	private int myMove; 
	private int[] searchOrder = {3, 4, 2, 5, 1, 6, 0}; 
	private Board board = new Board(); 
	private hashTable table = new hashTable();
	public int[][] weights = 
				{{3,  4,  5,  5,  4,  3},
				{  4,  6,  8,  8,  6,  4},
				{  5,  8, 11, 11,  8,  5},
				{  6, 10, 14, 14, 10, 6},
				{  5,  8, 11, 11,  8,  5},
				{  4,  6,  8,  8,  6,  4},
				{  3,  4,  5,  5,  4,  3}};
	int[][] weights2 = 
				{{1, 1, 1, 2, 1, 1, 1},	
	             {1, 2, 2, 3, 2, 2, 1},
	             {1, 2, 3, 4, 3, 2, 1},
	             {1, 2, 3, 4, 3, 2, 1},
	             {1, 2, 2, 3, 2, 2, 1},		  				  		
	             {1, 1, 1, 2, 1, 1, 1}};
	
	private int miniMax(final GameStateModule mod, Board myBoard, int depth, int myPlayer, int col, int alpha, int beta, hashTable myTable)
	{
		int v = 0; 
		if(depth == ply || terminate)
		{
			return myBoard.EvaluationFunction(mod, col, myPlayer, myTable);
		}
		if(mod.getCoins() == 42){ //draw
			return 0;
		}
		depth++; 
		Board temp;
		switch(myPlayer){
			case 1: for(int index:searchOrder) {//max
						if(mod.canMakeMove(index)){
							temp = new Board(myBoard);
							int h = mod.getHeightAt(index);
							temp.pushMove(1, index,h, mod);
							mod.makeMove(index);
							v = miniMax(mod, temp, depth, 2, index, alpha, beta, myTable); 
							if (alpha <  v){
								alpha = v;
								//System.out.println("alpha " + alpha);
								if(depth == 1)
									myMove = index;
							}
							mod.unMakeMove();
							if(beta <= alpha)
								break;
						}
					}
					return alpha; 
					
			default:for(int index:searchOrder){//min
						if(mod.canMakeMove(index)){
							temp = new Board(myBoard);
							int h = mod.getHeightAt(index);
							temp.pushMove(2, index, h, mod);
							mod.makeMove(index);
							v = miniMax(mod, temp, depth, 1, index, alpha, beta, myTable);
							if(beta > v){
								beta = v;
								//System.out.println("beta " + beta);						
							}
							mod.unMakeMove();
							if(beta <= alpha)
								break;
						}
					}
					return beta; 
		}
	}
	public void getNextMove(final GameStateModule mod)
	{
		board.enemyTurn(mod); 
		ply =0; 
		while(!terminate){
			miniMax(mod, board, 0, 1, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, table);
			ply++;  
			if(!terminate){
				chosenMove = myMove; 
			}
		}
		//System.out.println(ply);
		//board.printBoard();
		board.pushMove(1, chosenMove,mod.getHeightAt(chosenMove), mod);
		//board.printBoard();
	}
	
	private class hashTable{
		private long[][][] data;
		private HashMap<Long, Integer> map;
		private HashMap<Long, Integer> map2;
		
		public hashTable(){
			map = new HashMap<Long, Integer>(800000);
			map2 = new HashMap<Long, Integer>(800000);
			Random r = new Random();
			this.data = new long[7][6][3];
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < 6; j++){
					for (int k = 0; k < 3; k++){
						this.data[i][j][k] = r.nextLong();
					}
				}
			}
		}
		
		public long getHash(int[][] b){
			long temp = 0;
			for(int i = 0; i < 7; i++){
				for (int j = 0; j < 6; j++)
				{
					temp ^= this.data[i][j][b[i][j]];
				}
			}
			return temp;
		}
		
		public void add(long l, int val, int player){
			switch (player){
				case 1: 	this.map.put(l, val);
				case 2: 	this.map2.put(l, val);		
			}
		}
	}
	
	private class Board{
		
		public int[][] data;
		public int[][] data2;
		public int[][] data3;
		private int[][] data4; 
		public Board()
		{
			data4 = new int[8][9]; 
			data = new int [7][6];
			data2 = new int [8][9];
			data3 = new int[8][9];
		}
		
		public Board(Board b)
		{
			data = new int [7][6];
			data2 = new int[8][9];
			data3 = new int[8][9];
			data4 = new int[8][9]; 
			for(int i = 0; i < 7; i++)
			{
				for(int j = 0; j < 6; j++)
				{
					this.data[i][j] = b.data[i][j];
				}
			}
			for(int i = 0; i < 8; i++)
			{
				for(int j = 0; j < 9; j++)
				{
					this.data4[i][j] = b.data4[i][j]; 
					this.data2[i][j] = b.data2[i][j];
					this.data3[i][j] = b.data3[i][j];
				}
			}
		}
		
		public void pushMove( int player, int col,int tempr, final GameStateModule mod)
		{
			//this.q.clear();
			int constant = -1; 
			this.data[col][tempr ] = player;
			col++;
			if(player == 1)
				constant = 1; 
			int row = tempr + 1;
			//System.out.println(row + "," + tempr);
			if(col > 7 || col < 1 || row > 6 || row < 1)
				return; 
			int temp2 = constant*data4[row-1][col]; 
			if(temp2 <= 0){
				data4[row][col] = constant;
			}				
			else{
				for(int i = 0; i <= temp2; i++){
					data4[row-i][col] = temp2*constant + constant; 		
				}		 
			}
			for (int dx = -1 ;dx < 2; dx++){
				for (int dy = -1; dy < 2; dy++){
					if (row + dx < 0 || row + dx > 7 || col + dy < 0 || col + dy > 8 )
						continue;
					if (constant == 1){
						int temp =  data2[row + dx][col + dy];
						if ( data2[row + dx][col + dy] <= data2[row][col]){
							data2[row][col]++;
						}
					}
					else if (constant == -1){
						int temp =  data3[row + dx][col + dy];
						if ( data3[row + dx][col + dy]  >= data3[row ][col]){
							data3[row][col]--;
						}
					}
				}
			}
		}	
		
		public int EvaluationFunction(final GameStateModule mod, int x, int ID, hashTable myTable) 
		{
			long tempKey = myTable.getHash(this.data);
			int c = -1;
			switch(ID){
				case 1: c = 1;
						if (myTable.map.containsKey(tempKey)){
							return myTable.map.get(tempKey);
						}	
				case 2: if (myTable.map2.containsKey(tempKey)){
							return myTable.map2.get(tempKey);
						}
			}	
			int score = 0; 
			int y = mod.getHeightAt(x) - 1;
			boolean won = false;
			if(y == -1)
				y = 0;
			int myWin = 50000*(42-mod.getCoins() + 1) * c;
			analyze:
			for (int dx = -1; dx < 2; dx++){
				for (int dy = -1; dy < 2; dy++){
					//current spot
					if (dx == 0 && dy == 0){
						continue;
					}
					if(x + dx < 0 || y + dy < 0 || x + dx >6 || y + dy > 5 || x < 0 || x > 6 || y < 0 || y > 5){
						continue;
					}
					for(int i = 1; i < 7; i++){
						for(int j = 1; j < 8; j++){
							score += (data2[i][j] + data3[i][j] + data4[i][j])*(weights2[i - 1][j - 1]);
						}
					}
					//check surrounding coins
					
					if(this.getAt(x + dx, y + dy) == ID){// 2 in a row sx
						score += 2 * c * weights[x][y];
						if(this.getAt(x - dx, y - dy) == ID){ //3 in a row xsx
							score += 3 * c * weights[x][y];
							if(this.getAt(x - 2 * dx, y - 2 * dy) == ID){ //4 in a row xxsx
								score = myWin ;
								won = true;
								break analyze;
								//System.out.println(score);
							}
							else if(this.getAt(x + 2 * dx, y + 2 * dy) == ID){ //4 in a row xsxx
								score = myWin;
								won = true;
								break analyze;
								//System.out.println(score);
							}
							else if (this.getAt(x - 2 * dx, y - 2 * dy) == 0 && this.getAt(x + 2 * dx, y + 2 * dy) == 0){//oxsxo double threat
								score += 50 * c * weights[x][y];
								//break analyze;
							}
						}
						if (this.getAt(x + 2 * dx, y + 2 * dy) == ID)//sxx
							score += 3 * c * weights[x][y];
							if (this.getAt(x + 3 * dx, y + 3 * dy) == ID){//sxxx
								score = myWin;
								won = true;
								break analyze;
							}
							else if(this.getAt(x + 3 * dx, y + 3 * dy) == 0 && this.getAt(x - dx, y - dy) == 0){//double threat osxxo
								score += 50 * c * weights[x][y];
								//break analyze;
							}
						
					} 
					else if(this.getAt(x - dx, y - dy) == ID){// 2 in a row 0sx
						score += 2 * c * weights[x][y];
						if(this.getAt(x - 2 * dx, y - 2 * dy) == ID){ //3 in a row osxx
							score += 3 * c * weights[x][y];
							if(this.getAt(x - 3 * dx, y - 3 * dy) == ID){//win osxxx
								score = myWin ;
								won = true;
								break analyze;
							}
							else if(this.getAt(x - 3 * dx, y - 3 * dy) == 0 && this.getAt(x + dx, y + dy) == 0){ //double threat osxxo
								score += 50 * c * weights[x][y];
							}
						}
					}
				}
			}
			//dont let enemy take control of rows 2-6
			if(!won && y > 0){
				String s = "";
				for(int i = 0; i < 7; i++){
					s += this.getAt(i, y);
				}
				switch(ID){
					case 1: if(s.contains("01110")){
								score += 500*(42-mod.getCoins() + 1);
								//System.out.println(s + " " + y);
							}
							if(s.contains("1101") || s.contains("1011"))
								score += 200*(42-mod.getCoins() + 1);
					case 2:	if(s.contains("02220")){
								score -= 500*(42-mod.getCoins() + 1);
								//System.out.println(s + " " + y);
							}
							if(s.contains("2202") || s.contains("2022"))
								score -= 200*(42-mod.getCoins() + 1);
				}		
			}
				
			//System.out.println(s);
			if(!won){
				score = score *(42-mod.getCoins() + 1) / 2;
			}
			//System.out.println(score);
			myTable.add(tempKey, score, ID);
			return score; 		
		}
		
		public int getAt(int x, int y){
			if (x < 0 || y < 0 || x >=7 || y >= 6)
				return -1;
			else
				return this.data[x][y];
		}
		
		public void enemyTurn(final GameStateModule mod)
		{
			for(int i=0; i<6; i++) {
	            for(int j=0; j<7; j++) {
	                if( mod.getAt(j,i) != 0 && data[j][i] == 0 ) 
	                {
	                    pushMove(2, j,i, mod);
	                    return; 
	                }
	            }
	        }
		}
		
		public void printBoard(){
			/*
			System.out.println("data4");
			for(int i = 7; i >=0 ; i--)
			{
				for(int j = 0; j < 9; j++)
				{
					System.out.print(data4[i][j]); 
					if (data4[i][j] < 0)
						System.out.print("  "); 
					else
						System.out.print("   ");  
				}
				System.out.println("");
			}
			
			System.out.println("data");
			for(int i=5; i>=0; i--) {
	            for(int j=0; j<7; j++) {
	                System.out.print(data[j][i]);
					System.out.print("  "); 
	            }
				System.out.println("");
	        }*/
			
			System.out.println("data3");
			for(int i = 7; i >=0 ; i--)
			{
				for(int j = 0; j < 9; j++)
				{
					System.out.print(data3[i][j] + data2[i][j] + data4[i][j]); 
					if (data3[i][j] < 0)
						System.out.print("  "); 
					else
						System.out.print("   ");  
				}
				System.out.println("");
			}
		
			System.out.println("-------------------------");
		}
		
		
		
	}	
}