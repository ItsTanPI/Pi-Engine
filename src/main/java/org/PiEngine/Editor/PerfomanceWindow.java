package org.PiEngine.Editor;

import imgui.ImGui;
import org.PiEngine.Core.*;
import imgui.ImVec2;
import imgui.type.ImBoolean;

public class PerfomanceWindow extends EditorWindow
{
    public static int count = 0;

    /**
     * Constructs a new PerfomanceWindow.
     */
    public PerfomanceWindow()
    {
        super("Perfomance");
        id = count++;
    }

    /**
     * Constructs a new PerfomanceWindow with a custom name.
     * @param Name The window name
     */
    public PerfomanceWindow(String Name)
    {
        super(Name);
        id = count++;
    }

    /**
     * Renders the performance window, showing FPS and memory usage.
     */
    @Override
    public void onRender()
    {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();


        ImBoolean isOpen = new ImBoolean(true);
        if (!ImGui.begin(name + "##" + id, isOpen))
        {
            ImGui.end();
            return;
        }

        if (!isOpen.get())
        {
            Editor.get().queueRemoveWindow(this);
        }
        
        ImGui.text("DeltaTime : " + String.format("%.10f", Time.unscaledDeltaTime));
        ImGui.text("FPS : " + (int)(1.0 / Time.unscaledDeltaTime));
        ImGui.text("Average FPS : " + Time.getAverageFPS());
        ImGui.text("Delta Time (graph)");
        ImGui.plotLines("", Time.getDeltaHistory(), Time.getHistorySize(), 0, null, 0.0f, 0.01f, new ImVec2(0f, 80f));

        ImGui.separator();

        ImGui.text("Memory Usage:");
        ImGui.text("Used Memory: " + (usedMemory / (1024 * 1024)) + " MB");
        ImGui.text("Allocated Memory: " + (totalMemory / (1024 * 1024)) + " MB");
        ImGui.text("Max Memory: " + (maxMemory / (1024 * 1024)) + " MB");


        ImGui.end();
    }
}
