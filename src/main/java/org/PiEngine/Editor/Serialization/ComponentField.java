package org.PiEngine.Editor.Serialization;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImString;

import org.PiEngine.Component.Component;
import org.PiEngine.GameObjects.GameObject;

import java.lang.reflect.Field;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ComponentField extends SerializeField<Component> {
    private Component value;
    private Supplier<Component> getter;
    private Consumer<Component> setter;
    
    private final Class<?> fieldType;

    public ComponentField(String name, String label, Class<?> fieldType) 
    {
        super(name, label);
        this.fieldType = fieldType;
    }
    
    @Override
    public void set(Component initialValue) {
        this.value = initialValue;
    }

    @Override
    public Component getValue() {
        return value;
    }

    private Field field = null;
    
    public void set(Field initialValue) 
    {
        this.field = initialValue;
    }

    public void syncWith(Supplier<Component> getter, Consumer<Component> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public void handle() {
        if (getter != null && setter != null) {
            if (!ImGui.isAnyItemActive()) value = getter.get();
            draw();
            if (!ImGui.isAnyItemActive()) setter.accept(value);
        } else {
            draw();
        }
    }

    public void draw() {

        String displayName = (value != null) ? value.getClass().getSimpleName() + "(" + value.getGameObject().Name + ")" : "";
        String hint = fieldType.asSubclass(Component.class).getSimpleName(); 
        ImGui.inputTextWithHint(name, hint, new ImString(displayName), ImGuiInputTextFlags.None);

        if (ImGui.beginDragDropTarget()) {
            Object payloadObj = ImGui.acceptDragDropPayload("GAME_OBJECT"); 
            if (payloadObj instanceof GameObject droppedObj) {
                value = droppedObj.getComponent(fieldType.asSubclass(Component.class));
                if(setter != null) setter.accept(value);
            }
            ImGui.endDragDropTarget();
        }
        
        

        if (value != null) 
        {
            ImVec2 pos = ImGui.getItemRectMin();
            String contextId = "##Context_" + pos.x + "_" + pos.y + "_" +value.getId();
            if (ImGui.beginPopupContextItem(contextId))
            {
                if (ImGui.menuItem("Set to null"))
                {
                    value = null;
                    if (setter != null) setter.accept(null);
                }
                ImGui.endPopup();
            }
        }
        

    }

    public Component get() {
        return value;
    }
}
