package gremlins;


import processing.core.PApplet;
import processing.event.KeyEvent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;


public class SampleTest 
{
    private static App app;
    @BeforeEach
    public void setup()
    {
        app = new App();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.loop();
    }
    //Testing powerup
    @Test
    public void powerUp() 
    {
        app.delay(10000);
        app.keyCode = 40;
        app.keyPressed();
        app.delay(2000);
        assertTrue(app.wizard.poweredUp);
    }

    //Testing next level
    @Test
    public void nextLevel()
    {
        app.keyCode = 39;
        app.keyPressed();
        app.delay(1000);
        assertEquals(1, app.currentLevel);
    }
    //Testing wizard movement and collision with walls
    @Test
    public void collision() 
    {
        assertNotNull(app.wizard);
        assertNotNull(app.map);
        app.keyCode = 38;
        app.delay(500);
        app.keyPressed();
        app.delay(500);
        assertTrue(app.wizard.collide(app.map));
    }

    //Testing no collision
    @Test
    public void noCollision()
    {
        app.keyCode = 40;
        app.keyPressed();
        app.delay(100);
        assertFalse(app.wizard.collide(app.map));
    }

    //Testing for fireball
    @Test
    public void fireBall()
    {
        app.keyCode = 32;
        app.keyPressed();
        app.keyReleased();
        assertNotEquals(0, app.fireBalls.size());
        app.delay(2000);
        assertEquals(0, app.fireBalls.size());
    }

    //Testing multiple key inputs
    @Test
    public void keyInput()
    {
        app.keyCode = 38;
        app.keyPressed();
        app.delay(500);
        app.keyCode = 39;
        app.keyPressed();
        app.delay(500);
        assertEquals(2, app.wizard.xVel);
        app.keyCode = 38;
        app.keyReleased();
        app.delay(500);
        assertEquals(-2, app.wizard.yVel);
    }
}
