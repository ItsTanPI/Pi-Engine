package org.PiEngine.Editor.Serialization;

import imgui.ImGui;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntField extends SerializeField<Integer> {
    private final int[] value = {0};
    private Supplier<Integer> getter;
    private Consumer<Integer> setter;

    public IntField(String name, String label) {
        super(name, label);
    }

    @Override
    public void set(Integer initialValue) {
        this.value[0] = initialValue;
    }

    @Override
    public Integer getValue() {
        return value[0];
    }

    public void syncWith(Supplier<Integer> getter, Consumer<Integer> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public void handle() {
        if (getter != null && setter != null) {
            if (!ImGui.isAnyItemActive()) {
                set(getter.get());
            }

            // ImGui.text(name);
            // ImGui.sameLine();

            ImGui.pushID(label);
            boolean edited = ImGui.dragInt(name, value);
            ImGui.popID();

            if (edited) {
                setter.accept(value[0]);
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
        ImGui.dragInt(name, value);
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
