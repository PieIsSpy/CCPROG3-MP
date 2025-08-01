import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;

/**
 * This class represents the core game logic
 * and mechanics of Plants vs Zombies. It manages
 * the game's time progression, plant and zombie interactions, 
 * and other gameplay related behaviors.
 *
 *  @author PieIsSpy
 *  @author rachell-code
 *  @version 2.1
 *
 */
abstract class Level {

    /**
     * This constructor initializes all the necessary attributes
     * of a Level object such as its grid dimensions, enemy list, sun 
     * list, and the cooldowns of each plant type. 
     * 
     * 
     * @param n the level number of the game
     * @param t total time of the entire game
     * @param r maximum number of rows in game grid
     * @param c maximum number of columns in game grid
     * @param curTime starting time of the game 
     */
    public Level(int n, int t, int r, int c, int curTime) {
        // initialize basic attributes
        LEVEL_NUM = n;
        TIME_LENGTH = t;
        ROWS = r;
        COLUMNS = c;

        // initialize empty entities and game elements
        tiles = new Entity[r][c];
        enemies = new ArrayList<>();
        suns = new ArrayList<>();
        peas = new ArrayList<>();

        // initialize timers
        internal_start = curTime;
        sun_interval = curTime;
        endFlag = false;
    }

    /** This method initializes the available plants' cooldowns to be used in the game.
     *
     * @param p the available plants that will be initialized
     */
    public void initializePlants(Plant[] p) {
        int i;
        avaliable_plants = p;

        cooldowns = new Cooldown[avaliable_plants.length];
        for (i = 0; i < avaliable_plants.length; i++)
            cooldowns[i] = new Cooldown(avaliable_plants[i].getName(), avaliable_plants[i].getCooldown());
    }

    /**
     * This method returns the number of levels 
     * the game has. 
     * 
     * @return total number of levels in the game
     */
    public int getLEVEL_NUM() {
        return LEVEL_NUM;
    }

    /**
     * 
     * This method returns the game's time
     * limit. 
     * 
     * @return the time limit of the entire game 
     */
    public int getTIME_LENGTH() {
        return TIME_LENGTH;
    }

    /**
     * 
     * This method returns the maximum number of rows
     * the initialized game grid has. 
     * 
     * @return the maximum number of rows in game grid
     */
    public int getROWS() {
        return ROWS;
    }

    /**
     * This method returns the maximum number of columns
     * the initialized game grid has. 
     * 
     * @return the maximum number of columns in game grid
     */
    public int getCOLUMNS() {
        return COLUMNS;
    }

    /**
     * This method returns the number of suns that a player
     * has not collected yet. 
     * 
     * 
     * @return the number of uncollected suns by player
     */
    public int getUnclaimed_suns() {
        return unclaimed_suns;
    }

    /**
     * This method returns the 2D array representing the 
     * game grid where Entity objects will be placed.
     * 
     * @return 2D array of Entity objects
     */
    public Entity[][] getTiles() {
        return tiles;
    }

    /**
     * This method returns the list of zombies to be 
     * used in the game. 
     * 
     * @return list of zombie objects 
     */
    public ArrayList<Zombie> getEnemies() {
        return enemies;
    }

    /**
     * This method returns the available plant types
     * of the game (e.g., Sunflower, Peashooter, etc)
     * 
     * @return array of Plant objects that contains the available
     * plant types of the game
     */
    public Plant[] getAvaliable_plants() {
        return avaliable_plants;
    }

    /**
     * This method checks the given plant type among 
     * the game's available plant types. Once the plant type is 
     * found, it returns its respective Cooldown object. 
     * 
     * @param n name/type of plant object
     * @return cooldown of located plant object 
     */
    public Cooldown getCooldown(String n) {
        int i = 0;
        int found = -1;

        while (i < cooldowns.length && found == -1) {
            if (cooldowns[i].getPlantType().equalsIgnoreCase(n))
                found = i;
            else
                i++;
        }

        return cooldowns[found];
    }

    /**
     * This method returns the game's array list 
     * of Sun objects. 
     * 
     * @return the array list of Sun objects
     */
    public ArrayList<Sun> getSuns()
    {
        return suns;
    }

    /** This method returns the game's array list of
     *  Projectile objects.
     *
     * @return the arraylist of Projectile objects
     */
    public ArrayList<Projectile> getPeas() {
        return peas;
    }

    /**
     * This method updates the number of unclaimed suns
     * by the player. 
     * 
     * @param n number of suns to be 
     */
    public void setUnclaimed_suns(int n) {
        unclaimed_suns = n;
    }

    /** This method checks if the given row and col coordinates are valid or not.
     *
     *  @param r the row coordinate to be checked
     *  @param c the col coordinate to be checked
     *  @return true if the coordinates are valid, false if not
     */
    public boolean isValidCoordinate(int r, int c) {
        return r >= 0 && r < ROWS && c >= 0 && c < COLUMNS;
    }

    /**
     * This method checks to see if the tile
     * where plant is to be planted is unoccupied.
     *
     * @param row number of rows in game grid
     * @param col number of columns in game grid
     *
     * @return true if tile at the given position
     * is null/unoccupied, false otherwise.
     *
     */
    public boolean canBePlaced(int row, int col) {
        return isValidCoordinate(row, col) && tiles[row][col] == null;
    }

    /**
     * This method checks if player has won the game
     * by determining if all zombies are killed (enemies
     * array list is empty) or if a certain amount of
     * time has passed.
     *
     * @param time the current time
     * @return true if there are no enemies left
     * or if the current time has reached the time
     * limit, false otherwise.
     */
    public boolean isGameWon(int time) {
        return (time >= (int)Math.ceil(TIME_LENGTH * 0.94) && enemies.isEmpty()) || time >= TIME_LENGTH;
    }

    /**
     * This method checks if the game has ended by
     * determining if at least one of the zombies
     * have reached the player's house.
     *
     *
     * @return true if at least one zombie reached the house,
     * false otherwise.
     */
    public boolean isGameOver() {
        int i = 0;
        boolean condition = false;

        //if there is at least 1 zombie still alive
        if (!enemies.isEmpty()) { 
            //go through each enemy from the list
            while (i < enemies.size() && !condition) { 
                //check if at least one of them has reached tehe house, indicating that
                //the game is over
                if (enemies.get(i).isAtHouse()) 
                    condition = true;

                i++;
            }
        }

        return condition;
    }

    /**
     * This method spawns a new zombie at a random row
     * and in the rightmost column. It is then added
     * to the enemies array list.
     *
     * @param currentTime the current time frame of the game
     */
    public void spawnZombies(int currentTime) {
        enemies.add(new Zombie((int)(Math.floor(Math.random() * ROWS)), COLUMNS + 1, currentTime));
    }

    /** This method searches for entities
     * that have a health of 0, and removes
     * them from the game.
     *
     */
    public void despawn() {
        int i, j;

        // remove dead plants
        for (i = 0; i < ROWS; i++)
            for (j = 0; j < COLUMNS; j++)
                if (tiles[i][j] != null && tiles[i][j].getHealth() == 0)
                    tiles[i][j] = null;
    }

    /**
     * This method calls the behaviors of Zombie, Plant, Sun and Projectile
     * objects, allowing it to perform its actions with respect
     * to the game's time progression. 
     * 
     * @param currentTime the current time of the game 
     */
    public void behaviors(int currentTime) {
        int i, j;
        Plant p;

        //calls zombie behavior
        for (i = 0; i < enemies.size(); i++)
            enemies.get(i).behaviour(tiles[(int)enemies.get(i).getRow()], currentTime);

        //calls plant behavior
        for (i = 0; i < ROWS; i++)
            for (j = 0; j < COLUMNS; j++)
                if (tiles[i][j] != null && tiles[i][j] instanceof Plant) {
                    p = (Plant) tiles[i][j];
                    p.plantBehavior(this, currentTime);
                }

        // updates sun objects
        for (i = 0; i < suns.size(); i++)
            suns.get(i).update(currentTime);

        // updates pea objects
        for (i = 0; i < peas.size(); i++)
            peas.get(i).update(getEnemies(), currentTime);
    }

    /**
     * This method executes once cycle of the game given the
     * current time. It calls the behaviors of the other objects
     * and decides when objects like the Zombie and Sun
     * will be spawned in the game. 
     * 
     * 
     * @param currentTime the current time of the game
     */
    public void gameCycle(int currentTime) {
        int interval = 0;
        int i,j;

        //System.out.println(currentTime == lastPrint);

        if (currentTime != lastPrint) {
            System.out.println("Level " + LEVEL_NUM + " running: " + (currentTime));
            lastPrint = currentTime;
        }

        behaviors(currentTime);

        //determines how frequent zombies will spawn in the game, with respect to the game's current time
        if (currentTime >= (int)Math.floor(TIME_LENGTH * 0.17) && currentTime <= (int)Math.floor(TIME_LENGTH * 0.445))
            interval = 10; //1 zombie every 10 seconds
        else if (currentTime >= (int)Math.floor(TIME_LENGTH * 0.45) && currentTime <= (int)Math.floor(TIME_LENGTH * 0.78))
            interval = 5; //1 zombie every 5 seconds
        else if (currentTime >= (int)Math.floor(TIME_LENGTH * 0.785) && currentTime <= (int)Math.floor(TIME_LENGTH * 0.945))
            interval = 3; //1 zombie every 3 seconds
        
        //spawns the zombie based on the given interval 
        //internal_start is when the zombie was last spawned 
        //if the time in between is >= the interval, it spawns a zombie
        if (interval != 0 && currentTime - internal_start >= interval) {
            spawnZombies(currentTime);
            System.out.println("Spawned Zombie at (" + (enemies.get(enemies.size()-1).getRow() + 1) + ", " + (enemies.get(enemies.size()-1).getCol() + 1) + ")");
            internal_start = currentTime;
        }

        // spawns the hoard of zombies
        if (currentTime > (int)Math.floor(TIME_LENGTH * 0.945) && !endFlag) {
            enemies.add(new FlagZombie((int)(Math.floor(Math.random() * ROWS)), COLUMNS + 1, currentTime));

            for (i = 0; i < 4 + 2 * (LEVEL_NUM-1); i++)
                spawnZombies(currentTime);

            //spawn zombies on the gravestones
            for (i = 0; i < ROWS; i++)
                for (j = 0; j < COLUMNS; j++)
                    if (tiles[i][j] != null && tiles[i][j] instanceof Tombstone)
                        enemies.add(((Tombstone)tiles[i][j]).spawn(currentTime));

            endFlag = true;
        }

        //spawns a falling sun after a 10-second interval
        //sun_interval : when the last sun was spawned
         
        if (currentTime - sun_interval >= 10) {
            addSun(currentTime);
            //System.out.println("Sun appeared in (" + (suns.get(suns.size()-1).getRow()+1) + "," + (suns.get(suns.size()-1).getCol()+1) + ")");
            sun_interval = currentTime;
        }
    

        // remove dead entities and inactive game elements
        despawn();
        //if (interval != 0)
        //  System.out.println("Spawn rate: 1 zombie every " + interval + " seconds" );
    }

    /**
     * This method allows a sun to be spawned randomly
     * within the game, adding it to the player's unclaimed
     * suns. 
     * 
     * @param currentTime the current time of the game
     */
    public void addSun(int currentTime)
    {
        Random random = new Random();

        //randomize the column/row position
        suns.add(new Sun(random.nextInt(ROWS), random.nextInt(COLUMNS), currentTime));
        unclaimed_suns += suns.get(suns.size()-1).getAmount();
    }

    /**number of levels to track game progress */
    private final int LEVEL_NUM;
    /**time limit of game*/
    private final int TIME_LENGTH;
    /**maximum number of rows of game grid */
    private final int ROWS;
    /**maximum number of columns of game grid */
    private final int COLUMNS;
    /**2d array of entity objects to be used as game grid*/
    private Entity[][] tiles;
    /**array list of zombie objects */
    private ArrayList<Zombie> enemies;
    /**current unclaimed suns*/
    private int unclaimed_suns;
    /**available plant types */
    private Plant[] avaliable_plants;
    /**respective cooldowns of each available plant type */
    private Cooldown[] cooldowns;
    /**list of Sun objects used in the game */
    private ArrayList<Sun> suns;
    /**the projectiles present in the lawn*/
    private ArrayList<Projectile> peas;
    /**time an object has last performned an acion */
    private int internal_start;
    /**time a Sun object has last performed an action */
    private int sun_interval;
    /**dictates whether to spawn a hoard of zombie*/
    private boolean endFlag;
    /**the time of the last printed timer message*/
    private int lastPrint;
}
