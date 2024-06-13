import java.util.*;
import java.io.*;

public class Main {
	
	static int n,m;
	static int arriveCnt = 0;
	static int[][] board;
	static int[] dy = {-1,0,0,1};
	static int[] dx = {0,-1,1,0};
	
	static class Pair {
		int y,x;
		boolean isArrive;
		Pair(int y, int x) {
			this.y = y;
			this.x = x;
			this.isArrive = false;
		}
	}
	
	static List<Pair> baseCamp = new ArrayList<>();
	static Pair[] people, conv;
	static boolean[][] cantGo;
	static List<Pair> cantGoList = new ArrayList<>();
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		n = stoi(st.nextToken());
		m = stoi(st.nextToken());
		
		board = new int[n][n];
		for(int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < n; j++) {
				board[i][j] = stoi(st.nextToken());
				if (board[i][j] == 1) {
					baseCamp.add(new Pair(i,j));
				}
			}
		}
		
		people = new Pair[m+1];
		conv = new Pair[m+1];
		cantGo = new boolean[n][n];
		
		for(int i = 1; i <= m; i++) {
			st = new StringTokenizer(br.readLine());
			int r = stoi(st.nextToken()) - 1;
			int c = stoi(st.nextToken()) - 1;
			conv[i] = new Pair(r,c);
		}
		
		int time = 1;
		while (true) {
			simulate(time);
			
			if (arriveCnt == m) break;

			time++;
		}
		
		System.out.println(time);
	}
	
	static void simulate(int time) {
		

		move(time);
		
		if (time <= m) {
			Pair camp = findBaseCamp(conv[time]);
			cantGo[camp.y][camp.x] = true;
			people[time] = new Pair(camp.y, camp.x);
//			System.out.println(camp.y + " " + camp.x);
		}
		
		if (cantGoList.size() != 0) {
			for (Pair p : cantGoList) {
				cantGo[p.y][p.x] = true; 
			}
		}
	}
	
	static void move(int time) {
		
		if (time == 1) return;
		if (time > m+1) time = m+1;
		
		cantGoList.clear(); 
		for (int i = 1; i <= time - 1; i++) {
			Pair p = people[i];
			if (p.isArrive) continue;
			
			Queue<Pair> q = new LinkedList<>();
			boolean[][] visited = new boolean[n][n];
			
			visited[p.y][p.x] = true;
			q.add(p);
			
			Pair[][] trace = new Pair[n][n];
			while(!q.isEmpty()) {
				
				Pair cur = q.poll();
				if (cur.y == conv[i].y && cur.x == conv[i].x) {
					break;
				}
				
				for (int k = 0; k < 4; k++) {
					int ny = cur.y + dy[k];
					int nx = cur.x + dx[k];
					if (!inRange(ny,nx) || cantGo[ny][nx] || visited[ny][nx]) continue;
					q.add(new Pair(ny,nx));
					visited[ny][nx] = true;
					trace[ny][nx] = cur;
				}
			}

			q.clear();
			q.add(conv[i]);
			while(!q.isEmpty()) {
				
				Pair cur = q.poll();
				Pair next = trace[cur.y][cur.x];
//				System.out.println("time = " + time + " i = " + i + " " + cur.y + " " + cur.x);
//				System.out.println("next.y = " + next.y + " next.x = " + next.x);
				if (next.y == p.y && next.x == p.x) {
					p.y = cur.y;
					p.x = cur.x;
					
					if (p.y == conv[i].y && p.x == conv[i].x) {
						p.isArrive = true;
						cantGoList.add(conv[i]);
						arriveCnt++;
					}
					break;
				}
				
				q.add(next);
			}
		}
	}
	
	static Pair findBaseCamp(Pair con) {
		
		Queue<Pair> q = new LinkedList<>();
		boolean[][] visited = new boolean[n][n];
		
		q.add(con);
		visited[con.y][con.x] = true;
		
		while(!q.isEmpty()) {
			Pair cur = q.poll();
			
			if (board[cur.y][cur.x] == 1) {
				return new Pair(cur.y, cur.x);
			}
			
			for(int k = 0; k < 4; k++) {
				int ny = cur.y + dy[k];
				int nx = cur.x + dx[k];
				if (!inRange(ny,nx) || cantGo[ny][nx] || visited[ny][nx]) continue;
				
				q.add(new Pair(ny,nx));
				visited[ny][nx] = true;
			}
		}
		
		return null;
	}
	
	static void printPerson(int time) {
		
		if (time > m) time = m;
		System.out.println("============");
		for (int i = 1; i <= time; i++) {
			System.out.println("personInfo : " + people[i].y + " " + people[i].x);
		}
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < n && 0 <= x && x < n;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}