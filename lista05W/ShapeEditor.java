import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.awt.geom.*;
import javax.swing.*; // Added for JColorChooser

// Shape interface for common figure operations
interface Shape extends Serializable {
    void draw(Graphics g);
    boolean contains(int x, int y);
    void moveBy(int dx, int dy);
    void resize(double factor);
    void rotate(double degrees);
    void setColor(Color color);
    Color getColor();
}

// Circle implementation
class MyCircle implements Shape {
    private int x, y, radius;
    private Color color = Color.BLACK;
    private double rotation = 0;

    public MyCircle(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(Math.toRadians(rotation), x + radius, y + radius);
        g2d.setColor(color);
        g2d.fillOval(x, y, radius*2, radius*2);
        g2d.dispose();
    }

    public boolean contains(int mx, int my) {
        return new Ellipse2D.Float(x, y, radius*2, radius*2).contains(mx, my);
    }

    public void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void resize(double factor) {
        radius = (int) (radius * factor);
    }

    public void rotate(double degrees) {
        rotation += degrees;
    }

    public void setColor(Color color) { this.color = color; }
    public Color getColor() { return color; }
}

// Rectangle implementation
class MyRectangle implements Shape {
    private int x, y, width, height;
    private Color color = Color.BLACK;
    private double rotation = 0;

    public MyRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(Math.toRadians(rotation), x + width/2.0, y + height/2.0);
        g2d.setColor(color);
        g2d.fillRect(x, y, width, height);
        g2d.dispose();
    }

    public boolean contains(int mx, int my) {
        Point2D center = new Point2D.Double(x + width/2.0, y + height/2.0);
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(-rotation), center.getX(), center.getY());
        Point2D transformed = transform.transform(new Point(mx, my), null);
        return new Rectangle(x, y, width, height).contains(transformed);
    }

    public void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void resize(double factor) {
        int newWidth = (int)(width * factor);
        int newHeight = (int)(height * factor);
        x -= (newWidth - width)/2;
        y -= (newHeight - height)/2;
        width = newWidth;
        height = newHeight;
    }

    public void rotate(double degrees) { rotation += degrees; }
    public void setColor(Color color) { this.color = color; }
    public Color getColor() { return color; }
}

// Polygon implementation
class MyPolygon implements Shape {
    private ArrayList<Point> points = new ArrayList<>();
    private Color color = Color.BLACK;
    private double rotation = 0;

    public MyPolygon(ArrayList<Point> points) {
        this.points = new ArrayList<>(points);
    }

    public void draw(Graphics g) {
        if(points.size() < 2) return;
        Graphics2D g2d = (Graphics2D) g.create();
        
        Point2D center = getCenter();
        g2d.rotate(Math.toRadians(rotation), center.getX(), center.getY());
        
        g2d.setColor(color);
        int[] xPoints = points.stream().mapToInt(p -> p.x).toArray();
        int[] yPoints = points.stream().mapToInt(p -> p.y).toArray();
        g2d.fillPolygon(xPoints, yPoints, points.size());
        g2d.dispose();
    }

    public boolean contains(int mx, int my) {
        Point2D center = getCenter();
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(-rotation), center.getX(), center.getY());
        Point2D transformed = transform.transform(new Point(mx, my), null);
        return new Polygon(points.stream().mapToInt(p -> p.x).toArray(),
                          points.stream().mapToInt(p -> p.y).toArray(),
                          points.size()).contains(transformed);
    }

    private Point2D getCenter() {
        double sumX = 0, sumY = 0;
        for(Point p : points) {
            sumX += p.x;
            sumY += p.y;
        }
        return new Point2D.Double(sumX/points.size(), sumY/points.size());
    }

    public void moveBy(int dx, int dy) {
        for(Point p : points) {
            p.x += dx;
            p.y += dy;
        }
    }

    public void resize(double factor) {
        Point2D center = getCenter();
        for(Point p : points) {
            double dx = p.x - center.getX();
            double dy = p.y - center.getY();
            p.x = (int)(center.getX() + dx * factor);
            p.y = (int)(center.getY() + dy * factor);
        }
    }

    public void rotate(double degrees) { rotation += degrees; }
    public void setColor(Color color) { this.color = color; }
    public Color getColor() { return color; }
}

// Main editor class
public class ShapeEditor extends Frame {
    protected String currentShape = "Circle";
    private DrawingPanel drawingPanel = new DrawingPanel();
    private PopupMenu colorMenu = new PopupMenu();
    
    public ShapeEditor() {
        setSize(800, 600);
        setTitle("Shape Editor");
        setupMenu();
        setupColorMenu();
        add(drawingPanel);
        setupInfoButton();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }

    private void setupMenu() {
        MenuBar mb = new MenuBar();
        
        Menu fileMenu = new Menu("File");
        MenuItem save = new MenuItem("Save");
        save.addActionListener(e -> saveShapes());
        fileMenu.add(save);
        
        MenuItem load = new MenuItem("Load");
        load.addActionListener(e -> loadShapes());
        fileMenu.add(load);
        
        Menu shapesMenu = new Menu("Shapes");
        String[] shapes = {"Circle", "Rectangle", "Polygon"};
        for(String shape : shapes) {
            MenuItem item = new MenuItem(shape);
            item.addActionListener(e -> {
                currentShape = shape;
                drawingPanel.setPolygonMode(shape.equals("Polygon"));
            });
            shapesMenu.add(item);
        }
        
        Menu helpMenu = new Menu("Help");
        MenuItem info = new MenuItem("Info");
        info.addActionListener(e -> showInfo());
        helpMenu.add(info);
        
        mb.add(fileMenu);
        mb.add(shapesMenu);
        mb.add(helpMenu);
        setMenuBar(mb);
    }

    private void setupColorMenu() {
        MenuItem colorItem = new MenuItem("Change Color");
        colorItem.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Color", 
                drawingPanel.getActiveColor());
            if(newColor != null) drawingPanel.setActiveColor(newColor);
        });
        colorMenu.add(colorItem);
        drawingPanel.add(colorMenu);
    }

    private void setupInfoButton() {
        Button infoBtn = new Button("Info");
        infoBtn.addActionListener(e -> showInfo());
        add(infoBtn, BorderLayout.SOUTH);
    }

    private void showInfo() {
        Dialog info = new Dialog(this, "About", true);
        info.setLayout(new GridLayout(4, 1));
        info.add(new Label("Shape Editor 2025"));
        info.add(new Label("Author: Java AWT Programmer"));
        info.add(new Label("Instructions:"));
        info.add(new Label("1. Select shape from menu\n2. Click/draw to create\n3. Right-click to modify"));
        info.pack();
        info.setVisible(true);
    }

    private void saveShapes() {
        FileDialog fd = new FileDialog(this, "Save", FileDialog.SAVE);
        fd.setVisible(true);
        if(fd.getFile() == null) return;
        try(ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(fd.getDirectory() + fd.getFile()))) {
            oos.writeObject(drawingPanel.getShapes());
        } catch(Exception ex) { ex.printStackTrace(); }
    }

    private void loadShapes() {
        FileDialog fd = new FileDialog(this, "Load", FileDialog.LOAD);
        fd.setVisible(true);
        if(fd.getFile() == null) return;
        try(ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(fd.getDirectory() + fd.getFile()))) {
            drawingPanel.setShapes((ArrayList<Shape>) ois.readObject());
        } catch(Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) {
        new ShapeEditor().setVisible(true);
    }
}

// Drawing panel handling all interactions
class DrawingPanel extends Canvas {
    private ArrayList<Shape> shapes = new ArrayList<>();
    private Shape activeShape;
    private Point startPoint;
    private boolean polygonMode = false;
    private ArrayList<Point> polygonPoints = new ArrayList<>();
    private PopupMenu contextMenu;

    public DrawingPanel() {
        setBackground(Color.WHITE);
        setupContextMenu();
        setupListeners();
    }

    private void setupContextMenu() {
        contextMenu = new PopupMenu();
        MenuItem colorItem = new MenuItem("Change Color");
        colorItem.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Color", 
                getActiveColor());
            if(newColor != null) setActiveColor(newColor);
        });
        contextMenu.add(colorItem);
        add(contextMenu);
    }

    private void setupListeners() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(polygonMode && e.getButton() == MouseEvent.BUTTON1) {
                    polygonPoints.add(e.getPoint());
                    repaint();
                } else {
                    startPoint = e.getPoint();
                    selectShape(e.getX(), e.getY());
                }
            }

            public void mouseReleased(MouseEvent e) {
                if(polygonMode) return;
                
                if(currentShape() != null) {
                    shapes.add(currentShape());
                    activeShape = currentShape();
                    repaint();
                }
            }

            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {
                    if(activeShape != null && activeShape.contains(e.getX(), e.getY())) {
                        contextMenu.show(DrawingPanel.this, e.getX(), e.getY());
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if(activeShape != null && !polygonMode) {
                    int dx = e.getX() - startPoint.x;
                    int dy = e.getY() - startPoint.y;
                    activeShape.moveBy(dx, dy);
                    startPoint = e.getPoint();
                    repaint();
                }
            }
        });

        addMouseWheelListener(e -> {
            if(activeShape == null) return;
            if(e.isControlDown()) {
                activeShape.rotate(e.getWheelRotation() * 5);
            } else {
                activeShape.resize(1 + e.getWheelRotation() * 0.1);
            }
            repaint();
        });
    }

    private Shape currentShape() {
        if(polygonMode) {
            if(polygonPoints.size() < 3) return null;
            Shape poly = new MyPolygon(new ArrayList<>(polygonPoints));
            polygonPoints.clear();
            polygonMode = false;
            return poly;
        }
        
        int x1 = startPoint.x;
        int y1 = startPoint.y;
        int x2 = getMousePosition().x;
        int y2 = getMousePosition().y;
        
        switch(((ShapeEditor)getParent()).currentShape) {
            case "Circle":
                int radius = (int) Math.hypot(x2-x1, y2-y1);
                return new MyCircle(x1-radius, y1-radius, radius);
            case "Rectangle":
                return new MyRectangle(
                    Math.min(x1, x2), Math.min(y1, y2),
                    Math.abs(x2-x1), Math.abs(y2-y1));
            default: return null;
        }
    }

    private void selectShape(int x, int y) {
        for(int i = shapes.size()-1; i >= 0; i--) {
            if(shapes.get(i).contains(x, y)) {
                activeShape = shapes.get(i);
                repaint();
                return;
            }
        }
        activeShape = null;
    }

    public void paint(Graphics g) {
        for(Shape shape : shapes) shape.draw(g);
        if(activeShape != null) {
            activeShape.draw(g);
        }
        if(polygonMode) {
            g.setColor(Color.RED);
            for(Point p : polygonPoints) {
                g.fillOval(p.x-3, p.y-3, 6, 6);
            }
        }
    }

    public void setPolygonMode(boolean mode) { 
        polygonMode = mode;
        if(mode) polygonPoints.clear();
    }

    public ArrayList<Shape> getShapes() { return shapes; }
    public void setShapes(ArrayList<Shape> shapes) { 
        this.shapes = shapes;
        repaint();
    }
    
    public Color getActiveColor() { 
        return activeShape != null ? activeShape.getColor() : Color.BLACK;
    }
    
    public void setActiveColor(Color color) {
        if(activeShape != null) {
            activeShape.setColor(color);
            repaint();
        }
    }
}