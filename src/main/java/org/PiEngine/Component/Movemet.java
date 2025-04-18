package org.PiEngine.Component;

import org.PiEngine.Core.*;
import org.PiEngine.Math.*;
import static org.lwjgl.glfw.GLFW.*;


public class Movemet extends Component
{
    public float speed = 10.0f;

    @Override
    public void update() 
    {

        int x = (Input.isKeyDown(GLFW_KEY_D)?1:(Input.isKeyDown(GLFW_KEY_A)?-1:0));
        int y = (Input.isKeyDown(GLFW_KEY_W)?1:(Input.isKeyDown(GLFW_KEY_S)?-1:0));

        Vector input = new Vector(x, y, 0);
        input.normalize();

        transform.setLocalPosition(transform.getLocalPosition().add(input.scale(Time.deltaTime * speed)));
    }
}
