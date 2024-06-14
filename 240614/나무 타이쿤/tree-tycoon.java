import java.util.*;
import java.io.*;

public class Main {
	
	static int n,m;
	static boolean[][] vitamin;
	static int[][] board;
	static int[] ddy = {0,-1,-1,-1,0,1,1,1};
	static int[] ddx = {1,1,0,-1,-1,-1,0,1};
	static int[] dy = {1,1,-1,-1};
	static int[] dx = {1,-1,1,-1};
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		n = stoi(st.nextToken());
		m = stoi(st.nextToken());
		
		board = new int[n][n];
		vitamin = new boolean[n][n];
		initEnergy();
		
		for(int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < n; j++) {
				board[i][j] = stoi(st.nextToken());
			}
		}
		
		while(m-- > 0) {
			st = new StringTokenizer(br.readLine());
			int d = stoi(st.nextToken()) - 1;
			int p = stoi(st.nextToken());
			simulate(d,p);
		}
		
		System.out.println(getAnswer());
	}
	
	static int getAnswer() {
		
		int answer = 0;
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(board[i][j] > 0) answer += board[i][j];
			}
		}
		return answer;
	}
	
	static void simulate(int d, int p) {
		
		boolean[][] visited = new boolean[n][n];
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if (!vitamin[i][j]) continue;
				vitamin[i][j] = false;
				int ny = (n + i + ddy[d]*p) % n;
				int nx = (n + j + ddx[d]*p) % n;
//				System.out.println("ny = " + ny + " nx = " + nx);
				board[ny][nx]++;
				visited[ny][nx] = true;
			}
		}
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if (!visited[i][j]) continue;
				int cnt = getTreeCount(i,j);
				board[i][j] += cnt;
			}
		}
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(visited[i][j]) continue;
				if(board[i][j] >= 2) {
					board[i][j] -= 2;
					vitamin[i][j] = true;
				}
			}
		}
	}
	
	static int getTreeCount(int y, int x) {
		
		int count = 0;
		for(int k = 0; k < 4; k++) {
			int ny = y + dy[k];
			int nx = x + dx[k];
			if (!inRange(ny,nx) || board[ny][nx] == 0) continue;
			if (board[ny][nx] >= 1) count++;
		}
		
		return count;
	}
	
	static void initEnergy() {
		vitamin[n-1][0] = true;
		vitamin[n-1][1] = true;
		vitamin[n-2][0] = true;
		vitamin[n-2][1] = true;
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < n && 0 <= x && x < n;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}