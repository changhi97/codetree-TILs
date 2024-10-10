//BOJ_16235_나무 재테크

import java.io.*;
import java.util.*;

public class Main {
    static int N,M;

    //베이스캠프가 어디 있는지 표시
    static int [][]board;

    //가게의 정보를 표시
    static int [][] storeBoard;

    //사람이 베이스캠프나 상점에 도달했을대 그 지역은 더이상 가지 못한다.
    static boolean[][] canGo;
    static int[] dx= {-1,0,0,1}, dy={0,-1,1,0};

    //i시간에 움직일수 있는 사람의 위치
    static Node[] peoples;

    //가게의 정보를 각기 다른 id로 표시
    static Node[] stores;

    //현재 움직일 수 있는 사람여부, 시간이 되지 않았거나 상점에 도달했을 경우 움직이지 않는다.
    static boolean[] isActive;


    public static void main(String[] args) throws IOException {
//        String input = "5 3\n" +
//                "0 0 0 0 0\n" +
//                "1 0 0 0 1\n" +
//                "0 0 0 0 0\n" +
//                "0 1 0 0 0\n" +
//                "0 0 0 0 1\n" +
//                "2 3\n" +
//                "4 4\n" +
//                "5 1\n";
//        InputStream is = new ByteArrayInputStream(input.getBytes());
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st=new StringTokenizer(br.readLine());
        N=Integer.parseInt(st.nextToken());
        M=Integer.parseInt(st.nextToken());

        board=new int[N][N];
        canGo=new boolean[N][N];
        storeBoard=new int[N][N];

        peoples=new Node[M+1];
        stores =new Node[M+1];
        isActive=new boolean[M+1];

        for(int i =0; i<N; i++){
            st=new StringTokenizer(br.readLine());
            for(int j =0; j<N; j++){
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        int id = 1;
        for(int i =0; i<M; i++){
            st=new StringTokenizer(br.readLine());
            int x= Integer.parseInt(st.nextToken())-1;
            int y= Integer.parseInt(st.nextToken())-1;
            storeBoard[x][y] = id++;
            stores[i+1] = new Node(x,y);
        }


//        print();
        int t = 0;
        int count=0;
        while(true){
            t++;
//            System.out.println("time" +" "+t);
            if(t<=M){
                for(int i =1; i<=M; i++){
                    if(!isActive[i]) continue;

                    //사람을 이동시킨다. 만약 상점에 도달했으면 더이상 움직이 않아도 된다.
                    if(moveStore(i)){
                        isActive[i] = false;
                        count++;
                    }
                }
                peoples[t] = goBasecamp(t);
//                System.out.println("people start"+" "+peoples[t].x+" "+peoples[t].y);
                isActive[t] = true;
            }else{
                for(int i =1; i<=M; i++){
                    if(!isActive[i]) continue;
                    if(moveStore(i)){
                        isActive[i] = false;
                        count++;
                    }
                }
            }

            updateCanGo();
            if(count==M) break;

//            System.out.println();
//            System.out.println();
        }
        System.out.println(t);
    }

    public static boolean moveStore(int id){
        Node people = peoples[id];
        Node store = stores[id];

        //현재 사람이 최소 거리, 최소 루트로 이동해야한다.
        //canGo로 갈수 있는지 없는지 체크해야한다.

        //생각 1. 다익스트라로 구한다.

        Queue<Node> q=new LinkedList<>();
        int[][] dist= new int[N][N];
        int[][] routeX = new int[N][N];
        int[][] routeY = new int[N][N];
        for(int i =0; i<N; i++) Arrays.fill(dist[i] , Integer.MAX_VALUE);
        q.offer(people);
        dist[people.x][people.y] = 0;

        while(!q.isEmpty()){
            Node node = q.poll();
            if(store.x==node.x && store.y==node.y) break;

            for(int i =0; i<4; i++){
                int nx = node.x+dx[i];
                int ny = node.y+dy[i];
                if(nx<0||ny<0||nx>=N||ny>=N) continue;

                if(!canGo[nx][ny] && dist[nx][ny] > dist[node.x][node.y] + 1){
                    routeX[nx][ny] = node.x;
                    routeY[nx][ny] = node.y;
                    dist[nx][ny] = dist[node.x][node.y] + 1;
                    q.offer(new Node(nx,ny));
                }
            }
        }

        Stack<Node> route = new Stack<>();
        route.push(new Node(store.x, store.y));
        int x = store.x, y= store.y;
        while(true){
            if(routeX[x][y]==0 && routeY[x][y]==0) break;
            Node node = new Node(routeX[x][y], routeY[x][y]);
            route.push(node);
            x= routeX[x][y];
            y= routeY[x][y];
        }

//        System.out.println("id" +" "+id);
        if(route.size()>1){
//            System.out.print("["+route.peek().x+" "+route.peek().y+"] ");
            route.pop();
        }
        peoples[id] =route.peek();
        while(!route.isEmpty()){
            Node node = route.pop();
//            System.out.print("["+node.x+" "+node.y+"] ");

        }
//        System.out.println(people.x+" "+ people.y);
//        System.out.println(peoples[id].x+" "+ peoples[id].y);
//        people = route.pop();
        if(peoples[id].x == store.x && peoples[id].y== store.y){
            return true;
        }else {
            return false;
        }


    }

    public static Node goBasecamp(int id){
        Node store = stores[id];
        Queue<Node> q= new LinkedList<>();
        q.offer(store);
        boolean[][] v= new boolean[N][N];
        v[store.x][store.y] = true;
        while(!q.isEmpty()){
            Node node  =q.poll();

            //행, 열 순으로 작은걸 먼저 선택해야한다. 델타로 우선순위를 두었으니 여기서는 체크하지 않아도 된다.
            if(board[node.x][node.y] == 1){
                return node;
            }
            for(int i =0; i<4; i++){
                int nx = node.x+dx[i];
                int ny = node.y+dy[i];
                if(nx<0||ny<0||nx>=N||ny>=N) continue;

                if(!v[nx][ny] && !canGo[nx][ny]){
                    v[nx][ny] = true;
                    q.offer(new Node(nx,ny));
                }
            }
        }

        //가지 못하는 경우는 없다 했으니 null 반환
        return null;
    }

    public static void updateCanGo(){
        for(Node node : peoples){
            if(node == null) continue;
            //canGo는 어떤 사람이 베이스캠프나 상점에 도달했을때 true로 변경하며 그 지역은 더이상 지나가지 못한다.
            if(board[node.x][node.y] == 1 || storeBoard[node.x][node.y] > 0){
                canGo[node.x][node.y] = true;
            }
        }
    }

    public static void print(){
        for(int i =0; i<N; i++) System.out.println(Arrays.toString(board[i]));
        System.out.println();
        System.out.println();
    }
    static class Node{
        int x,y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}