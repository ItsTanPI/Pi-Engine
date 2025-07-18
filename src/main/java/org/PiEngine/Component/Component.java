package org.PiEngine.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.PiEngine.Core.Camera;
import org.PiEngine.GameObjects.*;
import org.PiEngine.Manager.AssetManager;
import org.PiEngine.Math.Vector;
import org.PiEngine.Utils.ComponentFactory;
import org.PiEngine.Utils.GUIDProvider;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.PiEngine.Engine.Console;
import org.PiEngine.Engine.Scene;

/**
 * Base class for all components that can be attached to a GameObject.
 * <p>
 * Subclass this to create custom behaviors (e.g., physics, scripts, renderers).
 * Lifecycle methods are called by the engine automatically.
 */
public abstract class Component
{
    /** Reference to the GameObject this component is attached to */
    public GameObject gameObject;
    public Transform transform;
    private int id;

    public Component()
    {
        id = IDGenerator.generateUniqueID();
    }

    /**
     * Called once when the component is first added to a GameObject.
     * Override this for setup logic, such as initialization or references.
     */
    public void start() {}

    /**
     * Called every frame. Use for real-time logic like movement, input, etc.
     */
    public void update() {}

    /**
     * Called at a fixed interval, typically used for physics calculations or consistent simulations.
     * Uses Time.fixedDeltaTime under the hood.
     */
    public void fixedUpdate() {}

    /**
     * Called once before the component is removed or the GameObject is destroyed.
     * Use this to clean up or detach listeners/resources.
     */
    public void onDestroy() {}

    /**
     * Called every frame for rendering-related behavior.
     * Override if your component needs to draw visuals using OpenGL or other rendering logic.
     * @param camera The camera to use for rendering
     */
    public void render(Camera camera) {}

    /**
     * Called every frame after rendering, mainly for development tools or debugging purposes.
     * Example: Drawing wireframes, bounding boxes, or debug info.
     */
    public void debugRender() {}


    // A helper method to get the line number from the exception
    private String getLineNumber(Exception e)
    {
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0)
        {
            for (StackTraceElement element : stackTrace)
            {
                // Filter or check specific methods for line numbers
                if (element.getClassName().equals(this.getClass().getName())) 
                {
                    return "Line: " + element.getLineNumber();
                }
            }
        }
        return "Unknown Line";
    }

    /**
     * Runs update() safely, catching and logging exceptions with line numbers.
     */
    final public void safeUpdate() 
    {
        try 
        {
            update(); 
        } 
        catch (Exception e) 
        {
            String errorMessage = "Exception in update: " + e.getMessage() + " (" + getLineNumber(e) + ")";
            Console.errorClass(errorMessage, this.getClass().getSimpleName()+".java");
        }
    }

    /**
     * Runs start() safely, catching and logging exceptions with line numbers.
     */
    final public void safeStart() 
    {
        try 
        {
            start(); 
        } 
        catch (Exception e) 
        {
            String errorMessage = "Exception in start: " + e.getMessage() + " (" + getLineNumber(e) + ")";
            Console.errorClass(errorMessage, this.getClass().getSimpleName()+".java");
        }
    }

    /**
     * Runs fixedUpdate() safely, catching and logging exceptions with line numbers.
     */
    final public void safeFixedUpdate() 
    {
        try 
        {
            fixedUpdate(); 
        } 
        catch (Exception e) 
        {
            String errorMessage = "Exception in fixedUpdate: " + e.getMessage() + " (" + getLineNumber(e) + ")";
            Console.errorClass(errorMessage, this.getClass().getSimpleName()+".java");
        }
    }

    final public void safeRender(Camera cam) 
    {
        try 
        {
            render(cam); 
        } 
        catch (Exception e) 
        {
            String errorMessage = "Exception in render: " + e.getMessage() + " (" + getLineNumber(e) + ")";
            Console.errorClass(errorMessage, this.getClass().getSimpleName()+".java");
        }
    }

    /**
     * Returns a map of property names and values for this component, including
     * serialized references for GameObjects and Components.
     * @return A map of property names to their values
     */
    public Map<String, Object> getProperties() 
    {
        Map<String, Object> properties = new HashMap<>();
    
        Field[] fields = this.getClass().getDeclaredFields();
    
        for (Field field : fields) 
        {
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (value instanceof GameObject) 
                {
                    value = GameObject.Location((GameObject)value);
                }
                if (Component.class.isAssignableFrom(field.getType()))
                {
                    Component cmp = (Component)field.get(this);
                    if(cmp != null)
                    { 
                        Object fieldValue = field.get(this);
                        String str = GameObject.Location(cmp.gameObject) + "<" + fieldValue.getClass().getSimpleName() +">";
                        value = str;
                    }
                }
                else if (value instanceof GUIDProvider)
                {
                    value = ((GUIDProvider)value).getGUID();
                }
                
    
                properties.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    
        return properties;
    }
    
    /**
     * Returns the serialized location of the GameObject and component type.
     * @param cmp The component to get the location for
     * @return A string representing the location of the component
     */
    public static String Location(Component cmp)
    {
        return GameObject.Location(cmp.gameObject) + "<" + cmp.getClass().getSimpleName() +">";
    }

    /**
     * Sets a component property using reflection, with support for nested GameObjects and Components.
     * @param propertyName The name of the property to set
     * @param propertyValue The value to set the property to
     */
    public void setComponentProperty(String propertyName, JsonElement propertyValue) 
    {
        try 
        {
            Field field = this.getClass().getDeclaredField(propertyName);
            Object value = field.get(this);
            field.setAccessible(true);

            Class<?> fieldType = field.getType();

            if (fieldType == String.class) 
            {
                field.set(this, propertyValue.getAsString());
            } 
            else if (fieldType == Integer.class || fieldType == int.class) 
            {
                field.set(this, propertyValue.getAsInt());
            } else if (fieldType == Float.class || fieldType == float.class) 
            {
                field.set(this, propertyValue.getAsFloat());
            } 
            else if (fieldType == Boolean.class || fieldType == boolean.class) 
            {
                field.set(this, propertyValue.getAsBoolean());
            } 
            else if (fieldType == Vector.class) 
            {
                JsonObject vectorObject = propertyValue.getAsJsonObject();
                float x = vectorObject.get("x").getAsFloat();
                float y = vectorObject.get("y").getAsFloat();
                float z = vectorObject.get("z").getAsFloat();
                field.set(this, new Vector(x, y, z));
            } 
            else if (fieldType == GameObject.class) 
            {
                String location = propertyValue.getAsString();
                GameObject target = GameObject.findGameObject(location, Scene.getInstance().getRoot());
                field.set(this, target);
            } 
            else if (Component.class.isAssignableFrom(field.getType()))
            {
                String full = propertyValue.getAsString();
                int start = full.indexOf('<');
                int end = full.indexOf('>');

                String component = full.substring(start + 1, end);
                String path = full.substring(0, start);

                
                GameObject target = GameObject.findGameObject(path, Scene.getInstance().getRoot());
                Class<? extends Component> componentClass = ComponentFactory.GetClass(component);
                if (componentClass != null && target != null) 
                {    
                    field.set(this, target.getComponent(componentClass));
                }
            }
            if (GUIDProvider.class.isAssignableFrom(field.getType()))
            {
                value = AssetManager.get(propertyValue.getAsString());
                field.set(this, value);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates fields in this component using reflection, clearing references to
     * destroyed GameObjects and Components.
     */
    final public void updateFields()
    {
        Class<?> clazz = this.getClass();
        while (clazz != null && clazz != Component.class)
        {
            for (Field field : clazz.getDeclaredFields())
            {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                try
                {
                    if (GameObject.class.isAssignableFrom(fieldType))
                    {
                        GameObject val = (GameObject) field.get(this);
                        if (val != null)
                        {
                            GameObject g = GameObject.findGameObject(val.Location, Scene.getInstance().getRoot());
                            if (g == null)
                            {
                                field.set(this, null);
                            }
                        }
                    }
                    else if (Component.class.isAssignableFrom(fieldType))
                    {
                        Component val = (Component) field.get(this);
                        if (val != null && val.gameObject != null)
                        {
                            GameObject g = GameObject.findGameObject(val.gameObject.Location, Scene.getInstance().getRoot());
                            if (g == null)
                            {
                                field.set(this, null); 
                            }
                        }
                    }
                    else if (GUIDProvider.class.isAssignableFrom(field.getType()))
                    {
                        GUIDProvider val = (GUIDProvider) field.get(this);
                        if(val != null)
                        {
                            Object guid = AssetManager.get(val.getGUID());
                            field.set(this, guid);
                        }
                    }
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    
    



    public GameObject getGameObject() {
        return gameObject;
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
