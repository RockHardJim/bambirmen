package org.ai;

import java.util.Iterator;
import java.util.Vector;
import java.util.Random;

/*
 * Projet d'Intelligence Artificielle (2007-2008) : Bomberman
 * Equipe : CAGLAYAN Ozan, ELMAS Can
 */

public class CaglayanElmas extends ArtificialIntelligence
{
	private static final long serialVersionUID = 1L;
	private enum searchType{HORIZONTAL,VERTICAL};
	
	// "Pseudo-Random Number Generator"
	private Random prng;
	
	/* Les m�thodes disponibles depuis la classe "ArtificialIntelligence"
	 * 
	 * int[][] getZoneMatrix()
	 * int getZoneMatrixDimX()
	 * int getZoneMatrixDimY()
	 * int getBombPowerAt(int x, int y)
	 * int getPlayerCount()
	 * int getPlayerPosition(int index)
	 * int getPlayerDirection(int index)
	 * long getTimeBeforeShrink()
	 * int[] getNextShrinkPosition()
	 * int[] getOwnPosition()
	 * int getBombPosition()
	 * 
	 * void printZoneMatrix() (D�boggage)
	 * 
	 */

	public CaglayanElmas()
	{
		// Notre IA est appel� "Smart"
		super("Smart");
		
		// Init. prng
		this.prng = new Random();
	}
	
	/**
	 * D�termine la prochaine action que l'IA va effectuer
	 * (Bouger, ne rien faire, poser une bombe)
	 * 
	 * @return	AI_ACTION_XXXX
	 */
	
	public Integer call() throws Exception
	{
		// Le coup � jouer
		Integer play = AI_ACTION_DO_NOTHING;
		
		// Notre position
		int[] ownPosition = getOwnPosition();
		
		// Liste des coups possibles pour jouer
		Vector<Integer> possibleMoves = getPossibleMoves(ownPosition[0], ownPosition[1]);
		Iterator<Integer> i = possibleMoves.iterator();
		
		// Utilise getBombPosition() pour une bombe possible sur nous
		//System.out.println("bombPosition:"+getBombPosition());
		
		int[] bomb = bombCanKillMe(ownPosition[0], ownPosition[1]);
		
		//System.out.println(getOwnPosition()[0]+","+getOwnPosition()[1]);
		
		return play;
	}
	
	public int[] bombCanKillMe(int x, int y)
	{
		int[] result = {-1, -1};
		int[][] matrix = getZoneMatrix();
		
		int bombPower = 0;
		
		int dimX = getZoneMatrixDimX();
		int dimY = getZoneMatrixDimY();
		
		// FIXME : Il peut y exister plusieurs bombes!!
		
		for (int i = 1; i < dimY; i++)
		{
			// Cherche une bombe sur la meme ligne verticale que nous..
			if ((bombPower = getBombPowerAt(x, i)) != -1)
			{
				int min = Math.min(i, y);
				int max = Math.max(i, y);
				boolean wallExists = false;
						
				// Une bombe existe : (x,i)
				System.out.println("Bomb(v)["+bombPower+"] X:"+x+", Y:"+i);
				
				// Est-ce qu'il y a un mur entre nous?
				for (int k = min+1; k < max && !wallExists; k++)
					if (matrix[x][k] == AI_BLOCK_WALL_SOFT || matrix[x][k] == AI_BLOCK_WALL_HARD)
						wallExists = true;
				
				if ( !wallExists && Math.abs(y-i) <= bombPower)
				{
					// �a nous tue! Go go go!
					System.out.println("Bombe nous tue[V]");
				}
			}
		}
		
		for (int j = 1; j < dimX; j++)
		{	
			// Cherche une bombe sur la meme ligne horizontale que nous..
			if ((bombPower = getBombPowerAt(j, y)) != -1)
			{
				int min = Math.min(j, x);
				int max = Math.max(j, x);
				boolean wallExists = false;
				
				// Une bombe existe : (j,y)
				System.out.println("Bomb(h)["+bombPower+"] X:"+j+", Y:"+y);
				
				// Est-ce qu'il y a un mur entre nous?
				for (int k = min+1; k < max && !wallExists; k++)
					if (matrix[k][x] == AI_BLOCK_WALL_SOFT || matrix[k][x] == AI_BLOCK_WALL_HARD)
						wallExists = true;
				
				if ( !wallExists && Math.abs(x-j) <= bombPower)
				{
					// �a nous touche! Go go go!
					System.out.println("Bombe nous tue[H]");
				}
			}
		}
		return result;
	}
	
	/**
	 * Indique si le d�placement dont le code a �t� pass� en param�tre 
	 * est possible pour un personnage situ� en (x,y).
	 * @param x	position du personnage
	 * @param y position du personnage
	 * @param move	le d�placement � �tudier
	 * @return	vrai si ce d�placement est possible
	 */
	private boolean isMovePossible(int x, int y, int move)
	{
		boolean result = false;
		
		switch(move)
		{
			case AI_ACTION_GO_UP:
				result = (y > 0) && !isObstacle(x,y-1);
				break;
				
			case AI_ACTION_GO_DOWN:
				result = (y < getZoneMatrixDimY()-1) && !isObstacle(x,y+1);
				break;
				
			case AI_ACTION_GO_LEFT:
				result = (x > 0) && !isObstacle(x-1,y);
				break;
				
			case AI_ACTION_GO_RIGHT:
				result = (x < getZoneMatrixDimX()-1) && !isObstacle(x+1,y);
				break;
		}
		return result;
	}
	
	/**
	 * Indique si la case situ�e � la position pass�e en param�tre
	 * constitue un obstacle pour un personnage : bombe, feu, mur.
	 * @param x	position � �tudier
	 * @param y	position � �tudier
	 * @return	vrai si la case contient un obstacle
	 */
	private boolean isObstacle(int x, int y)
	{	
		boolean result = false;
		int state = getZoneMatrix()[x][y];
		
		// bombes
		result = result || (state == AI_BLOCK_BOMB);
		
		// feu
		result = result || (state == AI_BLOCK_FIRE);
		
		// murs
		result = result || (state == AI_BLOCK_WALL_HARD);
		result = result || (state == AI_BLOCK_WALL_SOFT);
		
		// on ne sait pas quoi
		result = result || (state == AI_BLOCK_UNKNOWN);
		
		// shrink
		result = result || (x == getNextShrinkPosition()[0] && y == getNextShrinkPosition()[1]);
		
		return result;
	}
	
	/**
	 * Renvoie la liste de tous les d�placements possibles
	 * pour un personnage situ� � la position (x,y)
	 * @param x	position du personnage
	 * @param y position du personnage
	 * @return	la liste des d�placements possibles
	 */
	private Vector<Integer> getPossibleMoves(int x, int y)
	{	
		Vector<Integer> result = new Vector<Integer>();
		
		for(int move = AI_ACTION_GO_UP; move <= AI_ACTION_GO_RIGHT; move++)
			if(isMovePossible(x, y, move))
				result.add(move);
		return result;
	}

	/**
	 * Calcule et renvoie la distance de Manhattan 
	 * entre le point de coordonn�es (x1,y1) et celui de coordonn�es (x2,y2). 
	 * @param x1	position du premier point
	 * @param y1	position du premier point
	 * @param x2	position du second point
	 * @param y2	position du second point
	 * @return	la distance de Manhattan entre ces deux points
	 */
	private int distance(int x1, int y1, int x2, int y2)
	{	
		int result = 0;
		result = result + Math.abs(x1-x2);
		result = result + Math.abs(y1-y2);
		return result;
	}
	
	/**
	 * Parmi les blocs dont le type correspond � la valeur blockType
	 * pass�e en param�tre, cette m�thode cherche lequel est le plus proche
	 * du point de coordonn�es (x,y) pass�es en param�tres. Le r�sultat
	 * prend la forme d'un tableau des deux coordon�es du bloc le plus proche.
	 * Le tableau est contient des -1 s'il n'y a aucun bloc du bon type dans la zone de jeu.
	 * @param x	position de r�f�rence
	 * @param y	position de r�f�rence
	 * @param blockType	le type du bloc recherch�
	 * @return	les coordonn�es du bloc le plus proche
	 */
	private int[] getClosestBlockPosition(int x, int y, int blockType)
	{	
		int minDistance = Integer.MAX_VALUE;
		int result[] = {-1, -1}; 
		int[][] matrix = getZoneMatrix();
		int dimX = getZoneMatrixDimX();
		int dimY = getZoneMatrixDimY();
		
		for(int i = 0; i < dimX; i++)
			for(int j = 0; j < dimY; j++)
				if(matrix[i][j] == blockType)
				{	
					int tempDistance = distance(x, y, i, j); 	
					if(tempDistance < minDistance)
					{	
						minDistance = tempDistance;
						result[0] = i;
						result[1] = j;
					}
				}
		return result;
	}

}
