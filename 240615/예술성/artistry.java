import java.util.*;
import java.io.*;

public class Main {
	
	static int n;
	static int[][] board, groupInfo;
	static boolean[] visited;
	static List<int[]> candidate;
	static int[] dy = {0,1,0,-1};
	static int[] dx = {1,0,-1,0};
	static class Pair {
		int y,x;
		Pair(int y, int x) {
			this.y = y;
			this.x = x;
		}
	}
	static int groupCnt = 0;
	static int answer = 0;
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		n = stoi(br.readLine());
		
		board = new int[n][n];
		for(int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < n; j++) {
				board[i][j] = stoi(st.nextToken());
			}
		}
		
		for(int i = 0; i < 4; i++) {
			simulate();
		}
		System.out.println(answer);
//		printBoard(board);
	}
	
	static void simulate() {
		
		divideGroup();
		for(int i = 1; i < groupCnt; i++) {
			for(int j = i+1; j <= groupCnt; j++) {
				getScore(i,j);
			}
		}
		
		rotateSquare(0,0);
		rotateSquare(0,n/2 + 1);
		rotateSquare(n/2 + 1,0);
		rotateSquare(n/2 + 1,n/2 + 1);
		rotateCenter();
		
//		printBoard(board);
//		System.out.println(answer);
	}
	
	static void rotateCenter() {
		
		int[][] temp = new int[n][n];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				temp[i][j] = board[i][j];
			}
		}
		
		for(int k = 0; k < n; k++) {
			board[n/2][k] = temp[k][n/2];
			board[k][n/2] = temp[n/2][n-1-k];
		}
	}
	
	static void rotateSquare(int y, int x) {
		
		int[][] temp = new int[n/2][n/2];
		
		for(int i = 0; i < n/2; i++) {
			for(int j = 0; j < n/2; j++) {
				temp[i][j] = board[i + y][j + x];
			}
		}
		
		for(int i = 0; i < n/2; i++) {
			for(int j = 0; j < n/2; j++) {
				
				int ry = j;
				int rx = n/2 - 1 - i;
				board[ry + y][rx + x] = temp[i][j];
			}
		}
	}
	
	static void divideGroup() {
		
		groupInfo = new int[n][n];
		boolean[][] visit = new boolean[n][n];
		int group = 1;
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if (visit[i][j]) continue;
				
				int v = board[i][j];
				Queue<Pair> q = new LinkedList<>();
				groupInfo[i][j] = group;
				visit[i][j] = true;
				q.add(new Pair(i,j));
				
				while(!q.isEmpty()) {
					Pair cur = q.poll();
					for(int k = 0; k < 4; k++) {
						int ny = cur.y + dy[k];
						int nx = cur.x + dx[k];
						if (!inRange(ny, nx) || visit[ny][nx] || board[ny][nx] != v) continue;
						q.add(new Pair(ny,nx));
						visit[ny][nx] = true;
						groupInfo[ny][nx] = group;
					}
				}
				
				group++;
			}
		}
		
		groupCnt = group - 1;
//		printBoard(groupInfo);
	}
	
	static void getScore(int group1, int group2) {
		
		int score = 0;
		int group1Cnt = 0, group2Cnt = 0;
		int group1Value = 0, group2Value = 0;
		int side = 0;
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if (groupInfo[i][j] == group1) {
					group1Cnt++;
					group1Value = board[i][j];
					for(int k = 0; k < 4; k++) {
						int ny = i + dy[k];
						int nx = j + dx[k];
						if(inRange(ny,nx) && groupInfo[ny][nx] == group2) {
							side++;
						}
					}
				}
				else if (groupInfo[i][j] == group2) {
					group2Cnt++;
					group2Value = board[i][j];
				}
			}
		}
		
		score = (group1Cnt + group2Cnt) * group1Value * group2Value * side;
		answer += score;
	}
	
	static void printBoard(int[][] b) {
		System.out.println("=====================");
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				System.out.print(b[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < n && 0 <= x && x < n;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}