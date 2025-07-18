package org.PiEngine.Editor;

import org.PiEngine.Engine.Scene;
import org.PiEngine.Manager.AssetManager;
import org.PiEngine.Scripting.ScriptLoader;

import imgui.ImGui;

public class NavigationWindow extends EditorWindow
{

    /**
     * Constructs a new NavigationWindow.
     */
    public NavigationWindow()
    {
        super("Navigation");
    }

    /**
     * Renders the navigation window and its menu bar.
     */
    @Override
    public void onRender()
    {


        // Files dropdown
        ImGui.beginMainMenuBar();
        if (ImGui.beginMenu("File"))
        {
            if (ImGui.menuItem("New", "Ctrl+N"))
            {
                
            }
            if (ImGui.menuItem("Open", "Ctrl+O"))
            {
            }

            if (ImGui.beginMenu("Load"))
            {
                if (ImGui.menuItem("Cube")) 
                {
                    Scene.getInstance().Load("Cube.json");       
                }
                if (ImGui.menuItem("Sysra")) 
                {
                    Scene.getInstance().Load("Sysra.json");   
                }
                if (ImGui.menuItem("Level")) 
                {
                    Scene.getInstance().Load("Level.json");   
                }
                if (ImGui.menuItem("Demo")) 
                {
                    Scene.getInstance().Load("Demo.json");   
                }
                ImGui.endMenu();    
            }

            if (ImGui.beginMenu("Save"))
            {
                if (ImGui.menuItem("Cube")) 
                {
                    Scene.getInstance().Save("Cube.json");       
                }
                if (ImGui.menuItem("Sysra")) 
                {
                    Scene.getInstance().Save("Sysra.json");;   
                }
                if (ImGui.menuItem("Level")) 
                {
                    Scene.getInstance().Save("Level.json");
                }
                if (ImGui.menuItem("Demo")) 
                {
                    Scene.getInstance().Save("Demo.json");
                }
                ImGui.endMenu();
            }

            if (ImGui.menuItem("Exit", "Ctrl+Esc"))
            {
                // Handle exit action
            }
            ImGui.endMenu();
        }

        // Edit dropdown
        if (ImGui.beginMenu("Edit"))
        {
            if (ImGui.menuItem("Undo"))
            {
                // Handle undo action
            }
            if (ImGui.menuItem("Redo"))
            {
                // Handle redo action
            }
            if (ImGui.menuItem("Copy"))
            {
                // Handle copy action
            }
            if (ImGui.menuItem("Paste"))
            {
                // Handle paste action
            }
            ImGui.endMenu();
        }

        // Window dropdown
        if (ImGui.beginMenu("Window"))
        {
            if (ImGui.menuItem("Console"))
            {
                Editor.getInstance().queueAddWindow(new ConsoleWindow());
            }
            if (ImGui.menuItem("Performance"))
            {
                Editor.getInstance().queueAddWindow(new PerfomanceWindow());
            }
            if (ImGui.menuItem("Hierarchy"))
            {
                 Editor.getInstance().queueAddWindow(new HierarchyWindow());
            }
            if (ImGui.menuItem("Inspector"))
            {
                Editor.getInstance().queueAddWindow(new InspectorWindow());
            }
            if (ImGui.menuItem("Layer"))
            {
                Editor.getInstance().queueAddWindow(new LayerWindow());
            }
            if (ImGui.menuItem("Render Inspector"))
            {
                Editor.getInstance().queueAddWindow(new RendererInspector(Scene.getInstance().getGameRenderer()));
            }
            if (ImGui.menuItem("Render Graph"))
            {
                Editor.getInstance().queueAddWindow(new RenderGraphEditorWindow(Scene.getInstance().getGameRenderer()));
            }
            if (ImGui.menuItem("Scene View"))
            {
                Editor.getInstance().queueAddWindow(new SceneWindow("Scene View", Scene.getInstance().getSceneRenderer().getFinalFramebuffer()));
            }
            if (ImGui.menuItem("Game View"))
            {
                Editor.getInstance().queueAddWindow(new SceneWindow("Game View", Scene.getInstance().getGameRenderer().getFinalFramebuffer()));
            }
            if (ImGui.menuItem("Files"))
            {
                Editor.getInstance().queueAddWindow(new FileWindow());
            }
            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Assets"))
        {
            if (ImGui.menuItem("Compile Script"))
            {
                ScriptLoader.reset();
                Thread assetThread = new Thread(() -> {
                    AssetManager assetManager = new AssetManager() {};
                    assetManager.run();
                });
                assetThread.start();
            }
            


            ImGui.endMenu();
        }

        

        
        // Help dropdown
        if (ImGui.beginMenu("Help"))
        {
            if (ImGui.menuItem("Documentation"))
            {
                // Handle documentation action
            }
            if (ImGui.menuItem("About"))
            {
                // Handle about action
            }
            ImGui.endMenu();
        }
        ImGui.endMainMenuBar();
        
    }
}
