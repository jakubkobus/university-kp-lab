import java.util.Random;

/**
 * Base class for animals (Wolf and Rabbit).
 */
abstract class Animal extends Thread {
  protected int x, y;
  protected final Board board;
  protected final int k;
  protected final Random random;
  protected volatile boolean suspended = false;
  protected volatile int restCycles = 0;

  public Animal(int x, int y, Board board, int k, Random random) {
    this.x = x;
    this.y = y;
    this.board = board;
    this.k = k;
    this.random = random;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void setSuspended(boolean suspended) {
    this.suspended = suspended;
  }

  public boolean isSuspended() {
    return suspended;
  }

  protected int getDelay() {
    return (int)(k * (0.5 + random.nextFloat()));
  }
}