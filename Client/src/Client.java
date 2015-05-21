import java.io.*;
import java.net.Socket;

/**
 * Created by Martin on 19.05.2015.
 */
public class Client{
    Socket client;
    String[][] field;
    private Player player;
    BufferedReader reader;
    ObjectInputStream objectStream;

    public static void main(String[] args){
        new Client().connect();
    }


    public Client(){
        player = new Player();
    }

    public void getMessageFromServer(String msg){
        System.out.println(msg);
    }
    public void updateField(String[][] f){
        field = f;
        drawField();
    }

    private void connect(){
        try {
            client = new Socket("localhost",1530);
            objectStream = new ObjectInputStream(client.getInputStream());
            reader = new BufferedReader(new InputStreamReader(objectStream,"UTF-16"));
            System.out.println("Listening...");
            while(true){
                //Client is listening for commands
                String command = (String) reader.readLine();
                if(command.getClass().isInstance(String[][].class) == false)
                    System.out.println("NO STRING[][]");
                else
                    System.out.println("GOT A STRING ARRAY!");

                System.out.println("\t######## RECEIVED THIS: " + command + "########");
                System.out.println(command);
                getCommandFromServer(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawField(){
        for(int i = 0; i < 6; i++){
            System.out.println();
            for(int j=0; j < 5; j++){
                System.out.print(field[i][j]);
            }
        }System.out.println();
    }

    public void getCommandFromServer(String s){
            switch(s){
            case "makeToken1":
                player.setToken("[ X ]\t");
                System.out.println("OMG");
                break;
            case "makeToken2":
                player.setToken("[ O ]\t");
                break;
            case "updateField":
                try {
                    field = (String[][]) objectStream.readObject();
                    System.out.println("I got a field!");
                    updateField(field);
                } catch (Exception e){
                    System.out.println("Couldn't get a Field!");
                }

                break;

            case "makeTurn":
                try {
                    client.getOutputStream().write(player.makeTurn());
                } catch(Exception e){
                    System.out.println("Couldn't send player turn to server!");
                }
                break;

            default:
                System.out.println("Hey Server, I don't understand you!");
                System.out.println("All I got is this: " + s);
                break;

        }
    }

}

