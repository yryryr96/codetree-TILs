import java.util.*;
import java.io.*;

public class Main {
	
	static int[] dy = {0,1,0,-1};
	static int[] dx = {1,0,-1,0};
	static int n,m;
	static int[][] board;
	
	static class Pair {
		int y,x;
		Pair (int y, int x) {
			this.y = y;
			this.x = x;
		}
	}
	
	static class Dice {
		int u,f,r;
		int y,x;
		Dice(int y, int x, int u, int f, int r) {
			this.y = y;
			this.x = x;
			this.u = u;
			this.f = f;
			this.r = r;
		}
	}
	
	static int dir = 0;
	static int ans = 0;
	static boolean[][] visited;
	static Dice dice = new Dice(0,0,1,2,3);
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		n = stoi(st.nextToken());
		m = stoi(st.nextToken());
		board = new int[n][n];
		visited = new boolean[n][n];
		
		for (int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < n; j++) {
				board[i][j] = stoi(st.nextToken());
			}
		}
		
		while (m-- > 0) {
			//simulate();
			moveDice();
		}
		
		System.out.println(ans);
	}
	
	static void moveDice() {
		
		int y = dice.y;
		int x = dice.x;
		
		int ny = dice.y + dy[dir];
		int nx = dice.x + dx[dir];
		if (!inRange(ny,nx)) {
			dir = (dir+2) % 4;
			ny = dice.y + dy[dir];
			nx = dice.x + dx[dir];
		}
		
		dice.y = ny;
		dice.x = nx;
		
		// 점수 구하기
		initVisited();
		bfs(dice.y, dice.x);
		
		// 방향 정하기
		if (dir == 0) {
			dice = new Dice(dice.y, dice.x, 7-dice.r, dice.f, dice.u);
		} 
		else if (dir == 1) {
			dice = new Dice(dice.y, dice.x, 7-dice.f, dice.u, dice.r);
		}
		else if (dir == 2) {
			dice = new Dice(dice.y, dice.x, dice.r, dice.f, 7-dice.u);
		} 
		else {
			dice = new Dice(dice.y, dice.x, dice.f, 7-dice.u, dice.r);
		}
		
		int down = 7 - dice.u;
		if (down > board[dice.y][dice.x]) { dir = (dir + 1) % 4; }
		else if (down < board[dice.y][dice.x]) { dir = (dir + 3) % 4; }

	}
	
	static void bfs(int y, int x) {
		
		int v = board[y][x];
		int cnt = 0;
		
		Queue<Pair> q = new LinkedList<>();
		q.add(new Pair(y,x));
		visited[y][x] = true;
		
		while(!q.isEmpty()) {
			
			Pair now = q.poll();
			cnt++;
			
			for (int k = 0; k < 4; k++) {
				int ny = now.y + dy[k];
				int nx = now.x + dx[k];
				
				if (!inRange(ny,nx) || visited[ny][nx]) continue;
				if (board[ny][nx] == v) {
					visited[ny][nx] = true;
					q.add(new Pair(ny,nx));
				}
			}
		}
		
		int score = v*cnt;
		ans += score;
	}
	
	static void initVisited() {
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				visited[i][j] = false;
			}
		}
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < n && 0 <= x && x < n;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}

}