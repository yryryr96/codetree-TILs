import java.util.*;
import java.io.*;

public class Main {
	
	static int N,M,K;
	static int[] dy = {0,1,0,-1,1,1,-1,-1};
	static int[] dx = {1,0,-1,0,1,-1,1,-1};
	static int[][] map, turn;
	static class Pair {
		int y,x;
		Pair(int y, int x) {
			this.y = y;
			this.x = x;
		}
	}
	static Pair[][] trace;
	static boolean[][] isAffected;
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = stoi(st.nextToken());
		M = stoi(st.nextToken());
		K = stoi(st.nextToken());
		
		map = new int[N][M];
		turn = new int[N][M];
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < M; j ++) {
				map[i][j] = stoi(st.nextToken());
			}
		}
		
		for (int t = 1; t <= K; t++) {
			if (simulate(t)) {
				break;
			}
//			printMap();
		}
		
		System.out.println(getAnswer());
	}
	
	static int getAnswer() {
		
		int maxPower = 0;
		for (int i = 0; i < N; i++) {
			for(int j = 0; j < M; j++) {
				if (maxPower < map[i][j]) {
					maxPower = map[i][j];
				}
			}
		}
		
		return maxPower;
	}
	
	static boolean simulate(int t) {
		
		Pair attacker = getAttacker(t);
		Pair target = getTarget();
		map[attacker.y][attacker.x] += N+M;
//		System.out.println("ay = " + attacker.y + " ax = " + attacker.x + " ty = " + target.y + " tx = " + target.x);
		attack(attacker, target);
		int destroyedCount = destroyAndRecovery();
//		System.out.println("map[attacker.y][attacker.x] = " + map[attacker.y][attacker.x]);
		return destroyedCount == N*M - 1;
		
	}
	
	static int destroyAndRecovery() {
		
		int cnt = 0;
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < M; j++) {
				if(map[i][j] <= 0) {
					map[i][j] = 0;
					cnt++;
				}
				else if (!isAffected[i][j]) {
					map[i][j]++;
				}
			}
		}
		
		return cnt;
	}
	
	static void attack(Pair attacker, Pair target) {
		
		isAffected = new boolean[N][M];
		
		isAffected[attacker.y][attacker.x] = true; 
		isAffected[target.y][target.x] = true;
		
		if (lazerAttack(attacker, target)) {
		} else {
			misileAttack(attacker, target);
		}
	}
	
	static void misileAttack(Pair attacker, Pair target) {

		int power = map[attacker.y][attacker.x];
	
		map[target.y][target.x] -= power;
		
		for (int k = 0; k < 8; k++) {
			
			int ny = (N + target.y + dy[k]) % N;
			int nx = (M + target.x + dx[k]) % M;
			if (map[ny][nx] == 0 || (ny == attacker.y && nx == attacker.x)) continue;
			else {
				map[ny][nx] -= power/2;
				isAffected[ny][nx] = true;
			}
		}
	}
	
	static boolean lazerAttack(Pair attacker, Pair target) {
		
		Queue<Pair> q = new LinkedList<>();
		trace = new Pair[N][M];
		boolean[][] visited = new boolean[N][M];
		
		q.add(attacker);
		visited[attacker.y][attacker.x] = true;
		
		boolean temp = false;
		while(!q.isEmpty()) {
			
			Pair cur = q.poll();
			
			if (cur.y == target.y && cur.x == target.x) {
				temp = true;
				break;
			}
			
			for(int k = 0; k < 4; k++) {
				int ny = (N + cur.y + dy[k]) % N;
				int nx = (M + cur.x + dx[k]) % M;
				if (map[ny][nx] == 0 || visited[ny][nx]) continue;
				
				q.add(new Pair(ny,nx));
				visited[ny][nx] = true;
				trace[ny][nx] = cur;
			}
		}
		
		if (!temp) return temp;
		else {
			
			int power = map[attacker.y][attacker.x];
			map[target.y][target.x] -= power;
			
			Queue<Pair> nq = new LinkedList<>();
			nq.add(target);
			
			while(!nq.isEmpty()) {
				
				Pair cur = nq.poll();
				Pair next = trace[cur.y][cur.x];
				
				if (next.y == attacker.y && next.x == attacker.x) {
					break;
				}
				
				isAffected[next.y][next.x] = true;
				map[next.y][next.x] -= power/2; 
				nq.add(next);
			}
			return temp;
		}
	}
	
	static Pair getAttacker(int t) {
		
		int minPower = 5001;
		int y = 0, x = 0;
		
		// 열이 가장 큰
		for (int j = 0; j < M; j++) {
			for (int i = 0; i < N; i++) {
				if (map[i][j] == 0) continue;
				
				int v = map[i][j];
				// 공격력이 더 작으면
				if (minPower > v) {
					minPower = v;
					y = i; x = j;
				}
				// 공격력이 같으면
				else if (minPower == v) {
					
					// 가장 최근에 공격한 포탑
					if (turn[y][x] < turn[i][j]) {
						y = i; x = j;
					}
					// 턴이 같다면
					else if (turn[y][x] == turn[i][j]) {
						// 행 열 합
						if (y + x <= i + j) {
							y = i; x = j;
						}
					}
				}
			}
		}
		
		turn[y][x] = t;
		return new Pair(y,x);
	}
	
	static Pair getTarget() {
		
		int maxPower = 0;
		int y = 0, x = 0;
		
		for (int j = M-1; j >= 0; j--) {
			for (int i = N-1; i >= 0; i--) {
				if (map[i][j] == 0) continue;
				
				int v = map[i][j];
				// 공격력이 더 크면
				if (maxPower < v) {
					maxPower = v;
					y = i; x = j;
				}
				// 공격력이 같으면
				else if (maxPower == v) {
					
					// 가장 오래전에 공격한 포탑
					if (turn[y][x] > turn[i][j]) {
						y = i; x = j;
					}
					// 턴이 같다면
					else if (turn[y][x] == turn[i][j]) {
						// 행 열 합
						if (y + x >= i + j) {
							y = i; x = j;
						}
					}
				}
			}
		}
		
		return new Pair(y,x);
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < N && 0 <= x && x < M;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
	
	static void printMap() {
		for (int i = 0; i < N; i++) {
			for(int j = 0; j < M; j++) {
				System.out.print(map[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("===============");
	}
}