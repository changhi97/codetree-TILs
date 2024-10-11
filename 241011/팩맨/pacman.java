//SWEA_팩맨

import java.io.*;
import java.util.*;

public class Main {

    static int N, M, T;
    static int[][] dead;
    static Queue<Node> eggs;
    static Node packMan;
    static List<Node> monsters;

    static int[][] monsterCount;

    static int[] dxM = {-1, -1, 0, 1, 1, 1, 0, -1}, dyM = {0, -1, -1, -1, 0, 1, 1, 1};
    static int[] dxP = {-1, 0, 1, 0}, dyP = {0, -1, 0, 1};

    static int[][] dist;

    static int targetX;
    static int targetY;
    static int eatCount;
    static int[] outputX;
    static int[] outputY;


    public static void main(String[] args) throws IOException {
//        String input = "4 1\n" +
//                "3 1\n" +
//                "1 3 5\n" +
//                "2 2 7\n" +
//                "3 4 6\n" +
//                "4 2 2\n";
//        InputStream is = new ByteArrayInputStream(input.getBytes());
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());

        N = 4;
        M = Integer.parseInt(st.nextToken());
        T = Integer.parseInt(st.nextToken());

        monsterCount = new int[N][N];
        dist = new int[N][N];

        dead = new int[N][N];
        eggs = new LinkedList<>();

        st = new StringTokenizer(br.readLine());
        packMan = new Node(Integer.parseInt(st.nextToken()) - 1, Integer.parseInt(st.nextToken()) - 1);

        monsters = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken()) - 1;
            monsters.add(new Node(x, y, d));

            monsterCount[x][y]++;
        }

        for (int i = 0; i < T; i++) {
            targetX = 0;
            targetY = 0;
            eatCount = -1;
            outputX = new int[3];
            outputY = new int[3];

//            System.out.println(i + "===================");
            copyMonster();
//            System.out.println("move monster");
            moveMonster();
//            printMonsterCount();

//            System.out.println("eat" + " " + packMan.x + " " + packMan.y);
            movePackMan();
//            printMonsterCount();

//            System.out.println("update");
            updateMonster();
//            printMonsterCount();
        }

        int answer = 0;
        for(int i =0; i<N; i++){
            for(int j =0; j<N; j++){
                answer+=monsterCount[i][j];
            }
        }
        System.out.println(answer);


    }

    public static void updateMonster() {
        for (int i = monsters.size() - 1; i >= 0; i--) {
            Node m = monsters.get(i);
            if (!m.isDead) continue;
            if (m.deadCount > 0) {
                monsters.get(i).deadCount -= 1;
            } else {
                dead[m.x][m.y] -= 1;
                monsters.remove(i);
            }
        }

        while (!eggs.isEmpty()) {
            Node node = eggs.poll();
            monsters.add(node);
            monsterCount[node.x][node.y] += 1;
        }

    }

    public static void movePackMan() {
        boolean[][] v = new boolean[N][N];
        v[packMan.x][packMan.y] = true;

        int[] routeX = new int[N];
        int[] routeY = new int[N];

        tryMovePackMan(v, routeX,routeY,packMan.x, packMan.y, 0, 0);

        List<Node> eat = new ArrayList<>();

        for(int i =0; i<3; i++){
            int nx = outputX[i];
            int ny = outputY[i];
            if (monsterCount[nx][ny] > 0) {
                monsterCount[nx][ny] = 0;
                eat.add(new Node(nx, ny));
            }
        }

        packMan.x = targetX;
        packMan.y = targetY;


        for (Node ghost : eat) {
            for (Node monster : monsters) {
                if (monster.isDead) continue;
                if (ghost.x == monster.x && ghost.y == monster.y) {
                    monster.isDead = true;
                    monster.deadCount = 2;
                    dead[ghost.x][ghost.y] += 1;
                }
            }
        }

    }

    public static void tryMovePackMan(boolean[][] v,int[] routeX, int[] routeY, int x, int y, int depth, int sum) {
        if (depth == 3) {
            if (eatCount < sum) {
                eatCount = sum;
                targetX = x;
                targetY = y;
                outputX = routeX.clone();
                outputY=routeY.clone();
            }
            return;
        }
        for (int i = 0; i < 4; i++) {
            int nx = x + dxP[i];
            int ny = y + dyP[i];

            if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
            if (!v[nx][ny]) {
                v[nx][ny] = true;
                routeX[depth] = nx;
                routeY[depth] = ny;
                tryMovePackMan(v, routeX,routeY,nx, ny, depth + 1, sum + monsterCount[nx][ny]);
                v[nx][ny] = false;
            }
        }

    }

//    public static int[] tryMovePackMan() {
//        Queue<Node> q = new LinkedList<>();
//
//        boolean[][] v = new boolean[N][N];
//
//        int[] result = new int[2];
//        result[0] = -1;
//        result[1] = -1;
//
//        for (int i = 0; i < N; i++) Arrays.fill(dist[i], 0);
//
//        q.offer(new Node(packMan.x, packMan.y));
//        dist[packMan.x][packMan.y] = 0;
//        v[packMan.x][packMan.y] = true;
//
//        int maxCount = -1;
//        int maxX, maxY;
//
//        while (!q.isEmpty()) {
//            Node node = q.poll();
//
//            if (node.count == 3) {
//                if (dist[node.x][node.y] > maxCount) {
//                    maxX = node.x;
//                    maxY = node.y;
//                    maxCount = dist[node.x][node.y];
//                    result[0] = maxX;
//                    result[1] = maxY;
//                }
//                continue;
//            }
//
//            for (int i = 0; i < 4; i++) {
//                int nx = node.x + dxP[i];
//                int ny = node.y + dyP[i];
//
//                if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
//
//                if (dist[nx][ny] < dist[node.x][node.y]+monsterCount[nx][ny]&& node.count < 3) {
//                    v[nx][ny] = true;
//                    dist[nx][ny] = dist[node.x][node.y] + monsterCount[nx][ny];
//                    Node newNode = new Node(nx, ny);
//                    newNode.count = node.count + 1;
//                    q.offer(newNode);
//                }
//            }
//        }
//
//        System.out.println("TLqkf");
//        for(int i =0; i<N; i++ )System.out.println(Arrays.toString(dist[i]));
//        return result;
//    }

    public static void moveMonster() {
        int[][] temp = new int[N][N];
        for (Node m : monsters) {
            if (m.isDead) continue;

            int x = m.x;
            int y = m.y;
            int dir = m.dir;
            boolean canMove = false;

            int nx = x + dxM[dir];
            int ny = y + dyM[dir];

            if (nx < 0 || ny < 0 || nx >= N || ny >= N || (nx == packMan.x && ny == packMan.y) || dead[nx][ny] > 0) {
                for (int i = 0; i < 8; i++) {
                    dir = (dir + 1) % 8;
                    nx = x + dxM[dir];
                    ny = y + dyM[dir];

                    if (nx < 0 || ny < 0 || nx >= N || ny >= N || (nx == packMan.x && ny == packMan.y) || dead[nx][ny] > 0) {
                        continue;
                    }
                    canMove = true;
                    break;
                }
            } else {
                canMove = true;
            }

            if (!canMove) continue;


            m.x = nx;
            m.y = ny;
            m.dir = dir;

            temp[nx][ny]++;
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                monsterCount[i][j] = temp[i][j];
            }
        }
    }

    public static void copyMonster() {
        for (Node m : monsters) {
            if (m.isDead) continue;
            eggs.offer(new Node(m.x, m.y, m.dir));
        }
    }

    public static void printMonsterCount() {
        for (int i = 0; i < N; i++) System.out.println(Arrays.toString(monsterCount[i]));
        System.out.println();
    }

    static class Node {
        int x, y, deadCount, dir, count;
        boolean isDead;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Node(int x, int y, int dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }


}