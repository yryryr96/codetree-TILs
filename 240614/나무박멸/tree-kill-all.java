import java.util.*;
import java.io.*;

public class Main {
	
	static int n,m,k,c;
	static int[][] board;
	static int[][] drug;
	
	static int[] dy = {0,1,0,-1};
	static int[] dx = {1,0,-1,0};
	static int[] ddy = {1,1,-1,-1};
	static int[] ddx = {1,-1,1,-1};
	static int ans = 0;
	
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		n = stoi(st.nextToken());
		m = stoi(st.nextToken());
		k = stoi(st.nextToken());
		c = stoi(st.nextToken());
		
		board = new int[n][n];
		drug = new int[n][n];
		for(int i = 0; i < n; i++) {
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
		growTree();
//		printBoard();
		spreadTree();
//		printBoard();
		refreshDrug();
		spreadDrug();
	}
	
	static void refreshDrug() {
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if (drug[i][j] > 0) drug[i][j]--;
			}
		}
	}
	
	static void spreadDrug() {
		
		int maxCnt = 0;
		int ri = -1, rj = -1;
		
		// 찾기
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if (board[i][j] <= 0) continue;
				
				int cnt = board[i][j];
				for(int l = 0; l < 4; l++) {
					for(int z = 1; z <= k; z++) {
						int ny = i + ddy[l]*z;
						int nx = j + ddx[l]*z;

						if (!inRange(ny,nx) || board[ny][nx] <= 0) break;
						cnt += board[ny][nx];

					}
				}
				
				if (maxCnt < cnt) {
					maxCnt = cnt;
					ri = i; rj = j;
				}
			}
		}

        if (ri == -1 && rj == -1) return;
        
		// 제초제 뿌리기
		int treeDieCnt = board[ri][rj];
		drug[ri][rj] = c;
		board[ri][rj] = 0;
		for(int l = 0; l < 4; l++) {
			for(int z = 1; z <= k; z++) {
				int ny = ri + ddy[l]*z;
				int nx = rj + ddx[l]*z;
				if (!inRange(ny,nx)) break;
				if (board[ny][nx] == -1) break;
				if (board[ny][nx] == 0) {
					drug[ny][nx] = c;
					break;
				}
				
				treeDieCnt += board[ny][nx];
				drug[ny][nx] = c;
				board[ny][nx] = 0;
			}
		}
	
		ans += treeDieCnt;
	}
	
	static void spreadTree() {
		
		int[][] spreadInfo = new int[n][n];
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				
				if (board[i][j] <= 0) continue;
				
				int cnt = 0;
				for (int k = 0; k < 4; k++) {
					int ny = i + dy[k];
					int nx = j + dx[k];
					if (!inRange(ny,nx) || board[ny][nx] != 0 || drug[ny][nx] != 0) continue;
					cnt++;
				}
				
				if (cnt == 0) continue;
				
				int spreadCount = board[i][j] / cnt;
				for (int k = 0; k < 4; k++) {
					int ny = i + dy[k];
					int nx = j + dx[k];
					if (!inRange(ny,nx) || board[ny][nx] != 0 || drug[ny][nx] != 0) continue;
					spreadInfo[ny][nx] += spreadCount;
				}
			}
		}
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				board[i][j] += spreadInfo[i][j];
			}
		}
	}
	
	static void growTree() {

		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				
				if (board[i][j] <= 0) continue;
				
				int cnt = 0;
				for (int k = 0; k < 4; k++) {
					int ny = i + dy[k];
					int nx = j + dx[k];
					if (!inRange(ny,nx) || board[ny][nx] <= 0) continue;
					cnt++;
				}
				
				board[i][j] += cnt;
			}
		}
	}
	
	static void printBoard() {
		
		System.out.println("=================");
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < n && 0 <= x && x < n;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}