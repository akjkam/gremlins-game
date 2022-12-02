package gremlins;

import processing.core.PImage;
/**
* Abstract class that inherits from the object class.
* Any objects that can move are of this class.
* Key functions for collision and movement determined by the x and y velocity attributes
*/
public abstract class Moveable extends Object
{
    protected int xVel;
    protected int yVel;
    protected int movementSpeed;

    public Moveable(int x, int y, PImage sprite)
    {
        super(x, y, sprite);
    }

    public void tick()
    {
        this.x += this.xVel;
        this.y += this.yVel;
    }

    public boolean collide(Object object, Object[][] objects)
    {
        int newX = (int)Math.ceil((this.x + this.xVel) / 20.0);
        int newY = (int)Math.ceil((this.y + this.yVel) / 20.0);
        //Moving left or up, get floor
        if (xVel < 0 || yVel < 0)
        {
            newX = (int)Math.floor((this.x + this.xVel) / 20.0);
            newY = (int)Math.floor((this.y + this.yVel) / 20.0);
        }
        int[] objectCoords = App.findObject(object, objects);
        if (objectCoords[0] == newY && objectCoords[1] == newX)
        {
            return true;
        }
        return false;
    }
}
