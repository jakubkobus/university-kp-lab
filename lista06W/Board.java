import java.util.ArrayList;
import java.util.List;

/**
 * Represents the simulation board with animals.
 */
public class Board {
  private final int n, m;
  private final Animal[][] grid;
  private Wolf wolf;
  private final List<Rabbit> rabbits = new ArrayList<>();

  public Board(int n, int m) {
    this.n = n;
    this.m = m;
    this.grid = new Animal[n][m];
  }

  public synchronized void addWolf(Wolf wolf) {
    this.wolf = wolf;
    grid[wolf.getX()][wolf.getY()] = wolf;
  }

  public synchronized void addRabbit(Rabbit rabbit) {
    rabbits.add(rabbit);
    grid[rabbit.getX()][rabbit.getY()] = rabbit;
  }

  public synchronized Animal getAnimalAt(int x, int y) {
    if (x < 0 || x >= n || y < 0 || y >= m)
      return null;
    return grid[x][y];
  }

  public synchronized Wolf getWolf() {
    return wolf;
  }

  public synchronized List<Rabbit> getRabbits() {
    return new ArrayList<>(rabbits);
  }

  public synchronized MoveStatus moveAnimal(Animal animal, int newX, int newY) {
    int oldX = animal.getX();
    int oldY = animal.getY();

    if (newX < 0 || newX >= n || newY < 0 || newY >= m)
      return MoveStatus.INVALID;

    Animal target = grid[newX][newY];

    if(animal instanceof Wolf)
      if(target instanceof Rabbit)
        if(target.isSuspended())
          return MoveStatus.INVALID;

    if (animal instanceof Wolf) {
      if (target instanceof Rabbit) {
        Rabbit rabbit = (Rabbit) target;
        rabbits.remove(rabbit);
        rabbit.interrupt();
        grid[newX][newY] = null;
      }
    }

    if(target != null && !(animal instanceof Wolf && target instanceof Rabbit))
      return MoveStatus.INVALID;

    grid[oldX][oldY] = null;
    animal.setPosition(newX, newY);
    grid[newX][newY] = animal;

    if(animal instanceof Wolf && target instanceof Rabbit)
      return MoveStatus.CAUGHT_RABBIT;

    return MoveStatus.MOVED;
  }

  public synchronized boolean isValidPosition(int x, int y) {
    return x >= 0 && x < n && y >= 0 && y < m;
  }

  public synchronized boolean isCellEmpty(int x, int y) {
    return grid[x][y] == null;
  }
}