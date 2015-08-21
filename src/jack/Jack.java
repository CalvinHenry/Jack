/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jack;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 *
 * @author Calvin
 */
public class Jack implements ActionListener{

    public static boolean gameStarted;
    public static boolean gameOver;
    public static ArrayList<Player> players = new ArrayList<>();
    public Timer time;
    static int seconds = 0;
    Stopwatch timer;

    public static void main(String[] args) {
        Jack server = new Jack();
        server.runGame();
    }
    public Jack(){
        timer = new Stopwatch(0);
        time = new Timer(1000, this);
        time.start();
        System.out.println("Sever running");
        
        try {
            ServerSocket listener = new ServerSocket(8520);
            while (!gameStarted) {
                new Player(listener.accept()).start();
                System.out.println("Players; " + players.size());
                if (players.size() > 3 ) {
                    Thread.sleep(1000);
                    gameStarted = true;
                    for (int i = 0; i < players.size(); i++) {
                        players.get(i).print("Start Game");
                    }
                }
            }

        } catch (Exception e) {

        }
    }
    public void runGame(){
        while (true) {
            long time = System.currentTimeMillis();
            assignRanks();
            for (int i = 0; i < players.size(); i++) {
                try {
                    Thread.sleep(5);
                } catch (Exception e) {

                }
                if (!gameOver) {
                    players.get(i).print("Time passed " + timer.getTime()  );
                }
                if (players.get(i).score > 100) {
                    gameOver = true;
                    for (int j = 0; j < players.size(); j++) {
                        players.get(j).print("Game over");
                    }
                }
            }
        }
    }

    public static void assignRanks() {
        int max = 0;
        int index = 0;
        for (int i = 0; i < players.size(); i++) {
            players.get(i).rankUpdated = false;
        }
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < players.size(); j++) {
                if (players.get(j).score > max && !players.get(j).updated()) {
                    max = players.get(j).score;
                    index = j;
                }
            }
            players.get(index).setRank(i + 1);
            players.get(index).rankUpdated = true;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == time){
            timer.incrementTime(1);
        }
    }

    private static class Player extends Thread {

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        public int score = 0;
        public int rank;
        public boolean rankUpdated = true;
        public int displayedRank;
        public long time;

        public Player(Socket socket) {
            players.add(this);
            this.socket = socket;
            rank = 0;
            displayedRank = -1;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (Exception e) {

            }
        }

        public void print(String s) {
            out.println(s);
        }
//10.35.25.140

        public void run() {
            String input;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                    time = System.currentTimeMillis();
                    if (!gameStarted) {
                        System.out.println("waiting");
                        out.println("Waiting for game to start");
                        Thread.sleep(10);
                        continue;
                    }
                    System.out.println("Game running");
                    input = in.readLine();
                    if (input == null) {

                    } else if (input.equals("WOOHOO")) {
                        System.out.println("score: " + score);
                        score++;
                        out.println("Score change " + score);
                    }
                    if (rank != displayedRank) {
                        out.println("Rank change " + rank);
                        displayedRank = rank;
                    }
                    out.println("Time passed " + time);

                }
            } catch (Exception e) {

            }
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public boolean updated() {
            return rankUpdated;
        }

    }

}
