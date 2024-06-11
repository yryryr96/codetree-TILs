import java.util.*;
import java.io.*;

public class Main {
	
	static int R,C,K;
	static int[] dy = {-1,0,1,0};
	static int[] dx = {0,1,0,-1};
	static int[][] forest = new int[73][70];
	static boolean[][] isExit = new boolean[73][70];
	static int ans = 0;
	
	public static void main(String[] args) throws IOException {
		
		// System.setIn(new FileInputStream("src/eclipse/input.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		R = stoi(st.nextToken());
		C = stoi(st.nextToken());
		K = stoi(st.nextToken());
		
		for(int i = 1; i <= K; i++) {
			st = new StringTokenizer(br.readLine());
			int x = stoi(st.nextToken()) - 1;
			int d = stoi(st.nextToken());
			down(1, x, d, i);

		}
		
		System.out.println(ans);
	}
	
	private static void printForest() {
		for(int i = 0; i < R + 3; i++) {
			for(int j = 0; j < C; j++) {
				System.out.print(forest[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	private static void down(int y, int x, int d, int c) {
		
		if (canGo(y+1,x)) {
			down(y+1, x, d, c);
		} else if (canGo(y+1, x-1)) {
			down(y+1, x-1, (d+3) % 4, c);
		} else if (canGo(y+1, x+1)) {
			down(y+1, x+1, (d+1) % 4, c);
		} else {
			if (!inRange(y+1,x+1) || !inRange(y-1,x-1)) {
				initForest();
			}
			
			else {
				forest[y][x] = c;
				for(int i = 0; i< 4; i++) {
					forest[y + dy[i]][x + dx[i]] = c;
				}
				isExit[y+dy[d]][x+dx[d]] = true;
//				printForest();
				ans += bfs(y,x);
			}
		}
	}
	
	private static int bfs(int y, int x) {
		
//		System.out.println("y = " + (y-3) + " x = " + x);
		int MAX = y;
		Queue<int[]> q = new LinkedList<>();
		boolean[][] visited = new boolean[73][70];
		
		q.add(new int[] {y,x});
		visited[y][x] = true;
		
		while (!q.isEmpty()) {
			int[] now = q.poll();
//			System.out.println("now.y = " + (now[0]-3) + " now.x = " + now[1] + " v = " + forest[now[0]][now[1]]);
			for(int i = 0; i < 4; i ++) {
				int ny = now[0] + dy[i];
				int nx = now[1] + dx[i];
				if (!inRange(ny,nx) || visited[ny][nx]) continue;
				if (isExit[now[0]][now[1]] && forest[ny][nx] != 0) {
					q.add(new int[] {ny,nx});
					visited[ny][nx] = true;
					MAX = Math.max(MAX, ny);
				}
				else if (forest[ny][nx] == forest[now[0]][now[1]]) {
					q.add(new int[] {ny,nx});
					visited[ny][nx] = true;
					MAX = Math.max(MAX, ny);
				}
			}
		}
//		System.out.println(MAX - 3 + 1);
		return MAX - 3 + 1;
	}
	
	private static void initForest() {
		
		for(int i = 0; i < R+3; i++) {
			for(int j = 0; j < C; j++) {
				forest[i][j] = 0;
				isExit[i][j] = false;
			}
		}
	}
	
	private static boolean canGo(int y, int x) {
		boolean flag = 0 <= x - 1 && x + 1 < C && y + 1 < R + 3;
		if (!flag) return flag;
		
		return onlyNearCheck(y, x) && onlyNearCheck(y-1, x); 
	}
	
	private static boolean onlyNearCheck(int y, int x) {
		for (int k = 0; k < 4; k++) {
			if (forest[y + dy[k]][x + dx[k]] != 0) {
				return false;
			}
		}
		return true;
	}
		
	private static boolean inRange(int y, int x) {
		return 3 <= y && y < R + 3 && 0 <= x && x < C;
	}
	
	private static int stoi(String s) {
		return Integer.parseInt(s);
	}
}