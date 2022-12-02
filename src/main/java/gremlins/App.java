package gremlins;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;

import java.util.*;

import gremlins.Projectiles.Fireball;
import gremlins.Projectiles.Slimeball;

import java.io.*;
import java.nio.file.Paths;


public class App extends PApplet {

    public static final int WIDTH = 720;
    public static final int HEIGHT = 720;
    public static final int SPRITESIZE = 20;
    public static final int BOTTOMBAR = 60;

    public static final int FPS = 60;

    public static final Random randomGenerator = new Random();

    public String configPath;
    public int currentLevel = 0;

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private boolean isMoving;
    private boolean keyReleased;
    private boolean keyReleasedSpaceBar = true;
    private int gameState = 0;
    private boolean fireBallCooldown;
    public boolean slimeBallCooldown;
    private int levels;
    private int lostLives = 0;
    private int startTime;
    private int randomInterval;
    public int resetTime = 0;
    private int lastAttack;
    private int lastSlimeAttack;
    private double manaBarIncrements;
    private double manaBarCurrentLength;
    public int lastKeyPressed;

    private double enemyCooldown;
    private double wizardCooldown;
    private int lives;
    private int lastPowerUp;
    private double powerUpCooldown;
    private double powerUpBar;
    private boolean startingPowerUp;
    private int[] powerUpCoords = new int[2];

    PImage powerUpSprite;
    PImage stoneWallSprite;
    PImage brickWallSprite;
    PImage wizardRightSprite;
    PImage wizardLeftSprite;
    PImage wizardUpSprite;
    PImage wizardDownSprite;
    PImage gremlinSprite;
    PImage fireBallSprite;
    PImage slimeBallSprite;
    PImage doorSprite;
    PImage teleportSprite;
    PImage brickWallDestroyed0;
    PImage brickWallDestroyed1;
    PImage brickWallDestroyed2;
    PImage brickWallDestroyed3;
    
    public Wizard wizard;
    public Door door;
    public Powerup powerUp;
    public List<Gremlin> gremlins;
    public List<Fireball> fireBalls;
    public List<BrickWall> brickWallDestroy;
    public List<Integer> count;
    public List<PImage> brickWallTransitions;
    public List<Slimeball> slimeBalls;
    public List<Teleport> teleports;
    public List<Integer> keysHeld;
    public Object[][] map;

    public App() 
    {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
    */
    public void settings() 
    {
        size(WIDTH, HEIGHT);
    }

    /**
    * Reset map to original or next level design
    */
    public void reset()
    {
        this.startingPowerUp = true;
        this.randomInterval = new Random().nextInt(10000) + 1;
        this.startTime = millis();
        this.map = new Object[33][36];
        this.gremlins = new ArrayList<>();
        this.fireBalls = new ArrayList<>();
        this.slimeBalls = new ArrayList<>();
        this.brickWallDestroy = new ArrayList<>();
        this.count = new ArrayList<>();
        this.brickWallTransitions = new ArrayList<>();
        this.keysHeld = new ArrayList<>();
        this.teleports = new ArrayList<>();
        // Parse json file and get variables based on current level
        JSONObject conf = loadJSONObject(new File(this.configPath));


        lives = conf.getInt("lives");
        lives -= this.lostLives;
        levels = conf.getJSONArray("levels").size();
        String lvl = conf.getJSONArray("levels").getJSONObject(currentLevel).getString("layout");
        enemyCooldown = conf.getJSONArray("levels").getJSONObject(currentLevel).getDouble("enemy_cooldown");
        wizardCooldown = conf.getJSONArray("levels").getJSONObject(currentLevel).getDouble("wizard_cooldown");
        int currentX = 0;
        int currentY = 0;

        stoneWallSprite = loadImage(this.getClass().getResource("stonewall.png").getPath().replace("%20", ""));
        brickWallSprite = loadImage(this.getClass().getResource("brickwall.png").getPath().replace("%20", ""));
        powerUpSprite = loadImage(this.getClass().getResource("powerup.png").getPath().replace("%20", ""));
        powerUpSprite.resize(20, 20);
        brickWallDestroyed0 = loadImage(this.getClass().getResource("brickwall_destroyed0.png").getPath().replace("%20", ""));
        brickWallDestroyed1 = loadImage(this.getClass().getResource("brickwall_destroyed1.png").getPath().replace("%20", ""));
        brickWallDestroyed2 = loadImage(this.getClass().getResource("brickwall_destroyed2.png").getPath().replace("%20", ""));
        brickWallDestroyed3 = loadImage(this.getClass().getResource("brickwall_destroyed3.png").getPath().replace("%20", ""));
        this.brickWallTransitions.add(brickWallDestroyed0);
        this.brickWallTransitions.add(brickWallDestroyed1);
        this.brickWallTransitions.add(brickWallDestroyed2);
        this.brickWallTransitions.add(brickWallDestroyed3);
        wizardRightSprite = loadImage(this.getClass().getResource("wizard1.png").getPath().replace("%20", ""));
        wizardLeftSprite = loadImage(this.getClass().getResource("wizard0.png").getPath().replace("%20", ""));
        wizardUpSprite = loadImage(this.getClass().getResource("wizard2.png").getPath().replace("%20", ""));
        wizardDownSprite = loadImage(this.getClass().getResource("wizard3.png").getPath().replace("%20", ""));
        gremlinSprite = loadImage(this.getClass().getResource("gremlin.png").getPath().replace("%20", ""));
        fireBallSprite = loadImage(this.getClass().getResource("fireball.png").getPath().replace("%20", ""));
        slimeBallSprite = loadImage(this.getClass().getResource("slime.png").getPath().replace("%20", ""));
        doorSprite = loadImage(this.getClass().getResource("door.png").getPath().replace("%20", ""));
        teleportSprite = loadImage(this.getClass().getResource("teleport.png").getPath().replace("%20", ""));
        teleportSprite.resize(20, 20);
        doorSprite.resize(20,20);

        //Create objects based on txt file layout
        int row = 0;
        try (Scanner scanner = new Scanner(Paths.get(lvl)))
        {
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                int col = 0;
                for (char c: line.toCharArray())
                {
                    String check = Character.toString(c);
                    if (check.equals("X"))
                    {
                        this.map[row][col] = new StoneWall(currentX, currentY, stoneWallSprite);
                    }
                    else if (check.equals("B"))
                    {
                        this.map[row][col] = new BrickWall(currentX, currentY, brickWallSprite);
                    }
                    else if (check.equals("W"))
                    {
                        this.wizard = new Wizard(currentX, currentY, wizardRightSprite, wizardCooldown, lives);
                        this.map[row][col] = this.wizard;
                    }
                    else if (check.equals("G"))
                    {
                        Gremlin gremlin = new Gremlin(currentX, currentY, gremlinSprite, enemyCooldown);
                        this.map[row][col] = gremlin;
                        gremlins.add(gremlin);
                    }
                    else if (check.equals("E"))
                    {
                        this.door = new Door(currentX, currentY, doorSprite);
                        this.map[row][col] = this.door;
                    }
                    else if (check.equals("P"))
                    {
                        this.powerUp = new Powerup(currentX, currentY, powerUpSprite);
                        this.map[row][col] = this.powerUp;
                        powerUpCoords[0] = row;
                        powerUpCoords[1] = col;
                    } 
                    else if (check.equals("T"))
                    {
                        Teleport teleport = new Teleport(currentX, currentY, teleportSprite);
                        this.map[row][col] = teleport;
                        this.teleports.add(teleport);
                    }
                    currentX += SPRITESIZE;
                    col++;
                }
                currentY += SPRITESIZE;
                currentX = 0;
                row++;
            }
            mapDesignCheck();
        }
        catch (Exception e)
        {
            print(e.getMessage() + "\n");
            exit();
        }
    }
    /**
    * Called at start of program to generate initial map design
    */
    public void setup() 
    {
        frameRate(FPS);
        reset();
    }

    /**
     * Receive key pressed signal from the keyboard.
    */
    public void keyPressed()
    {
        //Restart game if gameover and key is pressed
        if (gameState == 1 && keyReleased)
        {
            gameState = 0;
            this.lostLives = 0;
            textSize(12);
            reset();
        }
        //FireBall
        if (keyCode == 32 && keyReleasedSpaceBar)
        {
            if (this.wizard.poweredUp)
            {
                manaBarIncrements = 100 / (wizardCooldown / 2 * 60);
            }
            else
            {
                manaBarIncrements = 100 / (wizardCooldown * 60);

            }
            int time = millis();
            if (time > this.lastAttack + this.wizard.cooldown * 1000)
            {
                Fireball ball = new Fireball(this.wizard.x, this.wizard.y, fireBallSprite);
                if (this.wizard.sprite == wizardRightSprite)
                {
                    ball.x += 2;
                    ball.update(4);
                }
                else if (this.wizard.sprite == wizardLeftSprite)
                {
                    ball.x += -2;
                    ball.update(2);
                }
                else if (this.wizard.sprite == wizardUpSprite)
                {
                    ball.y += -2;
                    ball.update(1);
                }
                else
                {
                    ball.y += 2;
                    ball.update(3);
                }
                this.fireBalls.add(ball);
                this.lastAttack = time;
                fireBallCooldown = true;
                manaBarCurrentLength = 0;
            }
            keyReleasedSpaceBar = false;
        }
        //Update movement values based on key pressed
        else if (keyCode == UP)
        {
            if (!(keysHeld.contains(keyCode)))
            {
                keysHeld.add(keyCode);
            }
            this.up = true;
            this.wizard.changeSprite(wizardUpSprite);
        }
        else if (keyCode == DOWN)
        {
            if (!(keysHeld.contains(keyCode)))
            {
                keysHeld.add(keyCode);
            }
            this.down = true;
            this.wizard.changeSprite(wizardDownSprite);
        }
        else if (keyCode == LEFT)
        {
            if (!(keysHeld.contains(keyCode)))
            {
                keysHeld.add(keyCode);
            }
            this.left = true;
            this.wizard.changeSprite(wizardLeftSprite);
        }
        else if (keyCode == RIGHT)
        {
            if (!(keysHeld.contains(keyCode)))
            {
                keysHeld.add(keyCode);
            }
            this.right = true;
            this.wizard.changeSprite(wizardRightSprite);
        }
        keyReleased = false;
        lastKeyPressed = keyCode;
    }
    
    /**
     * Receive key released signal from the keyboard.
    */
    public void keyReleased()
    {
        if (keysHeld.contains(keyCode))
        {
            keysHeld.remove(keysHeld.indexOf(keyCode));
        }
        if (keyPressed && keysHeld.size() > 0)
        {
            int key = keysHeld.get(keysHeld.size() - 1);
            if (key == UP)
            {
                this.wizard.changeSprite(wizardUpSprite);
            }
            else if (key == DOWN)
            {
                this.wizard.changeSprite(wizardDownSprite);
            }
            else if (key == LEFT)
            {
                this.wizard.changeSprite(wizardLeftSprite);
            }
            else if (key == RIGHT)
            {
                this.wizard.changeSprite(wizardRightSprite);
            }
        }
        keyReleased = true;
        if (keyCode == UP)
        {
            this.up = false;
        }
        else if (keyCode == DOWN)
        {
            this.down = false;
        }
        else if (keyCode == LEFT)
        {
            this.left = false;
        }
        else if (keyCode == RIGHT)
        {
            this.right = false;
        }
        else if (keyCode == 32)
        {
            keyReleasedSpaceBar = true;
        }
    }


    /**
     * Draw all elements in the game by current frame. 
	 */
    public void draw()
    {
        if (gameState == 0)
        {
            this.wizard.update(this.up, this.down, this.left, this.right);
            if (!moveGremlins() || !removeSlimeBalls())
            {
                reset();
            }
            if (this.wizard.collide(this.door, this.map))
            {
                currentLevel++;
                if (currentLevel == levels)
                {
                    gameState = 1;
                    currentLevel = 0;
                    background(191, 153, 114);
                    textSize(100);
                    fill(255, 255, 255);
                    text("YOU WIN!",100,315);
                    return;  
                }
                else
                {
                    reset();
                }
            }
            int time = millis();
            if (fireBallCooldown && time > this.lastAttack + this.wizard.cooldown * 1000)
            {
                fireBallCooldown = false;
            }
            fill(191, 153, 114);
            rect(-1, -1, WIDTH + 2, HEIGHT + 2);
            for (int row = 0; row < this.map.length; row++)
            {
                for (int col = 0; col < this.map[row].length; col++)
                {
                    if (this.map[row][col] != null && !(this.map[row][col].equals(this.wizard)) && !(this.map[row][col].equals(this.powerUp)))
                    {
                        this.map[row][col].draw(this);
                    }
                }
            }
            if (this.map[powerUpCoords[0]][powerUpCoords[1]] == this.powerUp &&
                this.wizard.collide(this.powerUp, this.map))
            {
                this.randomInterval = new Random().nextInt(10000) + 1;
                this.wizard.cooldown /= 2;
                this.startingPowerUp = false;
                this.wizard.poweredUp = true;
                this.lastPowerUp = time;
                int[] cords = findObject(this.powerUp, this.map);
                this.map[cords[0]][cords[1]] = null;
                powerUpCooldown = 100.0 / ((this.randomInterval * 60 / 1000.0));
                powerUpBar = 0;

            }
            if (time > this.randomInterval + lastPowerUp && this.wizard.poweredUp)
            {
                this.wizard.cooldown *= 2;
                this.wizard.poweredUp = false;
                this.startingPowerUp = true;
                if (this.map[powerUpCoords[0]][powerUpCoords[1]] == null)
                {
                    this.map[powerUpCoords[0]][powerUpCoords[1]] = this.powerUp;
                }
            }
            if (time > this.startTime + this.randomInterval && startingPowerUp)
            {
                if (this.map[powerUpCoords[0]][powerUpCoords[1]] == null)
                {
                    this.map[powerUpCoords[0]][powerUpCoords[1]] = this.powerUp;
                }
                this.powerUp.draw(this);
            }
            if (!(this.wizard.collide(this.map)))
            {
                isMoving = true;
                this.wizard.tick();
            }
            else
            {
                isMoving = false;
            }
            //If wizard not moving, check if its fully on tile
            if (!isMoving)
            {
                int[] coords = App.findObject(App.findWizard(this.map), this.map);
                int newX = coords[1] * 20;
                int newY = coords[0] * 20;
                if (this.wizard.x != newX)
                {
                    if (this.wizard.x < newX)
                    {
                        this.wizard.x += this.wizard.movementSpeed;
                    }
                    else if (this.wizard.x > newX)
                    {
                        this.wizard.x += -this.wizard.movementSpeed;
                    }
                }
                if (this.wizard.y != newY)
                {
                    if (this.wizard.y < newY)
                    {
                        this.wizard.y += this.wizard.movementSpeed;
                    }
                    else if (this.wizard.y > newY)
                    {
                        this.wizard.y += -this.wizard.movementSpeed;
                    }
                }
            }
            this.wizard.draw(this);
            this.fireBalls.stream().forEach((fireBall) -> fireBall.draw(this));
            this.fireBalls.stream().forEach((fireBall) -> fireBall.tick());
            removeFireBalls();
            this.slimeBalls.stream().forEach((slimeBall) -> slimeBall.draw(this));
            this.slimeBalls.stream().forEach((slimeBall) -> slimeBall.tick());
            shootSlime();
            brickWallTransition();
            drawUi();
            teleportWizard();
            if (keyPressed && keysHeld.size() > 0)
            {
                int key = keysHeld.get(keysHeld.size() - 1);
                if (key == UP)
                {
                    this.up = true;
                    this.down = false;
                    this.left = false;
                    this.right = false;
                }
                else if (key == DOWN)
                {
                    this.down = true;
                    this.up = false;
                    this.left = false;
                    this.right = false;
                }
                else if (key == LEFT)
                {
                    this.left = true;
                    this.right = false;
                    this.up = false;
                    this.down = false;
                }
                else if (key == RIGHT)
                {
                    this.right = true;
                    this.left = false;
                    this.up = false;
                    this.down = false;
                }
            }
        }
        if (lives == 0)
        {
            gameState = 1;
            background(191, 153, 114);
            textSize(100);
            fill(255, 255, 255);
            text("GAME OVER",100,315);   
        }
    }

    /**
    * Find the coordinates of an object if it is found on the map
    *
    * @param  object  the object to be found
    * @param  objects the 2d array of objects
    * @return      the coordinates of the object if it is on the map
    */
    public static int[] findObject(Object object, Object[][] objects)
    {
        for (int row = 0; row < objects.length; row++)
        {
            for (int col = 0; col < objects[row].length; col++)
            {
                if (objects[row][col] == object)
                {
                    int[] res = {row, col};
                    return res;
                }
            }
        }
        return null;
    }

    /**
    * Find the wizard on the map. Used to keep track of where the wizard is after movement.
    *
    * @param  objects  2d array of objects
    * @return      Wizard object
    */
    public static Wizard findWizard(Object[][] objects)
    {
        for (int row = 0; row < objects.length; row++)
        {
            for (int col = 0; col < objects[row].length; col++)
            {
                if (objects[row][col] instanceof Wizard)
                {
                    return (Wizard) objects[row][col];
                }
            }
        }
        return null;
    }

    /**
    * Draw the UI on screen. Includes cooldown bar which changes depending on whether wizard has powerup
    */
    public void drawUi()
    {
        int curLevel = this.currentLevel + 1;
        fill(0);
        text("Lives: ", 10, 690);
        text("Level " + curLevel + "/2", 650, 690);
        int next = 0;
        for (int i = 0; i < this.lives; i++)
        {
            image(wizardRightSprite, 45 + next, 680);
            next += 20;
        }
        if (fireBallCooldown)
        {
            fill(255, 255, 255);
            rect(450, 685, 100, 5);
            if (this.wizard.poweredUp)
            {
                fill(255, 0, 0);
            }
            else
            {
                fill(0, 0, 0);
            }
            rect(450, 685, (float)(manaBarCurrentLength + manaBarIncrements), 5);
            manaBarCurrentLength += manaBarIncrements;
        }
        if (this.wizard.poweredUp)
        {
            fill(0, 0, 0);
            text("FireBall Cooldown Decreased!", 300, 675);
            fill(255, 255, 255);
            rect(300, 685, 100, 5);
            fill(0, 0, 0);
            rect(300, 685, (float)(powerUpBar + powerUpCooldown), 5);
            powerUpBar += powerUpCooldown;
        }

    }

    /**
    * Move gremlins and checks for collision with wizard
    * @return      boolean variable
    */
    public boolean moveGremlins()
    {
        int rand = new Random().nextInt(4) + 1;
        for (Gremlin gremlin: this.gremlins)
        {
            if (gremlin.xVel == 0 && gremlin.yVel == 0)
            {
                gremlin.update(rand);
            }
            if (this.wizard.collide(gremlin, this.map) || gremlin.collide(this.wizard, this.map))
            {
                this.lostLives++;
                return false;
            }
            rand = gremlin.getDirection();
            int bannedDir = gremlin.getOppositeDirection();
            List<Integer> checkedDirections = new ArrayList<>();
            checkedDirections.add(rand);
            int checkDirections = 1;
            while (gremlin.collide(this.map))
            {
                while (checkedDirections.contains(rand) || rand == bannedDir)
                {
                    rand = new Random().nextInt(4) + 1;
                    if (checkDirections == 3)
                    {
                        rand = bannedDir;
                        break;
                    }
                }
                gremlin.update(rand);
                checkedDirections.add(rand);
                checkDirections++;
            }
            gremlin.tick();
        }
        return true;
    }

    /**
    * Shoot slimeballs on a frequency based on specified cooldown
    */
    public void shootSlime()
    {
        int slimeTime = millis();
        if (slimeTime > this.lastSlimeAttack + this.enemyCooldown * 1000)
        {
            for (Gremlin gremlin: this.gremlins)
            {
                slimeBallCooldown = false;
                Slimeball ball = new Slimeball(gremlin.x, gremlin.y, slimeBallSprite);
                int gremlinDirection = gremlin.getDirection();
                ball.update(gremlinDirection);
                this.slimeBalls.add(ball);
                this.lastSlimeAttack = slimeTime;
            }
        }
        else
        {
            slimeBallCooldown = true;
        }
    }

    /**
    * Check collision of slimeballs with other objects
    * @return      boolean for wizard collision to call reset function
    */
    public boolean removeSlimeBalls()
    {
        Iterator<Slimeball> itr = this.slimeBalls.iterator();
        while (itr.hasNext())
        {
            Slimeball slimeBall = itr.next();
            Object collision = slimeBall.collideObjects(this.map);
            if (slimeBall.collideObjects(this.map) != null)
            {
                if (collision instanceof Wizard)
                {
                    this.lostLives++;
                    return false;
                }
                if (collision instanceof Gremlin || collision instanceof Slimeball)
                {
                    continue;
                }
                if (collision instanceof Fireball)
                {
                    int coords[] = findObject(collision, map);
                    if (coords != null)
                    {
                        map[coords[0]][coords[1]] = null;
                    }
                    this.fireBalls.remove(collision);
                }
                int coords[] = findObject(slimeBall, map);
                if (coords != null)
                {
                    map[coords[0]][coords[1]] = null;
                }
                itr.remove();
            }
            else if (slimeBall.xVel == 0 && slimeBall.yVel == 0)
            {
                int coords[] = findObject(slimeBall, map);
                if (coords != null)
                {
                    map[coords[0]][coords[1]] = null;
                }
                itr.remove();
            }
            
        }
        return true;
    }

    /**
    * Check collision of fireballs to certain objects
    *
    */
    public void removeFireBalls()
    {
        Iterator<Fireball> itr = this.fireBalls.iterator();
        while (itr.hasNext())
        {
            Fireball fireBall = itr.next();
            Object collision = fireBall.collideObjects(this.map);
            if (collision != null)
            {
                //Destroy brickwall
                if (collision instanceof BrickWall)
                {
                    BrickWall wall = (BrickWall) collision;
                    this.brickWallDestroy.add(wall);
                    this.count.add(0);
                }
                //Move gremlin to random spot at least 10 tiles away from wizard
                else if (collision instanceof Gremlin)
                {
                    Random rand = new Random();
                    int randY = rand.nextInt(31) + 1;
                    int randX = rand.nextInt(34) + 1;
                    int[] wizCords = findObject(findWizard(this.map), this.map);
                    while (randY < wizCords[0] - 10 && randX < wizCords[1] - 10 || randX > wizCords[1] + 10 && randY > wizCords[0] + 10 && this.map[randY][randX] != null)
                    {
                        randY = rand.nextInt(31) + 1;
                        randX = rand.nextInt(34) + 1;
                    }
                    int[] coords = findObject(collision, this.map);
                    if (coords != null)
                    {
                        this.map[coords[0]][coords[1]] = null;
                    }
                    collision.x = randX * 20;
                    collision.y = randY * 20;
                    this.map[randY][randX] = collision;
                }
                else if (collision instanceof Wizard || collision instanceof Fireball)
                {
                    continue;
                }
                //Destroy slime
                else if (collision instanceof Slimeball)
                {
                    int coords[] = findObject(collision, map);
                    if (coords != null)
                    {
                        map[coords[0]][coords[1]] = null;
                    }
                    this.slimeBalls.remove(collision);
                }
                int[] coords = findObject(fireBall, map);
                if (coords != null)
                {
                    map[coords[0]][coords[1]] = null;
                }
                itr.remove();
            }
        }
    }

    /**
    * Smoothly transition brick wall objects on fireball collision.
    * Uses another array to keep track of transition phase and
    * uses time to transition sprites every 50ms
    */
    public void brickWallTransition()
    {
        Iterator<BrickWall> itr = this.brickWallDestroy.iterator();
        while (itr.hasNext())
        {
            BrickWall brickWall = itr.next();
            Integer transition = this.count.get(this.brickWallDestroy.indexOf(brickWall));
            if (transition != 3)
            {
                brickWall.changeSprite(this.brickWallTransitions.get(transition));
                if( millis() - startTime > 50) 
                {
                    this.count.set(this.count.indexOf(transition), transition + 1);
                    startTime= millis();
                }
            }
            else
            {
                int[] coords = findObject(brickWall, this.map);
                this.count.remove(this.count.indexOf(transition));
                if (coords != null)
                {
                    this.map[coords[0]][coords[1]] = null;
                }
                itr.remove();
            }
        }
    }

    /**
    * Move wizard if it collides with teleport objects
    */
    public void teleportWizard()
    {
        for (Teleport teleport: this.teleports)
        {
            if (this.wizard.collide(teleport, this.map))
            {
                int[] newCords = new int[2];
                while (this.map[newCords[0]][newCords[1]] != null)
                {
                    Random random = new Random();
                    newCords[0] = random.nextInt(31) + 1;
                    newCords[1] = random.nextInt(34) + 1;
                }
                int[] oldCords = findObject(findWizard(this.map), this.map);
                this.map[oldCords[0]][oldCords[1]] = null;
                this.map[newCords[0]][newCords[1]] = this.wizard;
                this.wizard.x = newCords[1] * 20;
                this.wizard.y = newCords[0] * 20;
            }
        }
    }

    /**
    * Check for valid map design
    *
    * @throws Exception
    */
    public void mapDesignCheck() throws Exception
    {
        if (this.wizard == null || this.door == null)
        {
            throw new Exception("Error: No wizard or door in map design.");
        }
        boolean validMap = true;
        //Check edge of map are stonewalls
        for (int i = 0; i < this.map[0].length; i++)
        {
            if (!(this.map[0][i] instanceof StoneWall))
            {
                validMap = false;
            }
        }
        for (int i = 0; i < this.map[this.map.length - 1].length; i++)
        {
            if (!(this.map[this.map.length - 1][i] instanceof StoneWall))
            {
                validMap = false;
            }
        }
        for (int i = 0; i < this.map.length; i++)
        {
            if (!(this.map[i][0] instanceof StoneWall))
            {
                validMap = false;
            }
        }
        for (int i = 0; i < this.map.length; i++)
        {
            if (!(this.map[i][this.map[0].length - 1] instanceof StoneWall))
            {
                validMap = false;
            }
        }
        if (!validMap)
        {
            throw new Exception("Error: Invalid map design");
        }
    }
    public static void main(String[] args) {
        PApplet.main("gremlins.App");
    }
}
