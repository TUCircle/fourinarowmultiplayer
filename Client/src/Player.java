import java.io.Serializable;
import java.util.Scanner;

/**
 * Created by Martin on 20.05.2015.
 */
public class Player{
    String[][] field;
    public String token;
    Scanner scanner;
    String player;

    public Player(){
       scanner = new Scanner(System.in);
    }

    public int makeTurn(){

        int col = scanner.nextInt();
        return col;
    }
    public void printHello(){
        System.out.println(this.toString() + ": HELLO!");
    }
    public void setToken(String t){
        token = t;
    }
}
