//SWEA_꼬리잡기놀이

import java.io.*;
import java.util.*;

public class Main {
    static int N, M, H, K;
    static boolean[][] trees;
    static int[][] count;

    static int[] dx = {1, 0, -1, 0}, dy = {0, 1, 0, -1};
    static List<Player> rms;
    static Player hero;

    static int[][] temp;

    static List<Player> route;


    public static void main(String[] args) throws IOException {
//        String input = "5 24 20 82\n" +
//                "4 5 2\n" +
//                "2 1 1\n" +
//                "1 4 2\n" +
//                "2 5 1\n" +
//                "1 1 1\n" +
//                "1 3 1\n" +
//                "5 3 1\n" +
//                "3 1 2\n" +
//                "3 5 2\n" +
//                "4 4 2\n" +
//                "4 3 2\n" +
//                "2 2 2\n" +
//                "3 2 2\n" +
//                "1 2 2\n" +
//                "1 5 1\n" +
//                "5 1 1\n" +
//                "4 1 2\n" +
//                "2 3 2\n" +
//                "2 4 1\n" +
//                "5 4 1\n" +
//                "5 2 2\n" +
//                "4 2 2\n" +
//                "3 4 1\n" +
//                "5 5 1\n" +
//                "3 2\n" +
//                "3 5\n" +
//                "2 2\n" +
//                "4 2\n" +
//                "3 3\n" +
//                "5 4\n" +
//                "3 4\n" +
//                "5 5\n" +
//                "2 4\n" +
//                "2 3\n" +
//                "1 1\n" +
//                "2 5\n" +
//                "5 1\n" +
//                "1 2\n" +
//                "5 3\n" +
//                "4 4\n" +
//                "2 1\n" +
//                "4 5\n" +
//                "1 4\n" +
//                "4 3";
//
//        InputStream is = new ByteArrayInputStream(input.getBytes());
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        H = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        trees = new boolean[N][N];
        count = new int[N][N];
        rms = new ArrayList<>();
        hero = new Player(N / 2, N / 2, 0);
        route = new ArrayList<>();


        temp = new int[N][N];

        int idx = 1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                temp[i][j] = idx++;
            }
        }

        //d가 1인 경우 좌우로 움직임을, 2인 경우 상하로만 움직임
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());
            if(d==2) d=0;
            rms.add(new Player(x, y, d));
            count[x][y] += 1;
        }

        for (int i = 0; i < H; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            trees[x][y] = true;
        }

        boolean[][] v = new boolean[N][N];
        v[0][0] = true;
        setRoute(v, 0, 0, 0);

        int now = route.size() / 2;

        int answer = 0;
//        printTree();
//        printRun();
        for (int i = 0; i < K; i++) {
//            System.out.println(i+"===============");
            run();
//            printRun();
            now = (now+1)%route.size();
            answer+= moveHero(now)*(i+1);

//            printRun();
        }
        System.out.println(answer);

    }

    public static int moveHero(int now) {
        Player next = route.get(now);
        hero.x = next.x;
        hero.y = next.y;
        hero.dir = next.dir;

        return watch(hero.x,hero.y,hero.dir);


    }

    public static int watch(int x, int y, int dir) {
        int nx = x;
        int ny = y;
        int result = 0;
        for (int i = 0; i < 3; i++) {
            if (nx >= 0 && ny >= 0 && nx < N && ny < N && !trees[nx][ny] && count[nx][ny]>0) {
                result += count[nx][ny];
                count[nx][ny] = 0;
            }
            nx += dx[dir];
            ny += dy[dir];
        }

        updateRms();

        return result;

    }

    public static void updateRms() {
        for (int i = rms.size() - 1; i >= 0; i--) {
            Player p = rms.get(i);
            if (count[p.x][p.y] == 0) rms.remove(i);
        }
    }

    public static void run() {
        for (Player p : rms) {
            if (calDist(p.x, p.y, hero.x, hero.y) > 3) continue;

            int dir = p.dir;
            int nx = p.x + dx[dir];
            int ny = p.y + dy[dir];
            if (nx < 0 || ny < 0 || nx >= N || ny >= N) {
                dir = (dir + 2) % 4;
                nx = p.x + dx[dir];
                ny = p.y + dy[dir];

            }
            if (hero.x == nx && hero.y == ny) continue;
            count[p.x][p.y] -= 1;
            count[nx][ny] += 1;

            p.x = nx;
            p.y = ny;
            p.dir = dir;
        }
    }

    public static void setRoute(boolean[][] v, int x, int y, int dir) {
        if (x == N / 2 && y == N / 2) {
            return;
        }
        int nx = x + dx[dir];
        int ny = y + dy[dir];
        if (nx < 0 || ny < 0 || nx >= N || ny >= N || v[nx][ny]) {
            dir = (dir + 1) % 4;
            nx = x + dx[dir];
            ny = y + dy[dir];
        }

        v[nx][ny] = true;
        route.add(new Player(x, y, dir));
        setRoute(v, nx, ny, dir);
        route.add(new Player(nx, ny, (dir + 2) % 4));

    }

    public static void printRun(){
        for(int i =0; i<N; i++) System.out.println(Arrays.toString(count[i]));
        System.out.println();
    }

    public static void printTree(){
        for(int i =0; i<N; i++){
            for(int j =0; j<N; j++){
                if(trees[i][j]) System.out.print("1 ");
                else System.out.print("0 ");
            }
            System.out.println();
        }
        System.out.println();
    }



    public static int calDist(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);

    }

    static class Player {
        int x, y, dir;


        public Player(int x, int y, int dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }

}