import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Timer;

/**
 * GUI for the simulation.
 */
public class SimulationGUI extends Frame {
  private final Board board;
  private final Panel[][] cells;

  public SimulationGUI(Board board, int n, int m) {
    this.board = board;
    setTitle("Wolf and Rabbits Simulation");
    setLayout(new GridLayout(n, m));

    cells = new Panel[n][m];
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            Panel panel = new Panel();
            cells[i][j] = panel;
            add(panel);
            panel.addMouseListener(new CellMouseListener(i, j));
        }
    }

    setSize(800, 600);
    setVisible(true);

    Timer timer = new Timer(100, e -> refresh());
    timer.start();

    addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    });
  }

  private void refresh() {
    synchronized (board) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Animal animal = board.getAnimalAt(i, j);
                Panel panel = cells[i][j];
                if (animal instanceof Wolf) {
                    panel.setBackground(animal.isSuspended() ? Color.PINK : Color.RED);
                } else if (animal instanceof Rabbit) {
                    panel.setBackground(animal.isSuspended() ? Color.LIGHT_GRAY : Color.GRAY);
                } else {
                    panel.setBackground(Color.WHITE);
                }
            }
        }
    }
  }

private class CellMouseListener extends MouseAdapter {
    private final int x;
    private final int y;

    public CellMouseListener(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        synchronized (board) {
            Animal animal = board.getAnimalAt(x, y);
            if (animal != null) {
                animal.setSuspended(!animal.isSuspended());
            }
        }
    }
  }
}