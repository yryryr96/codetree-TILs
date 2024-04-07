import java.io.*;
import java.util.*;

public class Main {

    static int N,Q;
    static int[][] parent;
    static int[] authority;
    static boolean[][] related;
    static int ans = 0;

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = stoi(st.nextToken());
        Q = stoi(st.nextToken());
        parent = new int[N+1][2];
        authority = new int[N+1];

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());

            String cmd = st.nextToken();
            if (cmd.equals("100")) {
                for (int j = 1; j <= N; j++) {
                    parent[j] = new int[]{stoi(st.nextToken()), 1};
                }

                for (int j = 1; j <= N; j++) {
                    authority[j] = stoi(st.nextToken());
                }
            }

            else if(cmd.equals("200")) {
                int c = stoi(st.nextToken());
                switchAlarm(c);
            }

            else if (cmd.equals("300")) {

                int c = stoi(st.nextToken());
                int power = stoi(st.nextToken());
                switchPower(c, power);
            }

            else if (cmd.equals("400")) {
                int c1 = stoi(st.nextToken());
                int c2 = stoi(st.nextToken());
                switchParent(c1, c2);
            }

            else {
                int c = stoi(st.nextToken());
                ans = 0;
//                System.out.println("i = " + i);
                for (int j = 1; j <= N ; j++) {
                    getParent(0, j, j, c);
                }

                sb.append(ans).append("\n");
            }
        }

        System.out.println(sb.toString());
    }

    static int getParent(int depth, int start, int current, int target) {
//        System.out.println("start = " + start + " current = " + current + " parent[current] = " + parent[current]);
//        System.out.println("related[current][parent[current]] = " + related[current][parent[current]]);
        if (current == 0) return 0;
        if (depth == authority[start]) return current;
        if (parent[current][1] == 1) {
            int n = parent[current][0];
            if (n == target) ans++;
            return getParent(depth+1, start, n, target);
        }
        return 0;
    }

    static void switchParent(int c1, int c2) {

        int temp = parent[c1][0];
        parent[c1][0] = parent[c2][0];
        parent[c2][0] = temp;
    }

    static void switchPower(int c, int power) {
        authority[c] = power;
    }

    static void switchAlarm(int c) {

        if (parent[c][1] == 1) {
            parent[c][1] = -1;
        } else {
            parent[c][1] = 1;
        }
    }

    static int stoi(String s) {
        return Integer.parseInt(s);
    }
}