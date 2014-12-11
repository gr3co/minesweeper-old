import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

public class Minesweeper{

    private PlayBoard board;
    public Minesweeper(){
	board = new PlayBoard(this);
    }

    public static void main(String [] args){
	Minesweeper game = new Minesweeper();
    }


    /*
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
*/


    private class PlayBoard extends JFrame implements ActionListener,MouseListener{

	Patch[][] grid;
	HashSet clicked;
	int row, col;
	int totalBombs;
	Minesweeper game;
	JMenuItem difficulty, newgame;
	JPanel panel;
	int started;


	public PlayBoard(Minesweeper g){
	    super();
	    game = g;
	    newgame = new JMenuItem("New Game");
	    difficulty = new JMenuItem("Options");
	    panel = new JPanel();
	    JMenuBar bar = new JMenuBar();
	    JMenu menu = new JMenu();
	    newgame.addActionListener(this);
	    bar.add(newgame);
	    difficulty.addActionListener(this);
	    bar.add(difficulty);
	    setJMenuBar(bar);
	    super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    super.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    super.pack();
	    super.setTitle("Minesweeper");
	    super.setVisible(true);
	    super.setLocation(300,300);
	    started = 0;
	    clicked = new HashSet();
	}

	public Minesweeper getGame(){
	    return game;
	}

	public void fillBoard(int x, int y) {
	    if (started == 0){
		int remainingBombs = totalBombs;
		Random gen = new Random();
		while (remainingBombs != 0){
		    int a = gen.nextInt(row);
		    int b = gen.nextInt(col);
		    if (!(a == x && b == y) && !grid[a][b].borders(get(x,y)) && grid[a][b].setBomb())
			remainingBombs--;
		}
		for (int i = 0; i < row; i++) {
		    for (int j = 0; j < col; j++) {
			grid[i][j].getNeighbors();
		    }
		}
		grid[x][y].flip();
	    }   
	}

	public void addPatch(Patch x){
	    clicked.add(x);
	}

	public void setup(){
	    super.remove(panel);
	    panel = new JPanel();
	    panel.setPreferredSize(new Dimension(1080,720));
	    panel.setLayout(new GridLayout(row, col));
	    grid = new Patch[row][col];
	    for(int i = 0; i<row; i++){
		for(int j = 0; j<col; j++){
		    grid[i][j] = new Patch(this,i,j);
		    panel.add(grid[i][j]);
		    grid[i][j].validate();
		}
	    }
	    super.add(panel,BorderLayout.SOUTH);
	    super.pack();
	}

	public void actionPerformed(ActionEvent e){
	    if (e.getSource() instanceof Patch && started == 0){
		fillBoard(((Patch)e.getSource()).getXCor(),((Patch)e.getSource()).getYCor());
		started = 1;
	    }
	    if (e.getSource() == difficulty){
		row = Integer.parseInt((String)JOptionPane.showInputDialog(this,"Rows","Rows", JOptionPane.PLAIN_MESSAGE,null,null,10));
		col = Integer.parseInt((String)JOptionPane.showInputDialog(this,"Columns","Columns", JOptionPane.PLAIN_MESSAGE,null,null,20));
		totalBombs = Integer.parseInt((String)JOptionPane.showInputDialog(this,"Mines","Mines",JOptionPane.PLAIN_MESSAGE,null,null,50));
	    }
	    if (e.getSource() == newgame && row != 0 && col != 0){
		started = 0;
		setup();
	    }
	    if (e.getSource() == newgame && row == 0 & col == 0){
	    	started = 0;
	    	row = 10;
	    	col = 20;
	    	totalBombs = 50;
	    	setup();
	    }
	}

	public void gameOver(){
	    for (int i = 0; i < row; i ++){
		for (int j = 0; j < col; j++){
		    grid[i][j].gameOverFlip(); 
		} 
	    }
	    JOptionPane.showMessageDialog(this,"Game Over");
	}
    	    
	public Patch get(int x, int y){
	    if (x >= row || y >= col || x < 0 || y < 0)
		return null;
	    return grid[x][y];
	}

	public void flip(Patch p){
	    int x = p.getXCor();
	    int y = p.getYCor();
	    if (get(x-1,y-1) != null && get(x-1,y-1).getLabel() != "0")get(x-1,y-1).flip();
	    if (get(x-1,y) != null && get(x-1,y).getLabel() != "0")get(x-1,y).flip();
	    if (get(x-1,y+1) != null && get(x-1,y+1).getLabel() != "0")get(x-1,y+1).flip();
	    if (get(x,y+1) != null && get(x,y+1).getLabel() != "0")get(x,y+1).flip();
	    if (get(x,y-1) != null && get(x,y-1).getLabel() != "0")get(x,y-1).flip();
	    if (get(x+1,y-1) != null && get(x+1,y-1).getLabel() != "0")get(x+1,y-1).flip();
	    if (get(x+1,y) != null && get(x+1,y).getLabel() != "0")get(x+1,y).flip();
	    if (get(x+1,y+1) != null && get(x+1,y+1).getLabel() != "0")get(x+1,y+1).flip();
	    
	}

	public void mouseClicked(MouseEvent e) {
	    if (e.getButton() == MouseEvent.BUTTON1 && e.getSource() instanceof Patch && ((Patch)e.getSource()).isBomb() && !((Patch)e.getSource()).isFlagged()) {
		gameOver();	   
		((Patch)e.getSource()).setIcon(new ImageIcon("mine.png"));
	    }
	    else if (e.getButton() == MouseEvent.BUTTON1 && ((Patch)e.getSource()) instanceof Patch && !((Patch)e.getSource()).isBomb() && !((Patch)e.getSource()).isFlagged()){
		((Patch)e.getSource()).flip();
	    }
	    else if (e.getButton() == MouseEvent.BUTTON3 && ((Patch)e.getSource()) instanceof Patch) {
		if (!((Patch)e.getSource()).isFlagged() && ((Patch)e.getSource()).flaggable())
		    ((Patch)e.getSource()).setIcon(new ImageIcon("flag.png"));
		else
		    ((Patch)e.getSource()).setIcon(new ImageIcon());
		((Patch)e.getSource()).flag();
	    }
	    if (clicked.size() >= (row * col) - totalBombs)
		JOptionPane.showMessageDialog(this,"You win.");
		
	}

	public void mouseExited(MouseEvent e){
	    ((Patch)e.getSource()).setVisible(true);}
	public void mouseEntered(MouseEvent e){
	    ((Patch)e.getSource()).setVisible(true);}
	public void mousePressed(MouseEvent e){
	    ((Patch)e.getSource()).setVisible(true);}
	public void mouseReleased(MouseEvent e){
	    ((Patch)e.getSource()).setVisible(true);}
	
	
	
    }

    /*
////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
*/

    private class Patch extends JButton{ 

	private boolean isBomb, isFlagged, isSelected, flaggable;
	private PlayBoard board;
	private int xcor, ycor, bombNeighbors;
	private ArrayList<Patch> neighbors;

	public Patch(PlayBoard j, int x, int y) {
	    super();
	    board = j;
	    xcor = x;
	    ycor = y;
	    isBomb = isFlagged = isSelected = false;
	    bombNeighbors = 0;
	    neighbors = new ArrayList<Patch>();
	    addMouseListener(board);
	    addActionListener(board);
	    flaggable = true;
	}

	public boolean equals(Patch p){
	    if (p != null && board == p.getFrame() && 
		xcor == p.getXCor() && ycor == p.getYCor())
		return true;
	    return false;
	}

	public void gameOverFlip() {
	    if (!isBomb() && isFlagged())
		setIcon(new ImageIcon("redx.png"));
	    if (isBomb())
		setIcon(new ImageIcon("mine.png"));
	}

	public void flip() {
	    if (isBlank()) {
		setLabel("0");
		board.flip(this);
		board.addPatch(this);
	    }
	    else if (!isFlagged && !isBomb){
		setLabel("" + getBombNeighbors());
		board.addPatch(this);
	    }
	    else if (isBomb())
		gameOverFlip();
	    flaggable = false;
	}

	public void flag(){
	    isFlagged = !isFlagged;
	}

	public boolean flaggable(){
	    return flaggable;
	}

	public boolean setBomb() {
	    if (isBomb)
		return false;
	    isBomb = true;
	    return true;
	}

	public boolean borders(Patch other){
	    return (other.equals(board.get(xcor-1,ycor-1)) || other.equals(board.get(xcor-1,ycor)) || other.equals(board.get(xcor-1,ycor+1)) || other.equals(board.get(xcor,ycor-1)) || other.equals(board.get(xcor,ycor+1)) || other.equals(board.get(xcor+1,ycor-1)) || other.equals(board.get(xcor+1,ycor)) || other.equals(board.get(xcor,ycor+1)));
	}
    
	public boolean isFlagged(){
	    return isFlagged;
	}

	public boolean isSelected(){
	    return isSelected;
	}

	public boolean isBomb(){
	    return isBomb;
	}

	public PlayBoard getFrame(){
	    return board;
	}

	public int getXCor(){
	    return xcor;
	}

	public int getYCor(){
	    return ycor;
	}

	public boolean isBlank(){
	    if (!isBomb &&  getBombNeighbors() == 0)
		return true;
	    return false;
	}


	public int getBombNeighbors(){
	    return neighbors.size();
	}

	public void getNeighbors() {
	    if (board.get(xcor-1,ycor-1) != null && board.get(xcor-1,ycor-1).isBomb())
		neighbors.add(board.get(xcor-1,ycor-1));
	    if (board.get(xcor-1,ycor) != null && board.get(xcor-1,ycor).isBomb())
		neighbors.add(board.get(xcor-1,ycor));	    
	    if (board.get(xcor-1,ycor+1) != null && board.get(xcor-1,ycor+1).isBomb())
		neighbors.add(board.get(xcor-1,ycor+1));
	    if (board.get(xcor,ycor-1) != null && board.get(xcor,ycor-1).isBomb())
		neighbors.add(board.get(xcor,ycor-1));
	    if (board.get(xcor,ycor+1) != null && board.get(xcor,ycor+1).isBomb())
		neighbors.add(board.get(xcor,ycor+1));
	    if (board.get(xcor+1,ycor-1) != null && board.get(xcor+1,ycor-1).isBomb())
		neighbors.add(board.get(xcor+1,ycor-1));
	    if (board.get(xcor+1,ycor) != null && board.get(xcor+1,ycor).isBomb())
		neighbors.add(board.get(xcor+1,ycor));
	    if (board.get(xcor+1,ycor+1) != null && board.get(xcor+1,ycor+1).isBomb())
		neighbors.add(board.get(xcor+1,ycor+1));
	}


	public boolean isCorrectlyFlagged(){
	    return (isFlagged && isBomb);
	}

    }

    /*	private class Adapter extends MouseAdapter{

	public Adapter(){
	super();
	}

	public void mouseClicked(MouseEvent e) {
	if (e.getButton() == MouseEvent.BUTTON1 && isBomb && !isFlagged) {
	board.gameOver();	   
	setIcon(new ImageIcon("mine.png"));
	}
	else if (e.getButton() == MouseEvent.BUTTON1 && !isBomb && !isFlagged){
	flip();
	}
	else if (e.getButton() == MouseEvent.BUTTON3) {
	if (!isFlagged)
	setIcon(new ImageIcon("flag.png"));
	else
	setIcon(new ImageIcon());
	isFlagged = !isFlagged;
	}
	}

	    
	}*/
}
