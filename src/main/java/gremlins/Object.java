package gremlins;

import processing.core.PApplet;
import processing.core.PImage;

/**
* All objects on map inherit from this class
* x and y coordinates indicate the position on the map
* Exact position of object on 2d map is determined by the floor or ceiling of coordinate X/Y divided by tile length/width
*/
public abstract class Object 
{
    protected int x;
    protected int y;

    protected PImage sprite;

    public Object(int x, int y, PImage sprite)
    {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    //Draw function for Objects
    public void draw(PApplet app)
    {
        app.image(this.sprite, this.x, this.y);
    }


    public void changeSprite(PImage sprite)
    {
        this.sprite = sprite;
    }

}
