import java.util.*;
import java.io.*;

public class Main {
	
	static int n,m,h,k;
	static int[][] map;
	static boolean[][] tree;
	static int[] dy = {-1,0,1,0};
	static int[] dx = {0,1,0,-1};
	static class Pair {
		int y,x,d;
		boolean isOut;
		Pair (int y, int x, int d) {
			this.y = y;
			this.x = x;
			this.d = d;
			isOut = false;
		}
		
		Pair(int y, int x) {
			this.y = y;
			this.x = x;
		}
	}
	static List<Pair> runners = new ArrayList<>();
	static Pair master;
	static Pair[][] way, reverseWay;
	static Pair[][] curWay;
	static int ans = 0;
	static int outCnt = 0;
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		n = stoi(st.nextToken());
		m = stoi(st.nextToken());
		h = stoi(st.nextToken());
		k = stoi(st.nextToken());
		map = new int[n][n];
		tree = new boolean[n][n]; 
		way = new Pair[n][n];
		reverseWay = new Pair[n][n];
		
		for (int i = 0; i < m; i++) {
			st = new StringTokenizer(br.readLine());
			int y = stoi(st.nextToken());
			int x = stoi(st.nextToken());
			int d = stoi(st.nextToken());
			
			runners.add(new Pair(y-1, x-1, d));
		}
		
		for (int i = 0; i < h; i++) {
			st = new StringTokenizer(br.readLine());
			int y = stoi(st.nextToken());
			int x = stoi(st.nextToken());
			tree[y-1][x-1] = true;
		}
		
		master = new Pair(n/2, n/2, 0);
		
		// 경로 초기화
		initWay();
		initReverseWay();
		curWay = way;

		for (int i = 1; i <= k; i++) {

			simulate(i);
			if (outCnt == m) break;
		}
		
		System.out.println(ans);
	}
	
	static void simulate(int time) {
		
		// 도망자 움직임
		runnerMove();
		
		// 술래 이동
		masterMove(time);
//		System.out.println(" mas.y = " + master.y + " mas.x = " + master.x + " mas.d = " + master.d);
	}
	
	// 술래 이동
	static void masterMove(int time) {
				
		Pair next = curWay[master.y][master.x];
		
		if (next.y == 0 && next.x == 0) curWay = reverseWay;
		else if (next.y == n/2 && next.x == n/2) curWay = way;
		
		master.y = next.y;
		master.x = next.x;
		int d = getDirection(curWay);
		master.d = d;
		
		List<Pair> canSee = new ArrayList<>();
		if (!tree[master.y][master.x]) canSee.add(new Pair(master.y, master.x));
		
		for (int i = 1; i < 3; i++) {
			int ny = master.y + dy[master.d]*i;
			int nx = master.x + dx[master.d]*i;
			if (!inRange(ny,nx) || tree[ny][nx]) continue;
			canSee.add(new Pair(ny,nx));
		}
		
		int cnt = 0;
		// 범인 잡기
		for (Pair runner : runners) {
			if(runner.isOut) continue;
			for (Pair see : canSee) {
				if (see.y == runner.y && see.x == runner.x) {
					runner.isOut = true;
					cnt++;
					outCnt++;
				}
			}
		}
		
		ans += cnt*time;
	}
	
	// 도망자 이동
	static void runnerMove() {
		
		for (Pair runner : runners) {
			
			if (runner.isOut) continue;
			if (getDistance(runner.y, runner.x, master.y, master.x) > 3) continue;
			
			int ny = runner.y + dy[runner.d];
			int nx = runner.x + dx[runner.d];
			
			// 격자 내
			if (inRange(ny, nx)) {
				  
				if (ny == master.y && nx == master.x) continue;
				else runner.y = ny; runner.x = nx;
			} 
			// 격자 외
			else {
				
				runner.d = (runner.d + 2) % 4;
				ny = runner.y + dy[runner.d];
				nx = runner.x + dx[runner.d];
				if (ny != master.y && nx != master.x) runner.y = ny; runner.x = nx;
			}
		}
	}
	
	static int getDirection(Pair[][] curWay) {
		
		Pair next = curWay[master.y][master.x];
		int ddy = next.y - master.y;
		int ddx = next.x - master.x;
//		System.out.println(" mas.y = " + master.y + " mas.x = " + master.x + " mas.d = " + master.d);
//		System.out.println("next = " + next.y + " " + next.x);
		int d = -1;
		for (int i = 0; i < 4; i++) {
			if (ddy == dy[i] && ddx == dx[i]) {
				d = i;
			}
		}
		
		return d;
	}
	
	static void initWay() {
		
		Queue<Pair> q = new LinkedList<>();
		Pair start = new Pair(-1,0,2);
		q.add(start);
		
		while (!q.isEmpty()) {
			
			Pair now = q.poll();
			int ny = now.y + dy[now.d];
			int nx = now.x + dx[now.d];
			
			if (now.y == n/2 && now.x == n/2) break;
			if (!inRange(ny,nx) || way[ny][nx] != null) {
				now.d = (now.d + 3) % 4;
				ny = now.y + dy[now.d];
				nx = now.x + dx[now.d];
			}
			
			way[ny][nx] = new Pair(now.y, now.x, now.d);
			q.add(new Pair(ny, nx, now.d));
		}
	}
	
	static void initReverseWay() {
		
		Queue<Pair> q = new LinkedList<>();
		Pair start = new Pair(n/2,n/2,0);
		q.add(start);
		reverseWay[n/2][n/2] = new Pair(n/2 - 1, n/2, 0);
		
		while (!q.isEmpty()) {
			
			Pair now = q.poll();
			if (now.y == 0 && now.x == 0) break; 
			Pair next = way[now.y][now.x];
//			System.out.println("next.y = " + next.y + " next.x = " + next.x);
			reverseWay[next.y][next.x] = new Pair(now.y, now.x, now.d);
			q.add(new Pair(next.y, next.x, next.d));
		}
	}
	
	static int getDistance(int y1, int x1, int y2, int x2) {
		return (int) Math.abs(y1 - y2) + Math.abs(x1 - x2);
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= x && x < n && 0 <= y && y < n;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}