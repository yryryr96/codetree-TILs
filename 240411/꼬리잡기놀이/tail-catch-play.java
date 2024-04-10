import java.util.*;
import java.io.*;

public class Main {

	static int n,m,k;
	static int[][] map;
	static int[] dy = {-1,0,1,0};
	static int[] dx = {0,1,0,-1};
	static class Pair {
		int y,x,d,team,v;
		Pair (int y, int x, int v, int team) {
			this.y = y;
			this.x = x;
			this.v = v;
			this.team = team;
		}
		
		Pair (int y, int x) {
			this.y = y;
			this.x = x;
		}

	}
	
	static boolean[][] visited;
	static int[][] teamNumber;
	static HashMap<Integer, List<Pair>> teams = new HashMap<>();
	static int ans = 0;
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		n = stoi(st.nextToken());
		m = stoi(st.nextToken());
		k = stoi(st.nextToken());
		map = new int[n][n];
		teamNumber = new int[n][n];
		
		for (int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < n; j++) {
				map[i][j] = stoi(st.nextToken());
			}
		}
		
		initGroup();
		

		for (int i = 1; i <= k; i++) {
			simulate(i);
			
		}

		System.out.println(ans);
	}
	
	static void simulate(int time) {
		
		move();
		attack(time);
	}
	
	static void attack(int time) {
		
		int t = time % (4*n);

		int si = 0, sj = 0, d = -1;
		if (0 < t && t <= n) {
			si = t - 1;
			sj = 0;
			d = 1;
		} else if (n < t && t <= 2*n) {
			si = n - 1;
			sj = t - n - 1;
			d = 0;
		} else if (2*n < t && t <= 3*n) {
			
			si = n - 1 - (t - 2*n - 1);
			sj = n-1;
			d = 3;
		} else if ((3*n < t && t < 4*n) || t == 0) {
			
			si = 0;
			sj = t == 0 ? si = 0 : n - 1 - (t - 3*n - 1);
			d = 2;
		}

		int teamNum = -1;
		while (inRange(si,sj)) {

			if(map[si][sj] != 4 && map[si][sj] != 0) {
				teamNum = teamNumber[si][sj];
				List<Pair> team = teams.get(teamNum);
				for (int i = 0; i < team.size(); i++) {
					Pair p = team.get(i);
					if (p.y == si && p.x == sj) {
						ans += ((team.size() - i)*(team.size() - i));
						break;
					}
				}
				
				break;
			}
			
			si += dy[d];
			sj += dx[d];
		}
		

		if (teamNum != -1) reverseTeam(teamNum);
	}
	
	static void reverseTeam(int teamNumber) {
		
		List<Pair> next = new ArrayList<>();
		List<Pair> team = teams.get(teamNumber);
		
		for (int i = team.size() - 1; i >= 0; i--) {
			Pair person = team.get(i);
			next.add(new Pair(person.y, person.x, person.v, person.team));
		}
		
		teams.put(teamNumber, next);
	}
		
	static void move() {
		
		for (int i = 1; i <= m; i++) {
//			System.out.println("i = " + i);
			List<Pair> team = teams.get(i);
			boolean temp = true;
			Pair head = team.get(team.size()-1);
			Pair tail = team.get(0);
			if (getDistance(head.y, head.x, tail.y, tail.x) > 1) {temp = false;}
			
			for (int j = team.size() - 1; j >= 0; j--) {
				Pair now = team.get(j);
//				System.out.println("now = " + now.y + " " + now.x + " d = " + now.d);
				for (int k = 0; k < 4; k++) {
					int ny = now.y + dy[k];
					int nx = now.x + dx[k];
					
					if (!inRange(ny,nx)) continue;
					if (j == team.size() - 1) {
						
						if (map[ny][nx] != 0 && map[ny][nx] != 2) {
							map[now.y][now.x] = -1;
							now.y = ny;
							now.x = nx;
							map[ny][nx] = now.v;
							break;
						}
					}
					else if (j == 0) {
						if(map[ny][nx] == -1) {
							map[ny][nx] = now.v;
							if (!temp) {
								map[now.y][now.x] = 4;
							}
							
							now.y = ny;
							now.x = nx;
							break;
						}
					}
					else {
						if(map[ny][nx] == -1) {

							map[now.y][now.x] = -1;
							now.y = ny;
							now.x = nx;
							map[ny][nx] = now.v;
							break;
						}
					}
				}
			}
		}
	}
	
	static void initGroup() {
		
		visited = new boolean[n][n];
		int team = 1;
		for(int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if(map[i][j] == 3) {
					bfs(team, i, j);
					initTeamNumber(team, i, j);
					team++;
				}
			}
		}
	}
	
	static void bfs(int team, int y, int x) {

		int d = -1;
		for (int i = 0; i < 4; i++) {
			int ny = y + dy[i];
			int nx = x + dy[i];
			if (inRange(ny,nx) && map[ny][nx] == 2) {
				d = i;
				break;
			}
		}
		
		
		Queue<Pair> q = new LinkedList<>();
		Pair start = new Pair(y,x,3,team);
		q.add(start);
		visited[y][x] = true;
		List<Pair> tq = new ArrayList<>();
		
		int headY = 0, headX = 0;
		while(!q.isEmpty()) {
			
			Pair now = q.poll();
			
			for (int k = 0; k < 4; k++) {
				int ny = now.y + dy[k];
				int nx = now.x + dx[k];
				if (!inRange(ny, nx) || visited[ny][nx] || map[ny][nx] == 0 || map[ny][nx] == 4) continue;
				if(map[ny][nx] == 2) {
					tq.add(new Pair(now.y, now.x, map[now.y][now.x], team));
					q.add(new Pair(ny, nx));
					visited[ny][nx] = true;
				} 
				
				else if (map[ny][nx] == 1) {
					if (now.y == start.y && now.x == start.x) continue;
					else {
						tq.add(new Pair(now.y, now.x, map[now.y][now.x], team));
						headY = ny;
						headX = nx;
						break;
					}
				}
			}
		}
		
//		System.out.println("headY = " + headY + " headX = " + headX);
		
		for (int i = 0; i < 4; i++) {
			int ny = headY + dy[i];
			int nx = headX + dx[i];
			if(!inRange(ny,nx)) continue;
			if(map[ny][nx] == 4 || map[ny][nx] == 3) {
				tq.add(new Pair(headY, headX, 1, team));
				break;
			}
		}
		
//		System.out.println(tq.size());
		teams.put(team, tq);
	}
	
	static void initTeamNumber(int team, int y, int x) {
		
		boolean[][] visit = new boolean[n][n];
		Queue<Pair> q = new LinkedList<>();
		q.add(new Pair(y,x));
		visit[y][x] = true;
		teamNumber[y][x] = team;
		while(!q.isEmpty()) {
			
			Pair now = q.poll();
			for (int i = 0; i < 4; i++) {
				int ny = now.y + dy[i];
				int nx = now.x + dx[i];
//				System.out.println(ny + " " + nx);
				if (!inRange(ny,nx) || map[ny][nx] == 0 || visit[ny][nx]) continue;
				visit[ny][nx] = true;
				q.add(new Pair(ny,nx));
				teamNumber[ny][nx] = team;
			}
		}
	}
	
	static int getDistance(int y1, int x1, int y2, int x2) {
		return (int) Math.abs(x1-x2) + Math.abs(y1-y2);
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < n && 0 <= x && x < n;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}