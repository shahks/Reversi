import java.io.*;
import java.util.*;
class Node
{
		char [][] state = new char [8][8];
		int turn, depth, value;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		char player;
		boolean pass = false;
		Node next;
}
class agent
{
	static ArrayList<String> list = new ArrayList<String>();
	static int listcount = 0;
	static int task, cutoff;
	static char [][] current = new char [8] [8];
	static int [][] heuristic =  { {99,-8,8,6,6,8,-8,99},
				 				  {-8,-24,-4,-3,-3,-4,-24,-8},
				 				  {8,-4,7,4,4,7,-4,8},
				 					{6,-3,4,0,0,4,-3,6},
				 					{6,-3,4,0,0,4,-3,6},
				 					{8,-4,7,4,4,7,-4,8},
				 					{-8,-24,-4,-3,-3,-4,-24,-8},
				 					{99,-8,8,6,6,8,-8,99},
								  };
	static char	player, opponent;
	static Node [] tree;
	static int inf = Integer.MAX_VALUE;
	static int neginf = Integer.MIN_VALUE;

	public static void main (String [] args) throws IOException{
			readinputfile();								//to read input.txt

			tree = new Node [cutoff+1];
			for(int i=0;i<tree.length;i++)
				tree[i] = new Node();

			if(player == 'X')
				opponent ='O';
			else
				opponent = 'X';

			Node root = new Node();							//values of root node
			root.state = current;
			root.depth = 0;
			root.value = neginf;
			root.player = player;
			root.turn = 1;
			tree[0] = root;

			switch(task){
				case 1: Greedy(root.player,root.state);break;
				case 2: MinMax_Decision();break;
				case 3: AlphaBeta_Search(root);
			}
	}

	static void Greedy(char player,char [] [] state)throws IOException{
			FileWriter fw = new FileWriter("output.txt");
			PrintWriter out = new PrintWriter(fw);
			int heuristic_value = Integer.MIN_VALUE;
			int final_heuristic_value = Integer.MIN_VALUE;
			char [][] next_state = new char [8][8];
			char [][] prev_state = new char [8][8];
			char [][] final_state = new char [8][8];
			int value;
			char  opponent;
			int x,y;

			for(int i = 0; i < 8; i++){
					for(int j = 0; j < 8; j++){
							if(state[i][j] == '*'){
									if(islegal(i,j,player,state)){
											next_state = changeboard(i,j,player,state);
											heuristic_value = calc_heuristic_value(player,next_state);
											if (heuristic_value > final_heuristic_value){
													final_heuristic_value = heuristic_value;
													for(int a=0;a<8;a++)
															for(int b=0;b<8;b++)
																	final_state[a][b] = next_state[a][b];
											}
									}
							}
					}
			}
			for(int a = 0; a < 8; a++){
					for(int b = 0; b < 8; b++)
							out.print(final_state[a][b]);
					out.println();
			}
			out.close();
			fw.close();
	}

	static void MinMax_Decision()throws IOException {
			FileWriter fw = new FileWriter("output.txt");
			PrintWriter out = new PrintWriter(fw);
			int value, final_value = Integer.MIN_VALUE;
			int	next_state_value = Integer.MAX_VALUE;
			char[][] next_state = new char [8][8];
			boolean childfound = false;
			String pqr, text;
			if (!endgame(tree[0])){
					text = ("root" + "," + tree[0].depth + "," + printval(final_value));
					list.add(text);
					for(int i=0;i<8;i++)
							for(int j=0;j<8;j++){
									if(islegal(i,j,tree[0].player,tree[0].state)){
											childfound = true;
											tree[1].state = changeboard(i,j,tree[0].player,tree[0].state);
											tree[1].depth = tree[0].depth + 1;
											value = Minimax_Value(tree[1],i,j);
											if(value > final_value){
													final_value = value;
													for(int x=0;x<8;x++)
															for(int y=0;y<8;y++)
																	next_state[x][y] = (tree[1].state)[x][y];
											}
											text = ("root" + "," + tree[0].depth + "," + printval(final_value));
											list.add(text);
									}
							}
					if (!childfound){
							final_value = Integer.MAX_VALUE;
							tree[1].depth = tree[0].depth + 1;
							for(int x=0;x<8;x++)
									for(int y=0;y<8;y++)
											tree[1].state[x][y] = (tree[0].state)[x][y];
							tree[1].pass = true;
							value = Minimax_Value(tree[1],0,0);
							final_value = value;
							for(int x=0;x<8;x++)
									for(int y=0;y<8;y++)
											next_state[x][y] = tree[0].state[x][y];
							text = ("root" + "," + tree[0].depth + "," + printval(final_value));
							list.add(text);
					}
			}
			else{
					final_value = calc_heuristic_value(player,tree[0].state);
					text = ("root" + "," + tree[0].depth + "," + printval(final_value));
					list.add(text);
					for(int x=0;x<8;x++)
							for(int y=0;y<8;y++)
									next_state[x][y] = tree[0].state[x][y];
			}
			for(int a=0;a<8;a++){
					for(int b =0;b<8;b++)
							out.print(next_state[a][b]);
					out.println();
			}
			out.println("Node,Depth,Value");
			for(int m = 0; m < list.size() - 1; m++)
					out.println(list.get(m));
			out.print(list.get(list.size()-1));
			out.close();
			fw.close();
	}

	static int Minimax_Value(Node now,int x,int y) throws IOException{
			String text;
			if(!endgame(now)){
					if (now.depth == cutoff){
							int value = calc_heuristic_value(player,now.state);
							String str =((char)(y+97) + "" + (x+1));
							text = (str + "," + now.depth + "," + printval(value));
							list.add(text);
							return value;
					}
					else{
							if((now.depth)%2 == 1){
									int min_value = inf;
									boolean haschild = false;
									String str;
									if (now.pass)
											str = "pass";
									else
											str =((char)(y+97) + "" + (x+1));
									text = (str + "," + now.depth + "," + printval(min_value));
									list.add(text);
									for(int i=0;i<8;i++)
											for(int j=0;j<8;j++){
													if (islegal(i, j, opponent , now.state)){
															haschild = true;
															Node newer = new Node();
															newer.state = changeboard(i,j,'O',now.state);
															newer.depth = now.depth +1;
															int value = Minimax_Value(newer, i ,j);
															if(value < min_value)
																	min_value = value;
															if (now.pass)
																	str = "pass";
															else
																	str =((char)(y+97) + "" + (x+1));
															text = (str + "," + now.depth + "," + printval(min_value));
															list.add(text);
													}
											}
									if(!haschild){
											Node newer = new Node();
											newer.depth = now.depth + 1;
											for(int a=0;a<8;a++)
													for(int b=0;b<8;b++)
															newer.state[a][b] = (now.state)[a][b];
											newer.pass = true;
											if(now.pass == true && newer.pass == true){
													int value = calc_heuristic_value(player,now.state);
													text = ("pass" + "," + newer.depth + "," + printval(value));
													list.add(text);
													min_value = value;
											}
											else
													min_value = Minimax_Value(newer,0,0);
											if (now.pass)
													str = "pass";
											else
													str =((char)(y+97) + "" + (x+1));
											text = (str + "," + now.depth + "," + printval(min_value));
											list.add(text);
									}
									return min_value;
							}
							else{
									boolean haschild = false;
									int max_value = neginf;
									String str;
									if (now.pass)
											str = "pass";
									else
											str =((char)(y+97) + "" + (x+1));
									text = (str + "," + now.depth + "," + printval(max_value));
									list.add(text);
									for(int i=0;i<8;i++)
											for(int j=0;j<8;j++){
													if (islegal(i, j, player, now.state)){
															haschild = true;
															Node newer = new Node();
															newer.state = changeboard(i,j,'X',now.state);
															newer.depth = now.depth +1;
															int value = Minimax_Value(newer,i,j);
															if(value > max_value)
																max_value = value;
															if (now.pass)
																	str = "pass";
															else
																	str =((char)(y+97) + "" + (x+1));
															text = (str + "," + now.depth + "," + printval(max_value));
															list.add(text);
													}
											}
									if(!haschild){
											Node newer = new Node();
											newer.depth = now.depth + 1;
											for(int a=0;a<8;a++)
													for(int b=0;b<8;b++)
															newer.state[a][b] = (now.state)[a][b];
											newer.pass = true;
											if(now.pass == true && newer.pass == true){
													int value = calc_heuristic_value(player,now.state);
													text = ("pass" + "," + newer.depth + "," + printval(value));
													list.add(text);
													max_value = value;
											}
											else
													max_value = Minimax_Value(newer,0,0);
											if (now.pass)
													str = "pass";
											else
													str =((char)(y+97) + "" + (x+1));
											text = (str + "," + now.depth + "," + printval(max_value));
											list.add(text);
									}
									return max_value;
							}
					}
			}
			else{
					int value = calc_heuristic_value(player,now.state);
					String str =((char)(y+97) + "" + (x+1));
					text = (str + "," + now.depth + "," + printval(value));
					list.add(text);
					return value;
			}
	}

	static void AlphaBeta_Search(Node root)throws IOException
	{
			String text;
			FileWriter fw = new FileWriter("output.txt");
			PrintWriter out = new PrintWriter(fw);
			Node child = new Node();
			boolean childfound = false;
			int value = Integer.MIN_VALUE, final_value = Integer.MIN_VALUE;
			char [][] final_state = new char [8][8];
			if (!endgame(tree[0])){
					text = ("root" + "," + root.depth + "," + printval(final_value) + "," + printval(root.alpha) + "," + printval(root.beta));
					list.add(text);
					for(int i=0;i<8;i++)
							for(int j=0;j<8;j++){
									if(islegal(i,j,root.player,root.state)){
											childfound = true;
											child.state = changeboard(i,j,root.player,root.state);
											child.depth = root. depth +1;
											child.alpha = root.alpha;
											child.beta = root.beta;
											value = Min_Value(child,i,j);
											if(value > final_value){
													final_value = value;
													root.alpha = final_value;
													for(int x=0;x<8;x++)
															for(int y=0;y<8;y++)
																	final_state[x][y] = child.state[x][y];
											}
											text = ("root" + "," + root.depth + "," + printval(final_value) + "," + printval(root.alpha) + "," + printval(root.beta));
											list.add(text);
									}
							}
					if (!childfound){
							final_value = Integer.MAX_VALUE;
							child.depth = root.depth + 1;
							child.alpha = root.alpha;
							child.beta = root.beta;
							for(int x=0;x<8;x++)
									for(int y=0;y<8;y++)
											child.state[x][y] = root.state[x][y];
							child.pass = true;
							value = Min_Value(child,0,0);
							final_value = value;
							if(final_value > root.alpha)
									root.alpha = final_value;
							for(int x=0;x<8;x++)
									for(int y=0;y<8;y++)
											final_state[x][y] = root.state[x][y];
							text = ("root" + "," + root.depth + "," + printval(final_value) + "," + printval(root.alpha) + "," + printval(root.beta));
							list.add(text);
					}
			}
			else{
					final_value = calc_heuristic_value(player,root.state);
					for(int x=0;x<8;x++)
							for(int y=0;y<8;y++)
									final_state[x][y] = root.state[x][y];
					text = ("root" + "," + root.depth + "," + printval(final_value) + "," + printval(root.alpha) + "," + printval(root.beta));
					list.add(text);
			}
			for(int a=0;a<8;a++){
					for(int b =0;b<8;b++)
							out.print(final_state[a][b]);
					out.println();
			}
			out.println("Node,Depth,Value,Alpha,Beta");
			for(int m = 0; m < list.size()-1;m++)
					out.println(list.get(m));
			out.print(list.get(list.size()-1));
			out.close();
			fw.close();
	}

	static int Max_Value(Node now,int x,int y)throws IOException {
			String text;
			if(!endgame(now)){
					if(now.depth == cutoff){
							int value = calc_heuristic_value(player,now.state);
							String str =((char)(y+97) + "" + (x+1));
							text = (str + "," + now.depth + "," + printval(value) + "," + printval(now.alpha) + "," + printval(now.beta));
							list.add(text);
							return value;
					}
					else{
							int max_value = Integer.MIN_VALUE;
							boolean haschild = false;
							String str;
							if (now.pass)
									str = "pass";
							else
									str =((char)(y+97) + "" + (x+1));
							text = (str + "," + now.depth + "," + printval(max_value) + "," + printval(now.alpha) + "," + printval(now.beta));
							list.add(text);
							for(int i=0;i<8;i++)
									for(int j=0;j<8;j++){
											if(islegal(i,j,player,now.state)){
													haschild = true;
													Node newer = new Node();
													newer.state = changeboard(i,j,player,now.state);
													newer.depth = now.depth + 1;
													newer.alpha = now.alpha;
													newer.beta = now.beta;
													int value = Min_Value(newer, i ,j);
													if(value>max_value)
															max_value = value;
													if(max_value >= now.beta){
															max_value = value;
															text = (str + "," + now.depth + "," + printval(max_value) + "," + printval(now.alpha) + "," + printval(now.beta));
															list.add(text);
															return value;
													}
													else{
															if(max_value > now.alpha)
																	now.alpha = max_value;
													}
													if (now.pass)
															str = "pass";
													else
															str =((char)(y+97) + "" + (x+1));
													text = (str + "," + now.depth + "," + printval(max_value) + "," + printval(now.alpha) + "," + printval(now.beta));
													list.add(text);
											}
									}
							if(!haschild){
									Node newer = new Node();
									newer.depth = now.depth + 1;
									newer.alpha = now.alpha;
									newer.beta = now.beta;
									for(int a=0;a<8;a++)
											for(int b=0;b<8;b++)
													newer.state[a][b] = now.state[a][b];
									newer.pass = true;
									if(now.pass == true && newer.pass == true){
											int value = calc_heuristic_value(player,	now.state);
											text = ("pass" + "," + newer.depth + "," + printval(value) + "," + printval(newer.alpha) + "," + printval(newer.beta) );
											list.add(text);
											max_value = value;
									}
									else
											max_value = Min_Value(newer,0,0);
									if (now.pass)
											str = "pass";
									else
											str =((char)(y+97) + "" + (x+1));
									if(max_value >= now.beta){
											text = (str + "," + now.depth + "," + printval(max_value) + "," + printval(now.alpha) + "," + printval(now.beta));
											list.add(text);
											return max_value;
									}
									else{
											if(max_value > now.alpha)
													now.alpha = max_value;
									}
									text = (str + "," + now.depth + "," + printval(max_value) + "," + printval(now.alpha) + "," + printval(now.beta));
									list.add(text);
								}
								return max_value;
					}
			}
			else{
					int value = calc_heuristic_value(player,now.state);
					String str =((char)(y+97) + "" + (x+1));
					text = (str + "," + now.depth + "," + printval(value) + "," + printval(now.alpha) + "," + printval(now.beta));
					list.add(text);
					return value;
			}
	}

	static int Min_Value(Node now, int x, int y) throws IOException {
			String valinf,alpinf,betinf,text;
			if(!endgame(now)){
					if(now.depth == cutoff){
							int value = calc_heuristic_value(player,now.state);
							String str =((char)(y+97) + "" + (x+1));
							text = (str + "," + now.depth + "," + printval(value) + "," + printval(now.alpha) + "," + printval(now.beta));
							list.add(text);
							return value;
					}
					else{
							int min_value = Integer.MAX_VALUE;
							boolean haschild = false;
							String str;
							if (now.pass)
									str = "pass";
							else
									str =((char)(y+97) + "" + (x+1));
							text = (str + "," + now.depth + "," + printval(min_value) + "," + printval(now.alpha) + "," + printval(now.beta));
							list.add(text);
							for(int i=0;i<8;i++)
									for(int j=0;j<8;j++){
											if(islegal(i,j,opponent,now.state)){
													haschild = true;
													Node newer = new Node();
													newer.state = changeboard(i,j,opponent,now.state);
													newer.depth = now.depth + 1;
													newer.alpha = now.alpha;
													newer.beta = now.beta;
													int value = Max_Value(newer, i ,j);
													if(value < min_value)
															min_value = value;
													if(min_value <= now.alpha){
															min_value = value;
															text = (str + "," + now.depth + "," + printval(min_value) + "," + printval(now.alpha) + "," + printval(now.beta));
															list.add(text);
															return value;
													}
													else{
															if(min_value < now.beta)
																	now.beta = min_value;
													}
													if (now.pass)
															str = "pass";
													else
															str =((char)(y+97) + "" + (x+1));
													text = (str + "," + now.depth + "," + printval(min_value) + "," + printval(now.alpha) + "," + printval(now.beta));
													list.add(text);
											}
									}
							if(!haschild){
									Node newer = new Node();
									newer.depth = now.depth + 1;
									newer.alpha = now.alpha;
									newer.beta = now.beta;
									for(int a=0;a<8;a++)
											for(int b=0;b<8;b++)
													newer.state[a][b] = now.state[a][b];
									newer.pass = true;
									if(now.pass == true && newer.pass == true){
											int value = calc_heuristic_value(player,now.state);
											text = ("pass" + "," + newer.depth + "," + printval(value) + "," + printval(newer.alpha) + "," + printval(newer.beta) );
											list.add(text);
											min_value = value;
									}
									else
											min_value = Max_Value(newer,0,0);
									if (now.pass)
											str = "pass";
									else
											str =((char)(y+97) + "" + (x+1));
									if(min_value <= now.alpha){
											text = (str + "," + now.depth + "," + printval(min_value) + "," + printval(now.alpha) + "," + printval(now.beta));
											list.add(text);
											return min_value;
									}
									else{
											if(min_value < now.beta)
													now.beta = min_value;
									}
									text = (str + "," + now.depth + "," + printval(min_value) + "," + printval(now.alpha) + "," + printval(now.beta));
									list.add(text);
							}
							return min_value;
					}
			}
			else{
					int value = calc_heuristic_value(player,now.state);
					String str =((char)(y+97) + "" + (x+1));
					text = (str + "," + now.depth + "," + printval(value) + "," + printval(now.alpha) + "," + printval(now.beta));
					list.add(text);
					return value;
			}
	}

	static String printval(int value){
			String ret_str;
			if(value == inf)
					ret_str = "Infinity";
			else if(value == neginf)
					ret_str = "-Infinity";
			else
					ret_str = "" + value;
			return ret_str;
	}

	static char getplayer(Node x){
			char p,opponent;
			if(player == 'X')
					opponent ='O';
			else
					opponent = 'X';

			if (x.depth%2 == 0)
					p = player;
			else
					p = opponent;
			return p;
	}

	static boolean endgame(Node now){
			int countp = 0;
			int counto = 0;
			for(int i=0;i<8;i++)
					for(int j=0;j<8;j++){
							if(now.state[i][j] == player)
									countp++;
							if(now.state[i][j] == opponent)
									counto++;
					}
			if (countp == 0 || counto == 0)
					return true;
			else
					return false;
	}

	static void readinputfile()throws IOException {
			String path = "./input.txt";
			FileInputStream fis = new FileInputStream(path);
			BufferedReader in = new BufferedReader(new InputStreamReader(fis));
			task = Integer.parseInt(in.readLine());
			String abc = in.readLine();
			player = abc.charAt(0);
			cutoff = Integer.parseInt(in.readLine());
			String line = null;
			for (int i = 0; i < 8; i++){
					line = in.readLine();
					for (int j = 0; j < 8; j++){
							current[i][j] = line.charAt(j);
						}
			}
			fis.close();
			in.close();
	}

	static char[][] changeboard(int i, int j, char player, char [][] state) throws IOException {
			char temp [][] = new char [8][8];
			for(int x = 0; x < 8; x++)
					for(int y = 0;y < 8; y++)
							temp[x][y] = state[x][y];
			int x,y;
			boolean hf = false, hb = false, vu = false, vd = false;
			boolean dd = false, du = false, adu = false, add = false;
			char opponent;
			if(player == 'X')
				opponent = 'O';
			else
				opponent = 'X';

			if((j+1)<8 && temp[i][j+1]==opponent){ //horizontal forward search
					x=j+1;
					while(x<=7){
							if(temp[i][x] == player){
								hf = true;
								break;
							}
							x++;
					}
					if (hf == true){
							for(int a = j; a <= x; a++)
									temp[i][a] = player;
					}
			}

			if((j-1)>=0 && temp[i][j-1]==opponent){ //horizontal backward search
					x=j-1;
					while(x >= 0){
							if(temp[i][x] == player){
									hb = true;
									break;
							}
							x--;
					}
					if (hb == true){
						for(int a=j;a>=x;a--)
							temp[i][a] = player;
					}
			}

			if((i-1)>=0 && temp[i-1][j]==opponent){ //vertical upward search
					x = i-1;
					while(x >= 0){
							if(temp[x][j] == player){
									vu = true;
									break;
							}
							x--;
					}
					if (vu == true){
						for(int a=x;a<=i;a++)
								temp[a][j] = player;
					}
			}

			if((i+1)<8 && temp[i+1][j]==opponent){ //vertical downward search
					x = i+1;
					while(x<=7){
						if(temp[x][j] == player){
								vd = true;
								break;
						}
						x++;
					}
					if (vd == true){
							for(int a=i;a<=x;a++)
									temp[a][j] = player;
					}
			}

			if(((i+1)<8 && (j+1)<8) && temp[i+1][j+1]==opponent){ //diagonal downward search
					x = i+1;
					y = j+1;
					while(x<=7 && y<=7){
							if(temp[x][y] == player){
									dd = true;
									break;
							}
							x++;
							y++;
					}
					if (dd == true){
							int b = j;
							for(int a=i;a<=x;a++,b++)
									temp[a][b] = player;
					}
			}

			if(((i-1)>=0 && (j-1)>=0) && temp[i-1][j-1]==opponent){ //diagonal upward search
					x=i-1;
					y=j-1;
					while(x>=0 && y>=0){
							if(temp[x][y] == player){
									du = true;
									break;
							}
							x--;
							y--;
					}
					if (du == true){
							for(int a=x,b=y;a<=i && b<=j;a++,b++)
									temp[a][b] = player;
					}
			}

			if(((i-1)>=0 && (j+1)<8) && temp[i-1][j+1]==opponent){ //anti-diagonal upward search
					x = i-1;
					y = j+1;
					while(x>=0 && y<=7){
							if(temp[x][y] == player){
									adu = true;
									break;
							}
							x--;
							y++;
					}
					if (adu == true){
							for(int a=i,b=j;a>=x && b<=y;a--,b++)
									temp[a][b] = player;
					}
			}

			if(((i+1)<8 && (j-1)>=0) && temp[i+1][j-1]==opponent){ //anti-diagonal downward search
					x=i+1;
					y=j-1;
					while(x<=7 && y>=0){
							if(temp[x][y] == player){
									add = true;
									break;
							}
							x++;
							y--;
					}
					if (add == true){
							for(int a=i,b=j;a<=x && b>=y;a++,b--)
									temp[a][b] = player;
					}
			}
			return temp;
	}

	static boolean islegal(int i, int j, char player, char [][] state) throws IOException
	{
			char opponent;
			int x,y;
			boolean legal_value = false;

			if(player == 'X')
				opponent = 'O';
			else
				opponent = 'X';

			if(state [i][j] == '*'){
					if(((j+1) < 8) && (state[i][j+1] == opponent)){ //horizontal forward search
							x = j+1;
							while( x <= 7){
									if(state[i][x] == player){
											legal_value = true;
											break;
									}
									if(state[i][x] == '*')
											break;
									x++;
							}
					}

					if(((j-1) >= 0) && (state[i][j-1] == opponent)){ //horizontal backward search
							x = j-1;
							while(x >= 0){
									if(state[i][x] == player){
											legal_value = true;
											break;
									}
									if(state[i][x] == '*')
											break;
									x--;
							}
					}

					if(((i-1) >= 0) && (state[i-1][j]==opponent)){ //vertical upward search
							x = i-1;
							while(x>=0){
									if(state[x][j] == player){
											legal_value = true;
											break;
									}
									if(state[x][j] == '*')
											break;
									x--;
							}
					}

					if((i+1)<8 && state[i+1][j]==opponent){ //vertical downward search
							x=i+1;
							while(x<=7){
									if(state[x][j] == player){
											legal_value = true;
											break;
									}
									if(state[x][j] == '*')
											break;
									x++;
							}
					}

					if(((i+1)<8 && (j+1)<8) && state[i+1][j+1]==opponent){ //diagonal downward search
							x=i+1;
							y=j+1;
							while(x<=7 && y<=7){
									if(state[x][y] == player){
											legal_value = true;
											break;
									}
									if(state[x][y] == '*')
											break;
									x++;
									y++;
							}
					}

					if(((i-1)>=0 && (j-1)>=0) && state[i-1][j-1]==opponent){ //diagonal upward search
							x=i-1;
							y=j-1;
							while(x>=0 && y>=0){
									if(state[x][y] == player){
											legal_value = true;
											break;
									}
									if(state[x][y] == '*')
											break;
									x--;
									y--;
							}
					}

					if(((i-1)>=0 && (j+1)<8) && state[i-1][j+1]==opponent){ //anti-diagonal upward search
							x=i-1;
							y=j+1;
							while(x>=0 && y<=7){
									if(state[x][y] == player){
											legal_value = true;
											break;
									}
									if(state[x][y] == '*')
											break;
									x--;
									y++;
							}
					}

					if(((i+1)<8 && (j-1)>=0) && state[i+1][j-1]==opponent){ //anti-diagonal downward search
							x=i+1;
							y=j-1;
							while(x<=7 && y>=0){
									if(state[x][y] == player){
											legal_value = true;
											break;
									}
									if(state[x][y] == '*')
										break;
									x++;
									y--;
							}
					}
			}
			return legal_value;
	}

	static int calc_heuristic_value(char player, char [] [] state){
			char opponent;
			if(player == 'X')
				opponent = 'O';
			else
				opponent = 'X';

			int sum_player = 0;
			int sum_opponent = 0;

			for(int a=0; a<8;a++){
					for(int b=0;b<8;b++){
							if(state[a][b] == player)
									sum_player += heuristic[a][b];
							if(state[a][b] == opponent)
									sum_opponent += heuristic[a][b];
					}
			}
			return (sum_player - sum_opponent);
		}
}
