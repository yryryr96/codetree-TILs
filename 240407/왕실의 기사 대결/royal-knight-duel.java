import java.io.*;
import java.util.*;

public class Main {

    static int L,N,Q;
    static int[][] board;
    static class Night {
        int number,r,c,h,w,k,damage;
        boolean live;
        Night (int number, int r, int c, int h, int w, int k, int damage, boolean live) {
            this.number = number;
            this.r = r;
            this.c = c;
            this.h = h;
            this.w = w;
            this.k = k;
            this.damage = damage;
            this.live = live;
        }
    }

    static int[] dy = {-1,0,1,0};
    static int[] dx = {0,1,0,-1};
    static int[][] visited;
    static List<int[]> waiting;
    static HashMap<Integer, Night> nights = new HashMap<>();
    static HashMap<Integer, Integer> candidates = new HashMap<>();
    static List<Pair> findPairs =  new ArrayList<>();

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        L = stoi(st.nextToken());
        N = stoi(st.nextToken());
        Q = stoi(st.nextToken());

        board = new int[L][L];
        visited = new int[L][L];

        for (int i = 0; i < L; i++) {
            String[] input = br.readLine().split(" ");
            for (int j = 0; j < L; j++) {
                board[i][j] = stoi(input[j]);
            }
        }

        for (int i = 1; i <= N; i++) {
            int r,c,h,w,k;

            st = new StringTokenizer(br.readLine());
            r = stoi(st.nextToken());
            c = stoi(st.nextToken());
            h = stoi(st.nextToken());
            w = stoi(st.nextToken());
            k = stoi(st.nextToken());

            r--;
            c--;

            for (int j = r; j < r+h; j++) {
                for (int l = c; l < c+w; l++) {
                    visited[j][l] = i;
                }
            }

            nights.put(i, new Night(i, r,c,h,w,k,0,true));
        }

//        for (int i = 0; i < L; i++) {
//            System.out.println(Arrays.toString(visited[i]));
//        }

        for (int i = 0; i < Q; i++) {
            int id, d;
            st = new StringTokenizer(br.readLine());
            id = stoi(st.nextToken());
            d = stoi(st.nextToken());
            simulate(i+1, id, d);
        }
        System.out.println(getAnswer());
    }

    static int getAnswer() {

        int ans = 0;
        for (Integer key : nights.keySet()) {
            Night night = nights.get(key);
            if (night.live) ans += night.damage;
        }

        return ans;
    }

    static void simulate(int depth, int id, int d) {

//        System.out.println("depth = " + depth);
        Night night = nights.get(id);
        if (!night.live) return;
        if(!getCandidate(night, d)) return;

        int size = candidates.size();
        waiting = new ArrayList<>();

        for (Integer key : candidates.keySet()) {
            Night n = nights.get(key);
            if (!canMove(night.number, n,d)) return;
        }

        for (int[] wait : waiting) {
            candidates.put(wait[0], wait[1]);
        }

        if (size != candidates.size()) {
            int before = size;
            while (true) {
                waiting = new ArrayList<>();
                for (Integer key : candidates.keySet()) {
                    Night n = nights.get(key);
                    if (!canMove(night.number, n,d)) return;
                }

                if (waiting.size() == 0) break;

                for (int[] wait : waiting) {
                    candidates.put(wait[0], wait[1]);
                }

                before = candidates.size();
            }
        }

        List<int[]> moveCandidate = new ArrayList<>();
        for (Integer key : candidates.keySet()) {
//            System.out.println("key = " + key);
            Integer distance = candidates.get(key);
            moveCandidate.add(new int[]{key, distance});
        }
        
        Collections.sort(moveCandidate, (a,b) -> b[1] - a[1]);
        for (int[] candi : moveCandidate) {
            move(nights.get(candi[0]), d);
        }

        for (int[] candi : moveCandidate) {
            if (candi[0] == night.number) continue;
            receiveDamage(nights.get(candi[0]));
        }


//        for (int i = 0; i < L; i++) {
//            System.out.println(Arrays.toString(visited[i]));
//        }
//
//        System.out.println("getAnswer() = " + getAnswer());
////
//        for (int i = 1; i <= 2; i++) {
//            System.out.println(i + " " + nights.get(i).damage);
//        }

//        System.out.println(nights.get(7).k);
    }

    static boolean getCandidate(Night night, int d) {

        candidates.clear();
        List<Pair> pairs = getPairs(night, d);
        for (Pair pair : pairs) {
            int y = pair.y;
            int x = pair.x;
//            System.out.println("y = " + y + " x = " + x);
            while (isInRange(y,x)) {

                if (visited[y][x] == 0) break;
                if (board[y][x] == 2) return false;

                if (!candidates.containsKey(visited[y][x])) {
                    candidates.put(visited[y][x], Math.abs(pair.y - y) + Math.abs(pair.x - x));
                }

                y += dy[d];
                x += dx[d];
            }

            if (!isInRange(y,x)) return false;
        }

        return true;
    }

    static List<Pair> getPairs(Night night, int d) {

        List<Pair> pairs = new ArrayList<>();

        if (d == 0) {
            for (int j = night.c; j < night.c + night.w; j++) {
                pairs.add(new Pair(night.r, j));
            }
        } else if (d == 1) {
            for (int i = night.r; i < night.r + night.h; i++) {
                pairs.add(new Pair(i, night.c + night.w - 1));
            }
        } else if (d == 2) {
            for (int j = night.c; j < night.c + night.w; j++) {
                pairs.add(new Pair(night.r + night.h - 1, j));
            }
        } else {
            for (int i = night.r; i < night.r + night.h; i++) {
                pairs.add(new Pair(i, night.c));
            }
        }

        return pairs;
    }

    static boolean canMove(int num, Night night, int d) {
//        System.out.println("night.number = " + night.number);
        List<Pair> pairs = getPairs(night, d);
        for (Pair pair : pairs) {
            int ny = pair.y + dy[d];
            int nx = pair.x + dx[d];
            if (isInRange(ny,nx) && visited[ny][nx] != 0 && !candidates.containsKey(visited[ny][nx])) {
//                System.out.println("ny = " + ny + " nx = " + nx);
//                System.out.println("visited[ny][nx] = " + visited[ny][nx]);
                int distance;
                if (d == 0 || d == 2) {
                    distance = Math.abs(nights.get(num).r - ny);
                } else {
                    distance = Math.abs(nights.get(num).c - nx);
                }

                waiting.add(new int[]{visited[ny][nx], distance});
            }

            if (!isInRange(ny,nx) || board[ny][nx] == 2) return false;
        }

        return true;
    }

    static void move(Night night, int d) {

//        System.out.println("night.number = " + night.number);
        for (int i = night.r; i < night.r + night.h; i++) {
            for (int j = night.c; j < night.c + night.w; j++) {
                visited[i + dy[d]][j + dx[d]] = night.number;
//                System.out.println("(i+dy[d]) = " + (i + dy[d]) + " (j+dy[d]) = " + (j+dy[d]));
            }
        }

        if (d == 0) {
            for (int i = night.c; i < night.c + night.w; i++) {
                visited[night.r + night.h - 1][i] = 0;
            }
        } else if (d == 1) {
            for (int i = night.r; i < night.r + night.h; i++) {
                visited[i][night.c] = 0;
            }
        } else  if (d == 2) {
            for (int i = night.c; i < night.c + night.w; i++) {
                visited[night.r][i] = 0;
            }
        } else {
            for (int i = night.r; i < night.r + night.h; i++) {
                visited[i][night.c + night.w - 1] = 0;
            }
        }

        night.r = night.r + dy[d];
        night.c = night.c + dx[d];

//        //
//        for (int i = 0; i < L; i++) {
//            System.out.println(Arrays.toString(visited[i]));
//        }
//        System.out.println("===============");
    }

    static void receiveDamage(Night night) {

        int damage = 0;
        for (int i = night.r; i < night.r + night.h; i++) {
            for (int j = night.c; j < night.c + night.w; j++) {
                if (board[i][j] == 1) damage++;
            }
        }

        night.k -= damage;
        night.damage += damage;
        if (night.k <= 0) {
            night.live = false;
            for (int i = night.r; i < night.r + night.h; i++) {
                for (int j = night.c; j < night.c + night.w; j++) {
                    visited[i][j] = 0;
                }
            }
        }
        return;
    }

    static boolean isInRange(int i, int j) {
        return 0 <= i && 0 <= j && i < L && j < L;
    }

    static int stoi(String s) {
        return Integer.parseInt(s);
    }

    static class Pair {
        int y,x;
        Pair (int y, int x) {
            this.y = y;
            this.x = x;
        }
    }
}