import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * This class represents the Gameplay Gui of the game.
 *
 *  @author PieIsSpy
 *  @author rachell-code
 *  @version 1.0
 */
public class LawnPanel extends JPanel {
    /**
     * This constructor initializes the file image of a lawn
     * background and sets its layout to null. 
     *
     * @param width the width of the panel
     * @param height the height of the panel
     * @param forfeit the forfeit button to be formatted
     */
    public LawnPanel(int width, int height, JButton forfeit)
    {

        // get lawn bg
        try {
            lawnImg = new ImageIcon(getClass().getResource("/img/lawn/lawnImg.png"));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // get seed slot img
        try {
            seedSlotImg = new ImageIcon(getClass().getResource("img/lawn/seedSlotImg.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // read image files
        plantsImgResources = readFiles("/img/lawn/plants"); //already stores the images
        System.out.println("entitiesImg size: " + plantsImgResources.length);
        System.out.println();

        zombiesImgResources = readFiles("/img/lawn/zombies"); //already stores the images
        System.out.println("zombiesImgsize: " + zombiesImgResources.length);
        System.out.println();

        gameElementsImgResources = readFiles("/img/lawn/gameElements");
        System.out.println("gameElementsImgResources size: " + gameElementsImgResources.length);
        System.out.println();

        seedPacketsImgResources = readFiles("/img/lawn/draggable/seedPackets");
        System.out.println("seedPacketsImgResources size: " + seedPacketsImgResources.length);
        System.out.println();

        plantStateImgResources = readFiles("/img/lawn/plantStates");
        System.out.println("plantStatesImg size: " + plantStateImgResources.length);
        System.out.println();

        // lawn area
        TILE_WIDTH = FIELD_WIDTH / 9;
        TILE_HEIGHT = FIELD_HEIGHT / 5;

        // panel size
        PANEL_WIDTH = width;
        PANEL_HEIGHT = height;

        // image containers
        tileGameImages = new GameImage[5][9];
        zombieGameImages = new ArrayList<>();
        sunGameImages = new ArrayList<>();
        projectileGameImages = new ArrayList<>();
        seedPackets = new Draggable[6];

        setLayout(null);
        addComponents(forfeit);
    }

    /**
     * This method overrides the paintComponent method from the JPanel class, 
     * allowing the lawnPanel to draw/display the image. 
     * 
     */
    @Override
    public void paintComponent(Graphics g)
    {
        int i, j;
        int x, y;
        super.paintComponent(g);

        if (lawnImg != null)
            g.drawImage(lawnImg.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);

        if (seedSlotImg != null)
            g.drawImage(seedSlotImg.getImage(), 10,10, (int)(seedSlotImg.getIconWidth()*0.8),(int)(seedSlotImg.getIconHeight()*0.8), null);

        for (i = 0; i < tileGameImages.length; i++) {
            for (j = 0; j < tileGameImages[i].length; j++) {
                GameImage target = tileGameImages[i][j];
                if (target != null)
                    g.drawImage(target.getImageIcon().getImage(), (int)target.getPixelX(), (int)target.getPixelY(), TILE_WIDTH, TILE_HEIGHT, null);
            }
        }

        for(i = 0; i < zombieGameImages.size(); i++)
        {
            if(zombieGameImages.get(i) != null)
            {
                x = (int) zombieGameImages.get(i).getPixelX();
                y = (int) zombieGameImages.get(i).getPixelY();

                g.drawImage(zombieGameImages.get(i).getImageIcon().getImage(), x, y, TILE_WIDTH, TILE_HEIGHT, null);

                if (getZombieGameImages().get(i).isSlowed()) {
                    g.setColor(new Color(0, 0, 255, 50));
                    g.fillRect(x,y, TILE_WIDTH, TILE_HEIGHT);
                }
            }
        }

        for(i = 0; i < sunGameImages.size(); i++)
        {
            if(sunGameImages.get(i) != null)
            {
                g.drawImage(sunGameImages.get(i).getImageIcon().getImage(), (int)sunGameImages.get(i).getPixelX(), (int)sunGameImages.get(i).getPixelY(), (int)(TILE_WIDTH*0.7), (int)(TILE_HEIGHT*0.7), null);
            }
        }
        
        for (i = 0 ; i < projectileGameImages.size(); i++)
            if (projectileGameImages.get(i) != null)
                g.drawImage(projectileGameImages.get(i).getImageIcon().getImage(), (int)projectileGameImages.get(i).getPixelX(), (int)projectileGameImages.get(i).getPixelY(), (int)projectileGameImages.get(i).getImageIcon().getIconWidth(), (int)projectileGameImages.get(i).getImageIcon().getIconHeight(), null);
    }

    /** This method is responsible for reading all files in a folder
     *  and converts them into an array of image icons.
     *
     * @param folderPath the folder to be read
     * @return the array of image icons read by the method
     */
    public ImageIcon[] readFiles(String folderPath) {
        int i = 0;

        try {
            String url = getClass().getResource(folderPath).getPath(); //string pathname
            System.out.println(url);
            File path = new File(url); //creates a new file instance pointing to the location of that directory
            File[] files = path.listFiles(); //stores the files in the given path

            // read and store images
            if (files !=null) {
                if (folderPath.equalsIgnoreCase("/img/lawn/draggable/seedPackets")) {
                    plantNames = new String[files.length]; //instatiates the plant names based on number of files
                    readPlantNames(files);
                }

                System.out.println("Files read: " + files.length);
                ImageIcon[] container = new ImageIcon[files.length]; //stores the images in the given path
                readImages(files, container);

                System.out.println("Final check: ");
                while (i < container.length && container[i] != null) {
                    System.out.println(container[i].toString());
                    i++;
                }

                System.out.println("Returning " + i + " elements");

                return container; //returns the images in the given directory path
            }
        } catch (Exception e) {
            System.out.println("readFiles()");
            System.out.println(e.getMessage());
        }

        return null;
    }

    /** This method is responsible for reading all the image contents
     *  of a given file array and stores them into an image icon array
     *  container.
     *
     * @param files the files to be read
     * @param container the container to be stored
     */
    public void readImages(File[] files, ImageIcon[] container) {
        int i = 0;

        // for every file in the container, scan for images
        for (File f : files) {
            try {
                System.out.println(f.getName());
                container[i] = new ImageIcon(ImageIO.read(files[i]));
                i++;
            } catch (Exception e) {
                System.out.println("readImages()");
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Images read: " + i);
    }

    /** This method reads the filenames of a plant image source folder and
     *  stores them as plant names.
     *
     * @param files the files to be read
     */
    public void readPlantNames(File[] files) {
        int i = 0;
        String name;

        for (File f : files) {
            try {
                name = f.getName();
                plantNames[i] = name.substring(0,name.lastIndexOf("."));
                i++;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /** This method is responsible for initializing the draggable objects
     *  inside the seedPacket array.
     *
     * @param plants the plants to be represented as draggable objects
     */
    public void initializeSeedPackets(Plant[] plants) {
        int i;
        int x = 22, y = 75;
        ImageIcon found;
        String name;

        for (i = 0; i < plants.length; i++) {
            name = plants[i].getName();
            found = seedPacketsImgResources[findSeedPacket(name)];
            seedPackets[i] = new Draggable(name, found, x, y);
            seedPackets[i].setBounds(0,0,getWidth(),getHeight());
            dragArea.add(seedPackets[i]);
            y += 65;
        }

        try {
            ImageIcon shovel = new ImageIcon(getClass().getResource("/img/lawn/draggable/shovel.png"));
            shovelDraggable = new Draggable("Shovel", shovel,50, 475);
            shovelDraggable.setBounds(0,0,getWidth(),getHeight());
            dragArea.add(shovelDraggable);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println(seedPackets.length);
        for (i = 0; i < seedPackets.length; i++) {
            if (seedPackets[i] != null) {
                System.out.println(seedPackets[i].getName());
                System.out.println(seedPackets[i].getImageSprite());
                System.out.println(seedPackets[i].getImageCorner().x);
                System.out.println(seedPackets[i].getImageCorner().y);
            }
        }
    }

    /** This method searches for the given name inside the initialized
     *  plant names that could be used as a name of the draggable object.
     *
     * @param name the name of the plant to be searched
     * @return the index of the found plant name
     */
    public int findSeedPacket(String name) {
        int i;
        int found = -1;

        for (i = 0; i < plantNames.length && found == -1; i++)
            if (name.equalsIgnoreCase(plantNames[i])) {
                System.out.println(i + ") Found " + plantNames[i]);
                found = i;
            }

        return found;
    }

    /** This method is responsible for adding all the components needed
     *  to be rendered in the Lawn Panel.
     *
     * @param forfeit the forfeit button to be formatted
     */
    public void addComponents(JButton forfeit) {

        // sun count
        sunCount = new JLabel("0");
        sunCount.setFont(new Font("D050000L", Font.PLAIN,20));
        sunCount.setForeground(Color.WHITE);
        sunCount.setHorizontalTextPosition(JLabel.CENTER);
        sunCount.setBounds(87,20,100,30);
        add(sunCount);

        // forfeit button
        forfeit.setBounds(PANEL_WIDTH - 150, 0, 100, 60);
        forfeit.setFont(new Font("Lucida Handwriting", Font.BOLD, 15));
        forfeit.setOpaque(true);
        forfeit.setFocusPainted(false);
        forfeit.setBackground(Color.lightGray);
        forfeit.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 10));
        forfeit.setHorizontalTextPosition(JLabel.CENTER);
        add(forfeit);

        // drag and drop container
        dragArea = new JPanel(new BorderLayout());
        dragArea.setOpaque(false);
        dragArea.setBounds(0,0,PANEL_WIDTH,PANEL_HEIGHT);
        add(dragArea);
    }

    /** This method updates the sun count text of the panel
     *
     * @param sun the current amount of sun
     */
    public void updateSunCount(int sun) {
        sunCount.setText(Integer.toString(sun));
    }

    /** This method clears all entity, game element and draggable object
     *  renders of the Lawn Panel.
     *
     */
    public void clearImages() {
        dragArea.removeAll();
        zombieGameImages.clear();
        sunGameImages.clear();
        projectileGameImages.clear();

        int i, j;
        for (i = 0; i < seedPackets.length; i++)
            seedPackets[i] = null;

        for (i = 0; i < tileGameImages.length; i++)
            for (j = 0; j < tileGameImages[i].length; j++)
                tileGameImages[i][j] = null;
    }

    /** This method adds an image into the arraylist of zombie images
     *  to be rendered
     *
     * @param image the image to be added for rendering
     */
    public void addZombieImage(GameImage image)
    {
        zombieGameImages.add(image);
        System.out.println("Added zombie image!");
    }

    /** This method adds an image into the array of tile images
     *  to be rendered
     *
     * @param image the image to be added for rendering
     * @param row the row of the tile image
     * @param col the col of the tile image
     */
    public void addTileImage(GameImage image, int row, int col)
    {
        tileGameImages[row][col] = image;
        //plantGameImages.add(image);
        System.out.println("Added plant image!");
    }

    /** This method adds a sun image into the arraylist of sun images
     *  to be rendered
     *
     * @param image the sun image to be added
     */
    public void addSunImage(GameImage image) {
        sunGameImages.add(image);
    }

    /** This method adds a projectile image into the arraylist of projectile images
     *  to be rendered
     *
     * @param image the projectile image to be rendered
     */
    public void addProjectileImage(GameImage image) {
        projectileGameImages.add(image);
    }

    /** This method gets all zombie images to be rendered.
     *
     * @return the arraylist of zombie images to be rendered
     */
    public ArrayList<GameImage> getZombieGameImages() {
        return zombieGameImages;
    }

    /** This method gets all tile images to be rendered.
     *
     * @return the array of all tile images to be rendered
     */
    public GameImage[][] getTileGameImages() {
        return tileGameImages;
    }

    /** This method gets all sun images to be rendered
     *
     * @return all sun images to be rendered
     */
    public ArrayList<GameImage> getSunGameImages() {
        return sunGameImages;
    }

    /** This method gets all projectile images to be rendered
     *
     * @return all projectile images to be rendered
     */
    public ArrayList<GameImage> getProjectileGameImages() {
        return projectileGameImages;
    }

    /** This method returns all draggable seed packets present in the lawn.
     *
     * @return all draggable seed packets present in the lawn
     */
    public Draggable[] getSeedPackets() {
        return seedPackets;
    }

    /** This method returns the draggable shovel object of the panel.
     *
     * @return the draggable shovel of the panel
     */
    public Draggable getShovelDraggable() {
        return shovelDraggable;
    }

    /** This method returns the list of plant names read by the
     *  readPlantNames() method.
     *
     * @return the array of plant names read
     */
    public String[] getPlantNames() {
        return plantNames;
    }

    /** This method gets the array of plant image resources to be used
     *
     * @return the array of plant image resources
     */
    public ImageIcon[] getPlantsImgResources()
    {
        return plantsImgResources;
    }

    /** This method gets the array of zombie image resources to be used
     *
     * @return the array of zombie image resources
     */
    public ImageIcon[] getZombiesImgResources()
    {
        return zombiesImgResources;
    }

    /** This method gets the image resources for game elements.
     *
     * @return the array of image resources for game elements
     */
    public ImageIcon[] getGameElementsImgResources()
    {
        return gameElementsImgResources;
    }

    /** This method gets the image resources for plant states.
     *
     * @return the array of image resources for plant states
     */
    public ImageIcon[] getPlantStateImgResources() {
        return  plantStateImgResources;
    }

    /** This method gets the width of the lawn area.
     *
     * @return the width of the lawn area.
     */
    public int getFieldWidth()
    {
        return FIELD_WIDTH;
    }

    /** This method gets the height of the lawn area.
     *
     * @return the height of the lawn area.
     */
    public int getFieldHeight()
    {
        return FIELD_HEIGHT;
    }

    /** This method returns the x coordinate of the reference point of the lawn
     *  area.
     *
     * @return the x coordinate of the reference point of the lawn
     */
    public int getFieldPosX()
    {
        return FIELD_X;
    }

    /** This method returns the y coordinate of the reference point of the lawn
     *  area.
     *
     * @return the y coordinate of the reference point of the lawn
     */
    public int getFieldPosY()
    {
        return FIELD_Y;
    }

    /** This method returns the tile width of a lawn tile.
     *
     * @return the tile width of a lawn tile
     */
    public int getTileWidth()
    {
        return TILE_WIDTH;
    }

    /** This method returns the tile height of a lawn tile.
     *
     * @return the tile height of a lawn tile
     */
    public int getTileHeight()
    {
        return TILE_HEIGHT;
    }



    /** the panel width*/
    private final int PANEL_WIDTH;
    /** the panel height*/
    private final int PANEL_HEIGHT;
    /**lawn background image to be displayed */
    private ImageIcon lawnImg;
    /** the seed slot image to be displayed*/
    private ImageIcon seedSlotImg;
    /** the width of the lawn area*/
    private final int FIELD_WIDTH = 565;
    /** the height of the lawn area*/
    private final int FIELD_HEIGHT = 475;
    /** the x coordinate of the reference point of the lawn area*/
    private final int FIELD_X = 195;
    /** the y coordinate of the reference point of the lawn area*/
    private final int FIELD_Y = 85;
    /** the tile width of a lawn tile.*/
    private final int TILE_WIDTH;
    /** the tile height of a lawn tile.*/
    private final int TILE_HEIGHT;
    /** the container for draggable objects*/
    private JPanel dragArea;
    /** the label text for the player sun count*/
    private JLabel sunCount;
    /** the tiles to be rendered*/
    private GameImage[][] tileGameImages;
    /** the zombies to be rendered*/
    private ArrayList<GameImage> zombieGameImages;
    /**the sun elements to be rendered*/
    private ArrayList<GameImage> sunGameImages;
    /** the projectile elements to be rendered*/
    private ArrayList<GameImage> projectileGameImages;
    /** the draggable seed packets to be used*/
    private Draggable[] seedPackets;
    /** the shovel draggable to be used*/
    private Draggable shovelDraggable;
    /** the image resources for plants*/
    private ImageIcon[] plantsImgResources;
    /**the image resources for zombies*/
    private ImageIcon[] zombiesImgResources;
    /**the image resources for game elements*/
    private ImageIcon[] gameElementsImgResources;
    /**the image resources for seed packets*/
    private ImageIcon[] seedPacketsImgResources;
    /** the image resources for plant states*/
    private ImageIcon[] plantStateImgResources;
    /**the names of plants to be represented in a draggable object*/
    private String[] plantNames;
}