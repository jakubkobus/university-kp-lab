import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a Rabbit thread.
 */
public class Rabbit extends Animal {
  public Rabbit(int x, int y, Board board, int k, Random random) {
    super(x, y, board, k, random);
  }

  @Override
  public void run() {
    while (!isInterrupted()) {
      try {
        if (suspended) {
          Thread.sleep(getDelay());
          continue;
        }

        Wolf wolf = board.getWolf();
        if (wolf == null)
          continue;

        List<Point> possibleMoves = generatePossibleMoves();
        List<Point> validMoves = filterValidMoves(possibleMoves);
        List<Point> awayMoves = filterAwayMoves(validMoves, wolf.getX(), wolf.getY());

        List<Point> selectedMoves = awayMoves.isEmpty() ? validMoves : awayMoves;
        if (!selectedMoves.isEmpty()) {
          Point move = selectedMoves.get(random.nextInt(selectedMoves.size()));
          board.moveAnimal(this, move.x, move.y);
        }

        Thread.sleep(getDelay());
      } catch (InterruptedException e) {
        interrupt();
        break;
      }
    }
  }

  private List<Point> generatePossibleMoves() {
    List<Point> moves = new ArrayList<>();
    for (int dx = -1; dx <= 1; dx++) {
      for (int dy = -1; dy <= 1; dy++) {
        if (dx == 0 && dy == 0)
          continue;
        int newX = x + dx;
        int newY = y + dy;
        if (board.isValidPosition(newX, newY)) {
          moves.add(new Point(newX, newY));
        }
      }
    }
    return moves;
  }

  private List<Point> filterValidMoves(List<Point> moves) {
    List<Point> valid = new ArrayList<>();
    for (Point move : moves) {
      if (board.isCellEmpty(move.x, move.y)) {
        valid.add(move);
      }
    }
    return valid;
  }

  private List<Point> filterAwayMoves(List<Point> moves, int wolfX, int wolfY) {
    List<Point> away = new ArrayList<>();
    double currentDist = Math.sqrt(Math.pow(x - wolfX, 2) + Math.pow(y - wolfY, 2));
    for (Point move : moves) {
      double newDist = Math.sqrt(Math.pow(move.x - wolfX, 2) + Math.pow(move.y - wolfY, 2));
      if (newDist > currentDist) {
        away.add(move);
      }
    }
    return away;
  }
}