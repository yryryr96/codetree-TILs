import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Main {
    static final int N_large = 5; // 고대 문명 전체 격자 크기입니다.
    static final int N_small = 3; // 회전시킬 격자의 크기입니다.

    // 고대 문명 격자를 정의합니다
    static class Board {
        int[][] a = new int[N_large][N_large];

        // 생성자입니다. 격자에 유물조각들을 초기화 합니다.
        public Board() {
            for (int i = 0; i < N_large; i++) {
                for (int j = 0; j < N_large; j++) {
                    a[i][j] = 0;
                }
            }
        }

        // 주어진 y, x가 고대 문명 격자의 범위안에 있는지 확인하는 함수 입니다.
        private boolean inRange(int y, int x) {
            return 0 <= y && y < N_large && 0 <= x && x < N_large;
        }

        // 현재 격자에서 sy, sx를 좌측상단으로 하여 시계방향 90도 회전을 cnt번 시행했을때 결과를 return 합니다.
        public Board rotate(int sy, int sx, int cnt) {
            Board result = new Board();
            for (int i = 0; i < N_large; i++) {
                for (int j = 0; j < N_large; j++) {
                    result.a[i][j] = this.a[i][j];
                }
            }
            for (int k = 0; k < cnt; k++) {
                // sy, sx를 좌측상단으로 하여 시계방향 90도 회전합니다.
                int tmp = result.a[sy + 0][sx + 2];
                result.a[sy + 0][sx + 2] = result.a[sy + 0][sx + 0];
                result.a[sy + 0][sx + 0] = result.a[sy + 2][sx + 0];
                result.a[sy + 2][sx + 0] = result.a[sy + 2][sx + 2];
                result.a[sy + 2][sx + 2] = tmp;
                tmp = result.a[sy + 1][sx + 2];
                result.a[sy + 1][sx + 2] = result.a[sy + 0][sx + 1];
                result.a[sy + 0][sx + 1] = result.a[sy + 1][sx + 0];
                result.a[sy + 1][sx + 0] = result.a[sy + 2][sx + 1];
                result.a[sy + 2][sx + 1] = tmp;
            }
            return result;
        }

        // 현재 격자에서 유물을 획득합니다.
        // 새로운 유물 조각을 채우는것은 여기서 고려하지 않습니다.
        public int calScore() {
            int score = 0;
            boolean[][] visit = new boolean[N_large][N_large];
            int[] dy = {0, 1, 0, -1}, dx = {1, 0, -1, 0};

            for (int i = 0; i < N_large; i++) {
                for (int j = 0; j < N_large; j++) {
                    if (!visit[i][j]) {
                        // BFS를 활용한 Flood Fill 알고리즘을 사용하여 visit 배열을 채웁니다.
                        // 이때 trace 안에 조각들의 위치가 저장됩니다.
                        Queue<int[]> q = new LinkedList<>();
                        Queue<int[]> trace = new LinkedList<>();
                        q.offer(new int[]{i, j});
                        trace.offer(new int[]{i, j});
                        visit[i][j] = true;
                        while (!q.isEmpty()) {
                            int[] cur = q.poll();
                            for (int k = 0; k < 4; k++) {
                                int ny = cur[0] + dy[k], nx = cur[1] + dx[k];
                                if (inRange(ny, nx) && a[ny][nx] == a[cur[0]][cur[1]] && !visit[ny][nx]) {
                                    q.offer(new int[]{ny, nx});
                                    trace.offer(new int[]{ny, nx});
                                    visit[ny][nx] = true;
                                }
                            }
                        }
                        // 위에서 진행된 Flood Fill을 통해 조각들이 모여 유물이 되고 사라지는지 확인힙니다.
                        if (trace.size() >= 3) {
                            // 유물이 되어 사라지는 경우 가치를 더해주고 조각이 비어있음을 뜻하는 0으로 바꿔줍니다.
                            score += trace.size();
                            while (!trace.isEmpty()) {
                                int[] t = trace.poll();
                                a[t[0]][t[1]] = 0;
                            }
                        }
                    }
                }
            }
            return score;
        }

        // 유물 획득과정에서 조각이 비어있는 곳에 새로운 조각을 채워줍니다.
        public void fill(Queue<Integer> que) {
            // 열이 작고 행이 큰 우선순위로 채워줍니다.
            for (int j = 0; j < N_large; j++) {
                for (int i = N_large - 1; i >= 0; i--) {
                    if (a[i][j] == 0 && !que.isEmpty()) {
                        a[i][j] = que.poll();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        // 입력을 받습니다.
        Scanner sc = new Scanner(System.in);
        int K = sc.nextInt();
        int M = sc.nextInt();
        Queue<Integer> q = new LinkedList<>();
        Board board = new Board();

        for (int i = 0; i < N_large; i++) {
            for (int j = 0; j < N_large; j++) {
                board.a[i][j] = sc.nextInt();
            }
        }
        for (int i = 0; i < M; i++) {
            q.offer(sc.nextInt());
        }

        // 최대 K번의 탐사과정을 거칩니다.
        while (K-- > 0) {
            int maxScore = 0;
            Board maxScoreBoard = null;
            // 회전 목표에 맞는 결과를 maxScoreBoard에 저장합니다.
            // (1) 유물 1차 획득 가치를 최대화
            // (2) 회전한 각도가 가장 작은 방법을 선택
            // (3) 회전 중심 좌표의 열이 가장 작은 구간을, 그리고 열이 같다면 행이 가장 작은 구간을 선택
            for (int cnt = 1; cnt <= 3; cnt++) {
                for (int sx = 0; sx <= N_large - N_small; sx++) {
                    for (int sy = 0; sy <= N_large - N_small; sy++) {
                        Board rotated = board.rotate(sy, sx, cnt);
                        int score = rotated.calScore();
                        if (maxScore < score) {
                            maxScore = score;
                            maxScoreBoard = rotated;
                        }
                    }
                }
            }
            // 회전을 통해 더 이상 유물을 획득할 수 없는 경우 탐사를 종료합니다.
            if (maxScoreBoard == null) {
                break;
            }
            board = maxScoreBoard;
            // 유물의 연쇄 획득을 위해 유물 조각을 채우고 유물을 획득하는 과정을 더이상 획득할 수 있는 유물이 없을때까지 반복합니다.
            while (true) {
                board.fill(q);
                int newScore = board.calScore();
                if (newScore == 0) break;
                maxScore += newScore;
            }

            System.out.print(maxScore + " ");
        }
    }
}