package org.PiEngine.Editor.Serialization;

import imgui.ImGui;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DoubleField extends SerializeField<Double> {
    private final float[] value = {0.0f};  // Use a float array for compatibility with ImGui
    private Supplier<Double> getter;
    private Consumer<Double> setter;

    public DoubleField(String name, String label) {
        super(name, label);
    }

    @Override
    public void set(Double initialValue) {
        this.value[0] = initialValue.floatValue();  // Convert Double to float for ImGui compatibility
    }

    @Override
    public Double getValue() {
        return (double) value[0];
    }

    public void syncWith(Supplier<Double> getter, Consumer<Double> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public void handle() {
        if (getter != null && setter != null) {
            if (!ImGui.isAnyItemActive()) {
                value[0] = getter.get().floatValue();  // Convert Double to float explicitly
            }

            boolean changed = false;
            // ImGui.text(name);
            // ImGui.sameLine();

            ImGui.pushID(label);
            changed = ImGui.dragFloat(name, value, 0.1f);  
            ImGui.popID();

            if (changed) {
                setter.accept((double) value[0]);  // Convert float back to Double explicitly
            }
        } else {
            draw();
        }
    }

    @Override
    public void draw() {
        // ImGui.text(name);
        // ImGui.sameLine();

        ImGui.pushID(label);
        ImGui.dragFloat(name, value, 0.1f);
        ImGui.popID();
        ImGui.sameLine();
        ImGui.textDisabled("(?)");
        if (ImGui.isItemHovered())
        {
            ImGui.beginTooltip();
            ImGui.text("is set to null in script");
            ImGui.endTooltip();
        }
    }
}
