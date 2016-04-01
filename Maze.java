/*============================================================*/
//      8
//    1 * 4    <-- encodings of various directions around a cell
//      2
//
//      +--+--+    +--+--+
//      |     |    |11 12|    11  12   a maze and its representation
//      +--+  +    +--+  +
//      |     |    |11 06|    11  06
//      +--+--+    +--+--+
//
//     16 16 16 16   initial maze contents returned by constructor
//     16 15 15 16
//     16 15 15 16
//     16 16 16 16
//
/*============================================================*/
import java.util.Random;

public class Maze {
   
   private int[][] m;   // maze representation
   private boolean[][] marked; // have we visited this cell yet
   private int rows;    // number of rows in the maze
   private int cols;    // number of columns in the maze
   private final static byte[] TWO = { 1, 2, 4, 8, 16};
   private final static byte[] DX  = { 0,+1, 0,-1};
   private final static byte[] DY  = {-1, 0,+1, 0};
   private boolean done;  // used in finding a single solution.
   private long   count;  // used in finding the number of solutions.
   private Random r;      // for generating random integers.

   public int getRows() { return( rows ); }
   public int getCols() { return( cols ); }

   public Maze ( int nr, int nc, int seed ) {
      r = new Random( seed );
      rows = nr;  cols = nc;
      m = new int[nr+2][nc+2];
      for (int r=1; r<=nr; ++r )
         for (int c=1; c<=nc; ++c )
            m[r][c] = 15;
      for (int r=0; r<nr+2; ++r )
            m[r][0] = m[r][nc+1] = 16;
      for (int c=0; c<nc+2; ++c )
         m[0][c] = m[nr+1][c] = 16;
      Create( nr/2+1, nc/2+1, 0 );
   }

   // Wall in direction p?  
   public boolean ok ( int x, int y, int p ) {
      return( (m[x][y] & TWO[p]) == TWO[p] );
   }

   private boolean downWall( int x, int y, int p ) {
      if (ok(x,y,p) && m[x+DX[p]][y+DY[p]] != 16) {
         m[x][y] ^= TWO[p];  
         m[x+DX[p]][y+DY[p]] ^= TWO[p^2];
         return true;
      }
      return false;
   }
   
   private void knockDown( int count ) {
      // Caution: make sure there are at least count walls!
      for (int i=0; i<count; ++i) {
         int x = 1+r.nextInt(rows);
         int y = 1+r.nextInt(cols);
         if (!downWall( x, y, r.nextInt(4))) --i;
      }
   }
   
   private void Create ( int x, int y, int val ) {
      int[] perm = randPerm( 4 );
      m[x][y] ^= val;  
      for (int i=0; i<4; ++i) {
         int p = perm[i];
         if (m[x+DX[p]][y+DY[p]] == 15) {
            m[x][y] ^= TWO[p];  
            Create( x+DX[p], y+DY[p], TWO[p^2] );
         }
      }
   }

   private int[] randPerm( int n ) {
      // This algorithm should look familiar!
      int[] perm = new int[n];
      for (int k=0; k<n; ++k) perm[k] = k;
      for (int k=n; k>0; --k) {
         int rand = r.nextInt(k);
         int t = perm[rand];  perm[rand] = perm[k-1];  perm[k-1] = t;
      }
      return( perm );
   }
   
   public String toString() {
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          System.out.print(m[i][j] + " ");
        }
        System.out.println();
      }

      // FOR YOU TO FILL IN.  MUST FOLLOW CORRECT FORMAT.
      PrintMaze.displayMaze(this);
      return new String();
   }

   private boolean topLeftCorner(int x, int y) {
     return x == 0 && y == 0;
   }

   private boolean outsideTopWall(int x, int y) {
     return y == 0;
   }

   private boolean outsideBottomWall(int x, int y) {
     return y == rows - 1;
   }

   private boolean outsideLeftWall(int x, int y) {
     return x == 0;
   }
   
   private boolean outsideRightWall(int x, int y) {
     return x == cols - 1;
   }

   private boolean bottomRightCorner(int x, int y) {
     return outsideRightWall(x, y) && outsideBottomWall(x, y);
   }

   private boolean outsideWall(int x, int y) {
     return outsideTopWall(x, y) || outsideBottomWall(x, y) || outsideRightWall(x, y) || outsideLeftWall(x, y);
   }

   private void back(int x, int y) {
     marked[x][y] = true;
     System.out.println("Visiting X: " + x + " Y: " + y + " m[x][y]: " + m[x][y]);

     if (bottomRightCorner(x, y)) {
       count++;
       marked[x][y] = false;
       return;
     }

     if (noWallRight(x, y) && !marked[x + 1][y]) {
       back(x + 1, y);
     }

     if (noWallDown(x, y) && !marked[x][y + 1]) {
       back(x, y + 1);
     }

     if (noWallUp(x, y) && y > 0) {
       if (!marked[x][y - 1]) back(x, y - 1);
     }

     if (noWallLeft(x, y) && x > 0) {
       if (!marked[x - 1][y]) back(x - 1, y);
     }
      
     marked[x][y] = false;
     System.out.println("Leaving X: " + x + " Y: " + y + " m[x][y]: " + m[x][y]);
   }

   private boolean visited(int x, int y) {
     return outsideWall(x, y) ? m[x][y] > 16 : m[x][y] > 15;
   }

   private boolean noWallUp(int x, int y) {
     return ((m[x][y] & 8) == 0) && !outsideTopWall(x, y);
   }

   private boolean noWallLeft(int x, int y) {
     return ((m[x][y] & 1) == 0) && !outsideLeftWall(x, y);
   }

   private boolean noWallRight(int x, int y) {
     return ((m[x][y] & 4) == 0) && !outsideRightWall(x, y);
   }

   private boolean noWallDown(int x, int y) {
     return ((m[x][y] & 2) == 0) && !outsideBottomWall(x, y);
   }


   public void solveMaze() {
      // FOR YOU TO CODE.
      /* start at 0,0
      for each direction
        if path in that direction
          if soln num soln ++
          else back()
      */
      int x = 0;
      int y = 0;
      marked = new boolean[m.length][m[0].length];
      back(x, y);
   }
      
   public long numSolutions() {
     return count;
   }
   
   public static void main ( String[] args ) {
      int row = Integer.parseInt( args[0] );
      int col = Integer.parseInt( args[1] );
      Maze maz = new Maze( row, col, 9999 );
      //System.out.print( maz );
      //maz.solveMaze();
      //System.out.println( "Solutions = "+maz.numSolutions() );
      maz.knockDown( (row+col)/4 );
      System.out.print( maz );
      maz.solveMaze();
      System.out.println( "Solutions = "+maz.numSolutions() );
      //maz = new Maze( row, col, 9999 );  // creates the same maze anew.
      //maz.solveMaze();
      //System.out.print( maz );
   }
}

