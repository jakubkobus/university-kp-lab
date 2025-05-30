import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a Wolf thread.
 */
public class Wolf extends Animal {
  public Wolf(int x, int y, Board board, int k, Random random) {
    super(x, y, board, k, random);
  }

  @Override
  public void run() {
    while (!isInterrupted()) {
      try {
        if (restCycles > 0) {
          restCycles--;
          Thread.sleep(getDelay());
          continue;
        }

        if (suspended) {
          Thread.sleep(getDelay());
          continue;
        }

        List<Rabbit> rabbits = board.getRabbits();
        if (rabbits.isEmpty()) {
          interrupt();
          break;
        }

        Rabbit closest = findClosestRabbit(rabbits);
        if (closest == null)
          continue;

        List<Point> possibleMoves = generatePossibleMoves();
        Point bestMove = selectBestMove(closest, possibleMoves);

        if (bestMove != null) {
          MoveStatus status = board.moveAnimal(this, bestMove.x, bestMove.y);
          if (status == MoveStatus.CAUGHT_RABBIT) {
            restCycles = 5;
          }
        }

        Thread.sleep(getDelay());
      } catch (InterruptedException e) {
        interrupt();
        break;
      }
    }
  }

  private Rabbit findClosestRabbit(List<Rabbit> rabbits) {
    Rabbit closest = null;
    double minDist = Double.MAX_VALUE;
    for (Rabbit r : rabbits) {
      if (r.isSuspended())
        continue;
      double dist = distance(r.getX(), r.getY());
      if (dist < minDist) {
        minDist = dist;
        closest = r;
      }
    }
    return closest;
  }

  private double distance(int x2, int y2) {
    return Math.sqrt(Math.pow(x - x2, 2) + Math.pow(y - y2, 2));
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

  private Point selectBestMove(Rabbit target, List<Point> possibleMoves) {
    double minDist = Double.MAX_VALUE;
    List<Point> bestMoves = new ArrayList<>();

    for (Point move : possibleMoves) {
      Animal animal = board.getAnimalAt(move.x, move.y);
      if (animal instanceof Rabbit) {
        bestMoves.clear();
        bestMoves.add(move);
        minDist = 0;
        break;
      } else if (animal == null) {
        double dist = Math.sqrt(Math.pow(move.x - target.getX(), 2) + Math.pow(move.y - target.getY(), 2));
        if (dist < minDist) {
          minDist = dist;
          bestMoves.clear();
          bestMoves.add(move);
        } else if (dist == minDist) {
          bestMoves.add(move);
        }
      }
    }

    if (bestMoves.isEmpty())
      return null;
    return bestMoves.get(random.nextInt(bestMoves.size()));
  }
}