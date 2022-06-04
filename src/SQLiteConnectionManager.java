import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.io.FileInputStream;
import java.io.IOException;

public class SQLiteConnectionManager {

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

    //private Connection wordleDBConn = null;
    private String databaseURL = "";
    
    private String wordleDropTableString = "DROP TABLE IF EXISTS wordlist;";
    private String wordleCreateString = 
          "CREATE TABLE wordlist (\n" 
        + "	id integer PRIMARY KEY,\n"
        + "	word text NOT NULL\n"
        + ");";
    
    private String validWordsDropTableString = "DROP TABLE IF EXISTS validWords;";
    private String validWordsCreateString = 
          "CREATE TABLE validWords (\n" 
        + "	id integer PRIMARY KEY,\n"
        + "	word text NOT NULL\n"
        + ");";

    //private String populateWordle;
    //private String populateValidWords;


    /**
     * Set the database file name in the sqlite project to use
     *
     * @param fileName the database file name
     */
    public SQLiteConnectionManager(String filename)
    {
        databaseURL = "jdbc:sqlite:sqlite/" + filename;
    }

    /**
     * Connect to a sample database
     *
     * @param fileName the database file name
     */
    public void createNewDatabase(String fileName) {

        try (Connection conn = DriverManager.getConnection(databaseURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                logger.log(Level.INFO, "The driver name is " + meta.getDriverName());
                logger.log(Level.INFO, "A new database has been created.");
                
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
    }

    /**
     * Check that the file has been cr3eated
     *
     * @return true if the file exists in the correct location, false otherwise. If no url defined, also false.
     */
    public boolean checkIfConnectionDefined(){
        if(databaseURL == ""){
            return false;
        }else{
            try (Connection conn = DriverManager.getConnection(databaseURL)) {
                if (conn != null) {
                    return true; 
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "SQLException", e);
                return false;
            }
        }
        return true;
    }

    /**
     * Create the table structures (2 tables, wordle words and valid words)
     *
     * @return true if the table structures have been created.
     */
    public boolean createWordleTables(){
        if(databaseURL != ""){
            try (   Connection conn = DriverManager.getConnection(databaseURL);
                    Statement stmt = conn.createStatement()
                ) 
            {
                if (conn != null) {
                    stmt.execute(wordleDropTableString);
                    stmt.execute(wordleCreateString);
                    stmt.execute(validWordsDropTableString);
                    stmt.execute(validWordsCreateString);
                    return true;  
                } 
            } catch (SQLException e) {
                logger.log(Level.WARNING, "SQLException", e);
                return false;
            }
            
        }
        return false;
        
    }

    /**
     * Take an id and a word and store the pair in the valid words
     * @param id the unique id for the word
     * @param word the word to store
     */
    public void addValidWord(int id, String word){

        String sql = "INSERT INTO validWords(id,word) VALUES(?,?)";

        try (Connection conn = DriverManager.getConnection(databaseURL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, word);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }

    }
    /**
     * get the entry in the validWords database
     * @param index the id of the word entry to get
     * @return
     */
    public String getWordAtIndex(int index){
        String sql = "SELECT word FROM validWords where id="+index+";";
        String result = "";
        try (Connection conn = DriverManager.getConnection(databaseURL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //pstmt.setInt(1, index);
            ResultSet cursor = pstmt.executeQuery();
            if(cursor.next()){
                logger.log(Level.INFO, "successful next curser sqlite");
                result = cursor.getString(1);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQLException", e);
        }
        logger.log(Level.INFO,"getWordAtIndex===========================");
        logger.log(Level.INFO,"sql: " + sql);
        logger.log(Level.INFO,"result: " + result);

        return result;
    }

    /**
     * Possible weakness here?
     * @param guess the string to check if it is a valid word.
     * @return true if guess exists in the database, false otherwise
     */
    public boolean isValidWord(String guess)
    {
        //Parameterized Statement (Option 1 defense)
        String sql = "SELECT count(id) as total FROM validWords WHERE word like ?;";
        
        try (   Connection conn = DriverManager.getConnection(databaseURL);
                    PreparedStatement stmt = conn.prepareStatement(sql)
                ) 
            {
                if (conn != null) {
                    stmt.setString(1,guess);
                    ResultSet resultRows  = stmt.executeQuery();
                    while (resultRows.next())
                    {
                        int result = resultRows.getInt("total");
                        logger.log(Level.INFO,"Total found:" + result);
                        if(result >= 1)
                        {
                            return true;
                        } 
                        else
                        {
                            return false;
                        }
                    }
                     
                }
                return false;

            } catch (SQLException e) {
                logger.log(Level.WARNING, "SQLException", e);
                return false;
            }

    }
}
