package gremlins;

import gremlins.Projectiles.Fireball;
import gremlins.Projectiles.Slimeball;
import processing.core.PImage;
/**
* Gremlin class inheriting from Moveable class.
* 
*/
public class Gremlin extends Moveable
{
    protected double cooldown;

    public Gremlin(int x, int y, PImage sprite, double cooldown)
    {
        super(x, y, sprite);
        this.cooldown = cooldown;
        this.movementSpeed = 1;
    }

    public void update(int direction)
    {
        xVel = 0;
        yVel = 0;
        switch (direction)
        {
            case 1: yVel = -movementSpeed; break;
            case 2: xVel = -movementSpeed; break;
            case 3: yVel = movementSpeed; break;
            case 4: xVel = movementSpeed; break;
        }

    }

    public int getOppositeDirection()
    {
        if (yVel == -movementSpeed)
        {
            return 3;
        }
        if (xVel == -movementSpeed)
        {
            return 4;
        }
        if (yVel == movementSpeed)
        {
            return 1;
        }
        if (xVel == movementSpeed)
        {
            return 2;
        }
        return 0;
    }

    public int getDirection()
    {
        if (yVel == -movementSpeed)
        {
            return 1;
        }
        if (xVel == -movementSpeed)
        {
            return 2;
        }
        if (yVel == movementSpeed)
        {
            return 3;
        }
        if (xVel == movementSpeed)
        {
            return 4;
        }
        return 0;
    }

    public boolean collide(Object[][] objects)
    {
        int newX = (int)Math.ceil((this.x + this.xVel) / 20.0);
        int newY = (int)Math.ceil((this.y + this.yVel) / 20.0);
        //Moving left or up, get floor
        if (xVel < 0 || yVel < 0)
        {
            newX = (int)Math.floor((this.x + this.xVel) / 20.0);
            newY = (int)Math.floor((this.y + this.yVel) / 20.0);
        }
        if (objects[newY][newX] == null || objects[newY][newX].equals(this))
        {
            int[] coords = App.findObject(this, objects);
            objects[coords[0]][coords[1]] = null;
            objects[newY][newX] = this;
            return false;
        }
        else if (objects[newY][newX] instanceof Gremlin ||
                 objects[newY][newX] instanceof Slimeball ||
                 objects[newY][newX] instanceof Fireball ||
                 objects[newY][newX] instanceof Wizard ||
                 objects[newY][newX] instanceof Door ||
                 objects[newY][newX] instanceof Teleport)
        {
            return false;
        }
        return true;
    }
}
