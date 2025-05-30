import java.util.*;

/**
 * Main class to start the simulation.
 */
public class Main {
  public static void main(String[] args) {
    if (args.length < 4) {
      System.out.println("Usage: java Main n m rabbits k");
      return;
    }
    int n = Integer.parseInt(args[0]);
    int m = Integer.parseInt(args[1]);
    int numRabbits = Integer.parseInt(args[2]);
    int k = Integer.parseInt(args[3]);

    Board board = new Board(n, m);
    Random random = new Random();

    // Place Wolf
    int wolfX, wolfY;
    do {
      wolfX = random.nextInt(n);
      wolfY = random.nextInt(m);
    } while (board.getAnimalAt(wolfX, wolfY) != null);
    Wolf wolf = new Wolf(wolfX, wolfY, board, k, random);
    board.addWolf(wolf);

    // Place Rabbits
    for (int i = 0; i < numRabbits; i++) {
      int x, y;
      do {
        x = random.nextInt(n);
        y = random.nextInt(m);
      } while (board.getAnimalAt(x, y) != null);
      Rabbit rabbit = new Rabbit(x, y, board, k, random);
      board.addRabbit(rabbit);
      rabbit.start();
    }

    wolf.start();

    // Start GUI
    new SimulationGUI(board, n, m);
  }
}