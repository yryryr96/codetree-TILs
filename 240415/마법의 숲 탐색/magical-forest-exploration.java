import java.io.*;
import java.util.*;

public class Main {

    static int R,C,K;
    static int[] dy = {-1,0,1,0};
    static int[] dx = {0,1,0,-1};
    static class Pair {
        int y,x,exitDir;
        boolean isExit;

        Pair (int y, int x, int exitDir) {
            this.y = y;
            this.x = x;
            this.exitDir = exitDir;
        }

        Pair(int y, int x, boolean isExit) {
            this.y = y;
            this.x = x;
            this.isExit = isExit;
        }
    }

    static List<Pair> spaceShip = new ArrayList<>();
    static int[][] map;
    static boolean[][] visited;
    static int ans = 0;

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        R = stoi(st.nextToken());
        C = stoi(st.nextToken());
        K = stoi(st.nextToken());
        map = new int[R+3][C];
        visited = new boolean[R+3][C];

        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            int sc = stoi(st.nextToken());
            int exitDir = stoi(st.nextToken());
            spaceShip.add(new Pair(1, sc-1, exitDir));
        }

        simulate();
        System.out.println(ans);
    }

    static void simulate() {

        for (int i = 1; i <= K; i++) {

            Pair ship = spaceShip.get(i-1);

            while(true) {

                boolean temp1 = false, temp2 = false, temp3 = false;
                int beforeY = ship.y;
                int beforeX = ship.x;

                gravity(ship);
                if (beforeY == ship.y && beforeX == ship.x) {
                    temp1 = true;
                } else {
                    continue;
                }

                leftMove(ship);
                if (beforeY == ship.y && beforeX == ship.x) {
                    temp2 = true;
                } else {
                    continue;
                }

                rightMove(ship);
                if (beforeY == ship.y && beforeX == ship.x) {
                    temp3 = true;
                } else {
                    continue;
                }

                if (temp1 && temp2 && temp3) break;
            }

            if (0 <= ship.y && ship.y <= 3) {
                initMap();
                continue;
            }

            map[ship.y][ship.x] = i;
            for (int k = 0; k < 4; k++) {
                map[ship.y + dy[k]][ship.x + dx[k]] = i;
            }

            map[ship.y + dy[ship.exitDir]][ship.x + dx[ship.exitDir]] = -i;
            getAnswer(ship);

        }
    }

    static void getAnswer(Pair ship) {

        initVisited();
        Queue<Pair> q = new LinkedList<>();
        q.add(new Pair(ship.y,ship.x,false));
        visited[ship.y][ship.x] = true;
        int maxScore = ship.y;

        while(!q.isEmpty()) {

            Pair now = q.poll();
            if (maxScore < now.y) {
                maxScore = now.y;
            }

            for (int i = 0; i < 4; i++) {
                int ny = now.y + dy[i];
                int nx = now.x + dx[i];
                if(!inRange(ny,nx) || visited[ny][nx]) continue;

                if (!now.isExit) {

                    if (map[ny][nx] == map[now.y][now.x]) {
                        q.add(new Pair(ny,nx,false));
                        visited[ny][nx] = true;
                    } else if (map[ny][nx] == -map[now.y][now.x]) {
                        q.add(new Pair(ny,nx,true));
                        visited[ny][nx] = true;
                    }
                }
                else {

                    if (map[ny][nx] != 0) {

                        if (map[ny][nx] > 0) {
                            q.add(new Pair(ny,nx,false));
                            visited[ny][nx] = true;
                        } else {
                            q.add(new Pair(ny,nx,true));
                            visited[ny][nx] = true;
                        }
                    }
                }
            }
        }

        int score = maxScore - 2;
        ans += score;
    }

    static void initMap() {

        for (int i = 0; i < R+3; i++) {
            for (int j = 0; j < C; j++) {
                map[i][j] = 0;
            }
        }
    }

    static void initVisited() {

        for (int i = 0; i < R+3; i++) {
            for (int j = 0; j < C; j++) {
                visited[i][j] = false;
            }
        }
    }

    static void gravity(Pair ship) {

        int y = ship.y;
        int x = ship.x;

        if (checkRange(y+1, x)) {
            ship.y = y + 1;
            ship.x = x;
            return;
        }
    }

    static void rightMove(Pair ship) {

        int y = ship.y;
        int x = ship.x;
        if (checkRange(y, x + 1) && checkRange(y + 1, x + 1)) {
            ship.y = y + 1;
            ship.x = x + 1;
            ship.exitDir = (ship.exitDir + 1) % 4;
        }
    }

    static void leftMove(Pair ship) {

        int y = ship.y;
        int x = ship.x;
        if (checkRange(y, x - 1) && checkRange(y + 1, x - 1)) {
            ship.y = y + 1;
            ship.x = x - 1;
            ship.exitDir = (ship.exitDir + 3) % 4;
        }
    }

    static boolean checkRange(int y, int x) {

        for (int i = 0; i < 4; i++) {
            int ny = y + dy[i];
            int nx = x + dx[i];
            if (!inRange(ny,nx) || map[ny][nx] != 0) return false;
        }

        return true;
    }

    static boolean inRange(int y, int x) {
        return 0 <= y && y < R+3 && 0 <= x && x < C;
    }

    static int stoi(String s) {
        return Integer.parseInt(s);
    }
}