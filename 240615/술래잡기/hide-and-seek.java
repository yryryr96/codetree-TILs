import java.util.*;
import java.io.*;

public class Main {
	
	static int n,m,h,k;
	static int[][] board;
	static boolean[][] tree;
	static boolean[] isOut;
	static class Pair {
		int y, x, d;
		Pair(int y, int x, int d) {
			this.y = y;
			this.x = x;
			this.d = d;
		}
	}
	
	static Pair[] thieves;
	static int[] dy = {1, 0, -1, 0};
	static int[] dx = {0, 1, 0, -1};
	static Pair master;
	static Pair[][] currentWay, way, reverseWay;
	static int answer = 0;
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		n = stoi(st.nextToken());
		m = stoi(st.nextToken());
		h = stoi(st.nextToken());
		k = stoi(st.nextToken());
		
		board = new int[n][n];
		tree = new boolean[n][n];
		thieves = new Pair[m];
		isOut = new boolean[m];
		
		for(int i = 0; i < m; i++) {
			st = new StringTokenizer(br.readLine());
			int y = stoi(st.nextToken()) - 1;
			int x = stoi(st.nextToken()) - 1;
			int d = stoi(st.nextToken());
			
			if (d == 1) {
				thieves[i] = new Pair(y,x,1);
			} else {
				thieves[i] = new Pair(y,x,0);
			}
		}
		
		for(int i = 0; i < h; i++) {
			st = new StringTokenizer(br.readLine());
			int y = stoi(st.nextToken()) - 1;
			int x = stoi(st.nextToken()) - 1;
			tree[y][x] = true;
		}
		
		master = new Pair(n/2,n/2,2);
		initWay();
		for(int turn = 1; turn <= k; turn++) {
			simulate(turn);
		}
		
		System.out.println(answer);
	}
	
	static void simulate(int turn) {
		thiefMove();
		masterMove();
		kill(turn);
	}
	
	static void kill(int turn) {
		
		int cnt = 0;
		for(int l = 0; l < 3; l++) {
			int ny = master.y + dy[master.d]*l;
			int nx = master.x + dx[master.d]*l;
			if(!inRange(ny,nx) || tree[ny][nx]) continue;
			for(int id = 0; id < m; id++) {
				if(isOut[id]) continue;
				Pair t = thieves[id];
				if(t.y == ny && t.x == nx) {
					isOut[id] = true;
					cnt++;
				}
			}
		}
		
//		System.out.println(cnt);
		answer += turn*cnt;
	}
	
	static void masterMove() {
		
		Pair next = currentWay[master.y][master.x];
		master.y = next.y;
		master.x = next.x;
		
		if (master.y == 0 && master.x == 0) {
			master.d = 0;
			currentWay = reverseWay;
		} else if (master.y == n/2 && master.x == n/2) {
			master.d = 2;
			currentWay = way;
		} else {
			Pair nxt = currentWay[master.y][master.x];
			for(int k = 0; k < 4; k++) {
				int ny = master.y + dy[k];
				int nx = master.x + dx[k];
				if(!inRange(ny,nx)) continue;
				if(ny == nxt.y && nx == nxt.x) {
					master.d = k;
					break;
				}
			}
		}
	}
	
	static void thiefMove() {
		
		for(int id = 0; id < m; id++) {
			
			if (isOut[id]) continue;
			if (!checkThief(id)) continue;
			
			Pair t = thieves[id];
			
			int ny = t.y + dy[t.d];
			int nx = t.x + dx[t.d];
			if (!inRange(ny,nx)) {
				
				t.d = (t.d + 2) % 4;
				ny = t.y + dy[t.d];
				nx = t.x + dx[t.d];
				
				if (master.y == ny && master.x == nx) continue;
				t.y = ny;
				t.x = nx;
			} else {
				
				if (master.y == ny && master.x == nx) continue;
				boolean tmp = true;
				for(int i = 0; i<m; i++) {
					Pair p = thieves[i];
					if(p.y == ny && p.x == nx) {
						tmp = false;
						break;
					}
				}
				
				if(tmp) {
					t.y = ny;
					t.x = nx;
				}
			}
		}
	}
	
	static void initWay() {
		
		way = new Pair[n][n];
		reverseWay = new Pair[n][n];
		currentWay = way;
		
		boolean[][] visited = new boolean[n][n];
		Queue<Pair> q = new LinkedList<>();
		q.add(new Pair(0,0,0));
		visited[0][0] = true;
		
		while(!q.isEmpty()) {
			
			Pair cur = q.poll();
			
			if (cur.y == n/2 && cur.x == n/2) return;
			
			int ny = cur.y + dy[cur.d];
			int nx = cur.x + dx[cur.d];
			
			if(!inRange(ny,nx) || visited[ny][nx]) {
				int d = (cur.d + 1) % 4;
				cur.d = d;
				ny = cur.y + dy[d];
				nx = cur.x + dx[d];
			}
			
			Pair next = new Pair(ny,nx,cur.d);
			
			way[ny][nx] = cur;
			reverseWay[cur.y][cur.x] = next;  
			
			q.add(next);
			visited[ny][nx] = true;
		}
	}
	
	static boolean checkThief(int id) {
		Pair t = thieves[id];
		return Math.abs(master.y - t.y) + Math.abs(master.x - t.x) <= 3; 
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < n && 0 <= x && x < n;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}