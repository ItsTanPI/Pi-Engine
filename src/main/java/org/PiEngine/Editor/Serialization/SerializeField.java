package org.PiEngine.Editor.Serialization;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class SerializeField<T>
{
    protected String name = new String();
    protected String label = new String();

    public SerializeField(String name, String label){this.name = name;this.label = label;}

    public String getName(){return name;}

    public String getLabel(){return label;}
    public void setName(String name){this.name = name;}
    public void setLabel(String label){this.label = label;}

    public abstract void draw();
    public abstract void syncWith(Supplier<T> getter, Consumer<T> setter);
    public abstract void handle();
    /**
     * Sets the value of the field.
     * @param value The value to set
     */
    public abstract void set(T value);
    /**
     * Gets the value of the field.
     * @return The value
     */
    public abstract T getValue();
}



