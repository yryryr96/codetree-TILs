import java.util.*;
import java.io.*;

public class Main {
	
	static int[][] board = new int[5][5];
	static int K,M;
	static Queue<Integer> wallNumber = new LinkedList<>();
	static int[] dy = {0,1,0,-1};
	static int[] dx = {1,0,-1,0};
	
	public static void main(String[] args) throws IOException {
		
		// System.setIn(new FileInputStream("src/eclipse/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		StringTokenizer st = new StringTokenizer(br.readLine());
		K = stoi(st.nextToken());
		M = stoi(st.nextToken());
		
		// board 초기화
		for(int i = 0; i < 5; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < 5; j++) {
				board[i][j] = stoi(st.nextToken());
			}
		}
		
		// 벽면 숫자 초기화
		st = new StringTokenizer(br.readLine());
		for(int i = 0; i < M; i++) {
			wallNumber.add(stoi(st.nextToken()));
		}
					
//		int[][] temp = getRotatedMap(1,1,1);
//		printBoard(temp);
//		System.out.println(getScore(temp));
//		board = temp;
//		fillBoard();
//		printBoard(temp);
		StringBuilder sb = new StringBuilder();
		while (K-- > 0) {
			
			int maxScore = 0;
			int[][] nextBoard = null;
			
			for(int cnt = 1; cnt <= 3; cnt++) {
				for(int j = 0; j <= 2; j++) {
					for(int i = 0; i <= 2; i++) {
						int[][] rotatedBoard = getRotatedMap(i,j,cnt);
						int score = getScore(rotatedBoard);
//						System.out.println("i = " + i + " j = " + j + " cnt = " + cnt);
//						printBoard(rotatedBoard);
						if(score > maxScore) {
							maxScore = score;
							nextBoard = rotatedBoard;
							
						}
					}
				}
			}
			
			if (nextBoard == null) {
				break;
			}
			
			board = nextBoard;
			while(true) {

				fillBoard();
				int score = getScore(board);
				if(score == 0) break;
				maxScore += score;
				
			}

			sb.append(maxScore + " ");
		}
		
		System.out.println(sb.toString());
	}
	
	private static int getScore(int[][] b) {
		
		int score = 0;
		boolean[][] visited = new boolean[5][5];
		
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				if (!visited[i][j]) {
					
					int v = b[i][j];
					Queue<int[]> q = new LinkedList<>();
					Queue<int[]> trace= new LinkedList<>();
					
					q.add(new int[] {i,j});
					trace.add(new int[] {i,j});
					visited[i][j] = true;
					
					while (!q.isEmpty()) {
						int[] cur = q.poll();
						
						for (int k = 0; k < 4; k++) {
							int ny = cur[0] + dy[k];
							int nx = cur[1] + dx[k];
							if (!inRange(ny,nx) || visited[ny][nx] || b[ny][nx] != v) continue;
							
							q.add(new int[] {ny,nx});
							trace.add(new int[] {ny,nx});
							visited[ny][nx] = true;
						}
					}
					
					if (trace.size() >= 3) {
						
						score += trace.size();
						while(!trace.isEmpty()) {
							int[] t = trace.poll();
							b[t[0]][t[1]] = 0;
						}
					}
					
				}
			}
		}
//		System.out.println("score = " + score);
		return score;
	}

	private static int[][] getRotatedMap(int y, int x, int cnt) {
		
		int[][] nBoard = new int[5][5];
		int[][] temp = new int[5][5];
		
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				nBoard[i][j] = board[i][j];
				temp[i][j] = board[i][j];
			}
		}
		
		for (int k = 1; k <= cnt; k++) {
			
			for (int i = y; i <= y + 2; i++) {
				for (int j = x; j <= x + 2; j++) {
					int oy = i - y;
					int ox = j - x;
					
					int ry = ox;
					int rx = 2 - oy;
					nBoard[ry+y][rx+x] = temp[i][j];
				}
			}

			for (int i = y; i <= y + 2; i++) {
				for (int j = x; j <= x + 2; j++) {
					temp[i][j] = nBoard[i][j];
				}
			}
		}
		
		return nBoard;
	}
	
	private static void fillBoard() {
		
		for(int j = 0; j < 5; j++) {
			for(int i = 4; i >= 0; i--) {
				if(board[i][j] == 0 && !wallNumber.isEmpty()) {
					board[i][j] = wallNumber.poll();
				}
			}
		}
	}
	
	private static void printBoard(int[][] b) {
		System.out.println("==============");
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j< 5; j++) {
				System.out.print(b[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("==============");
	}
	
	
	private static boolean inRange(int y, int x) {
		return 0 <= y && y < 5 && 0 <= x && x < 5;
	}
	
	private static int stoi(String s) {
		return Integer.parseInt(s);
	}
}