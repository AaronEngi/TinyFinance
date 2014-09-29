/*
 * jGnash, a personal finance application
 * Copyright (C) 2001-2014 Craig Cavanaugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jgnash.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Leif-Erik Dörr
 * @author Craig Cavanaugh
 */
public final class PluginFactory {

    private String pluginDirectory = null;
    private final static String PLUGIN_DIRECTORY_NAME = "plugins";
    private final static BigDecimal INTERFACE_VERSION = new BigDecimal("2.5");
    private static final Logger logger = Logger.getLogger(PluginFactory.class.getName());

    /* Singleton */
    private static final PluginFactory factory;
    private static final List<Plugin> plugins;
    private static boolean pluginsStarted = false;
    private static boolean pluginsLoaded = false;

    static {
        factory = new PluginFactory();
        plugins = new ArrayList<>();
    }

    private PluginFactory() {
        pluginDirectory = getPluginDirectory();
    }

    public static PluginFactory get() {
        return factory;
    }

    public static List<Plugin> getPlugins() {
        return Collections.unmodifiableList(plugins);
    }

    private synchronized String getPluginDirectory() {
        if (pluginDirectory == null) {
            pluginDirectory = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

            // decode to correctly handle spaces, etc. in the returned path
            try {
                pluginDirectory = URLDecoder.decode(pluginDirectory, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException ex) {
                logger.log(Level.SEVERE, null, ex);
            }

            pluginDirectory = new File(pluginDirectory).getParent();
            pluginDirectory += File.separator + PLUGIN_DIRECTORY_NAME + File.separator;

            logger.log(Level.INFO, "Plugin path: {0}", pluginDirectory);
        }

        return pluginDirectory;
    }

    public static void startPlugins() {
        if (!pluginsStarted) {

            for (Plugin plugin : plugins) {
                logger.info("Starting plugin: " + plugin.getName());
                plugin.start();
            }

            pluginsStarted = true;
        }
    }

    public static void stopPlugins() {
        if (pluginsStarted) {
            for (Plugin plugin : plugins) {
                logger.info("Stopping plugin: " + plugin.getName());
                plugin.stop();
            }

            pluginsStarted = false;
        }
    }

    public void loadPlugins() {

        if (!pluginsLoaded) {
            String[] paths = getPluginPaths();

            if (paths != null) {
                for (String plugin : paths) {
                    try {
                        Plugin p = loadPlugin(plugin);
                        if (p != null) {
                            plugins.add(p);
                        }
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                logger.info("Did not find any plugins");
            }

            pluginsLoaded = true;
        }
    }

    private String[] getPluginPaths() {
        File dir = new File(getPluginDirectory());        
        return dir.list(new PluginFilenameFilter());
    }

    private Plugin loadPlugin(final String jarFileName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {

        // If classLoader is closed, the plugin is not able to load new classes...
        @SuppressWarnings("resource")
        JarURLClassLoader classLoader = new JarURLClassLoader(new URL("file:///" + getPluginDirectory() + jarFileName));

        Plugin plugin = null;

        String pluginActivator = classLoader.getActivator();

        if (pluginActivator != null) {
            Object object = classLoader.loadClass(pluginActivator).newInstance();

            if (object instanceof Plugin) {
                plugin = (Plugin) object;
            }
        } else {
            logger.log(Level.SEVERE, "''{0}'' Plugin Interface was not implemented", jarFileName);
        }

        return plugin;
    }

    /**
     * @see java.net.URLClassLoader
     */
    private static class JarURLClassLoader extends URLClassLoader {

        private static final String PLUGIN_ACTIVATOR = "Plugin-Activator";
        private static final String PLUGIN_VERSION = "Plugin-Version";

        public JarURLClassLoader(final URL url) {
            super(new URL[]{url});
        }

        public String getActivator() throws IOException {

            String activator = null;

            URL u = new URL("jar", "", getURLs()[0] + "!/");

            JarURLConnection uc = (JarURLConnection) u.openConnection();

            Attributes attr = uc.getMainAttributes();

            if (attr != null) {

                BigDecimal version = null;
                try {
                    String value = attr.getValue(PLUGIN_VERSION);

                    if (value != null) {
                        version = new BigDecimal(attr.getValue(PLUGIN_VERSION));
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, null, ex);
                }

                if (version != null && INTERFACE_VERSION.compareTo(version) >= 0) {
                    activator = attr.getValue(PLUGIN_ACTIVATOR);
                }
            }

            return activator;
        }
    }

    private static class PluginFilenameFilter implements FilenameFilter {

        @Override
        public boolean accept(final File dir, final String name) {
            return name.endsWith(".jar");
        }
    }
}
