package org.PiEngine.Editor.Serialization;

import imgui.ImGui;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanField extends SerializeField<Boolean> {
    private boolean value;
    private Supplier<Boolean> getter;
    private Consumer<Boolean> setter;

    public BooleanField(String name, String label) {
        super(name, label);
    }

    @Override
    public void set(Boolean initialValue) {
        this.value = initialValue;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    public void syncWith(Supplier<Boolean> getter, Consumer<Boolean> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public void handle() {
        if (getter != null && setter != null) {
            value = getter.get();
            




            ImGui.pushID(label);
            if (ImGui.checkbox(name, value)) {
                setter.accept(!value);
            }
            ImGui.popID();

        } else {
            draw();
        }
    }

    @Override
    public void draw() {
        // ImGui.text(name);
        // ImGui.sameLine();

        boolean temp = value;
        ImGui.pushID(label);
        ImGui.checkbox(name, temp);
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
