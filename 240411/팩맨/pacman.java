import java.util.*;
import java.io.*;

public class Main {

	
	static int m,t;
	static int[] my = {-1,-1,0,1,1,1,0,-1};
	static int[] mx = {0,-1,-1,-1,0,1,1,1};
	static int[] dy = {-1,0,1,0};
	static int[] dx = {0,-1,0,1};
	static class Pair {
		int y,x,d,t;
		boolean isLive;
		
		Pair(int y, int x, int d) {
			this.y = y;
			this.x = x;
			this.d = d;
			t = 0;
			isLive = true;
		}
		
		Pair(int time) {
			this.t = time;
		}
		
		Pair(int y, int x) {
			this.y = y;
			this.x = x;
		}
	}
	
	static Pair packMan;
	static List<Pair> monsters = new ArrayList<>();
	static List<Pair> eggs = new ArrayList<>();
	static List<List<List<Pair>>> dieMonsters = new ArrayList<>();
	static int maxValue = 0;
	static List<Integer> way;
	static boolean[][] visited;
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		m = stoi(st.nextToken());
		t = stoi(st.nextToken());
		
		st = new StringTokenizer(br.readLine());
		int r = stoi(st.nextToken());
		int c = stoi(st.nextToken());
		
		packMan = new Pair(r-1, c-1);
		
		for (int i = 0; i < m; i++) {
			st = new StringTokenizer(br.readLine());
			int y,x,d;
			y = stoi(st.nextToken()) - 1;
			x = stoi(st.nextToken()) - 1;
			d = stoi(st.nextToken()) - 1;
			
			monsters.add(new Pair(y,x,d));
		}
		
		for (int i = 0; i < 4; i++) {
			dieMonsters.add(new ArrayList<>());
			for (int j = 0; j < 4; j++) {
				dieMonsters.get(i).add(new ArrayList<>());
			}
		}
		
		for (int i = 1; i <= t; i++) {
			 simulate(i);
//			 System.out.println(" time = " + i + " " + packMan.y + " " + packMan.x);
//			 System.out.println(maxValue);
		}
		
		System.out.println(getAnswer());
	}
	
	static int getAnswer() {
		
		int cnt = 0;
		for (Pair monster : monsters) {
			if (monster.isLive) {
//				System.out.println(monster.y + " " + monster.x);
				cnt++; 
			}
		}
		
		return cnt;
	}
	
	static void simulate(int time) {
		
		// 몬스터 복제 시도
		copyMonster();
		
		// 몬스터 이동
		monsterMove(time);
		
		//팩맨 이동
		packManMove(time);
		
		//시체 갱신
		initDieMonster(time);
		
		// 몬스터 복제 완성
		duplicateMonsters();
	}
	
	static void copyMonster() {
		
		eggs = new ArrayList<>();
		for (Pair monster : monsters) {
			if (!monster.isLive) continue;
			eggs.add(new Pair(monster.y, monster.x, monster.d));
		
		}
	}
	
	static void monsterMove(int time) {
		
		for (Pair monster : monsters) {
			if (!monster.isLive) continue;
			int ny = monster.y + my[monster.d];
			int nx = monster.x + mx[monster.d];
			// time - egg.t > 2 -> 움직일 수 있다.
			// 격자 외, 팩맨, 시체
			if (!inRange(ny,nx) || (ny == packMan.y && nx == packMan.x) || !checkDieMonster(ny,nx)) {
				
				int k = 1;
				while (k < 8) {
					
					ny = monster.y + my[(monster.d + k) % 8];
					nx = monster.x + mx[(monster.d + k) % 8];
					
					if (inRange(ny, nx) && (ny != packMan.y || nx != packMan.x) && checkDieMonster(ny, nx)) {
						monster.y = ny;
						monster.x = nx;
						monster.d = (monster.d + k) % 8;
						break;
					}
					
					k++;
				}
			} else {
				monster.y = ny;
				monster.x = nx;
			}

		}
	}
	
	static void packManMove(int time) {
		
		visited = new boolean[4][4];
		visited[packMan.y][packMan.x] = true;
		maxValue = 0;
		// 순열 dfs
		// 칸수, 마리수, 방향
		 dfs(0,packMan.y, packMan.x, 0,new ArrayList<>());
		 
		 for (Integer d : way) {
	
			 packMan.y = packMan.y + dy[d];
			 packMan.x = packMan.x + dx[d];
			 killMonster(packMan.y, packMan.x, time);
		 }
		 
	}
	
	static void killMonster(int y, int x, int time) {
		
		for (Pair monster : monsters) {
			if (!monster.isLive) continue;
			if (monster.y == y && monster.x == x) {
				monster.isLive = false;
				dieMonsters.get(y).get(x).add(new Pair(time));
			}
		}
	}
	
	static void initDieMonster(int time) {
		
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				List<Pair> dies = dieMonsters.get(i).get(j);
				List<Pair> nd = new ArrayList<>();
				for (Pair m : dies) {
					if (time - m.t >= 2) continue;
					nd.add(m);
				}
				
				dies = nd;
			}
		}
	}
	
	static void dfs(int depth, int y, int x, int cnt, List<Integer> d) {
		
		if (depth == 3) {
			
			if (cnt > maxValue) {

				way = new ArrayList<>();
				
				for (Integer dir : d) {
					way.add(dir);
				}
				
				maxValue = cnt;
			}
			
			return;
		}
		
		for (int i = 0; i < 4; i++) {
			int ny = y + dy[i];
			int nx = x + dx[i];
			
			if (inRange(ny,nx)) {
						
				int c = 0;
				for (Pair m : monsters) {
					if (!m.isLive) continue;
					if (m.y == ny && m.x == nx) c++;
				}
				
				List<Integer> directions = new ArrayList<>();
				for (Integer dir : d) {
					directions.add(dir);
				}
				directions.add(i);
					
				if (visited[ny][nx] || (ny == packMan.y && nx == packMan.x)) {
					dfs(depth + 1, ny, nx, cnt, directions);
				}
				else {
//					System.out.println("depth = " + depth + " ny = " + ny + " nx = " + nx + " c = " + c);
					visited[ny][nx] = true;
					dfs(depth + 1, ny, nx, cnt + c, directions);
					visited[ny][nx] = false;
				}
			}
		}
	}
	
	static void duplicateMonsters() {
		
		for (Pair egg : eggs) {
			monsters.add(new Pair(egg.y, egg.x, egg.d));
			
		}
	}
	
	static boolean checkDieMonster(int y, int x) {
				
		return dieMonsters.get(y).get(x).size() == 0;
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < 4 && 0 <= x && x < 4;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}