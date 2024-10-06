import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    static int R, C, K;
    static int[][] board;
    static int[][] door;
    static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    static int answer =0;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken()) + 3;
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        board = new int[R][C];
        door = new int[R][C];

        int id = 1;
        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            int c = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());

            down(1, c - 1, d, id++);


        }
        System.out.println(answer);

    }

    public static void down(int x, int y, int dir, int id) {
        if (canGo(x, y) && canGo(x + 1, y)) {
            down(x + 1, y, dir, id);
        } else if (canGo(x, y - 1) && canGo(x + 1, y - 1)) {
            down(x, y - 1, ((dir - 1) + 4) % 4, id);
        } else if (canGo(x, y + 1) && canGo(x + 1, y + 1)) {
            down(x, y + 1, (dir + 1) % 4, id);
        } else {
            if (x < 4) {
                resetBoard();
            } else {
                board[x][y] = id;
                for (int i = 0; i < 4; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];
                    board[nx][ny] = id;
                }

                door[x + dx[dir]][y + dy[dir]] = 1;

                int count = bfs(x,y);
                answer+=count-3;
            }

        }

    }

    public static int bfs(int x, int y) {
        Queue<Node> q = new LinkedList<>();
        boolean[][] v = new boolean[R][C];

        q.offer(new Node(x, y, board[x][y], false));
        v[x][y] = true;

        int result = 0;

        while (!q.isEmpty()) {
            Node node = q.poll();
            result = Math.max(result, node.x);
            for (int i = 0; i < 4; i++) {
                int nx = node.x + dx[i];
                int ny = node.y + dy[i];

                if (nx < 3 || ny < 0 || nx >= R || ny >= C || board[nx][ny] == 0) continue;

                if (!v[nx][ny] && node.id == board[nx][ny]) {
                    v[nx][ny] = true;
                    if (door[nx][ny] == 1) {
                        q.offer(new Node(nx, ny, board[nx][ny], true));
                    } else {
                        q.offer(new Node(nx, ny, board[nx][ny], false));
                    }
                } else if (!v[nx][ny] && node.id != board[nx][ny] && node.cheat) {
                    v[nx][ny] = true;
                    if (door[nx][ny] == 1) {
                        q.offer(new Node(nx, ny, board[nx][ny], true));
                    } else {
                        q.offer(new Node(nx, ny, board[nx][ny], false));
                    }
                }
            }
        }
        return result+1;
    }

    public static void resetBoard() {
        for (int i = 0; i < R; i++) Arrays.fill(board[i], 0);
        for (int i = 0; i < R; i++) Arrays.fill(door[i], 0);
    }

    public static boolean canGo(int x, int y) {
        if (x < 0 || y < 0 || x >= R || y >= C||board[x][y]!=0) return false;
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx < 0 || ny < 0 || nx >= R || ny >= C || board[nx][ny] != 0) return false;
        }
        return true;
    }

    public static void print() {
        for (int i = 0; i < R; i++) {
            System.out.println(Arrays.toString(board[i]));
        }
        System.out.println();
    }

    public static void printDoor() {
        for (int i = 0; i < R; i++) {
            System.out.println(Arrays.toString(door[i]));
        }
        System.out.println();
    }

    static class Node {
        int x, y, id;
        boolean cheat;

        public Node(int x, int y, int id, boolean cheat) {
            this.x = x;
            this.y = y;
            this.id = id;
            this.cheat = cheat;
        }
    }
}