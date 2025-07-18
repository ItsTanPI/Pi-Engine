package org.PiEngine.Editor;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

import org.PiEngine.Main;
import org.PiEngine.Manager.AssetManager;
import org.PiEngine.Utils.GUID;
import org.PiEngine.Utils.GUIDProvider;

/**
 * Displays a file explorer for src/main/resources directory.
 */
public class FileWindow extends EditorWindow
{
    private File rootDirectory;
    private static int count = 0;
    private static final Path BASE_PATH = Paths.get(Main.ResourceFolder).toAbsolutePath().normalize();

    private static final Path ABSOLUTE_BASE = Paths.get("").toAbsolutePath().normalize();
    private static final Path RELATIVE_BASE = Paths.get(Main.ResourceFolder).normalize();

    /**
     * Constructs a new FileWindow and sets the root directory.
     */
    public FileWindow()
    {
        super("File Explorer");
        id = count++;
        rootDirectory = new File(BASE_PATH.toString());
        // System.out.println(rootDirectory.exists() + " " + rootDirectory.isDirectory()); 
    }

    /**
     * Called every frame to update window logic.
     */
    @Override
    public void onUpdate()
    {
    }

    /**
     * Renders the file explorer window and its contents.
     */
    @Override
    public void onRender()
    {
        if (!isOpen || rootDirectory == null) return;

        ImBoolean open = new ImBoolean(true);
        if (!ImGui.begin(name + "##" + id, open))
        {
            ImGui.end();
            return;
        }

        if (!open.get())
        {
            Editor.get().queueRemoveWindow(this);
        }

        
        if (rootDirectory.exists() && rootDirectory.isDirectory())
        {
            renderDirectoryRecursive(rootDirectory);
        }
        else
        {
            ImGui.text("Directory not found: " + rootDirectory.getPath());
        }

        ImGui.end();
    }

    private void renderDirectoryRecursive(File directory)
    {
        if (directory == null || !directory.exists()) return;

        File[] files = directory.listFiles();
        if (files == null) return;

        Arrays.sort(files, Comparator
            .comparing(File::isFile)
            .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER));

        for (File file : files)
        {
            ImGui.pushID(file.getPath());

            int flags = ImGuiTreeNodeFlags.OpenOnArrow;
            if (file.isFile())
            {
                flags |= ImGuiTreeNodeFlags.Leaf;
            }

            boolean nodeOpen = ImGui.treeNodeEx(file.getName(), flags);

            if (file.isFile())
            {   
                
                String relativePath = ABSOLUTE_BASE.relativize(file.toPath().toAbsolutePath().normalize()).toString();
                String guid = GUID.generateGUIDFromPath(relativePath);
                
                if (AssetManager.get(guid) != null)
                {
                    Object asset = AssetManager.get(guid);
                    if (asset instanceof GUIDProvider && ImGui.beginDragDropSource())
                    {
                        GUIDProvider Fileguid = (GUIDProvider) asset;
                        ImGui.setDragDropPayload("GUIDPROVIDER", Fileguid, ImGuiCond.None);
                        ImGui.text("Dragging: " + Fileguid.getGUID());
                        ImGui.endDragDropSource();
                    }
                    
                }
            }

            if (nodeOpen)
            {
                if (file.isDirectory())
                {
                    renderDirectoryRecursive(file);
                }
                ImGui.treePop();
            }

            ImGui.popID();
        }
    }

    @Override
    public void setCustomTheme()
    {
    }
}
