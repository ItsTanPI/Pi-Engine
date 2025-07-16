package org.PiEngine.Editor.Serialization;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.PiEngine.Manager.AssetManager;
import org.PiEngine.Render.Texture;
import org.PiEngine.Utils.GUIDProvider;

public class TextureField extends SerializeField<Texture>
{
    private Texture value;
    private Supplier<Texture> getter;
    private Consumer<Texture> setter;

    public TextureField(String name, String label) {
        super(name, label);
    }

    @Override
    public void set(Texture initialValue) {
        this.value = initialValue;
    }

    @Override
    public Texture getValue() {
        return value;
    }

    public void syncWith(Supplier<Texture> getter, Consumer<Texture> setter) {
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

    @Override
    public void draw() {
        
        String displayName = (value != null) ? "Texture ID: " + value.getTextureID() : "NULL";
        ImGui.inputText(label, new ImString(displayName), ImGuiInputTextFlags.ReadOnly);
        
        if (value != null) {
            ImGui.image(value.getTextureID(), 64, 64, 0, 1, 1 , 0);
        }

        
        if (ImGui.beginDragDropTarget()) {
            Object payloadObj = ImGui.acceptDragDropPayload("GUIDPROVIDER");
            if (payloadObj instanceof GUIDProvider) {
                GUIDProvider fileGUID = (GUIDProvider) payloadObj;
                value = (Texture) AssetManager.get(fileGUID.getGUID());
                if (setter != null) {
                    setter.accept(value);
                }
            }
            ImGui.endDragDropTarget();
        }

    }
}
