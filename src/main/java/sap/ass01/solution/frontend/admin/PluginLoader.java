package sap.ass01.solution.frontend.admin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    public static Iterable<RequestPlugin> loadPlugins(String jarFilePath) throws Exception {
        List<RequestPlugin> result = new ArrayList<>();

        JarFile jarFile = new JarFile(jarFilePath);

        URL[] urls = { new URL("file:" + jarFilePath) };
        URLClassLoader classLoader = new URLClassLoader(urls);

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            if (entry.getName().endsWith(".class")) {
                // Convert the entry name to a fully qualified class name
                String className = entry.getName().replace(".class", "").replace("/", ".");

                Class<?> loadedClass = classLoader.loadClass(className);

                // Check if the class implements the target interface
                if (RequestPlugin.class.isAssignableFrom(loadedClass) && !loadedClass.isInterface()) {
                    // Instantiate the class (assuming a no-arg constructor)
                    result.add((RequestPlugin) loadedClass.getDeclaredConstructor().newInstance());
                }
            }
        }

        jarFile.close();
        classLoader.close();

        return result;
    }
}
