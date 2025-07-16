package org.PiEngine.Editor.Serialization;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImString;

import org.PiEngine.GameObjects.GameObject;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GameObjectField extends SerializeField<GameObject> {
    private GameObject value;
    private Supplier<GameObject> getter;
    private Consumer<GameObject> setter;
    
    public GameObjectField(String name, String label) {
        super(name, label);
    }
    
    @Override
    public void set(GameObject initialValue) {
        this.value = initialValue;
    }

    @Override
    public GameObject getValue() {
        return value;
    }

    private Field field = null;
    
    public void set(Field initialValue) 
    {
        this.field = initialValue;
    }

    public void syncWith(Supplier<GameObject> getter, Consumer<GameObject> setter) {
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

        String displayName = (value != null && value.Name != null) ? value.Name : "";
        String hint = "GameObject"; 
        ImGui.inputTextWithHint(name, hint, new ImString(displayName), ImGuiInputTextFlags.None);

        
        if (ImGui.beginDragDropTarget()) {
            Object payloadObj = ImGui.acceptDragDropPayload("GAME_OBJECT");
            if (payloadObj instanceof GameObject droppedObj) {
                value = droppedObj;
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

    public GameObject get() {
        return value;
    }
}
