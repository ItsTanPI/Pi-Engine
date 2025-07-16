package org.PiEngine.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.PiEngine.GameObjects.GameObject;
import org.PiEngine.Math.Vector;

public class FieldTester extends Component
{
    public Integer Integer;
    public Float Float;
    public Boolean Boolean;
    public String String;
    public Double Double;
    public Vector Vector;
    public GameObject Gameobject;
    public Component Component;
    public Movement Movement;
    public SpinComponent SpinComponent;
    public Follower Follower;
    public RendererComponent RendererComponent;
    public CameraComponent CameraComponent;
    public ArrayList<Float> ListFloat = new ArrayList<>(Arrays.asList(1.0f, 2.0f, 3.0f));


    @Override
    public void start()
    {
        ListFloat.add(Float);
    }
}
