import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    static int N, M, K;
    static int[][] board;
    static boolean[][] door;
    static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    static int answer = 0;


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken()) + 3;
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        board = new int[N][M];
        door = new boolean[N][M];

        int id = 1;
        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken()) - 1;
            int dir = Integer.parseInt(st.nextToken());

            sol(1, y, dir, id);
            id++;
        }
        System.out.println(answer);

    }

    public static boolean canGo(int x, int y) {
        if (x < 0 || y < 0 || x >= N || y >= M || board[x][y] != 0) return false;
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx < 0 || ny < 0 || nx >= N || ny >= M||board[nx][ny]!=0) return false;
        }
        return true;

    }

    public static void sol(int x, int y, int dir, int id) {
        if (canGo(x, y) && canGo(x + 1, y)) {
            sol(x + 1, y, dir, id);
        } else if (canGo(x, y - 1) && canGo(x + 1, y - 1)) {
            sol(x + 1, y - 1, ((dir - 1) + 4) % 4, id);
        } else if (canGo(x, y + 1) && canGo(x + 1, y + 1)) {
            sol(x + 1, y + 1, (dir + 1) % 4, id);
        } else {
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                board[nx][ny] = id;
            }
            board[x][y] = id;

            door[x + dx[dir]][y + dy[dir]] = true;
            if (x < 4) {
                resetMap();
            } else {
                int result= escape(x, y);
                answer+=result;
            }
        }

    }

    public static int escape(int x, int y) {
        Queue<Node> q = new LinkedList<>();
        boolean[][] v = new boolean[N][M];
        int minX = -1;

        q.offer(new Node(x, y, false));
        v[x][y] = true;

        while (!q.isEmpty()) {
            Node node = q.poll();
            if (minX < node.x) {
                minX = node.x;
            }

            for (int i = 0; i < 4; i++) {
                int nx = node.x + dx[i];
                int ny = node.y + dy[i];
                if (nx < 3 || ny < 0 || nx >= N || ny >= M || board[nx][ny] == 0 || v[nx][ny]) continue;
                if (board[node.x][node.y] == board[nx][ny]) {
                    v[nx][ny] = true;
                    if (door[nx][ny]) q.offer(new Node(nx, ny, true));
                    else q.offer(new Node(nx, ny, false));
                } else {
                    if (node.key) {
                        v[nx][ny] = true;
                        if (door[nx][ny]) q.offer(new Node(nx, ny, true));
                        else q.offer(new Node(nx, ny, false));
                    }
                }
            }
        }

        return minX-2;
    }

    public static void resetMap() {
        for (int i = 0; i < N; i++) Arrays.fill(board[i], 0);
        for (int i = 0; i < N; i++) Arrays.fill(door[i], false);
    }

    public static void print() {
        for (int i = 0; i < N; i++) System.out.println(Arrays.toString(board[i]));
        System.out.println();
    }

    static class Node {
        int x, y;
        boolean key;

        public Node(int x, int y, boolean key) {
            this.x = x;
            this.y = y;
            this.key = key;
        }
    }
}