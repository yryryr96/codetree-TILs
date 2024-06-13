import java.util.*;
import java.io.*;

public class Main {
	
	/**
	 * N*N, (1,1)
	 * 벽 : 내구도, 회전할 때 내구도 -1, 내구도 0 = 빈칸
	 * 출구 : 참가자가 해당 칸에 도달하면 즉시 탈출
	 * 1초 마다 모든 참가자 한칸 씩 이동, 두 위치 거리 = |x1-x2| + |y1-y2|
	 */
	
	static int N,M,K;
	static int[][] map;
	static class Person {
		int y,x;
		Person (int y, int x) {
			this.y = y;
			this.x = x;
		}
	}
	static Person[] personInfo;
	static boolean[] isOut;
	static int[] dy = {0,0,1,-1};
	static int[] dx = {1,-1,0,0};
	static int ey, ex;
	static int ans = 0;
	static int size, sy, sx;
    
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = stoi(st.nextToken());
		M = stoi(st.nextToken());
		K = stoi(st.nextToken());
		
		map = new int[N][N];
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) {
				map[i][j] = stoi(st.nextToken());
			}
		}
		
		personInfo = new Person[M];
		isOut = new boolean[M];
		for (int i = 0 ; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			int y = stoi(st.nextToken()) - 1;
			int x = stoi(st.nextToken()) - 1;
			personInfo[i] = new Person(y,x);
		}
		
		st = new StringTokenizer(br.readLine());
		ey = stoi(st.nextToken()) - 1;
		ex = stoi(st.nextToken()) - 1;
		
		
		while(K-- > 0) {
			move();
			
			boolean tmp = true;
			for (int i = 0; i < M; i++) {
				if (!isOut[i]) tmp = false;
			}
			
			if (tmp) break;
			
			findSquare();
			rotateMap();
			rotatePerson();
//			System.out.println("sy = " + sy + " sx = " + sx + " size = " + size + " ey = " + ey + " ex = " + ex);
//			printPerosonPair();
//			printMap();
		}
		
		System.out.println(ans);
		System.out.println((ey+1) + " " + (ex+1));
	}
	
	static void printMap() {
		
		System.out.println("===============");
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(map[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("===============");
	}
	
	static void printPerosonPair() {
		for (int i = 0; i < M; i++) {
			if (isOut[i]) continue;
			System.out.println(personInfo[i].y + " " + personInfo[i].x);
		}
	}
	
	static void move() {
		for (int i = 0; i < M; i++) {
			if (isOut[i]) continue;
			
			Person p = personInfo[i];
			int dist = getDistance(p);
			int next_y = p.y;
			int next_x = p.x;
			
			for (int k = 0; k < 4; k++) {
				
				int ny = p.y + dy[k];
				int nx = p.x + dx[k];
				if (!inRange(ny,nx) || map[ny][nx] != 0) continue;
				
				int nDist = Math.abs(ny - ey) + Math.abs(nx - ex);
				if (dist >= nDist) {
					next_y = ny;
					next_x = nx;
					dist = nDist;
				}
			}
			
			if (p.y != next_y || p.x != next_x) {
				p.y = next_y;
				p.x = next_x;
				ans++;
				if (p.y == ey && p.x == ex) {
					isOut[i] = true;
					continue;
				}
			}			
		}
	}
	
	static void findSquare() {
		
		for (int sz = 1; sz < N; sz++) {
			for (int y1 = 0; y1 < N - sz; y1++) {
				for (int x1 = 0; x1 < N - sz; x1++) {
					int y2 = y1 + sz;
					int x2 = x1 + sz;
					
					if (!(y1 <= ey && ey <= y2 && x1 <= ex && ex <= x2)) continue;
					
					for (int i = 0; i < M; i++) {
						if (isOut[i]) continue;
						Person p = personInfo[i];

						if (y1 <= p.y && p.y <= y2 && x1 <= p.x && p.x <= x2) {

							size = sz;
							sy = y1;
							sx = x1;
							return;
						}
					}
				}
			}
		}
	}
	
	static void rotateMap() {
		
		int[][] temp1 = new int[size+1][size+1];
		int[][] temp2 = new int[size+1][size+1];
		
		for (int i = sy; i <= sy + size; i++) {
			for (int j = sx; j <= sx + size; j++) {
				if (map[i][j] > 0) map[i][j]--;
			}
		}
		
		for (int i = 0; i <= size; i++) {
			for (int j = 0; j <= size; j++) {
				temp1[i][j] = map[i+sy][j+sx];
			}
		}
		
		for (int i = 0; i <= size; i++) {
			for (int j = 0; j <= size; j++) {
				temp2[i][j] = temp1[size-j][i];
			}
		}
		
		for (int i = 0; i <= size; i++) {
			for (int j = 0; j <= size; j++) {
				map[i+sy][j+sx] = temp2[i][j];
			}
		}
	}
	
	static void rotatePerson() {
		
		for (int i = 0; i < M; i++) {
			if (isOut[i]) continue;
			
			Person p = personInfo[i];
			if (sy <= p.y && p.y <= sy+size && sx <= p.x && p.x <= sx+size) {
				int oy = p.y - sy;
				int ox = p.x - sx;
				
				int ry = ox;
				int rx = size - oy;
				p.y = ry + sy;
				p.x = rx + sx;
			}
		}
		
		int oy = ey - sy;
		int ox = ex - sx;
		int ry = ox;
		int rx = size - oy;
		
		ey = ry + sy;
		ex = rx + sx;
	}
	
	static int getDistance(Person p) {
		return Math.abs(p.y - ey) + Math.abs(p.x - ex);
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < N && 0 <= x && x < N;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}