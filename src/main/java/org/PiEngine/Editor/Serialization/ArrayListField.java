package org.PiEngine.Editor.Serialization;

import imgui.ImGui;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.List;

/**
 * A serializable field for ArrayLists of any type.
 * @param <T> The type of elements in the ArrayList
 */
public class ArrayListField<T, F extends SerializeField<T>> extends SerializeField<ArrayList<T>>
{   
    private ArrayList<T> value;
    private ArrayList<F> fieldEditors = new ArrayList<>();
    private Supplier<ArrayList<T>> getter;
    private Consumer<ArrayList<T>> setter;

    private final Class<F> fieldClass;

    /**
     * Constructs a ArrayListField with the given name and label.
     * @param name The field name
     * @param label The field label
     */
    public ArrayListField(String name, String label, Class<F> fieldClass) 
    {
        super(name, label);
        this.fieldClass = fieldClass;
        this.value = new ArrayList<>(); // Always non-null
    }

    /**
     * Synchronizes this field with external getter/setter.
     * @param getter Supplier for getting the ArrayList value
     * @param setter Consumer for setting the ArrayList value
     */
    public void syncWith(Supplier<ArrayList<T>> getter, Consumer<ArrayList<T>> setter) 
    {
        this.getter = getter;
        this.setter = setter;
        // Initialize field editors for each element
        value = getter.get();
        fieldEditors.clear();
        if (value != null) {
            int idx = 0;
            for (T item : value) {
                try {
                    F fieldEditor = fieldClass.getConstructor(String.class, String.class).newInstance(name + "[" + idx + "]", label + "[" + idx + "]");
                    fieldEditor.set(item);
                    fieldEditors.add(fieldEditor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                idx++;
            }
        }
    }

    /**
     * Handles synchronization and updates the value from the getter/setter.
     */
    public void handle() 
    {
        if (getter != null && setter != null) {
            value = getter.get();
            for (int i = 0; i < fieldEditors.size(); i++) {
                F fieldEditor = fieldEditors.get(i);
                fieldEditor.set(value.get(i));
                fieldEditor.handle();
                value.set(i, fieldEditor.getValue());
            }
            setter.accept(value);
        }
    }

    /**
     * Draws the field in the editor UI using ImGui.
     * Displays each item and allows editing/removal/addition.
     */
    @Override
    public void draw() 
    {
        if (value == null) value = new ArrayList<>();
        ImGui.text(label);
        for (int i = 0; i < fieldEditors.size(); i++) {
            ImGui.pushID(i);
            fieldEditors.get(i).draw();
            if (ImGui.button("Remove")) {
                value.remove(i);
                fieldEditors.remove(i);
                break;
            }
            ImGui.popID();
        }
        if (ImGui.button("Add Item")) {
            try {
                F newField = fieldClass.getConstructor(String.class, String.class).newInstance(name + "[" + value.size() + "]", label + "[" + value.size() + "]");
                // Optionally set a default value for newField
                fieldEditors.add(newField);
                value.add(newField.getValue());
            } catch (Exception e) { e.printStackTrace(); }
        }
    }   

    /**
     * Gets the current value of the ArrayList.
     * @return The ArrayList value
     */
    public ArrayList<T> getValue() {
        return value;
    }

    /**
     * Sets the value of the ArrayList.
     * @param value The new ArrayList value
     */
    public void setValue(ArrayList<T> value) {
        this.value = value;
        // Rebuild field editors for new value
        fieldEditors.clear();
        if (value != null) {
            int idx = 0;
            for (T item : value) {
                try {
                    F fieldEditor = fieldClass.getConstructor(String.class, String.class).newInstance(name + "[" + idx + "]", label + "[" + idx + "]");
                    fieldEditor.set(item);
                    fieldEditors.add(fieldEditor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                idx++;
            }
        }
    }

    @Override
    public void set(ArrayList<T> value) {
        this.value = value;
        // Rebuild field editors for new value
        fieldEditors.clear();
        if (value != null) {
            int idx = 0;
            for (T item : value) {
                try {
                    F fieldEditor = fieldClass.getConstructor(String.class, String.class).newInstance(name + "[" + idx + "]", label + "[" + idx + "]");
                    fieldEditor.set(item);
                    fieldEditors.add(fieldEditor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                idx++;
            }
        }
    }

    /**
     * Sets the value from a List (for reflection compatibility).
     * @param value The new List value
     */
    public void set(List<T> value) {
        if (value instanceof ArrayList) {
            set((ArrayList<T>) value);
        } else {
            this.value = new ArrayList<>(value);
            // Rebuild field editors for new value
            fieldEditors.clear();
            if (this.value != null) {
                int idx = 0;
                for (T item : this.value) {
                    try {
                        F fieldEditor = fieldClass.getConstructor(String.class, String.class).newInstance(name + "[" + idx + "]", label + "[" + idx + "]");
                        fieldEditor.set(item);
                        fieldEditors.add(fieldEditor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    idx++;
                }
            }
        }
    }
}