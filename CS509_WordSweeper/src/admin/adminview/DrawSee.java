package admin.adminview;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import admin.admincontroller.ListGamesController;
import admin.admincontroller.ShowGameStateController;
import admin.adminmodel.AdministratorModel;
import admin.adminmodel.Game;
import client.ServerAccess; 

/**
 * This is the GUI class for interface.
 * 
 * @author Weihao Li
 *
 */
 
	  
  

public class DrawSee extends JFrame {
	/** implement singleton design pattern  */
	static AdministratorModel amodel;
	private static final long serialVersionUID = 1L;
	private static class DrawseeHolder{
	private static final DrawSee gui=new DrawSee(amodel);
	
	}
	public static DrawSee getGUI(){
		return DrawseeHolder.gui;
	}
	
	
    
    private static final int sx = 300;//x coordinate of square
    private static final int sy = 150;//y coordinate of square
    private static final int w = 40;//the lengths of edge of square 
    int rw;
    
    Container p;
    JScrollPane scrollPane;
    JPanel panel;
    JButton button;
    JButton button2;
    Choice gameChoice;
    JScrollPane scrollPane2;
    JScrollPane scrollPane3;
    JList<String> scorelist;
    JList<String> playerlist;
    JTextArea playerarea;
    JTextArea scorearea;
	
    private Graphics jg;
    Game g ;
    private Color rectColor = new Color(0xf5f5f5);
    private ServerAccess serverAccess;
	
    /** initiate components*/
    public DrawSee(AdministratorModel model) {
    	
    	amodel = model;
        p = getContentPane();
        setBounds(400, 100, 700, 500);
        
        p.setBackground(rectColor);
        setLayout(null);   
        setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(275, 100, 350, 350);
        getContentPane().add(scrollPane);
		
        JPanel panel = new JPanel();
        scrollPane.add(panel);
        setVisible(true);
		
        Choice gameChoice = new Choice();
        gameChoice.setBounds(45,55, 100, 30);
        getContentPane().add(gameChoice);
 		
        JButton button2 = new JButton("Select");
        button2.setBounds(175, 56, 80, 25);
        getContentPane().add(button2);
	
        button = new JButton("Refresh");
        button.setBounds(270, 56, 80, 25);
        p.add(button);
        button.addActionListener(new refreshAction());
		
	this.getGraphics();
	setVisible(true);
	    
	/** monitor selection button*/
	button2.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {     
	        	 new ShowGameStateController(DrawSee.this,amodel).process(gameChoice.getItem(gameChoice.getSelectedIndex()));
	         
	        	 playerarea = new JTextArea("");
	        	 playerarea.setForeground(Color.BLACK);
	        	 playerarea.setColumns(20);
	        	 playerarea.setRows(10);
	        	 playerarea.setEditable(false);
	        	 playerarea.setLineWrap(true);

	         		
	        	scrollPane2 = new JScrollPane();
	        	scrollPane2.setBounds(50, 100, 180, 350);
	        	scrollPane2.setViewportView(playerarea);
	        	add(scrollPane2);
	        
	         }
	      }); 
      
        gameComponents(gameChoice);
        
    }
    
    
    /** monitor refresh button*/
	class refreshAction implements ActionListener{
    	@Override
    	 public void actionPerformed(ActionEvent e) {
		if(amodel.obtainIdList().size()!=0){
    		repaint();
        	playerarea.setText("");
    		g =AdministratorModel.getGame(gameChoice.getItem(gameChoice.getSelectedIndex()));
    		g.getPlayerid().clear();
    		
    		gameChoice.removeAll();	   
    		amodel.obtainIdList().clear();
    		}  
		new ListGamesController(DrawSee.this,amodel).process();
    	    }
    }
	
    
public void gameComponents(Choice gamechoice){
	   	this.gameChoice=gamechoice;
   	for(String i:AdministratorModel.idlist){
		gameChoice.add(i);	   
	   }	        
   }
   
public Choice getchoice(){
	return gameChoice;
   }
   
public JList<String> getPlayerlist(){
	return playerlist;
}

public JScrollPane getScrollpane2(){
	return scrollPane2;
}

public JTextArea getPlayerarea(){
	return playerarea;
}

public JTextArea getScorearea(){
	return scorearea;
}

public void setPlayerList(JList<String> pllist,ArrayList<String> playerid){
	this.playerlist=pllist;
	String[] arr= new String[playerid.size()] ;
	arr=(String [])playerid.toArray();
	playerlist.setListData(arr);;
	
}
	
	/** paint global board*/
public void paintComponents(Graphics g,Game game) {
    	
        int size=game.getSize();
        rw=size*40;
       
       
        // set line color
        g.setColor(Color.BLACK);
        
        // draw outer square
        g.drawRect(sx, sy, rw, rw);
        
        
        for(int i = 0; i < size; i ++) {
            // draw inner square
            for(int j=0;j<size;j++){
            g.drawRect(sx + (i * w), sy + (j * w), w, w);}
        }   
            // fill the square
        
        ArrayList<Integer> playerlocation=game.getPlayerlocation();
        for(int i=0;i< playerlocation.size();i++){
        	Color Color = new Color(220+(int)(Math.random()*35),220+(int)(Math.random()*35),220+(int)(Math.random()*35));
			g.setColor(Color);
			g.fillRect((sx-w+((int)playerlocation.get(i)/10)*40),(sy-w+((int)playerlocation.get(i)%10)*40), 160, 160);
        }
      

        for(int i = 0; i <size; i ++){
            for(int j = 0; j < size; j ++) {
                drawString(g, j, i, game);                    
            }
        }
  
}
	/** draw letters in global board*/
    private void drawString(Graphics g, int x, int y, Game game) {
    				String[][] globalboard=game.getGlobalboard();
    	             g.setColor(Color.BLACK);
    	             g.setFont(new Font("Arial", 0, 25)); 
    	             g.drawString(globalboard[x][y] + "", sx + (y  * w) + 5, sy + ((x + 1) * w) - 5);
    	      
    	     }

	/** Record the means to communicate with server. */
	public void setServerAccess(ServerAccess access) {
		this.serverAccess = access;
	}
	
	/** Get the server access object. */
	public ServerAccess getServerAccess() {
		return serverAccess;
	}
	
    	 
    
}
