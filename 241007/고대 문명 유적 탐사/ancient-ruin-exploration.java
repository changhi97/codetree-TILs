import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class Main {
    static int N, K, M;
    static int[][] board;
    static int mPoint = 0;
    static int[] wall;
    static int[] dx = {1, 0, -1, -1, -1, 0, 1, 1}, dy = {-1, -1, -1, 0, 1, 1, 1, 0};
    static int[] dxB = {0, -1, 0, 1}, dyB = {-1, 0, 1, 0};
    static int[] round;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        N = 5;
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        board = new int[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        wall = new int[M];
        round = new int[K];
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            wall[i] = Integer.parseInt(st.nextToken());
        }

        for (int i = 0; i < K; i++) {
            if (!sol(i)) break;
        }

        StringBuilder answer=  new StringBuilder();

        for(int i : round){
            if(i==0) break;
            answer.append(i).append(" ");
        }
        System.out.println(answer);

    }

    public static boolean sol(int id) {

        int max = 0;
        Node maxNode = null;
        int maxRotate = 4;


        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 4; j++) {

                for (int r = 0; r < 3; r++) {
                    rotateRight(i, j);

                    boolean[][] v = new boolean[N][N];
                    int count = 0;

                    for (int x = 0; x < N; x++) {
                        for (int y = 0; y < N; y++) {
                            if (!v[x][y]) {
                                int result = bfs(v, x, y);


//                                if(i==2 && j==2){
//                                    System.out.println(i+" "+j+" "+" "+r+" "+result);
//                                    print();
//
//                                }
                                count+=result;
                            }
                        }
                    }


                    if (max < count || (max==count && maxRotate>r+1)) {
                        max = count;
                        maxNode = new Node(i, j);
                        maxRotate = r+1;
                    }
                }

                for (int r = 0; r < 3; r++) {
                    rotateLeft(i, j);
                }
            }
        }

        if (max == 0) return false;


        for (int r = 0; r < maxRotate; r++) {
            rotateRight(maxNode.x, maxNode.y);
        }

//        print();
        while(true){
            int countSum=0;
            boolean[][] v= new boolean[N][N];
            for(int i =0; i<N; i++){
                for(int j =0; j<N; j++){
                    if(!v[i][j] && bfs(v,i,j) > 0){
                        int result  = select(i,j);
//                        System.out.println(result);

                        countSum+=result;

                    }

                }
            }

//            System.out.println(countSum);

            if(countSum==0) break;
            round[id]+=countSum;

            fillBoard();
//            print();
        }

//        print();
//        answer += select(maxNode.x, maxNode.y);

//        fillBoard();


        return true;


    }

    public static void rotateRight(int x, int y) {
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            temp.add(board[nx][ny]);
        }

        for (int i = 0; i < 8; i++) {
            int nx = x + dx[(i + 2) % 8];
            int ny = y + dy[(i + 2) % 8];
            board[nx][ny] = temp.get(i);
        }

    }


    public static void rotateLeft(int x, int y) {
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int nx = x + dx[(i + 4) % 8];
            int ny = y + dy[(i + 4) % 8];
            temp.add(board[nx][ny]);
        }

        for (int i = 0; i < 8; i++) {
            int nx = x + dx[(i + 2) % 8];
            int ny = y + dy[(i + 2) % 8];
            board[nx][ny] = temp.get(i);
        }

    }

    public static int bfs(boolean[][] v, int x, int y) {
        Queue<Node> q = new LinkedList<>();
        q.offer(new Node(x, y));
        v[x][y] = true;
        int count = 1;

        while (!q.isEmpty()) {
            Node node = q.poll();

            for (int i = 0; i < 4; i++) {
                int nx = node.x + dxB[i];
                int ny = node.y + dyB[i];

                if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;

                if (!v[nx][ny] && board[x][y] == board[nx][ny]) {
                    v[nx][ny] = true;
                    count++;
                    q.offer(new Node(nx, ny));
                }

            }
        }

        if (count < 3) count = 0;
        return count;
    }

    public static int select(int x, int y) {
        Queue<Node> q = new LinkedList<>();
        q.offer(new Node(x, y));
        boolean[][] v = new boolean[N][N];
        v[x][y] = true;
        int count = 1;

        while (!q.isEmpty()) {
            Node node = q.poll();

            for (int i = 0; i < 4; i++) {
                int nx = node.x + dxB[i];
                int ny = node.y + dyB[i];

                if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;

                if (!v[nx][ny] && board[x][y] == board[nx][ny]) {
                    board[nx][ny] = 0;
                    v[nx][ny] = true;
                    count++;
                    q.offer(new Node(nx, ny));
                }

            }
        }
        board[x][y] =0;
        return count;

    }

    // (1) 열 번호가 작은 순으로 조각이 생겨납니다.
    // 만약 열 번호가 같다면 (2) 행 번호가 큰 순으로 조각이 생겨납니다.
    public static void fillBoard() {
        for (int i = 0; i < N; i++) {
            for (int j = N - 1; j >= 0; j--) {
                if (board[j][i] == 0) {
                    board[j][i] = wall[mPoint++];
                }

            }
        }
    }

    public static void print() {
        for (int i = 0; i < N; i++) {
            System.out.println(Arrays.toString(board[i]));
        }
        System.out.println();
    }

    static class Node {
        int x, y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}