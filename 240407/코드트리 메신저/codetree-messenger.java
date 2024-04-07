import java.io.*;
import java.util.*;

public class Main {

    static int N,Q;
    static int[] parent, authority;
    static boolean[][] related;
    static int ans = 0;

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = stoi(st.nextToken());
        Q = stoi(st.nextToken());
        parent = new int[N+1];
        authority = new int[N+1];
        related = new boolean[N+1][N+1];

        for (int i = 0; i <= N; i++) {
            Arrays.fill(related[i], true);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());

            String cmd = st.nextToken();
            if (cmd.equals("100")) {
                for (int j = 1; j <= N; j++) {
                    parent[j] = stoi(st.nextToken());
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
                    getParent(0, j, j, parent[j], c);
                }

                sb.append(ans).append("\n");
            }
        }

        System.out.println(sb.toString());
    }

    static int getParent(int depth, int start, int current, int next, int target) {
//        System.out.println("start = " + start + " current = " + current + " parent[current] = " + parent[current]);
//        System.out.println("related[current][parent[current]] = " + related[current][parent[current]]);

        if (depth == authority[start]) return current;
        if (related[current][parent[current]]) {
            int n = parent[current];
            if (n == target) ans++;
            getParent(depth+1, start, n, parent[n], target);
        }
        return 0;
    }

    static void switchParent(int c1, int c2) {

        int temp = parent[c1];
        parent[c1] = parent[c2];
        parent[c2] = temp;
    }

    static void switchPower(int c, int power) {
        authority[c] = power;
    }

    static void switchAlarm(int c) {

        for (int i = 1; i <= N; i++) {
            related[c][i] = !related[c][i];
        }
    }

    static int stoi(String s) {
        return Integer.parseInt(s);
    }
}