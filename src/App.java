import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.time.Duration;
import java.time.Instant;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;


public class App extends JFrame {

    static {
        // must set before the Logger
        // loads logging.properties 
        try {
            //If the program cannot find the file, right-click on the .properties file, and get the relative path
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            //No logger, yet. Printing to console
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());

    class WordleGame extends JPanel implements KeyListener{
        Board board;
        boolean stageBuilt = false;

        public WordleGame(){
            setPreferredSize(new Dimension(330, 570));
            this.addKeyListener(this);
            board = new Board();
            stageBuilt = true;
            this.setFocusable(true);
            this.requestFocus();
        }

        @Override
        public void paint(Graphics g) {
          if (stageBuilt && isVisible()) {
            board.paint(g);
          }
        }

        @Override
        public void keyPressed (KeyEvent e) {
            //Intentionally left empty
        }    
        
        @Override
        public void keyReleased (KeyEvent e) {
            board.keyPressed(e);
        }    

        @Override
        public void keyTyped (KeyEvent e) {
            //Intentionally left empty
        }    

    }

    public static void main(String[] args) throws Exception {
        App window = new App();
        window.run();
    }

    private App() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        WordleGame canvas = new WordleGame();
        this.setContentPane(canvas);
        this.pack(); 
        this.setVisible(true);
    }

    public void run() {
        while (true) {
            Instant startTime = Instant.now();
            this.repaint();
            Instant endTime = Instant.now();
            long howLong = Duration.between(startTime, endTime).toMillis();
            try {
                Thread.sleep(20L - howLong);
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Thread was interrupted", e);
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "Application can't keep up with framerate", e);
            }
        }
    }

}
