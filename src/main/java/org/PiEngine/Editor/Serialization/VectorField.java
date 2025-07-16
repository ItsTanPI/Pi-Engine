package org.PiEngine.Editor.Serialization;

import imgui.ImGui;
import org.PiEngine.Math.Vector;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class VectorField extends SerializeField<Vector>
{
    private final float[] x = {0.0f};
    private final float[] y = {0.0f};
    private final float[] z = {0.0f};

    private Supplier<Vector> getter;
    private Consumer<Vector> setter;

    public VectorField(String name, String label)
    {
        super(name, label);
    }

    public void set(Vector initial)
    {
        x[0] = initial.x;
        y[0] = initial.y;
        z[0] = initial.z;
    }

    public Vector get()
    {
        return new Vector(x[0], y[0], z[0]);
    }

    public void syncWith(Supplier<Vector> getter, Consumer<Vector> setter)
    {
        this.getter = getter;
        this.setter = setter;
    }

    public void handle()
    {
        if (getter != null && setter != null)
        {
            if (!ImGui.isAnyItemActive())
            {
                set(getter.get());
            }

            // ImGui.pushItemWidth(200); // slightly wider for all 3 floats
            ImGui.pushID(label); // ensure unique ID

            float[] xyz = {x[0], y[0], z[0]};
            boolean edited = ImGui.dragFloat3(name, xyz, 0.1f);

            // ImGui.sameLine();
            // ImGui.text(name);

            ImGui.popID();
            // ImGui.popItemWidth();

            if (edited)
            {
                x[0] = xyz[0];
                y[0] = xyz[1];
                z[0] = xyz[2];
                setter.accept(get());
            }
        }
        else
        {
            draw(); // fallback
        }
    }

    @Override
    public void draw()
    {
            // ImGui.text(name);
            // ImGui.sameLine();
        // ImGui.pushItemWidth(200);

        ImGui.pushID(label);
        float[] xyz = {x[0], y[0], z[0]};
        ImGui.dragFloat3(name, xyz, 0.1f);
        x[0] = xyz[0];
        y[0] = xyz[1];
        z[0] = xyz[2];
        ImGui.popID();
        ImGui.sameLine();
        ImGui.textDisabled("(?)");

        if (ImGui.isItemHovered())
        {
            ImGui.beginTooltip();
            ImGui.text("is set to null in script");
            ImGui.endTooltip();
        }

        // ImGui.popItemWidth();
    }

    @Override
    public Vector getValue() 
    {
        return get();
    }


}
