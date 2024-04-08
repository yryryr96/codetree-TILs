import java.io.*;
import java.util.*;

public class Main {

    static int N,M,K;
    static int[][] map, turn;
    static boolean[][] live;
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
        live = new boolean[N][M];

        // 살아있는 포탑 초기화
        for (int i = 0; i < N; i++) {
            Arrays.fill(live[i], true);
        }

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                map[i][j] = stoi(st.nextToken());
                if (map[i][j] == 0) live[i][j] = false;
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
//        System.out.println("t = " + t);
        Pair attacker = selectAttacker();
        Pair target = selectTarget(attacker);

//        System.out.println("attacker.y = " + attacker.y + " attacker.x = " + attacker.x + " power = " + attacker.power);
//        System.out.println("target.y = " + target.y + " target.x = " + target.x);
        affected = new boolean[N][M];
        if (!laserAttack(attacker, target)) throwAttack(attacker, target);
        turn[attacker.y][attacker.x] = t;
        repair();
//
//        for (int i = 0; i < N; i++) {
//            System.out.println(Arrays.toString(map[i]));
//        }
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
//            System.out.println("now.y = " + now.y + " now.x = " + now.x);
            if (now.y == target.y && now.x == target.x) {
                map[now.y][now.x] -= attacker.power;
                temp = true;
                break;
            }

            for (int k = 0; k < 4; k++) {
                int ny = now.y + dy[k];
                int nx = now.x + dx[k];

                if (ny >= N) ny = 0;
                else if (ny < 0) ny = N-1;
                else if (nx >= M) nx = 0;
                else if (nx < 0) nx = M-1;
//                System.out.println("ny = " + ny + " nx = " + nx);
//                System.out.println("visited[ny][nx] = " + visited[ny][nx]);
                if (map[ny][nx] > 0 && visited[ny][nx] == null) {
                    visited[ny][nx] = new Pair(now.y, now.x);
                    q.add(new Pair(ny,nx));
                }
            }
        }

        if (temp) {
//            System.out.println("laser");
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

//        System.out.println("throw");
        map[target.y][target.x] -= attacker.power;

        affected[attacker.y][attacker.x] = true;
        affected[target.y][target.x] = true;

        for (int i = 0; i < 8; i++) {
            int ny = target.y + dy[i];
            int nx = target.x + dx[i];

            if (ny >= N && nx >= M) {
                ny = 0; nx = 0;
            }
            else if (ny >= N && nx < 0) {
                ny = 0; nx = M-1;
            }
            else if (ny < 0 && nx >= M) {
                ny = N-1; nx = 0;
            }
            else if(ny < 0 && nx < 0) {
                ny = N-1; nx = M-1;
            }
            else if (ny >= N) ny = 0;
            else if (ny < 0) ny = N-1;
            else if (nx >= M) nx = 0;
            else if (nx < 0) nx = M-1;
            if (ny == attacker.y && nx == attacker.x) continue;

            if (map[ny][nx] > 0) {
//                System.out.println("ny = " + ny + " nx = " + nx);
                map[ny][nx] -= (int) (attacker.power/2);
                affected[ny][nx] = true;
            }
        }
    }

    static Pair selectAttacker() {

        List<Pair> attackers = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (map[i][j] > 0) attackers.add(new Pair(i,j, map[i][j]));
            }
        }

        Collections.sort(attackers, (a,b) -> {
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

        Pair attacker = attackers.get(0);
        map[attacker.y][attacker.x] += (N+M);
        attacker.power += (N+M);

        return attacker;
    }

    static Pair selectTarget(Pair attacker) {

        List<Pair> targets = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (i == attacker.y && j == attacker.x) continue;
                if (map[i][j] > 0) targets.add(new Pair(i,j, map[i][j]));
            }
        }

        Collections.sort(targets, (a,b) -> {
            if (map[a.y][a.x] != map[b.y][b.x]) return map[b.y][b.x] -  map[a.y][a.x];
            else {
                if (turn[a.y][a.x] != turn[b.y][b.x]) return turn[a.y][a.x] - turn[b.y][b.x];
                else {
                    if ((a.y + a.x) != (b.y + b.x)) return (a.y + a.x) - (b.y + b.x);
                    else {
                        return a.x - b.x;
                    }
                }
            }
        });

//        for (int i = 0; i < targets.size(); i++) {
//            System.out.println("targets.get(i).power = " + targets.get(i).power);
//        }
        return targets.get(0);
    }

    static int stoi(String s) {
        return Integer.parseInt(s);
    }
}