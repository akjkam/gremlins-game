package gremlins;


import processing.core.PImage;
/**
* Wizard class inheriting from Moveable
*/
public class Wizard extends Moveable
{
    protected int lives;
    protected double cooldown;
    protected boolean poweredUp;

    public Wizard(int x, int y, PImage sprite, double cooldown, int lives)
    {
        super(x, y, sprite);
        this.cooldown = cooldown;
        this.lives = lives;
        this.movementSpeed = 2;
    }

    // Set velocity based on direction moving (prevents initial stuttering when pressing key)
    public void update(boolean up, boolean down, boolean left, boolean right)
    {
        this.xVel = 0;
        this.yVel = 0;
        if (up)
        {
            this.yVel = -movementSpeed;
            return;
        }
        if (down)
        {
            this.yVel = movementSpeed;
            return;
        }
        if (left)
        {
            this.xVel = -movementSpeed;
            return;
        }
        if (right)
        {
            this.xVel = movementSpeed;
            return;
        }
    }

    public boolean collide(Object[][] objects)
    {
        if (xVel == 0 && yVel == 0)
        {
            return true;
        }
        int newX = (int)Math.ceil((this.x + this.xVel) / 20.0);
        int newY = (int)Math.ceil((this.y + this.yVel) / 20.0);
        //Check if wizard is inbetween tiles before moving to prevent clipping walls
        if (xVel != 0 && this.y % 20 != 0 || yVel != 0 && this.x % 20 != 0)
        {
            return true;
        }
        //Moving left or up, get floor
        if (xVel < 0 || yVel < 0)
        {
            newX = (int)Math.floor((this.x + this.xVel) / 20.0);
            newY = (int)Math.floor((this.y + this.yVel) / 20.0);
        }
        //Move wizard object to new coordinate on gridmap if no collision
        if (objects[newY][newX] == null || objects[newY][newX].equals(this))
        {
            int[] coords = App.findObject(App.findWizard(objects), objects);
            objects[coords[0]][coords[1]] = null;
            objects[newY][newX] = this;
            return false;
        }
        return true;
    }

}
