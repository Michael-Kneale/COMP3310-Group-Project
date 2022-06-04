import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Counter extends Rectangle{
    private static int size = 70;
    protected Color backbgroundColor;
    protected Color textColor;
    protected char displayCharacter;

    public Counter(int x, int y){
        super(x,y,size,size);
        backbgroundColor = Color.BLACK;
        textColor = Color.WHITE;
    }

    void paint(Graphics g){
        g.setColor(backbgroundColor);
        
        g.fillRect(130,490,size,size);
        
        Font f = new Font("Arial", Font.PLAIN, 40);
        FontMetrics metrics = g.getFontMetrics(f);
        int drawXPos = x + ((size - metrics.stringWidth(""+displayCharacter))/2);
        int drawYPos = y + ((size + metrics.getHeight())/2 - 10);
    
        g.setFont(f); 
        g.drawString(""+displayCharacter, drawXPos, drawYPos);
    }
}
