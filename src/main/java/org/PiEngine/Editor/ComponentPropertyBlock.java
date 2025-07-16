package org.PiEngine.Editor;

import imgui.ImGui;

import org.PiEngine.Component.Component;
import org.PiEngine.Editor.Serialization.*;
import org.PiEngine.GameObjects.GameObject;
import org.PiEngine.Math.Vector;
import org.PiEngine.Render.Texture;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;



/**
 * ComponentPropertyBlock is responsible for rendering UI controls
 * for editing public fields of a given Component using ImGui.
 */
public class ComponentPropertyBlock {
    private final String label;


     private static final Map<Class<?>, Class<?>> fieldTypeMap = new HashMap<>();

    static {
        // Populate the map with field types and corresponding field classes
        fieldTypeMap.put(Float.class, FloatField.class);
        fieldTypeMap.put(Vector.class, VectorField.class);
        fieldTypeMap.put(GameObject.class, GameObjectField.class);
        fieldTypeMap.put(Integer.class, IntField.class);
        fieldTypeMap.put(Boolean.class, BooleanField.class);
        fieldTypeMap.put(String.class, StringField.class);
        fieldTypeMap.put(Texture.class, TextureField.class);
        fieldTypeMap.put(Component.class, ComponentField.class);
        // For lists, map List.class to ListField.class
        fieldTypeMap.put(ArrayList.class, ArrayListField.class);


        
        // Add other mappings as needed (e.g., String.class -> StringField.class)
    }

    /**
     * Constructs a new ComponentPropertyBlock with the given label.
     * @param label The label for the property block
     */
    public ComponentPropertyBlock(String label) {
        this.label = label;
    }

    /**
     * Draws UI fields for all public attributes of a Component.
     * Skips internal fields like 'gameObject' or 'transform'.
     * Supports editing float, Vector, and GameObject references.
     * @param c The component to edit
     */
    public void drawComponentFields(Component c) {
        Field[] fields = c.getClass().getFields(); // Only public fields

        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            // Skip non-editable or internal references
            if (fieldName.equals("gameObject") || fieldName.equals("transform")) {
                continue;
            }

            for (Map.Entry<Class<?>, Class<?>> entry : fieldTypeMap.entrySet()) {
                
                Class<?> handlerClass = entry.getKey();
                Class<?> FieldClass = entry.getValue();
                
                try {
                    Object value = field.get(c); 
                    if (handlerClass.isInstance(value) && handlerClass != Component.class && handlerClass != ArrayList.class) 
                    {
                        Object fieldHandler = FieldClass.getConstructor(String.class, String.class).newInstance(field.getName(), field.getName());
                        

                        Supplier<Object> supplier = () -> {
                            try {
                                return field.get(c);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                return null;
                            }
                        };
                        
                        Consumer<Object> consumer = newVal -> {
                            try {
                                field.set(c, newVal);
                            } catch (IllegalAccessException e) {
                                ImGui.text("Cannot modify: " + field.getName());
                            }
                        };

                        Method syncWithMethod = FieldClass.getMethod("syncWith", Supplier.class, Consumer.class);
                        syncWithMethod.invoke(fieldHandler, supplier, consumer);

                        Method setMethod = FieldClass.getMethod("set", value.getClass());
                        setMethod.invoke(fieldHandler, value); 

                        Method handleMethod = FieldClass.getMethod("handle");
                        handleMethod.invoke(fieldHandler);
                    }
                    else if (Component.class.isAssignableFrom(fieldType) && handlerClass == Component.class)
                    {
                        Object fieldHandler = new ComponentField(fieldName, fieldName, fieldType);
                        Supplier<Component> supplier = () -> {
                            try {
                                return (Component) field.get(c);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                return null;
                            }
                        };

                        Consumer<Component> consumer = newVal -> {
                            try {
                                field.set(c, newVal);
                            } catch (IllegalAccessException e) {
                                ImGui.text("Cannot modify: " + field.getName());
                            }
                        };

                        Method syncWithMethod = FieldClass.getMethod("syncWith", Supplier.class, Consumer.class);
                        syncWithMethod.invoke(fieldHandler, supplier, consumer);

                        Component cmovalue = (Component) field.get(c);  // Get the instance
                        Method setMethod = FieldClass.getMethod("set", Component.class);  // Expecting an instance of Component
                        setMethod.invoke(fieldHandler, cmovalue);  // Set the value

                        Method handleMethod = FieldClass.getMethod("handle");
                        handleMethod.invoke(fieldHandler);
                    }
                    else if(fieldType == handlerClass && handlerClass != ArrayList.class)
                    {
                        Object fieldHandler = FieldClass.getConstructor(String.class, String.class).newInstance(field.getName(), field.getName());
                        Method syncWithMethod;

                        Consumer<Object> consumer = newVal -> {
                            try {
                                field.set(c, newVal);
                            } catch (IllegalAccessException e) {
                                ImGui.text("Cannot modify: " + field.getName());
                            }
                        };

                        syncWithMethod = FieldClass.getMethod("syncWith", Supplier.class, Consumer.class);
                        syncWithMethod.invoke(fieldHandler, null, consumer);

                        Method draMethod = FieldClass.getMethod("draw");;
                        draMethod.invoke(fieldHandler);
                        
                    }
                    else if (FieldClass == ArrayListField.class && value instanceof ArrayList) {
                        System.err.println("found a list");
                        // Try to resolve element type and field class
                        Class<?> elementType = null;
                        if (field.getGenericType() instanceof java.lang.reflect.ParameterizedType) {
                            java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) field.getGenericType();
                            if (pt.getActualTypeArguments().length > 0) {
                                elementType = (Class<?>) pt.getActualTypeArguments()[0];
                            }
                        }
                        Class<?> elementFieldClass = fieldTypeMap.getOrDefault(elementType, FloatField.class); // Default to FloatField if not found
                        Object fieldHandler = ArrayListField.class.getConstructor(String.class, String.class, Class.class)
                            .newInstance(field.getName(), field.getName(), elementFieldClass);
                        Supplier<Object> supplier = () -> {
                            try {
                                return field.get(c);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                return null;
                            }
                        };
                        Consumer<Object> consumer = newVal -> {
                            try {
                                field.set(c, newVal);
                            } catch (IllegalAccessException e) {
                                ImGui.text("Cannot modify: " + field.getName());
                            }
                        };
                        Method syncWithMethod = ArrayListField.class.getMethod("syncWith", Supplier.class, Consumer.class);
                        syncWithMethod.invoke(fieldHandler, supplier, consumer);
                        Method setMethod = ArrayListField.class.getMethod("set", List.class);
                        setMethod.invoke(fieldHandler, value);
                        Method handleMethod = ArrayListField.class.getMethod("handle");
                        handleMethod.invoke(fieldHandler);
                        break;
                    }


                } catch (Exception e) {
                    e.printStackTrace(); 
                    ImGui.text("Failed to access: " + fieldName);
                }
            }
        }
        ImGui.separator();
    }

}
