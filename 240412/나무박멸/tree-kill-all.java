import java.util.*;
import java.io.*;

public class Main {

	static int n,m,k,c;
	static int[][] board;
	static int[][] temp; // 번식 저장
	static int[][] drug; // 제초제 정보
	
	// 4방향 델타
	static int[] dy = {0,1,0,-1};
	static int[] dx = {1,0,-1,0};
	
	// 대각선 델타
	static int[] ddy = {1,1,-1,-1};
	static int[] ddx = {1,-1,1,-1};
	static int ans = 0;
	static class Pair {
		int y,x,cnt;
		Pair (int y, int x) {
			this.y = y;
			this.x = x;
		}
		
		Pair(int y, int x, int cnt) {
			this.y = y;
			this.x = x;
			this.cnt = cnt;
		}
	}
	
	static List<Pair> tempList = new ArrayList<>();
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		n = stoi(st.nextToken());
		m = stoi(st.nextToken());
		k = stoi(st.nextToken());
		c = stoi(st.nextToken());
		board = new int[n][n];
		temp = new int[n][n];
		drug = new int[n][n];
		
		for (int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < n; j++) {
				board[i][j] = stoi(st.nextToken());
			}
		}
		
		
		while (m-- > 0) {
			simulate();
		}
		
		System.out.println(ans);
	}
	
	static void simulate() {
		
		// 제초제 생명 초기화
		initDrug();
		
		// grow();
		grow();
		
		// 번식
		spreadSuccess();
//		printBoard();
		
		// 박멸
		goDrug();
	}
	
	static void printBoard() {
		
		for (int i =0; i < n; i++) {
			System.out.println(Arrays.toString(board[i]));
		}
	}
	
	// 1번, 2번 동시에
	static void grow() {
		
		initTemp();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (board[i][j] > 0) {
					int cnt = 0;
					int canSpread = 0;
					tempList.clear();
					for (int l = 0; l < 4; l++) {
						int ny = i + dy[l];
						int nx = j + dx[l];
						// 범위 밖, 빈칸 아니면, 제초제 있으면 continue
						if (!inRange(ny,nx) || drug[ny][nx] > 0 || board[ny][nx] == -1) continue;
						if (board[ny][nx] > 0) {
							cnt++;
							continue;
						}
						
						if (board[ny][nx] == 0) {
							tempList.add(new Pair(ny,nx));
							canSpread++;
						}
					}
					
					// 성장
					board[i][j] += cnt;
					if (canSpread == 0) continue;
					else {
						for (Pair p : tempList) {
							temp[p.y][p.x] += (board[i][j] / canSpread); 
						}
					}
				}
			}
		}
	}
	
	static void spreadSuccess() {
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				board[i][j] += temp[i][j];
			}
		}
	}
	
	static void goDrug() {
		
		List<Pair> drugList = new ArrayList<>();
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (board[i][j] <= 0) continue;
				
				int v = board[i][j];
//				System.out.println("i = " + i + " j = " + j);
				for (int d = 0; d < 4; d++) {
					for (int l = 1; l <= k; l++) {
						int ny = i + ddy[d]*l;
						int nx = j + ddx[d]*l;
						if (!inRange(ny,nx)) continue;
						
						// 나무가 있으면 제초제 초기화
						if (board[ny][nx] > 0) {
//							System.out.println(board[ny][nx]);
							v += board[ny][nx];
						}
						
						// 벽이거나 빈칸이면
						else if (board[ny][nx] == -1 || board[ny][nx] == 0) {
							break;
						}
					}
				}
				
				drugList.add(new Pair(i, j, v));
			}
		}
		
		Collections.sort(drugList, (a,b) -> {
			if (a.cnt != b.cnt) return b.cnt - a.cnt;
			else {
				if (a.y != b.y) return a.y - b.y;
				else return a.x - b.x;
			}
		});
		
		Pair target = drugList.get(0);
		int v = board[target.y][target.x];
		drug[target.y][target.x] = c + 1;
		board[target.y][target.x] = 0; 
		for (int d = 0; d < 4; d++) {
			for (int l = 1; l <= k; l++) {
				int ny = target.y + ddy[d]*l;
				int nx = target.x + ddx[d]*l;
				if (!inRange(ny,nx)) continue;
				
				// 나무가 있으면 제초제 초기화
				if (board[ny][nx] > 0) {
					v += board[ny][nx];
					board[ny][nx] = 0;
					drug[ny][nx] = c + 1;
				}
				
				// 벽이거나 빈칸이면
				else if (board[ny][nx] == -1 || board[ny][nx] == 0) {
					drug[ny][nx] = c + 1;
					break;
				}
			}
		}
		
		ans += v;
	}
	
	
	
	// 제초제 생명 초기화
	static void initDrug() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (drug[i][j] > 0) drug[i][j]--;
			}
		}
	}
	
	static void initTemp() {
		
		for (int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				temp[i][j] = 0;
			}
		}
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < n && 0 <= x && x < n;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}