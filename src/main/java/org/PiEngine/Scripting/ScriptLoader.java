package org.PiEngine.Scripting;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.PiEngine.Main;
import org.PiEngine.Component.Component;
import org.PiEngine.Engine.Console;
import org.PiEngine.Utils.ComponentFactory;

/**
 * Singleton class that handles runtime loading of compiled script components.
 * Manages class loading, registration with ComponentFactory, and resource cleanup.
 */
public class ScriptLoader 
{
    /** Singleton instance */
    private static ScriptLoader instance;

    /** Root directory for compiled scripts */
    private File rootDirectory;
    /** ClassLoader for dynamically loading compiled scripts */
    private URLClassLoader urlClassLoader;

    /**
     * Private constructor initializes the class loader for the compiled scripts directory.
     * @param compiledOutputPath Path to the compiled scripts directory
     * @throws Exception If directory is invalid or class loader creation fails
     */
    private ScriptLoader(String compiledOutputPath) throws Exception 
    {
        this.rootDirectory = new File(compiledOutputPath);

        if (!rootDirectory.exists()) 
        {
            throw new IllegalArgumentException("Compiled directory does not exist: " + compiledOutputPath);
        }

        URL[] urls = new URL[]{ rootDirectory.toURI().toURL() };
        this.urlClassLoader = new URLClassLoader(urls, getClass().getClassLoader());
    }

    /**
     * Gets or creates the ScriptLoader instance.
     * @return The singleton ScriptLoader instance
     */
    public static ScriptLoader getInstance() 
    {
        if (instance == null) 
        {
            try 
            {
                instance = new ScriptLoader(Main.ResourceFolder + "Compiled");
            } 
            catch (Exception e) 
            {
                throw new RuntimeException("Failed to initialize ScriptLoader", e);
            }
        }
        return instance;
    }

    /**
     * Resets the ScriptLoader state and cleans up resources.
     * Clears loaded components and closes the class loader.
     */
    public static void reset() 
    {
        if (instance != null) 
        {
            ComponentFactory.Clear();  
            instance.close();
            instance.urlClassLoader = null;
            instance = null;
        }
    }

    /**
     * Loads a class by its fully qualified name.
     * @param fullyQualifiedName The class name with package
     * @return The loaded Class object
     * @throws ClassNotFoundException If the class cannot be found
     */
    public Class<?> loadClass(String fullyQualifiedName) throws ClassNotFoundException 
    {
        return urlClassLoader.loadClass(fullyQualifiedName);
    }

    /**
     * Closes the URLClassLoader and releases resources.
     */
    public void close() 
    {
        try 
        {
            urlClassLoader.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    /**
     * Loads and registers a class as a Component if applicable.
     * @param fullClassName The full class name to load
     */
    private void loadClassAndRegister(String fullClassName)
    {
        try 
        {
            Class<?> scriptClass = loadClass(fullClassName);

            if (Component.class.isAssignableFrom(scriptClass)) 
            {
                ComponentFactory.register(scriptClass.getSimpleName(), () -> {
                    try {
                        return (Component) scriptClass.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                });

                Console.log("Loaded & registered component: " + fullClassName);
            } 
            else 
            {
                Console.warning("Skipped (not a Component): " + fullClassName);
            }
        } 
        catch (Exception e) 
        {
            Console.error("Failed to load: " + fullClassName);
            e.printStackTrace();
        }
    }

    /**
     * Loads and registers a single component script file.
     * @param scriptFile The .class file to load
     */
    public void loadComponentScript(File scriptFile) 
    {
        String className = scriptFile.getName().replace(".class", "");
        String fullClassName = "Scripts." + className;
        loadClassAndRegister(fullClassName);
    }

    /**
     * Loads and registers all component scripts from a folder.
     * @param folderPath Path to the folder containing .class files
     */
    public void loadComponentFolder(String folderPath) 
    {
        File componentDir = new File(folderPath);
        if (!componentDir.exists() || !componentDir.isDirectory()) return;

        File[] classFiles = componentDir.listFiles((dir, name) -> name.endsWith(".class"));
        if (classFiles == null) return;

        for (File file : classFiles) 
        {
            String className = file.getName().replace(".class", "");
            String fullClassName = "Scripts." + className;
            loadClassAndRegister(fullClassName);
        }
    }

    /** Loads system scripts from a folder (reserved for future use) */
    public void loadSystemScripts(String folderPath) { }

    /** Loads behavior scripts from a folder (reserved for future use) */
    public void loadBehaviorScripts(String folderPath) { }

    @Override
    protected void finalize() throws Throwable
    {
        System.out.println("Removed: " + this);
    }
}
