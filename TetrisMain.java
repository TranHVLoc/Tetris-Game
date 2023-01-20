/**
 * @author Loc Tran
 * Tetris game
 */

package com.sumit.plajava;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TetrisMain extends JPanel {
    
    /**
     * Create a list of Shapes based on Points
     */
    private final Point[][][] myPoint = {
            {
                // I
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},
            },
            
            {
                // J
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0)},
            },
            
            {
                // L
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
                {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
                {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0)},
            },
            
            {
                // 0
                {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            },
    };

    /**
     * This is a list of Shape color
     */
    private final Color[] shapeColor = {Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.YELLOW, Color.BLACK, Color.PINK, Color.RED};
    
    /**
     * This is the starting point of the shape (5, 2)
     */
    private Point pt;
    
    /**
     * This is the current shape that players are dealing with
     */
    private int currentPiece;
    
    /**
     * This is the rotation number
     */
    private int rotation;
    
    /**
     * This is a list of next pieces being ready
     */
    private ArrayList<Integer> nextPiece = new ArrayList<Integer>();
    
    /**
     * This is a player's score
     */
    private long score;
    
    /**
     * This is the range of shape's color 
     */
    private Color[][] well;
    
    /**
     * This method create the playground of the game and initialize a new Shape for players.
     * The edges will be filled with PINK while the middle field will be filled with BLACK
     */
    private void init() {
        well = new Color[12][24];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                if (i == 0 || i == 11 || j == 22) {
                    well[i][j] = Color.PINK;
                } else {
                    well[i][j] = Color.BLACK;
                }
            }
        }
        // Create a new piece
        newPiece();
    }
    
    
    public void newPiece() {
        pt = new Point(5, 2);
        rotation = 0;
        if (nextPiece.isEmpty()) {
            Collections.addAll(nextPiece, 0, 1, 2, 3);
            Collections.shuffle(nextPiece);
        }
        
        currentPiece = nextPiece.get(0);
        nextPiece.remove(0);
    }
    
    
    private boolean collidesAt(int x, int y, int rotation) {
        for (Point p : myPoint[currentPiece][rotation]) {
            if (well[p.x + x][p.y + y] != Color.BLACK) {
                return true;
            }
        }
        return false;
    }
    
    
    private void rotate(int i) {
        int newRotation = (rotation + i) % 4;
        
        if (newRotation < 0) {
            newRotation = 3;
        }
        
        if (!collidesAt(pt.x, pt.y, newRotation)) {
            rotation = newRotation;
        }
        
        repaint();
    }
    
    
    public void move(int i) {
        if (!collidesAt(pt.x + i, pt.y, rotation)) {
           pt.x += i; 
        }
        
        repaint();
    }
    
    
    public void drop() {
        if (!collidesAt(pt.x, pt.y + 1, rotation)) {
            pt.y += 1;
        } else {
            fixToWell();
        }
        
        repaint();
    }
    
    
    public void fixToWell() {
        for (Point p : myPoint[currentPiece][rotation]) {
            well[pt.x + p.x][pt.y + p.y] = shapeColor[currentPiece];
        }
        
        clearRows();
        newPiece();
    }
    
    
    public void deleteRow(int row) {
        for (int i = row - 1; i > 0; i--) {
            for (int j = 1; j < 11; j++) {
                well[j][i + 1] = well[j][i];
            }
        }
    }
    
    public void clearRows() {
        boolean gap;
        int numClear = 0;
        for (int i = 21; i > 0; i--) {
            gap = false;
            for (int j = 1; j < 11; j++) {
                if (well[j][i] == Color.BLACK) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                deleteRow(i);
                i += 1;
                numClear += 1;
            }
        }
        switch (numClear) {
        case 1:
            score += 100;
            break;
        case 2:
            score += 300;
            break;
        case 3:
            score += 500;
            break;
        case 4:
            score += 800;
            break;
        }
    }
    
    
    private void drawPiece(Graphics g) {
        g.setColor(shapeColor[currentPiece]);
        
        for (Point p : myPoint[currentPiece][rotation]) {
            g.fillRect((p.x + pt.x) * 26, (p.y + pt.y) * 26, 25, 25);
        }
    }
    
    public void paintComponent(Graphics g) {
        g.fillRect(0, 0, 26 * 12, 26 * 23);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                g.setColor(well[i][j]);
                g.fillRect(26 * i, 26 * j, 25, 25);
            }
        }
        
        g.setColor(Color.WHITE);
        g.drawString("Score is: " + score, 19 * 12, 25);
        drawPiece(g);
    }
    
    
    
    public static void main(String[] args) {
        
        JFrame f = new JFrame("Tetris");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(12 * 26 + 10, 26 * 23 + 25);
        
        final TetrisMain tetris = new TetrisMain();
        tetris.init();
        f.add(tetris);
        
        f.addKeyListener(new KeyListener() {
            
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    tetris.rotate(-1);
                    break;
                    
                case KeyEvent.VK_SPACE:
                    tetris.rotate(1);
                    break;
                    
                case KeyEvent.VK_LEFT:
                    tetris.move(-1);
                    break;
                    
                case KeyEvent.VK_RIGHT:
                    tetris.move(1);
                    break;
                    
                case KeyEvent.VK_DOWN:
                    tetris.drop();
                    tetris.score += 1;
                    break;
                
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(500);
                        tetris.drop();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        
        f.setVisible(true);
    }

}
