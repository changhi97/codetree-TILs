//BOJ_14890_경사로

import java.io.*;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.*;

public class Main {

    //마지막으로 공격한 라운드 기록, attackRound[i][j]가 클수록 최근에 공격한것이다
    static int[][] attackRound;

    static int[] dxL = {0, 1, 0, -1}, dyL = {1, 0, -1, 0};
    static int[] dxB = {1, 0, -1, -1, -1, 0, 1, 1}, dyB = {-1, -1, -1, 0, 1, 1, 1, 0};
    static int N, M, K;
    static int[][] board;

    public static void main(String[] args) throws IOException {


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        board = new int[N][M];
        attackRound = new int[N][M];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for(int i =0; i<K; i++){
//            System.out.println("======");
//            print();
            int[][] copy= new int[N][M];
            for(int x =0; x<N; x++) copy[x] = board[x].clone();

            int[] a = shooter(i);
            int[] b= targeting();
            board[a[0]][a[1]]+=N+M;

            //좌표가 -1일수가 있나
//            boolean[][] v= new boolean[N][M];
//            if(tryLaser(v,a[0],a[1],b[0],b[1],"")){
//                v= new boolean[N][M];
//                shootLaser(v,a[0],a[1],b[0],b[1],board[a[0]][a[1]]);
//            }else{
//                shootBomb(b[0],b[1],board[a[0]][a[1]]);
//            }
            List<Node> route = dijkstra(a[0], a[1], b[0], b[1]);
            if(!route.isEmpty()){
                for(Node node : route){
                    if(node.x==b[0] && node.y==b[1]) board[node.x][node.y] -= board[a[0]][a[1]];
                    else board[node.x][node.y] -= (board[a[0]][a[1]]/2);
                }
            }else{
                shootBomb(b[0],b[1],board[a[0]][a[1]]);
            }


            for(int x =0; x<N; x++){
                for(int y =0; y<M; y++){
                    if((x==a[0] && y==a[1]) || board[x][y]<=0 || board[x][y] != copy[x][y]) continue;
                    board[x][y] +=1;
                }
            }
//            print();
        }

        int answer= 0;
        for(int i =0; i<N; i++){
            for(int j=0; j<M; j++){
                answer=Math.max(answer,board[i][j]);
            }
        }
        System.out.println(answer);
    }

    public static void print() {
        for (int i = 0; i < N; i++) System.out.println(Arrays.toString(board[i]));
        System.out.println();
    }

    public static int[] shooter(int k) {
        int exp = Integer.MAX_VALUE;
        int recent = Integer.MIN_VALUE;
        int[] result = {-1, -1};
        for (int j = M - 1; j >= 0; j--) {
            for (int i = N - 1; i >= 0; i--) {
                if (board[i][j] == 0) continue;
                if (exp > board[i][j]) {
                    exp = board[i][j];
                    recent = attackRound[i][j];
                    result[0] = i;
                    result[1] = j;
                } else if (exp == board[i][j] && attackRound[i][j] > recent) {
                    recent = attackRound[i][j];
                    result[0] = i;
                    result[1] = j;
                }
            }
        }
        attackRound[result[0]][result[1]] = k+1;
        return result;
    }

    public static int[] targeting() {
        int exp = Integer.MIN_VALUE;
        int recent = Integer.MIN_VALUE;
        int[] result = {-1, -1};
        for (int j = 0; j < M; j++) {
            for (int i = 0; i < N; i++) {
                if (board[i][j] == 0) continue;

                if (exp < board[i][j]) {
                    exp = board[i][j];
                    recent = attackRound[i][j];
                    result[0] = i;
                    result[1] = j;
                } else if (exp == board[i][j] && attackRound[i][j] < recent) {
                    recent = attackRound[i][j];
                    result[0] = i;
                    result[1] = j;
                }
            }
        }
        return result;
    }



    public static void shootBomb(int x, int y, int exp) {
        board[x][y] -= exp;
        for (int i = 0; i < 8; i++) {
            int nx = x + dxB[i];
            int ny = y + dyB[i];

            if (nx < 0) nx = N - 1;
            else if (nx >= N) nx = 0;
            if (ny < 0) ny = M - 1;
            else if (ny >= M) ny = 0;

            if (board[nx][ny] > 0) {
                board[nx][ny] -= 8;
            }

        }
    }

    public static List<Node> dijkstra(int x1, int y1, int x2, int y2) {
        Stack<Node> route = new Stack<>();
        PriorityQueue<Node> q = new PriorityQueue<>((o1, o2) -> Integer.compare(o1.cost, o2.cost));
        int[][] dist = new int[N][N];
        for (int i = 0; i < N; i++) Arrays.fill(dist[i], Integer.MAX_VALUE);
        dist[x1][y1] = 0;
        q.offer(new Node(x1, y1, 0));
        route.push(new Node(x1, y1, 0));

        while (!q.isEmpty()) {
            Node node = q.poll();

            if (route.peek().cost < node.cost) route.push(node);
            if (node.x == x2 && node.y == y2) break;

            for (int i = 0; i < 4; i++) {
                int nx = node.x + dxL[i];
                int ny = node.y + dyL[i];

                if (nx < 0) nx = N - 1;
                else if (nx >= N) nx = 0;
                if (ny < 0) ny = M - 1;
                else if (ny >= M) ny = 0;

                if (board[nx][ny] > 0 && dist[nx][ny] > dist[node.x][node.y] + 1) {
                    dist[nx][ny] = dist[node.x][node.y] + 1;
                    Node newNode = new Node(nx, ny, dist[nx][ny]);
                    q.offer(newNode);
                }
            }

        }

        List<Node> list = new ArrayList<>();
        while (route.size() > 1) list.add(route.pop());
        return list;
    }

    static class Node {
        int x;
        int y;
        int cost;

        public Node(int x, int y, int cost) {
            this.x = x;
            this.y = y;
            this.cost = cost;
        }


        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}


//public static boolean tryLaser(boolean[][] v, int x1, int y1, int x2, int y2, int dir, String r) {
//    if (x1 == x2 && y1 == y2) {
//        System.out.println(r);
//        return true;
//    }
//    for (int i = 0; i < N; i++) {
//        int nx = x1 + dxL[i];
//        int ny = y1 + dyL[i];
//
//        if (nx < 0) nx = N - 1;
//        else if (nx >= N) nx = 0;
//        if (ny < 0) ny = M - 1;
//        else if (ny >= M) ny = 0;
//
//        if (!v[nx][ny] && board[nx][ny] > 0) {
//            v[nx][ny] = true;
//            return tryLaser(v, nx, ny, x2, y2, r+" ["+nx+" "+ny+"] ");
//        }
//    }
//    return false;
//}
//
//public static boolean shootLaser(boolean[][] v, int x1, int y1, int x2, int y2, int exp) {
//    if (x1 == x2 && y1 == y2) {
//        return true;
//    }
//    for (int i = 0; i < N; i++) {
//        int nx = x1 + dxL[i];
//        int ny = y1 + dyL[i];
//
//        if (nx < 0) nx = N - 1;
//        else if (nx >= N) nx = 0;
//        if (ny < 0) ny = M - 1;
//        else if (ny >= M) ny = 0;
//
//        if (!v[nx][ny] && board[nx][ny] > 0) {
//            v[nx][ny] = true;
//            if (shootLaser(v, nx, ny, x2, y2, exp)) {
//                if (nx == x2 && ny == y2) board[nx][ny] -= exp;
//                else board[nx][ny] -= (exp / 2);
//                return true;
//            }
//        }
//    }
//    return false;
//}