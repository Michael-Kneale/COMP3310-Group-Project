import java.awt.Graphics;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
// import java.util.Scanner;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.io.FileInputStream;

public class Board {

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

    //import logger
    private static final Logger logger = Logger.getLogger(App.class.getName());

    Grid grid;
    Counter counter;
    SQLiteConnectionManager wordleDatabaseConnection;
    int secretWordIndex;
    int numberOfWords;
    Random rand = new Random();

    public Board(){
        wordleDatabaseConnection = new SQLiteConnectionManager("words.db");
        int setupStage = 0;

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined())
        {
            logger.log(Level.INFO, "Wordle created and connected.");
            if(wordleDatabaseConnection.createWordleTables())
            {
                logger.log(Level.INFO, "Wordle structures in place.");
                setupStage = 1;
            }
        }

        if(setupStage == 1)
        {
            //let's add some words to valid 4 letter words from the data.txt file

            try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
                String line;
                int i = 1;
                while ((line = br.readLine()) != null) {
                   //System.out.println(line);
                   //Checks if String is lowercase letters a-z and 4 letters
                   if(line.matches("^[a-z]{4}$")){
                   wordleDatabaseConnection.addValidWord(i,line);
                }
                   i++;
                }
                numberOfWords = i;
                setupStage = 2;
            }catch(IOException e)
            {
                logger.log(Level.WARNING, "IOException", e);
            }

        }
        else{
            logger.log(Level.SEVERE, "Not able to Launch. Sorry!");
        }



        grid = new Grid(6,4, wordleDatabaseConnection);
        counter = new Counter(570, 10);
        //Selects the secret word from a random index
        secretWordIndex = rand.nextInt(numberOfWords);
        String theWord = wordleDatabaseConnection.getWordAtIndex(secretWordIndex);
        grid.setWord(theWord);
    }

    public void resetBoard(){
        grid.reset();
    }

    void paint(Graphics g){
        grid.paint(g);
        counter.paint(g);
    }    

    public void keyPressed(KeyEvent e){
        logger.log(Level.INFO, "Key Pressed! " + e.getKeyCode());

        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            grid.keyPressedEnter();
            logger.log(Level.INFO, "Enter Key");
        }
        if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
            grid.keyPressedBackspace();
            logger.log(Level.INFO, "Backspace Key");
        }
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            grid.keyPressedEscape();
            
            //Selects the secret word from a random index
            int secretWordIndex = rand.nextInt(numberOfWords);

            //secretWordIndex = ( secretWordIndex + 1 ) % numberOfWords;
            String theWord = wordleDatabaseConnection.getWordAtIndex(secretWordIndex);
            grid.setWord(theWord);

            logger.log(Level.INFO, "Escape Key");
        }
        if(e.getKeyCode()>= KeyEvent.VK_A && e.getKeyCode() <= KeyEvent.VK_Z){
            grid.keyPressedLetter(e.getKeyChar());
            logger.log(Level.INFO, "Character Key");
        }

    }
}
