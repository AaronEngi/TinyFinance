/*
 * jGnash, a personal finance application
 * Copyright (C) 2001-2015 Craig Cavanaugh
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
package jgnash.uifx.skin;

import java.util.Objects;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

import jgnash.util.NotNull;

/**
 * Theme manager
 *
 * @author Craig Cavanaugh
 */
public class ThemeManager {

    private static final Preferences preferences;

    private static final String LAST = "last";

    private static final String FONT_SIZE = "fontSize";

    private static final String BASE_COLOR = "baseColor";

    private static Menu themesMenu;

    private static final ToggleGroup toggleGroup = new ToggleGroup();

    private static final ThemeHandler themeHandler = new ThemeHandler();

    private static final String[][] KNOWN_THEMES = {
            {"Modena", Application.STYLESHEET_MODENA},
            {"Caspian", Application.STYLESHEET_CASPIAN},
    };

    private static final DoubleProperty fontScaleProperty = new SimpleDoubleProperty(1);

    private static final SimpleObjectProperty<Color> baseColorProperty = new SimpleObjectProperty<>();

    private static final StringExpression styleProperty;

    private static final String DEFAULT_CASPIAN_BASE_COLOR = "#d0d0d0";

    private static final String DEFAULT_MODENA_BASE_COLOR = "#ececec";

    static {
        preferences = Preferences.userNodeForPackage(ThemeManager.class);

        final StringProperty _baseColorProperty = new SimpleStringProperty();

        // restore the old value
        fontScaleProperty.set(preferences.getDouble(FONT_SIZE, 1));

        // Save the value when it changes
        fontScaleProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                preferences.putDouble(FONT_SIZE, newValue.doubleValue());
            }
        });

        baseColorProperty.setValue(Color.web(preferences.get(BASE_COLOR, DEFAULT_MODENA_BASE_COLOR)));

        // restore the old base color value
        switch (preferences.get(LAST, Application.STYLESHEET_MODENA)) {
            case Application.STYLESHEET_CASPIAN:
                baseColorProperty.setValue(Color.web(preferences.get(BASE_COLOR, DEFAULT_CASPIAN_BASE_COLOR)));
                break;
            case Application.STYLESHEET_MODENA:
                baseColorProperty.setValue(Color.web(preferences.get(BASE_COLOR, DEFAULT_MODENA_BASE_COLOR)));
                break;
            default:
                baseColorProperty.setValue(Color.web(preferences.get(BASE_COLOR, DEFAULT_MODENA_BASE_COLOR)));
        }

        _baseColorProperty.setValue(colorToHex(baseColorProperty.getValue()));

        // Save the value when it changes
        baseColorProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                preferences.put(BASE_COLOR, colorToHex(newValue));
                _baseColorProperty.setValue(colorToHex(newValue));
            }
        });

        // Create the binding format for the style / font size
        styleProperty = Bindings.format("-fx-font-size: %1$.6fem; -fx-base:%2$s",
                fontScaleProperty, _baseColorProperty);
    }

    private ThemeManager() {
        // Utility class
    }

    public static Color getDefaultBaseColor(final String theme) {
        switch (theme) {
            case Application.STYLESHEET_CASPIAN:
                return Color.web(DEFAULT_CASPIAN_BASE_COLOR);
            case Application.STYLESHEET_MODENA:
                return  Color.web(DEFAULT_MODENA_BASE_COLOR);
            default:
                return Color.web(DEFAULT_MODENA_BASE_COLOR);
        }
    }

    public static void addKnownThemes(@NotNull final Menu menu) {
        Objects.requireNonNull(menu);

        themesMenu = menu;

        for (final String[] theme : KNOWN_THEMES) {
            final RadioMenuItem radioMenuItem = new RadioMenuItem(theme[0]);
            radioMenuItem.setUserData(theme[1]);
            radioMenuItem.setOnAction(themeHandler);
            radioMenuItem.setToggleGroup(toggleGroup);

            themesMenu.getItems().add(radioMenuItem);
        }

        Platform.runLater(ThemeManager::syncToggle);
    }

    private static void syncToggle() {
        final String last = preferences.get(LAST, Application.STYLESHEET_MODENA);

        for (final MenuItem menuItem : themesMenu.getItems()) {
            if (menuItem.getUserData() != null && menuItem.getUserData().equals(last)) {
                ((RadioMenuItem) menuItem).setSelected(true);
                break;
            }
        }
    }

    public static void restoreLastUsedTheme() {
        Application.setUserAgentStylesheet(preferences.get(LAST, Application.STYLESHEET_MODENA));
    }

    public static ObservableValue<String> getStyleProperty() {
        return styleProperty;
    }

    public static DoubleProperty getFontScaleProperty() {
        return fontScaleProperty;
    }

    public static SimpleObjectProperty<Color> getBaseColorProperty() {
        return baseColorProperty;
    }

    public static String getCurrentTheme() {
        return preferences.get(LAST, Application.STYLESHEET_MODENA);
    }

    private static class ThemeHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(final ActionEvent event) {
            final MenuItem menuItem = (MenuItem) event.getSource();

            Application.setUserAgentStylesheet(menuItem.getUserData().toString());

            preferences.put(LAST, menuItem.getUserData().toString());

            final String current = preferences.get(LAST, null);

            if (current != null && !current.equals(menuItem.getUserData().toString())) {
                Application.setUserAgentStylesheet(menuItem.getUserData().toString());
            }
        }
    }

    private static String colorToHex(final Color value) {
        return "#" + Integer.toHexString(value.hashCode());
    }
}