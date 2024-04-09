import java.io.*;
import java.util.*;

public class Main {

    static int n,m;
    static class Pair {
        int y,x;
        Pair (int y, int x) {
            this.y = y;
            this.x = x;
        }
    }
    static Pair[] markets;
    static int[][] board;
    static int[] dy = {-1,0,0,1};
    static int[] dx = {0,-1,1,0};

    static HashMap<Integer, Pair> people = new HashMap<>();
    static boolean[] isLive;
    static int outCnt = 0;

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = stoi(st.nextToken());
        m = stoi(st.nextToken());
        markets = new Pair[n+1];
        board = new int[n][n];
        isLive = new boolean[n+1];

        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                board[i][j] = stoi(st.nextToken());
            }
        }

        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(br.readLine());
            int y = stoi(st.nextToken()) - 1;
            int x = stoi(st.nextToken()) - 1;
            markets[i] = new Pair(y,x);
        }

        int time = 1;
        while (true) {
            simulate(time);
            if (outCnt == m) break;
//            if (time == 5) break;
            time++;
        }

        System.out.println(time);
    }

    static void simulate(int time) {

//        System.out.println("time = " + time);
        move();
        if (time <= m) {
            Pair baseCamp = findBaseCamp(time);
            people.put(time, new Pair(baseCamp.y, baseCamp.x));
            board[baseCamp.y][baseCamp.x] = -1;
            isLive[time] = true;
        }

//        System.out.println(Arrays.toString(isLive));
        initMarket();

//        if (time >= 2) System.out.println(people.get(2).y + " " + people.get(2).x);
    }

    static void initMarket() {
        for (int i = 1; i <= n ; i++) {
            if (!isLive[i]) continue;

            Pair person = people.get(i);
            Pair target = markets[i];
            if (person.y == target.y && person.x == target.x) {
                board[person.y][person.x] = -1;
                isLive[i] = false;
                outCnt++;
            }
        }
    }

    static void move() {

        for (int i = 1; i <= n; i++) {
            if (!isLive[i]) continue;
            Pair person = people.get(i);

            Queue<Pair> q = new LinkedList<>();
            q.add(person);
            Pair[][] visited = new Pair[n][n];
            visited[person.y][person.x] = person;

            while (!q.isEmpty()) {

                Pair now = q.poll();
                if (now.y == markets[i].y && now.x == markets[i].x) {
                    break;
                }

                for (int k = 0; k < 4; k++) {
                    int ny = now.y + dy[k];
                    int nx = now.x + dx[k];
                    if (!isInRange(ny,nx) || visited[ny][nx] != null) continue;
                    if (board[ny][nx] == -1) continue;

                    visited[ny][nx] = new Pair(now.y, now.x);
                    q.add(new Pair(ny,nx));
                }
            }

            q.clear();
            q.add(new Pair(markets[i].y, markets[i].x));

            while(!q.isEmpty()) {

                Pair now = q.poll();
                Pair before = visited[now.y][now.x];
                if (before.y == person.y && before.x == person.x) {
                    person.y = now.y;
                    person.x = now.x;
                    break;
                }

                q.add(visited[now.y][now.x]);
            }
        }
    }

    static Pair findBaseCamp(int number) {

        List<int[]> baseCamps = new ArrayList<>();
        Pair market = markets[number];
        int[][] visited = new int[n][n];
        Queue<Pair> q = new LinkedList<>();

        q.add(market);
        visited[market.y][market.x] = 1;
        while(!q.isEmpty()) {

            Pair now = q.poll();
            if (board[now.y][now.x] == 1) {
                baseCamps.add(new int[]{visited[now.y][now.x], now.y, now.x});
            }

            for (int i = 0; i < 4; i++) {
                int ny = now.y + dy[i];
                int nx = now.x + dx[i];
                if (!isInRange(ny,nx) || board[ny][nx] == -1) continue;
                if (visited[ny][nx] != 0) continue;

                visited[ny][nx] = visited[now.y][now.x] + 1;
                q.add(new Pair(ny,nx));
            }
        }

        Collections.sort(baseCamps, (a,b) -> {
            if (a[0] != b[0]) return a[0] - b[0];
            else {
                if (a[1] != b[1]) return a[1] - b[1];
                else {
                    return a[2] - b[2];
                }
            }
        });

        // 거리, 행, 열
        int[] baseCampInfo = baseCamps.get(0);
        return new Pair(baseCampInfo[1], baseCampInfo[2]);
    }

    static boolean isInRange(int y, int x) {
        return 0 <= y && y < n && 0 <= x && x < n;
    }

    static int stoi(String s) {
        return Integer.parseInt(s);
    }
}