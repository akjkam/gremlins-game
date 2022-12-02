package gremlins.Projectiles;
import java.util.List;

import gremlins.Moveable;
import gremlins.Object;
/**
* Interface for all projectiles with unique collision and movement functions
*/
public interface Projectile
{
    public void update(int direction);
    public Object collideObjects(Object[][] objects);
}
