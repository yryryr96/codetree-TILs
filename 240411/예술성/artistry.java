import java.util.*;
import java.io.*;

public class Main {
	
	static int n;
	static int[][] board;
	static int ans = 0;
	static int[][] groupVisited;
	static int[] dy = {0,1,0,-1};
	static int[] dx = {1,0,-1,0};
	static class Pair {
		int y,x;
		Pair(int y, int x) {
			this.y = y;
			this.x = x;
		}
	}
	static HashMap<Integer, int[]> groupInfo = new HashMap<>();
	static int groupCnt;
	static List<int[]> combinations = new ArrayList<>();
	static HashMap<Integer, Pair> startPoint = new HashMap<>();
	static List<Pair> starting;
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		n = stoi(br.readLine());
		board = new int[n][n];
		for (int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < n; j++) {
				board[i][j] = stoi(st.nextToken());
			}
		}
		
		starting = getSquareStartPoint();
		for (int i = 1; i <= 4; i++) {
			simulate();
		}
		
		System.out.println(ans);
	}
	
	static void simulate() {
		
		int score = 0;
		combinations = new ArrayList<>();
		
		// 그룹 초기화
		initGroup();
		
		// 조합 구하기
		getGroupCombination(0, 1, new int[2]);
		
		// 점수 구하기
		for (int[] comb : combinations) {
			int s = getScore(comb[0], comb[1]);
			score += s;
		}
		
		// 회전 시키기
		for (Pair start : starting) {
			squareRotate(start.y, start.x);
		}
		
		plusRotate();
		
		// 점수 합쳐주기
		ans += score;
	}
	
	static List<Pair> getSquareStartPoint() {
		
		List<Pair> starting = new ArrayList<>();
		starting.add(new Pair(0,0));
		starting.add(new Pair(0, n/2+1));
		starting.add(new Pair(n/2 + 1,0));
		starting.add(new Pair(n/2 + 1, n/2 + 1));
		
		return starting;
	}
	
	static void squareRotate(int r, int c) {
		
		int[][] temp = new int[n/2][n/2];
		
		for (int i = 0; i < n/2; i++) {
			for (int j = 0; j < n/2; j++) {
				temp[j][n/2-1-i] = board[i + r][j + c];
			}
		}
		
		for (int i = 0; i < n/2; i++) {
			for (int j = 0; j < n/2; j++) {
				board[i + r][j + c] = temp[i][j];
			}
		}
	}
	
	static void plusRotate() {
				
		List<int[]> point = new ArrayList<>();
		
		for (int i = 0; i < n; i++) {
			point.add(new int[] {n-1-n/2, i, board[i][n/2]});
			point.add(new int[] {n-1-i, n/2, board[n/2][i]});
		}
		
		// 대입
		for (int i = 0; i < point.size(); i++) {
			int[] p = point.get(i);
			board[p[0]][p[1]] = p[2];
		}
	}
	
	static int getScore(int group1, int group2) {
		
		int attached = 0;	
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if(groupVisited[i][j] != group1) {continue;}
				for (int k = 0; k < 4; k++) {
					int ny = i + dy[k];
					int nx = j + dx[k];
					if (!inRange(ny,nx)) continue;
					if (groupVisited[ny][nx] == group2) attached++;
				}
			}
		}
		
		int score = 0;
		// a칸 + b칸
		int cnt = groupInfo.get(group1)[1] + groupInfo.get(group2)[1];
		int a = groupInfo.get(group1)[0];
		int b = groupInfo.get(group2)[0];
		score = cnt * a * b * attached;
		return score;
	}
	
	// 조합
	static void getGroupCombination(int depth, int idx, int[] comb) {
		
		if (depth == 2) {
			// 조합 더해주기
			combinations.add(comb.clone());
			return;
		}
		
		for (int i = idx; i <= groupCnt; i++) {
			comb[depth] = i;
			getGroupCombination(depth + 1, i+1, comb);
		}
	}
	
	
	// 그룹 초기화
	static void initGroup() {
		
		int group = 1;
		groupInfo = new HashMap<>();
		startPoint = new HashMap<>();
		groupVisited = new int[n][n];
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (groupVisited[i][j] == 0) {
					bfs(i,j,board[i][j], group);
					startPoint.put(group, new Pair(i,j));
					group++;
				}
			}
		}
		
		groupCnt = group - 1;
	}
	
	static void bfs(int y, int x, int v, int group) {
		
		Queue<Pair> q = new LinkedList<>();
		q.add(new Pair(y,x));
		groupVisited[y][x] = group;
		int cnt = 0;
		while(!q.isEmpty()) {
			
			Pair now = q.poll();
			cnt++;
			
			for (int k = 0; k < 4; k++) {
				int ny = now.y + dy[k];
				int nx = now.x + dx[k];
				if (!inRange(ny,nx) || groupVisited[ny][nx] != 0) continue;
				if (board[ny][nx] == v) {
					groupVisited[ny][nx] = group;
					q.add(new Pair(ny,nx));
				}
			}
		}
		
		// 그룹 번호, 이루고 있는 숫자, 칸수
		groupInfo.put(group, new int[]{v, cnt});
	}
	
	static boolean inRange(int y, int x) {
		return 0 <= y && y < n && 0 <= x && x < n;
	}
	
	static int stoi(String s) {
		return Integer.parseInt(s);
	}
}