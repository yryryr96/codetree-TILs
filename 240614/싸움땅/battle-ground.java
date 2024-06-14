import java.util.*;
import java.io.*;

public class Main {
	
	static int n,m,k;
	static int[] dy = {-1,0,1,0};
	static int[] dx = {0,1,0,-1};
	static class Gun {
		int power;
		Gun (int power) {
			this.power = power;
		}
	}
	
	static class Player {
		int id,y,x,d,power,point;
		Gun gun;
		
		Player (int id, int y, int x, int d, int power) {
			this.id = id;
			this.y = y;
			this.x = x;
			this.d = d;
			this.power = power;
			this.point = 0;
			this.gun = new Gun(0);
		}
	}
	
	static List<Gun>[][] guns;
	static Player[] players;
	public static void main(String[] args) throws IOException {
		
//		System.setIn(new FileInputStream("src/eclipse/iniput.txt"));
		Scanner sc = new Scanner(System.in);
		
		n = sc.nextInt();
		m = sc.nextInt();
		k = sc.nextInt();
		
		guns = new ArrayList[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				guns[i][j] = new ArrayList<>();
				guns[i][j].add(new Gun(sc.nextInt())) ;
			}
		}
		
		players = new Player[m+1];
		for (int i = 1 ; i <= m; i++) {
			int y = sc.nextInt() - 1;
			int x = sc.nextInt() - 1;
			int d = sc.nextInt();
			int s = sc.nextInt();
			players[i] = new Player(i,y,x,d,s);
		}
		
		while (k-- > 0) {
			simulate();
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= m; i++) {
//			System.out.println("player : " + players[i].y + " " + players[i].x);
			sb.append(players[i].point + " ");
		}
		
		System.out.println(sb.toString());
	}
	
	static void simulate() {
		for (int id = 1; id <= m; id++) {
		
			Player player = players[id];
			
			move(player);
			if (!isInPlayer(player, player.y, player.x)) {
				// 플레이어 없으면 총 확인 후 총 획득
				getGun(player);
			} else {
				// 플레이어 있으면 싸움
				fight(player);
			}
		}
	}
	
	// 단순 이동
	static void move(Player player) {
		
		int ny = player.y + dy[player.d];
		int nx = player.x + dx[player.d];
		
		if (inRange(ny,nx)) {
			player.y = ny;
			player.x = nx;
		} else {
			int d = (player.d + 2) % 4;
			ny = player.y + dy[d];
			nx = player.x + dx[d];
			player.y = ny;
			player.x = nx;
			player.d = d;
		}
	}
	
	static boolean isInPlayer(Player player, int y, int x) {
		for (int i = 1; i <= m; i++) {
			if (i == player.id) continue;
			
			Player p = players[i];
			if (p.y == y && p.x == x) return true;
		}
		
		return false;
	}
	
	static void fight(Player player) {
		
		Player river = null;
		for (int i = 1; i <= m; i++) {
			if (i == player.id) continue;
			if (players[i].y == player.y && players[i].x == player.x) {
				river = players[i];
				break;
			}
		}
		
		Player winner = player, loser = river;
		int playerScore = player.power + player.gun.power;
		int riverScore = river.power + river.gun.power;
		
		if (playerScore > riverScore) {
			winner = player;
			loser = river;
		} else if (playerScore < riverScore) {
			winner = river;
			loser = player;
		}else if (playerScore == riverScore) {
			if (player.power > river.power) {
				winner = player;
				loser = river;
			} else {
				winner = river;
				loser = player;
			}
		}
		
		winner.point += Math.abs(playerScore - riverScore);
		loserMove(loser);
		getGun(winner);
	}
	
	static void loserMove(Player loser) {

		guns[loser.y][loser.x].add(loser.gun);
		loser.gun = new Gun(0);
		
		int d = loser.d;
		
		for (int k = 0; k < 4; k++) {
			int ny = loser.y + dy[(d+k)%4];
			int nx = loser.x + dx[(d+k)%4];
			if (inRange(ny,nx) && !isInPlayer(loser, ny, nx)) {
				loser.y = ny;
				loser.x = nx;
				loser.d = (d+k)%4;
				getGun(loser);
				
				break;
			}	
		}

		
	}
	
	static void getGun(Player player) {
		
//		System.out.println(player.y + " " + player.x);
		List<Gun> gunInfo = guns[player.y][player.x];
		if (gunInfo.size() == 0) return;
		else {
			
			int maxGunPower = player.gun.power;
			Gun resultGun = player.gun;
			for (Gun gun : gunInfo) {
				if (gun.power > maxGunPower) {
					maxGunPower = gun.power;
					resultGun = gun;
				}
			}
			
			// 더 크면 버리고 획득
			if (maxGunPower > player.gun.power) {
				gunInfo.add(player.gun);
				gunInfo.remove(resultGun);
				player.gun = resultGun;
			}
		}
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < n && 0 <= x && x < n;
	}
}