import java.io.*;
import java.util.*;

public class Main {
	
	static int[] dy = {-1,0,1,0};
	static int[] dx = {0,1,0,-1};
	static int n,m,k;
	static int[][] map;
	static class Player {
		int y,x,d,s,score,gun;
		Player(int y, int x, int d, int s) {
			this.y = y;
			this.x = x;
			this.d = d;
			this.s = s;
			gun = 0;
			score = 0;
		}
	}
	
	static List<List<List<Integer>>> gunInfo = new ArrayList<>();
	static HashMap<Integer, Player> players = new HashMap<>();
	static int[][] visited;
	
	public static void main(String[] args) throws IOException {
		
		// System.setIn(new FileInputStream("src/eclipse/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		n = stoi(st.nextToken()); 
		m = stoi(st.nextToken());
		k = stoi(st.nextToken());
		map = new int[n][n];
		visited = new int[n][n];
		
		for (int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			gunInfo.add(new ArrayList<>());
			for (int j = 0; j < n; j++) {
				gunInfo.get(i).add(new ArrayList<>());
				map[i][j] = stoi(st.nextToken());
				if (map[i][j] != 0) {
					gunInfo.get(i).get(j).add(map[i][j]);
				}
			}
		}
		
		for (int i = 1; i <= m; i++) {
			st = new StringTokenizer(br.readLine());
			int y = stoi(st.nextToken());
			int x = stoi(st.nextToken());
			int d = stoi(st.nextToken());
			int s = stoi(st.nextToken());
			
			visited[y-1][x-1] = i;
			players.put(i, new Player(y-1,x-1,d,s));
		}
		
		for (int i = 0; i < k; i++) {
//			System.out.println("========" + (i+1) + "========");
			simulate();
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= m; i++) {
			sb.append(players.get(i).score).append(" "); 
		}
		
		System.out.println(sb.toString());
	}
	
	static void simulate() {
		
		for (int i = 1; i <= m; i++) {
			move(i);
		}
	}
	
	static void move(int num) {
		
		Player player = players.get(num);
		int ny = player.y + dy[player.d];
		int nx = player.x + dx[player.d];
		if (!inRange(ny,nx)) player.d = (player.d + 2) % 4;
		ny = player.y + dy[player.d];
		nx = player.x + dx[player.d];
		
		// 플레이어 x
		if (visited[ny][nx] == 0) {
			
			updateGun(ny,nx,player);
			
			visited[player.y][player.x] = 0; 
			player.y = ny;
			player.x = nx;
			visited[ny][nx] = num;
		} else {
			
			visited[player.y][player.x] = 0; 
			player.y = ny;
			player.x = nx;
			Player rival = players.get(visited[ny][nx]);
			int score = (player.s + player.gun) -  (rival.s + rival.gun);
			
//			System.out.println("rival = " + visited[ny][nx]);
//			System.out.println("playerPower = " + (player.s + player.gun) + " rivalPower = " + (rival.s + rival.gun));
						
			if (score > 0 || (score == 0 && player.s > rival.s)) {
				player.score += score;
				gunInfo.get(ny).get(nx).add(rival.gun);
				rival.gun = 0;
				loserMove(rival, visited[ny][nx]);
				winnerMove(player);
				visited[ny][nx] = num;
			}
			else if (score < 0 || (score == 0 && player.s < rival.s)){
				rival.score += (-score);
				gunInfo.get(ny).get(nx).add(player.gun);
				player.gun = 0;
				loserMove(player, num);
				winnerMove(rival);
			} 
		}
//		
//		System.out.println("num = " + num + " player.y = " + player.y + " player.x = " + player.x);
//		System.out.println("player.power = " + (player.s + player.gun));
	}
	
	static void updateGun(int y, int x, Player player) {
		
		List<Integer> guns = gunInfo.get(y).get(x);
		int idx = 0;
		int maxGunPower = player.gun;

		// 최대 총 공격력
		for (int i = 0; i < guns.size(); i++) {

			if (guns.get(i) > maxGunPower) {
				idx = i;
				maxGunPower = guns.get(i);
			}
		}
		
		// 플레이어 총 갱신
		if (maxGunPower > player.gun) {

			guns.set(idx, player.gun);
			player.gun = maxGunPower;
		};
	}
	
	static void winnerMove(Player winner) {
		
		updateGun(winner.y, winner.x, winner);
	}
	
	static void loserMove(Player loser, int num) {
		
		int d = loser.d;
		int ny = loser.y + dy[d];
		int nx = loser.x + dy[d];

		for (int i = 0; i <= 3; i++) {
			ny = loser.y + dy[(d+i)%4];
			nx = loser.x + dx[(d+i)%4];
			if (inRange(ny,nx) && visited[ny][nx] == 0) {
				loser.y = ny;
				loser.x = nx;
				loser.d = (d+i) % 4;
				visited[ny][nx] = num;
				break;
			}
		}
		 
		
		
		updateGun(loser.y, loser.x, loser);
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < n && 0 <= x && x < n;
	}

	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}