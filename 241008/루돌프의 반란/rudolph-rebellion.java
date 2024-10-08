//SWEA_루돌프의 반란

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.util.*;

public class Main {
    static int[] dxR = {-1, -1, -1, 0, 1, 1, 1, 0}, dyR = {-1, 0, 1, 1, 1, 0, -1, -1};
    static int[] dxS = {-1, 0, 1, 0}, dyS = {0, 1, 0, -1};
    static int N, M, P, C, D;
    static Node[][] board;
    static Rudol rudol;
    static List<Santa> player = new ArrayList<>();
    static int[] scores;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());
        scores = new int[P];

        board = new Node[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = new Node(false, false, true);
            }
        }

        st = new StringTokenizer(br.readLine());
        rudol = new Rudol(Integer.parseInt(st.nextToken()) - 1, Integer.parseInt(st.nextToken()) - 1, C);
        board[rudol.x][rudol.y] = new Node(false, true, false);

        for (int i = 0; i < P; i++) {
            st = new StringTokenizer(br.readLine());
            player.add(new Santa(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()) - 1, Integer.parseInt(st.nextToken()) - 1, D, false));
            Santa s = player.get(i);
            board[s.x][s.y] = new Node(true, false, true);
            board[s.x][s.y].id = s.id;
        }

//        printRudol();
//        printSanta();
//        System.out.println("=====================start");
        Collections.sort(player, (o1, o2) -> Integer.compare(o1.id, o2.id));
        for (int i = 0; i < M; i++) {
            moveRudol();
//            printRudol();
            moveSanta();
//            printSanta();
            collision();
//            printRudol();
//            printSanta();
//            printRudol();
//            System.out.println("---------");
            if (isEnd()) break;
        }

        StringBuilder answer = new StringBuilder();
        for(int i :scores) answer.append(i).append(" ");

        System.out.println(answer);

    }

    public static int moveRudol() {
        int minDist = Integer.MAX_VALUE;
        int maxX = -1, maxY = -1, id = 0;
        for (int i = 0; i < P; i++) {
            Santa santa = player.get(i);

            if (!canGo(santa.x, santa.y)) continue;

            int dist = calDist(rudol.x, rudol.y, santa.x, santa.y);

            if (minDist > dist) {
                minDist = dist;
                maxX = santa.x;
                maxY = santa.y;
                id = santa.id;
            } else if (minDist == dist && (santa.x > maxX || (santa.x == maxX && santa.y > maxY))) {
                maxX = santa.x;
                maxY = santa.y;
                id = santa.id;
            }
        }

        int dir = 0;
        minDist = Integer.MAX_VALUE;
        for (int i = 0; i < 8; i++) {

            int nx = rudol.x + dxR[i];
            int ny = rudol.y + dyR[i];

            if (!canGo(nx, ny)) continue;

            int dist = calDist(nx, ny, player.get(id - 1).x, player.get(id - 1).y);
            if (minDist > dist) {
                minDist = dist;
                dir = i;
            }
        }

        board[rudol.x][rudol.y].rudol = false;

        rudol.dir = dir;
        rudol.x += dxR[dir];
        rudol.y += dyR[dir];

        //루돌프가 움직였는데 산타칸에 왔을때
        board[rudol.x][rudol.y].rudol = true;
        if (board[rudol.x][rudol.y].santa) board[rudol.x][rudol.y].collision = 'R';

        return id - 1;
    }

    public static void moveSanta() {
        for (Santa santa : player) {

            //탈락하거나 스턴상태일때 못한다!
            //추후 그냥 stun을 false로 하고 넘허가면 안될까
            if (!canGo(santa.x, santa.y) || santa.stun) {
                santa.stun = false;
                continue;
            }

            int minDist = calDist(santa.x, santa.y, rudol.x, rudol.y);

            if (minDist == 0) {
                santa.stun = true;
                continue;
            }
            int dir = -1;
            for (int i = 1; i < 8; i += 2) {
                int nx = santa.x + dxR[i];
                int ny = santa.y + dyR[i];

                if (!canGo(nx, ny)) continue;

                int dist = calDist(nx, ny, rudol.x, rudol.y);
                //범위 밖이거나, 산타가 이미 있는 칸으로 갈때는 움직일 수 없다
                //산타와 루돌프가 충돌을 한 곳으로 또 오려고 하면? board[nx][ny].type=='S'이걸로 할 수 없다. 루돌프가 충돌한 시점에서 산타가 있어도 R로 바뀐다.
                //-> 해결. board에 산타, 루돌프, 빈칸 플래그를 멤버변수로 하여서 구분한다.
                if (board[nx][ny].santa || minDist <= dist) continue;

                if (minDist > dist) {
                    minDist = dist;
                    dir = i;
                }
            }

            //상하좌우로 산타있어서 못움직일때
            if (dir == -1 && minDist > 0) continue;

            //산타가 투돌프한테 갈때
            board[santa.x][santa.y].santa = false; //한칸에 두명이 있다면? 산타 - 루돌프 -> 이동 직후에는 있을수있다
            board[santa.x][santa.y].id = 0;

            santa.dir = dir;
            santa.x += dxR[dir];
            santa.y += dyR[dir];

            board[santa.x][santa.y].santa = true;
            board[santa.x][santa.y].id = santa.id;
            if (board[santa.x][santa.y].rudol) {
                santa.stun = true;
                if (board[rudol.x][rudol.y].collision == 'E') board[rudol.x][rudol.y].collision = 'S';
            }
        }
    }

    public static boolean collision() {
        int targetId = -1;
        for (Santa santa : player) {
            if (rudol.x == santa.x && rudol.y == santa.y) {
                targetId = santa.id;
                break;
            }
        }

        //누가 충돌을 먼저 했는지 알아야한다
        // 해결 -> board에 플래그 추가.

        if (targetId == -1) return false;
        if (board[rudol.x][rudol.y].collision == 'E') return false;

        //산타와 루돌프의 dir을 알아야한다.
        Santa santa = player.get(targetId - 1);
        int dir = 0;
        int dist = 0;
        if (board[rudol.x][rudol.y].collision == 'R') {
            scores[santa.id - 1] += C;
            dir = rudol.dir;
            dist = C;
        } else {
            scores[santa.id - 1] += D;
            dir = (santa.dir + 4) % 8;
            dist = D;
        }

        board[santa.x][santa.y].santa = false;
        for (int i = 0; i < dist; i++) santa.x += dxR[dir];
        for (int i = 0; i < dist; i++) santa.y += dyR[dir];

        int nx = santa.x;
        int ny = santa.y;
        while (true) {
            if (!canGo(nx,ny)) break;

            if (!board[nx][ny].santa) {
                board[nx][ny].santa = true;
                board[nx][ny].id = santa.id;
                break;
            }

            //기존에 있던 산타 가져오기
            int nextId = board[nx][ny].id;
            board[nx][ny].id = santa.id;

            santa = player.get(nextId - 1);

            //기존의 산타가 이동할 부분
            nx+= dxR[dir];
            ny += dyR[dir];

            santa.x=nx;
            santa.y=ny;


        }


        return true;
    }


    public static boolean canGo(int x, int y) {
        if (x < 0 || y < 0 || x >= N || y >= N) return false;
        return true;
    }

    public static int calDist(int x1, int y1, int x2, int y2) {
        return (int) Math.pow(x1 - x2, 2) + (int) Math.pow(y1 - y2, 2);

    }

    public static boolean isEnd() {
        boolean result = true;
        for (int i = 0; i < P; i++) {
            Santa santa = player.get(i);
            if (!canGo(santa.x, santa.y)) continue;

            result = false;

            scores[i]++;
        }
        return result;
    }


    public static void printSanta() {
        for (Node[] nodes : board) {
            for (Node node : nodes) {
                if (node.santa) {
                    System.out.print(node.id + " ");
                } else {
                    System.out.print(0 + " ");
                }
            }
            System.out.println();
        }
        System.out.println();

    }

    public static void printRudol() {
        for (Node[] nodes : board) {
            for (Node node : nodes) {
                if (node.rudol) {
                    System.out.print("R ");
                } else {
                    System.out.print(0 + " ");
                }
            }
            System.out.println();
        }
        System.out.println();

    }


    static class Rudol {
        int x, y, power, dir;

        public Rudol(int x, int y, int power) {
            this.x = x;
            this.y = y;
            this.power = power;
        }
    }

    static class Santa {
        int id, x, y, power, dir;
        boolean stun;


        public Santa(int id, int x, int y, int power, boolean stun) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.power = power;
            this.stun = stun;
        }
    }

    static class Node {
        int id;
        //[S:산타] [R:루돌프] [E:빈칸]

        boolean santa, rudol, empty;
        char collision = 'E';

        public Node(boolean santa, boolean rudol, boolean empty) {
            this.santa = santa;
            this.rudol = rudol;
            this.empty = empty;
        }
    }

}