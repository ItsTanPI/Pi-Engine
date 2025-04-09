package org.PiEngine;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.*;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import org.PiEngine.Math.*;
import org.PiEngine.Core.*;
import org.PiEngine.GameObjects.*;
import org.PiEngine.Component.*;
import org.PiEngine.Editor.*;
import org.PiEngine.Render.*;

public class Main
{
    public static void main(String[] args)
    {
        if (!glfwInit())
        {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        long window = glfwCreateWindow(1280, 720, "Pi-Engine", 0, 0);
        if (window == 0)
        {
            throw new RuntimeException("Failed to create window");
        }

        Input.init(window);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        int width = 1280;
        int height = 720;
        glViewport(0, 0, width, height);

        // --- ImGui Init ---
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        ImGuiImplGlfw imguiGlfw = new ImGuiImplGlfw();
        ImGuiImplGl3 imguiGl3 = new ImGuiImplGl3();
        imguiGlfw.init(window, true);
        imguiGl3.init("#version 330 core");

        // --- Camera Setup ---
        Camera camera = new Camera((float) width / height, 0.01f, 100.0f);
        camera.setPosition(new Vector(0, 0, 20.0f));
        camera.setRotation(new Vector(0, 0, 0));
        camera.setOrthographic( 8*-2, 8*2, -2 *4.5f, 2*4.5f, 1.0f, 100f);
        camera.updateProjectionMatrix();
        camera.updateViewMatrix();
        
        
        Camera camera1 = new Camera((float) 1, 0.01f, 100.0f);
        camera1.setPosition(new Vector(0, 0, 20.0f));
        camera1.setPerspective(45.0f, (float) width / height, 0.01f, 100f);
        camera1.updateProjectionMatrix();
        camera1.updateViewMatrix();

        glEnable(GL_DEPTH_TEST);

        // --- GameObject Setup ---
        GameObject world = new GameObject("World");
        GameObject player = new GameObject("Player");
        GameObject enemy = new GameObject("Enemy");
        GameObject enemy1 = new GameObject("Enemy1");
        GameObject enemy2 = new GameObject("Enemy2");
        GameObject enemy3 = new GameObject("Enemy3");
        GameObject holder = new GameObject("Holder");
        GameObject childHolder = new GameObject("ChildHolder");
        GameObject cChildHolder = new GameObject("CChildHolder");

        player.transform.setLocalPosition(new Vector(0f, 0, 0));
        holder.transform.setLocalPosition(new Vector(4f, 0, 0));
        childHolder.transform.setLocalPosition(new Vector(5f, 0, 0));
        cChildHolder.transform.setLocalPosition(new Vector(5f, 0, 0));

        world.addChild(player);
        world.addChild(enemy);
        world.addChild(enemy1);
        world.addChild(enemy2);
        world.addChild(enemy3);
        world.addChild(holder);
        holder.addChild(childHolder);
        childHolder.addChild(cChildHolder);

        // --- Components ---
        player.addComponent(new Movemet());
        enemy.addComponent(new Follower());
        enemy1.addComponent(new Follower());
        enemy2.addComponent(new Follower());
        enemy3.addComponent(new Follower());
        holder.addComponent(new SpinComponent());
        childHolder.addComponent(new SpinComponent());



        // Add RendererComponent to all objects
        player.addComponent(new RendererComponent());
        enemy.addComponent(new RendererComponent());
        enemy1.addComponent(new RendererComponent());
        enemy2.addComponent(new RendererComponent());
        enemy3.addComponent(new RendererComponent());
        holder.addComponent(new RendererComponent());
        childHolder.addComponent(new RendererComponent());
        cChildHolder.addComponent(new RendererComponent());


        enemy.getComponent(Follower.class).Target = player;
        enemy1.getComponent(Follower.class).Target = enemy;
        enemy2.getComponent(Follower.class).Target = enemy1;
        enemy3.getComponent(Follower.class).Target = enemy2;

        Time.timeScale = 1.0f;

        // --- Editor Setup ---
        Editor editor = new Editor(window, false);
        editor.init();

        editor.addWindow(new HierarchyWindow(world));
        editor.addWindow(new InspectorWindow(false));

        InspectorWindow inspector = new InspectorWindow(true);
        inspector.propertyObject = player;
        editor.addWindow(inspector);

        editor.addWindow(new PerfomanceWindow());

        SceneWindow sceneWindow = new SceneWindow("Orthograpic");
        editor.addWindow(sceneWindow);

        SceneWindow sceneWindow1 = new SceneWindow("Perspective");
        editor.addWindow(sceneWindow1);

        // --- Renderer Setup ---
        Shader mainShader = new Shader(
            "src/main/java/org/PiEngine/Shaders/Camera/camera.vert",
            "src/main/java/org/PiEngine/Shaders/Camera/camera.frag",
            null
        );

        Shader mainShader1 = new Shader(
            "src/main/java/org/PiEngine/Shaders/Camera/camera.vert",
            "src/main/java/org/PiEngine/Shaders/Camera/camera.frag",
            null
        );
        Renderer renderer = new Renderer(width / 2, height / 2, mainShader);
        Renderer renderer1 = new Renderer(width / 2, height / 2, mainShader1);


        // --- Main Loop ---
        while (!glfwWindowShouldClose(window))
        {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Time.update();
            Input.update();

            // --- Input Control ---
            float moveSpeed = 10f * Time.deltaTime;

            if (Input.isKeyDown(GLFW_KEY_UP)) camera.getPosition().y -= moveSpeed;
            if (Input.isKeyDown(GLFW_KEY_DOWN)) camera.getPosition().y += moveSpeed;
            if (Input.isKeyDown(GLFW_KEY_LEFT)) camera.getPosition().x -= moveSpeed;
            if (Input.isKeyDown(GLFW_KEY_RIGHT)) camera.getPosition().x += moveSpeed;

            if (Input.isKeyDown(GLFW_KEY_SPACE)) cChildHolder.reparentTo(world);
            if (Input.isKeyDown(GLFW_KEY_ENTER)) cChildHolder.reparentTo(childHolder);
            if (Input.isKeyDown(GLFW_KEY_N)) cChildHolder.reparentTo(player);

            // --- Game Logic ---
            world.update();
            camera.updateViewMatrix();
            camera.applyToShader(mainShader);

            
            // --- Render to Texture ---
            renderer.render(camera, world);
            int outputTex = renderer.getOutputTexture();
            sceneWindow.setid(outputTex);
            
            
            
            
            camera1.updateViewMatrix();
            camera1.applyToShader(mainShader1);
            renderer1.render(camera1, world);
            int outputTex1 = renderer1.getOutputTexture();
            sceneWindow1.setid(outputTex1);

            // --- Editor Update ---
            editor.update(Time.deltaTime);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        // --- Cleanup ---
        ImGui.destroyContext();
        glfwTerminate();
    }
}
