package org.PiEngine.Editor.Serialization;

import imgui.ImGui;
import imgui.type.ImString;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StringField extends SerializeField<String> {
    private String value = "";
    private Supplier<String> getter;
    private Consumer<String> setter;

    private final ImString buffer = new ImString(256); // You can adjust buffer size as needed

    public StringField(String name, String label) {
        super(name, label);
    }

    @Override
    public void set(String initialValue) {
        this.value = initialValue;
        buffer.set(initialValue);
    }

    @Override
    public String getValue() {
        return value;
    }

    public void syncWith(Supplier<String> getter, Consumer<String> setter) {
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
            boolean edited = ImGui.inputText(name, buffer);
            ImGui.popID();

            if (edited) {
                value = buffer.get();
                setter.accept(value);
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
        ImGui.inputText(name, buffer);
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
