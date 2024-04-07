import java.io.*;
import java.util.*;

public class Main {

    static int N,M,P,C,D;
    static int ry,rx;
    static class Node {
        int number,y,x,dir, score, stunTime;
        boolean stun, die;

        Node(int number, int y, int x, int dir, boolean stun, boolean die, int score, int stunTime) {
            this.number = number;
            this.y = y;
            this.x = x;
            this.dir = dir;
            this.stun = stun;
            this.die = die;
            this.score = score;
            this.stunTime = stunTime;
        }

        Node (int y, int x, int dir) {
            this.y = y;
            this.x = x;
            this.dir = dir;
        }
    }

    static Node roudolf;
    static List<Node> santas = new ArrayList<>();
    static int[] dy = {0,1,0,-1,1,1,-1,-1};
    static int[] dx = {-1,0,1,0,1,-1,1,-1};
    static int[][] visited;

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = stoi(st.nextToken());
        M = stoi(st.nextToken());
        P = stoi(st.nextToken());
        C = stoi(st.nextToken());
        D = stoi(st.nextToken());
        visited = new int[N][N];

        st = new StringTokenizer(br.readLine());
        ry = stoi(st.nextToken());
        rx = stoi(st.nextToken());
        roudolf = new Node(ry-1, rx-1, 0);
        santas.add(new Node(0,0,0));

        for (int i = 0; i < P; i++) {
            st = new StringTokenizer(br.readLine());
            int num = stoi(st.nextToken());
            int sy = stoi(st.nextToken());
            int sx = stoi(st.nextToken());
            visited[sy-1][sx-1] = num;
            santas.add(new Node(num, sy-1, sx-1, 0, false, false, 0, 0));
        }

//        System.out.println("roudolf = " + roudolf.y + " roudolf.x = " + roudolf.x);
//        for (int i = 0; i < N; i++) {
//            System.out.println(Arrays.toString(visited[i]));
//        }
//
        StringBuilder sb = new StringBuilder();
//        for (int i = 1; i < santas.size(); i++) {
//            sb.append(santas.get(i).score + " ");
//        }

        int time = 1;
        while (time <= M) {

            Collections.sort(santas, (a,b) -> a.number - b.number);
            roudolfMove(time);
            santaMove(time);
            overTurnAddScore();
            if(getDeathCount() == P) break;
//
//            System.out.println("roudolf = " + roudolf.y + " roudolf.x = " + roudolf.x);
//            for (int i = 0; i < N; i++) {
//                System.out.println(Arrays.toString(visited[i]));
//            }
//
//            sb = new StringBuilder();
//            for (int i = 1; i < santas.size(); i++) {
//                sb.append(santas.get(i).score + " ");
//            }
//
//            System.out.println(sb.toString());
//            System.out.println("===================" + time);

            time++;
        }

        sb = new StringBuilder();
        for (int i = 1; i < santas.size(); i++) {
            sb.append(santas.get(i).score + " ");
        }

        System.out.println(sb.toString());
    }

    static void roudolfMove(int t) {

        PriorityQueue<int[]> q = new PriorityQueue<>((a, b) -> {
            if (a[0] < b[0]) return -1;
            else if (a[0] > b[0]) return 1;
            else {
                if (a[1] < b[1]) return 1; // 두 번째 요소 중 큰 값으로 정렬
                else if (a[1] > b[1]) return -1;
                else {
                    return b[2] - a[2]; // 세 번째 요소 중 큰 값으로 정렬
                }
            }
        });

        for (int i = 1; i < santas.size(); i++) {

            Node santa = santas.get(i);
            if (santa.die) continue;

            int distance = getDistance(santa, roudolf);
            q.add(new int[]{distance, santa.y, santa.x});
        }

        int k = 0;
        int dist = Integer.MAX_VALUE;
        int[] target = q.poll();

        for (int i = 0; i < 8; i++) {
            int ny = roudolf.y + dy[i];
            int nx = roudolf.x + dx[i];
            if (0 > ny || 0 > nx || ny >= N || nx >= N) continue;
            if (Math.pow(ny - target[1],2) + Math.pow(nx - target[2],2) < dist) {
                dist = (int) (Math.pow(ny - target[1],2) + Math.pow(nx - target[2],2));
                k = i;
            }
        }

        roudolf.y = roudolf.y + dy[k];
        roudolf.x = roudolf.x + dx[k];
        roudolf.dir = k;

        if (visited[roudolf.y][roudolf.x] != 0) {

            int dir = roudolf.dir;
            Node santa = santas.get(visited[roudolf.y][roudolf.x]);
            santa.score += C;
            int ny = santa.y + dy[dir]*C;
            int nx = santa.x + dx[dir]*C;

            if (!isInRange(ny, nx)) {
                santa.die = true;
                visited[santa.y][santa.x] = 0;
            } else {
                if (visited[ny][nx] != 0) {
                    int firstY = ny;
                    int firstX = nx;
                    int lastY = firstY;
                    int lastX = firstX;

                    while(isInRange(lastY, lastX) && visited[lastY][lastX] != 0) {
                        lastY += dy[dir];
                        lastX += dx[dir];
                    }

                    if (!isInRange(lastY, lastX)) {
                        Node s = santas.get(visited[lastY - dy[dir]][lastX - dx[dir]]);
                        s.die = true;
                        lastY -= dy[dir];
                        lastX -= dx[dir];
                    }

                    while (isInRange(lastY, lastX) && (lastY != firstY || lastX != firstX)) {
                        visited[lastY][lastX] = visited[lastY - dy[dir]][lastX - dx[dir]];
                        Node s = santas.get(visited[lastY][lastX]);
                        s.y = lastY; s.x = lastX;
                        lastY -= dy[dir];
                        lastX -= dx[dir];
                    }
                }

                visited[roudolf.y][roudolf.x] = 0;
                santa.y = ny;
                santa.x = nx;
                visited[ny][nx] = santa.number;
                santa.stun = true;
                santa.stunTime = t + 2;
            }
        }
    }

    static void santaMove(int t) {

        // 스턴 확인
        for (int i = 1; i < santas.size(); i++) {

            Node santa = santas.get(i);
            if (santa.stun && santa.stunTime == t) {
                santa.stun = false;
                santa.stunTime = 0;
            }
        }

        for (int i = 1; i < santas.size(); i++) {
            Node santa = santas.get(i);
            if (santa.stun || santa.die) continue;
            int dir = getDirection(santa);
            if (dir == -1) continue;
            int ny = santa.y + dy[dir];
            int nx = santa.x + dx[dir];
            // 충돌
            boolean collision = false;
            if (roudolf.y == ny && roudolf.x == nx) {
                santa.score += D;
                dir = (dir+2) % 4;
                ny = ny + dy[dir]*D;
                nx = nx + dx[dir]*D;

                if (!isInRange(ny,nx)) {
                    santa.die = true;
                    visited[santa.y][santa.x] = 0;
                    continue;
                }

                collision = true;
                santa.stun = true;
                santa.stunTime = t+2;
            }

            if (collision) {

                if (visited[ny][nx] != 0 && D != 1) {
                    int firstY = ny;
                    int firstX = nx;
                    int lastY = firstY;
                    int lastX = firstX;
                    while(isInRange(lastY, lastX) && visited[lastY][lastX] != 0) {
                        lastY += dy[dir];
                        lastX += dx[dir];
                    }

                    if (!isInRange(lastY, lastX)) {
                        santas.get(visited[lastY - dy[dir]][lastX - dx[dir]]).die = true;
                        lastY -= dy[dir];
                        lastX -= dx[dir];
                    }

                    while (isInRange(lastY, lastX) && (lastY != firstY || lastX != firstX)) {

                        visited[lastY][lastX] = visited[lastY - dy[dir]][lastX - dx[dir]];
                        Node s = santas.get(visited[lastY][lastX]);
                        s.y = lastY; s.x = lastX;
                        lastY -= dy[dir];
                        lastX -= dx[dir];
                    }
                }
            }

            visited[santa.y][santa.x] = 0;
            visited[ny][nx] = santa.number;
            santa.y = ny;
            santa.x = nx;
            santa.dir = dir;
        }
    }

    static void overTurnAddScore() {

        for (Node santa : santas) {
            if (!santa.die) {
                santa.score++;
            }
        }
    }

    static int getDirection(Node santa) {

        int distance = getDistance(santa, roudolf);
        int dir = -1;
        for (int k = 0; k < 4; k++) {
            int ny = santa.y + dy[k];
            int nx = santa.x + dx[k];
            if (0 > ny || 0 > nx || ny >= N || nx >= N || visited[ny][nx] != 0) continue;

            int dist = (int) (Math.pow(ny - roudolf.y,2) + Math.pow(nx - roudolf.x, 2));
            if (dist <= distance) {
                distance = dist;
                dir = k;
            }
        }

        return dir;
    }

    static boolean isInRange(int i, int j) {
        return 0 <= i && 0 <= j && i < N && j < N;
    }

    static int stoi(String s) {
        return Integer.parseInt(s);
    }

    static int getDistance(Node node1, Node node2) {
        return (int) (Math.pow(node1.y - node2.y, 2) + Math.pow(node1.x - node2.x, 2));
    }

    static int getDeathCount() {

        int cnt = 0;
        for (int i = 1; i < santas.size(); i++) {
            if (santas.get(i).die) cnt++;
        }

        return cnt;
    }
}