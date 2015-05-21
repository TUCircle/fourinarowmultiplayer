//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


//TODO: PrintWriter mit ObjectOutputStream vereinigen, 2 verschiedene Streams für 1 Socket Stream  funktioniert nicht!
//TODO: Check4Win Funktion muss noch implementiert werden!
public class Server {
    int cols = 7;
    int rows = 6;
    String[][] field;
    boolean gameOver;
    ServerSocket serverSock;
    ArrayList<Socket> playerList;
    ArrayList<ObjectOutputStream> writers;
    int player = 0;

    public Server() {
        this.field = new String[this.rows][this.cols];
        this.gameOver = false;
    }

    public static void main(String[] args) {
        Server main = new Server();
        main.runServer();
        main.startGame();
    }

    private void runServer() {
        try {
            this.serverSock = new ServerSocket(1530);
            this.initPlayers();
        } catch (Exception var2) {
            var2.printStackTrace();
            System.out.println("Can\'t run server!");
        }

    }

    private void initPlayers() {
        this.playerList = new ArrayList();
        this.writers = new ArrayList();

        //getting 2 clients
        while(this.playerList.size() < 2) {
            try {
                System.out.println("Waiting for Clients..");
                Socket e = serverSock.accept();
                playerList.add(e);
                System.out.println("Got a new client!");
            } catch (IOException var3) {
                System.out.println("Problem with getting Players");
                var3.printStackTrace();
            }
        }
        try {
            //attaching a writer to the connected clients
            writers.add(new ObjectOutputStream(playerList.get(0).getOutputStream()));
            writers.get(0).writeChars("Hello Player 1\n");
            writers.get(0).reset();
            writers.add(new ObjectOutputStream(playerList.get(1).getOutputStream()));
            writers.get(1).writeChars("Hello Player 2!\n");
            writers.get(1).reset();
        } catch (IOException var2) {
            System.out.println("Couldn\'t initialize the writers!");
        }
        try {
            //Creating the tokens for player and 2  makeToken1 = X and makeToken2 = O
            writers.get(0).writeChars("makeToken1\n");
            System.out.println("Sent message to player1");
            writers.get(0).reset();
            writers.get(1).writeChars("makeToken2\n");
            writers.get(1).reset();

        for(int i = 0; i < 2; i++) {
            try {
                writers.get(i).writeChars("updateField\n");
                writers.get(i).reset();
                writers.get(i).writeObject((String[][]) field);
                writers.get(i).reset();
            } catch (Exception var4) {
                System.out.println("Error with creating the Object Output stream or sending the Field to Clients!");
            }
        }
        } catch(Exception e){
                System.out.println("Couldn't create player token!");
            }

        }

    private void startGame() {
        this.initField();
        this.drawField();
        System.out.println("Game started!");
        try {
            writers.get(0).writeChars("updateField\n");
            writers.get(0).reset();
            writers.get(0).writeObject((String[][]) this.field);
            writers.get(0).reset();
            writers.get(1).writeChars("updateField\n");
            writers.get(1).writeObject((String[][]) this.field);
            writers.get(1).reset();
            writers.get(1).reset();
        } catch(Exception e){
            System.out.println("Couldn't update player field!");
        }
        byte playerTurn = 0;
        Iterator it = writers.iterator();
        //main Loop!
        while(!gameOver) {
                //Checking if its player 1 turn
                if(playerTurn % 2 == 0) {
                    player = 1;
                    //Telling Client of player 1 to make a turn!
                    try {
                        writers.get(0).writeChars("makeTurn\n");
                        writers.get(0).reset();
                        int col = ((Socket)this.playerList.get(0)).getInputStream().read();
                        this.update(col,player);
                        playerTurn++;
                    } catch (IOException var5) {
                        System.out.println("Error with player 1 turn!");
                    }

                    //Updating Field of every Player
                    for(int i = 0; i < 2; i++) {
                        try {
                            writers.get(i).writeChars("updateField\n");
                            writers.get(i).reset();
                            writers.get(i).writeObject((String[][]) this.field);
                            writers.get(i).reset();
                        } catch (Exception var4) {
                            System.out.println("Error with creating the Object Output stream or sending the Field to Clients!");
                        }
                    }
                }
            if(playerTurn % 2 == 1) {
                //Telling Client of player 2 to make a turn!
                player = 2;
                try {
                    writers.get(1).writeChars("makeTurn\n");
                    writers.get(1).reset();
                    int col = ((Socket)this.playerList.get(1)).getInputStream().read();
                    this.update(col,player);
                    playerTurn++;
                } catch (IOException var5) {
                    System.out.println("Error with player 1 turn!");
                }

                //Updating Field of every Player
                for(int i = 0; i < 2; i++) {
                    try {
                        //Sending the updated field to the Players
                        writers.get(i).writeChars("updateField\n");
                        writers.get(i).reset();
                        writers.get(i).writeObject((String[][]) this.field);
                        writers.get(i).reset();
                    } catch (Exception var4) {
                        System.out.println("Error with creating the Object Output stream or sending the Field to Clients!");
                    }
                }
            }
            }

            }
    private void update(int col, int player) {
        if(field[getFreeRow(col)][col] != null) {
            switch(player){
                case 1:
                    field[getFreeRow(col)][col] = "[X]\t";
                    break;
                case 2:
                    field[getFreeRow(col)][col] = "[O]\t";
                    break;
            }

        }

        drawField();
    }

    //TODO
    private void checkForWin(int col) {
        switch(col) {
            case 0:
                if(this.field[4][0].equals("[X]") && this.field[3][0].equals("[X]") && this.field[2][0].equals("[X]") && this.field[1][0].equals("[X]")) {
                    ;
                }
            default:
        }
    }

    private int getFreeRow(int column) {
        for(int i = 5; i > 0; --i) {
           // System.out.println();
            //System.out.println(this.field[i][column]);
            if(this.field[i][column] != null && this.field[i][column].equals("[ ]\t")) {
               // System.out.println("Free slot:" + i);
                return i;
            }
        }

        return 0;
    }

    private void drawField() {
        for(int i = 0; i < this.cols - 1; ++i) {
            System.out.println();

            for(int j = 0; j < this.rows - 1; ++j) {
                System.out.print(this.field[i][j]);
            }
        }

        System.out.println();
    }

    private void initField() {
        for(int i = 0; i < this.cols - 1; ++i) {
            for(int j = 0; j < this.rows - 1; ++j) {
                this.field[i][j] = "[ ]\t";
            }
        }

    }
}
