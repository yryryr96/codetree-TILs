package eclipse;

import java.util.*;
import java.io.*;

public class Main {
	
	static int L,N,Q;
	static int[][] board;
	static int[] dy = {-1,0,1,0};
	static int[] dx = {0,1,0,-1};
	static class Knight {
		int id,r,c,h,w,hp;
		boolean isOut;
		
		Knight(int id, int r, int c, int h, int w, int hp) {
			this.id = id;
			this.r = r;
			this.c = c;
			this.h = h;
			this.w = w;
			this.hp = hp;
			this.isOut = false;
		}
	}
	static Knight[] knights;
	static int[] originalHP;
	
	public static void main(String[] args) throws IOException {
		
		System.setIn(new FileInputStream("src/eclipse/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		L = stoi(st.nextToken());
		N = stoi(st.nextToken());
		Q = stoi(st.nextToken());
		
		board = new int[L][L];
		for(int i = 0; i < L; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < L; j++) {
				board[i][j] = stoi(st.nextToken());
			}
		}
		
		knights = new Knight[N+1];
		originalHP = new int[N+1];
		for(int i = 1; i <= N; i++) {
			//r,c,h,w,k
			st = new StringTokenizer(br.readLine());
			int r = stoi(st.nextToken());
			int c = stoi(st.nextToken());
			int h = stoi(st.nextToken());
			int w = stoi(st.nextToken());
			int k = stoi(st.nextToken());
			
			knights[i] = new Knight(i,r-1,c-1,h,w,k);
			originalHP[i] = k;
		}
		
		for(int i = 1; i <= Q; i++) {
			
			st = new StringTokenizer(br.readLine());
			int id = stoi(st.nextToken());
			int d = stoi(st.nextToken()); 
			order(id,d);
		}
		
		int answer = 0;
		for(int i = 1; i <= N; i++) {
			if (knights[i].isOut) continue;
			answer += originalHP[i] - knights[i].hp;
		}
		
		System.out.println(answer);
	}
	
	static void order(int id, int d) {
		
		Knight k = knights[id];
		if (k.isOut) {
			return;
		}
		
		Queue<Knight> q = new LinkedList<>();
		boolean[] visited = new boolean[N+1];
		int[] damage = new int[N+1];
		
		q.add(k);
		visited[id] = true;
		
		while(!q.isEmpty()) {
			
			Knight cur = q.poll();

			int nr = cur.r + dy[d];
			int nc = cur.c + dx[d];
			
			for (int i = nr; i < nr + cur.h; i++) {
				for (int j = nc; j < nc + cur.w; j++) {
					if (!inRange(i,j) || board[i][j] == 2) {
//						System.out.println("cur.r = " + cur.r + " cur.c = " + cur.c + " i = " + i + " j = " + j);
						return;
					}
					else if(board[i][j] == 1) {
						damage[cur.id]++;
					}
				}
			}
			
			for (int i = 1; i <= N; i++) {
				if (visited[i] || knights[i].isOut) continue;
				Knight nk = knights[i];
	
				if (nr + cur.h <= nk.r || nk.r + nk.h <= nr || nc + cur.w <= nk.c || nk.c + nk.w <= nc) continue;
				q.add(nk);
				visited[i] = true; 
			}
		}

		damage[id] = 0;
		for (int i = 1; i<=N; i++) {
			if(!visited[i] || knights[i].isOut) continue;
			Knight cur = knights[i];
			
			if (cur.hp - damage[i] <= 0) {
				cur.isOut = true;
			} else {
				cur.r += dy[d];
				cur.c += dx[d];
				cur.hp -= damage[i];
			}
		}
	
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < L && 0 <= x && x < L;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}