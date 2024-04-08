import java.io.*;
import java.util.*;

public class Main {

    static int N,M,K;
    static int[] dy = {0,0,1,-1};
    static int[] dx = {1,-1,0,0};
    static int outCnt = 0;
    static class Person {
        int y,x,d;
        boolean isOut;

        Person(int y, int x) {
            this.y = y;
            this.x = x;
            this.isOut = false;
            this.d = 0;
        }
    }

    static int[][] maze;
    static List<Person> people = new ArrayList<>();
    static class Pair {
        int y, x;
        Pair (int y, int x) {
            this.y = y;
            this.x = x;
        }
    }

    static Pair exit;
    static boolean[][] visited;
    static PriorityQueue<int[]> q;

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = stoi(st.nextToken());
        M = stoi(st.nextToken());
        K = stoi(st.nextToken());
        maze = new int[N][N];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                maze[i][j] = stoi(st.nextToken());
            }
        }

        people.add(new Person(-100,-100));
        // 참가자 좌표
        for (int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine());
            int a = stoi(st.nextToken());
            int b = stoi(st.nextToken());

            people.add(new Person(a-1, b-1));
        }

        // 출구 좌표 초기화
        st = new StringTokenizer(br.readLine());
        int a = stoi(st.nextToken()) - 1;
        int b = stoi(st.nextToken()) - 1;
        exit = new Pair(a,b);
        maze[a][b] = -20; // 출구

        while (K-- > 0) {

//            System.out.println("K = " + K);
            move();
//            for (int i = 0; i < N; i++) {
//                System.out.println(Arrays.toString(maze[i]));
//            }

             if (outCnt == M) break;
        }

        int ans = 0;
        for (Person person : people) {
            ans += person.d;
        }

        System.out.println(ans);
        System.out.println((exit.y + 1) + " " + (exit.x + 1));
    }

    static void move() {

        for (int i = 0; i < people.size(); i++) {

            Person person = people.get(i);
            // 이미 나간사람 건너뛰기
            if (person.isOut) continue;

            int y = person.y;
            int x = person.x;
            int dir = getDirection(y, x);
            if (dir == -1) continue;
            person.y = person.y + dy[dir];
            person.x = person.x + dx[dir];
            person.d++;
            // 출구 도착하면 아웃

            if (person.y == exit.y && person.x == exit.x) {
                person.isOut = true;
                outCnt++;

                if (outCnt == M) return;
            }
        }

        q = new PriorityQueue<>((a,b) -> {
            if (a[0] != b[0]) return a[0] - b[0];
            else {
                if (a[1] != b[1]) return a[1] - b[1];
                else {
                    return a[2] - b[2];
                }
            }
        });
        
        // 정사각형 조사
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                getSquare(i,j);
            }
        }
        
        // 정사각형
        int[] square = q.poll();
        int size = square[0], r = square[1], c = square[2];
        rotate(r,c, size);
    }

    static boolean isAffected(int r, int c, int size, int py, int px) {

        return r <= py && py < r + size && c <= px && px < c + size;
    }

    static void getSquare(int r, int c) {
        for (int i = 2; i <= N-1; i++) {
            if (!isInRange(r + i -1, c + i -1)) continue;
            if (isInExit(r,c,r + i -1, c + i - 1) && isInPerson(r,c,r + i -1, c + i - 1)) {
                q.add(new int[]{i,r,c});
                return;
            }
        }
    }

    static boolean isInExit(int y1, int x1, int y2, int x2) {
        return y1 <= exit.y && exit.y <= y2 && x1 <= exit.x && exit.x <= x2;
    }

    static boolean isInPerson(int y1, int x1, int y2, int x2) {

        for (Person person : people) {
            if (person.isOut) continue;
            if (y1 <= person.y && person.y <= y2 && x1 <= person.x && person.x <= x2) return true;
        }

        return false;
    }

    static int getDirection(int y, int x) {

        int distance = getDistance(y,x,exit.y, exit.x);
        int dir = -1;

        for (int i = 0; i < 4; i++) {

            int ny = y + dy[i];
            int nx = x + dx[i];
            if (!isInRange(ny, nx) || maze[ny][nx] > 0) continue;

            int dist = getDistance(ny, nx, exit.y, exit.x);
            if (dist <= distance) {
                distance = dist;
                dir = i;
            }
        }

        return dir;
    }

    static void rotate(int si, int sj, int size) {

//        System.out.println("si = " + si + " sj = " + sj + " size = " + size);
        int[][] temp = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                temp[j][size-1-i] = maze[si + i][sj + j];
            }
        }

        for (Person person : people) {
            if (person.isOut) continue;
            if (isAffected(si,sj,size,person.y, person.x)) {
                int tmp1 = person.y - si;
                int tmp2 = person.x - sj;
                int y = tmp2;
                int x = size - 1 - tmp1;
                person.y = si + y;
                person.x = sj + x;
            }
        }

        // 벽 내구도 감소
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                maze[si + i][sj + j] = temp[i][j];
                if (maze[si + i][sj + j] >= 1) maze[si + i][sj + j]--;
                else if (maze[si+i][sj+j] == -20) {
                    exit.y = si+i;
                    exit.x = sj+j;
                }
            }
        }

    }

    static boolean isInRange(int y, int x) {
        return 0 <= y && 0 <= x && N > y && N > x;
    }

    static int stoi(String s) {
        return Integer.parseInt(s);
    }

    static int getDistance(int y1, int x1, int y2, int x2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}