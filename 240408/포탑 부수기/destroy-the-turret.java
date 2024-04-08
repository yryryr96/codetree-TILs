import java.io.*;
import java.util.*;

public class Main {

    static int N,M,K;
    static int[][] map, turn;
    static class Pair {
        int y, x, power;
        Pair (int y, int x, int power) {
            this.y = y;
            this.x = x;
            this.power = power;
        }

        Pair (int y, int x) {
            this.y = y;
            this.x = x;
        }
    }

    static int[] dy = {0,1,0,-1,1,1,-1,-1};
    static int[] dx = {1,0,-1,0,1,-1,1,-1};
    static boolean[][] affected;

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = stoi(st.nextToken());
        M = stoi(st.nextToken());
        K = stoi(st.nextToken());
        map = new int[N][M];
        turn = new int[N][M];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                map[i][j] = stoi(st.nextToken());
            }
        }

        for (int i = 1; i <= K; i++) {
            simulate(i);
            if (getLiveTowerCnt() == 1) break;
        }

        System.out.println(getAnswer());
    }

    static int getAnswer() {

        int ans = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                ans = Math.max(ans, map[i][j]);
            }
        }

        return ans;
    }

    static void simulate(int t) {

        List<Pair> liveTurrets = findLiveTurrets();
        Pair attacker = liveTurrets.get(0);
        Pair target = liveTurrets.get(liveTurrets.size()-1);

        affected = new boolean[N][M];
        if (!laserAttack(attacker, target)) throwAttack(attacker, target);
        turn[attacker.y][attacker.x] = t;
        repair();
    }

    static void repair() {

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (map[i][j] > 0 && !affected[i][j]) map[i][j]++;
                else if (map[i][j] < 0) map[i][j] = 0;
            }
        }
    }

    static int getLiveTowerCnt() {

        int cnt = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (map[i][j] > 0) cnt++;
            }
        }

        return cnt;
    }

    static boolean laserAttack(Pair attacker, Pair target) {

        Queue<Pair> q = new LinkedList<>();
        q.add(attacker);
        Pair[][] visited = new Pair[N][M];
        visited[attacker.y][attacker.x] = attacker;
        boolean temp = false;
        while(!q.isEmpty()) {

            Pair now = q.poll();
            if (now.y == target.y && now.x == target.x) {
                map[now.y][now.x] -= attacker.power;
                temp = true;
                break;
            }

            for (int k = 0; k < 4; k++) {

                int ny = (now.y + dy[k] + N) % N;
                int nx = (now.x + dx[k] + M) % M;
                if (map[ny][nx] > 0 && visited[ny][nx] == null) {
                    visited[ny][nx] = new Pair(now.y, now.x);
                    q.add(new Pair(ny,nx));
                }
            }
        }

        if (temp) {
            q.clear();
            q.add(visited[target.y][target.x]);
            affected[target.y][target.x] = true;

            while(!q.isEmpty()) {

                Pair now = q.poll();
                affected[now.y][now.x] = true;
                if (now.y == attacker.y && now.x == attacker.x) continue;
                map[now.y][now.x] -= (int) attacker.power/2;
                Pair before = visited[now.y][now.x];
                q.add(before);
            }
        }

        return temp;
    }

    static void throwAttack(Pair attacker, Pair target) {

        map[target.y][target.x] -= attacker.power;

        affected[attacker.y][attacker.x] = true;
        affected[target.y][target.x] = true;

        for (int i = 0; i < 8; i++) {
            int ny = (target.y + dy[i] + N) % N;
            int nx = (target.x + dx[i] + M) % M;
            if (ny == attacker.y && nx == attacker.x) continue;

            if (map[ny][nx] > 0) {
                map[ny][nx] -= (int) (attacker.power/2);
                affected[ny][nx] = true;
            }
        }
    }

    static List<Pair> findLiveTurrets() {

        List<Pair> liveTurrets = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (map[i][j] > 0) liveTurrets.add(new Pair(i,j, map[i][j]));
            }
        }

        Collections.sort(liveTurrets, (a,b) -> {
            if (map[a.y][a.x] != map[b.y][b.x]) return map[a.y][a.x] - map[b.y][b.x];
            else {
                if (turn[a.y][a.x] != turn[b.y][b.x]) return turn[b.y][b.x] - turn[a.y][a.x];
                else {
                    if ((a.y + a.x) != (b.y + b.x)) return (b.y + b.x) - (a.y + a.x);
                    else {
                        return b.x - a.x;
                    }
                }
            }
        });

        Pair attacker = liveTurrets.get(0);
        map[attacker.y][attacker.x] += (N+M);
        attacker.power += (N+M);

        liveTurrets.set(0, attacker);
        return liveTurrets;
    }

    static int stoi(String s) {
        return Integer.parseInt(s);
    }
}