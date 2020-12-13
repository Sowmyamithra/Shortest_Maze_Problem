import java.util.Scanner;
import java.util.*; 
import java.lang.*; 
import java.io.*; 
import java.awt.*;
import java.awt.event.*;
 
class Pair
{
	int i = -1;
	int j = -1;
	Pair(int a , int b)
	{
		i = a;
		j = b;
	}
	int getKey()
	{
		return i;
	}
	int getValue()
	{
		return j;
	}
} 
class Cell
{
    int i , j;
    double f_value;
}
class Details
{
    int[] parent = {-1 , -1};
    double g = Integer.MAX_VALUE;
    double h = Integer.MAX_VALUE;
    double f = Integer.MAX_VALUE;
    Details()
    {
        parent = new int[2];
        parent[0] = -1;
        parent[1] = -1;
        g = Integer.MAX_VALUE;
        h = Integer.MAX_VALUE;
        f = Integer.MAX_VALUE;
    }
}
class cellCmp implements Comparator<Cell>
{ 
    public int compare(Cell c1, Cell c2)
    { 
        if (c1.f_value > c2.f_value) 
        {
            return 1;
        }
        else if (c1.f_value < c2.f_value) 
        {
            return -1;
        }
        return 0; 
    } 
}
class Layout extends Frame implements WindowListener
{
	Layout(Pair src , Pair dest , int r , int c , int[][] grid , ArrayList<Pair> li)
	{
		String s1 = Integer.toString(src.getKey()) + '#' + Integer.toString(src.getValue());
		String s2 = Integer.toString(dest.getKey()) + '#' + Integer.toString(dest.getValue());
		setLayout(new GridLayout(r , c , 10 , 10));
		Button[][] b = new Button[r][c];
		for(int i = 0; i < r; i++) 
		{
			for(int j = 0; j < c; j++) 
			{
				String s = Integer.toString(i) + '#' + Integer.toString(j);
				b[i][j] = new Button("");
				if(s1.equals(s))
				{
					b[i][j].setBackground(Color.green);
					b[i][j].setLabel("src.");	
				}
				if(s2.equals(s))
				{
					b[i][j].setBackground(Color.red);
					b[i][j].setLabel("dest.");
				}
				if(grid[i][j] == 1)
				{
					b[i][j].setBackground(Color.black);
				}
				add(b[i][j]);
			}
		}
		addWindowListener(this);
		setTitle("Chase_Cheese");
		setSize(600,600);
		setVisible(true);
		int size = li.size();
		int pos = 1;
		while(!li.isEmpty())
		{
			Pair pr = li.get(0);
			li.remove(0);
			String s3 = Integer.toString(pr.getKey()) + '#' + Integer.toString(pr.getValue());
			for(int i = 0;i < r;i++)
			{
				for(int j = 0;j < c;j++)
				{
					String s = Integer.toString(i) + '#' + Integer.toString(j);
					if(s.equals(s3) && !s.equals(s1) && !s.equals(s2))
					{
						b[i][j].setBackground(Color.blue);
						b[i][j].setLabel(Integer.toString(pos++));
					}	
				}
			}
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
		}
		System.out.println("Finished");
	}
	public void windowActivated(WindowEvent we)
	{}
	public void windowClosed(WindowEvent we)
	{}
	public void windowClosing(WindowEvent we)
	{
	 	System.exit(0);
	}
	public void windowDeactivated(WindowEvent we)
	{}
	public void windowDeiconified(WindowEvent we)
	{}
	public void windowIconified(WindowEvent we)
	{}
	public void windowOpened(WindowEvent we)
	{}
}
class Helper
{
    int rows , cols;
    int[][] grid;
    Pair src , dest;
    int[][] indices = {
        {1 , 1} , {1 , 0} , {1 , -1} , {0 , -1} , {0 , 1} , {-1 , -1} , {-1 , 0} , {-1 , 1}
    };
    Details[][] details;
    boolean[][] closed;
    PriorityQueue<Cell> open = new PriorityQueue<Cell>(5 , new cellCmp());
    boolean found_dest;
    void initialise()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter rows , cols : ");
        rows = sc.nextInt();
        cols = sc.nextInt();
        grid = new int[rows][cols];
        System.out.println("Enter the values of grid : ");
        for(int i = 0;i < rows;i++)
        {
            System.out.println("Enter row : \n");
            for(int j = 0;j < cols;j++)
            {
                System.out.print("Enter grid value : ");
                grid[i][j] = sc.nextInt();
            }
        }
        System.out.println("Enter src : ");
        int i = sc.nextInt();
        int j = sc.nextInt();
        src = new Pair(i , j);
        System.out.println("Enter dest : ");
        i = sc.nextInt();
        j = sc.nextInt();
        dest = new Pair(i , j);
        details = new Details[rows][cols];
        for(int i1 = 0;i1 < rows;i1++)
        {
            for(int j1 = 0;j1 < cols;j1++)
            {
                details[i1][j1] = new Details();
            }
        }
        closed = new boolean[rows][cols];
        found_dest = false;
    }
    boolean isValid(Pair pr)
    {
        int x = (int)pr.getKey();
        int y = (int)pr.getValue();
        if(x < 0 || x >= rows || y < 0 || y >= cols)
        {
            return false;
        }
        return true;
    }
    boolean isBlocked(Pair pr)
    {
        int i = (int)pr.getKey();
        int j = (int)pr.getValue();
        if(grid[i][j] == 1)
        {
            return true;
        }
        return false;
    }
    boolean isDestination(Pair pr1 , Pair pr2)
    {
        int x1 = (int)pr1.getKey();
        int y1 = (int)pr1.getValue();
        int x2 = (int)pr2.getKey();
        int y2 = (int)pr2.getValue();
        if(x1 == x2 && y1 == y2)
        {
            return true;
        }
        return false;
    }
    double calHvalue(Pair pr1 , Pair pr2)
    {
        int x1 = (int)pr1.getKey();
        int y1 = (int)pr1.getValue();
        int x2 = (int)pr2.getKey();
        int y2 = (int)pr2.getValue();
        return Math.sqrt(Math.pow((x1 - x2) , 2) + Math.pow((y1 - y2) , 2));
    }
    void tracePath()
    {
        Stack<Pair> stk = new Stack<>();
        int i = (int)dest.getKey();
        int j = (int)dest.getValue();
        while(!(details[i][j].parent[0] == i && details[i][j].parent[1] == j))
        {
            Pair pr = new Pair(i , j);
            stk.push(pr);
            int x = details[i][j].parent[0];
            int y = details[i][j].parent[1];
            i = x;
            j = y;
        }
        System.out.println("\n_____________________Path____________________\n");
        //System.out.println("(" + src.getKey() + " , " + src.getValue() + ")");
        Pair pr = new Pair(i , j);
        stk.push(pr);
	ArrayList<Pair> List = new ArrayList<Pair>(); 
        while(!stk.empty())
        {
            Pair pr1 = stk.pop();
	    List.add(pr1);
            System.out.println("(" + pr1.getKey() + " , " + pr1.getValue() + ")");
        }
	new Layout(src , dest , rows , cols , grid , List);
        return;
    }
    void aStar()
    {
        if(!isValid(src))
        {
            System.out.println("Invalid source");
            return;
        }
        if(!isValid(dest))
        {
            System.out.println("Invalid destination");
            return;
        }
        if(isBlocked(src))
        {
            System.out.println("Source is blocked");
            return;
        }
        if(isBlocked(dest))
        {
            System.out.println("Destination is blocked");
            return;
        }
        if(isDestination(src , dest))
        {
            System.out.println("You are already at the destination");
            return;
        }
        Cell c = new Cell();
        c.i = (int)src.getKey();
        c.j = (int)src.getValue();
        c.f_value = 0.0;
        open.add(c);
        int a = (int)src.getKey();
        int b = (int)src.getValue();
        details[a][b].parent[0] = a; 
        details[a][b].parent[1] = b;
        details[a][b].g = 0.0;
        details[a][b].h = 0.0;
        details[a][b].f = 0.0;
        while(!(open.size() == 0))
        {
           // System.out.println("enter");
            Cell cell = open.poll();
            int p1 = cell.i;
            int p2 = cell.j;
            closed[p1][p2] = true;
            for(int i = 0;i < 8;i++)
            {
                int x = p1 + indices[i][0];
                int y = p2 + indices[i][1];
                Pair pt = new Pair(x , y);
                if(isValid(pt))
                {
                    if(isDestination(pt , dest))
                    {
                        //System.out.println("dest");
                        details[x][y].parent[0] = p1;
                        details[x][y].parent[1] = p2;
                        System.out.println("Destination is found");
                        found_dest = true;
                        tracePath();
                        return;
                    }
                    else if(!isBlocked(pt) && closed[x][y] == false)
                    {
                        //System.out.println("alt");
                        double G = details[p1][p2].g + 1.0;
                        double H = calHvalue(pt , dest);
                        double F = G + H;
                        if(F < details[x][y].f || details[x][y].f == Integer.MAX_VALUE)
                        {
                            Cell c1 = new Cell();
                            c1.i = x;
                            c1.j = y;
                            c1.f_value = F;
                            open.add(c1);
                            details[x][y].parent[0] = p1;
                            details[x][y].parent[1] = p2;
                            details[x][y].g = G;
                            details[x][y].h = H;
                            details[x][y].f = F;
                        }
                    }
                }
            }
        }
        if(!found_dest)
        {
            System.out.println("Failed to find the path");
            return;
        }
    }
}
public class aStar
{
    public static void main(String args[])
    {
        Helper h = new Helper();
        h.initialise();
        h.aStar();
    }
}

