package gremlins.Projectiles;

import java.util.List;

import gremlins.App;
import gremlins.BrickWall;
import gremlins.Gremlin;
import gremlins.Moveable;
import gremlins.Object;
import gremlins.StoneWall;
import processing.core.PImage;
/**
* Fireball class inheriting from Moveable as a projectile
*/
public class Fireball extends Moveable implements Projectile
{

    public Fireball(int x, int y, PImage sprite)
    {
        super(x, y, sprite);
        this.movementSpeed = 4;
    }

    // Set velocity based on wizard direction
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

    public Object collideObjects(Object[][] objects)
    {
        int newX = (int)Math.ceil((this.x + this.xVel) / 20.0);
        int newY = (int)Math.ceil((this.y + this.yVel) / 20.0);
        //Moving left or up, get floor
        if (xVel < 0 || yVel < 0)
        {
            newX = (int)Math.floor((this.x + this.xVel) / 20.0);
            newY = (int)Math.floor((this.y + this.yVel) / 20.0);
        }
        //If collide with objects
        if (objects[newY][newX] != null && !(objects[newY][newX] instanceof Fireball))
        {
            return objects[newY][newX];
        }
        int[] coords = App.findObject(this, objects);
        if (coords != null)
        {
            objects[coords[0]][coords[1]] = null;
        }
        objects[newY][newX] = this;
        return null;
    }

}
