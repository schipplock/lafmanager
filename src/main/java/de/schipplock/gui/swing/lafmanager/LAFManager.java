/*
 * Copyright 2022 Andreas Schipplock
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.schipplock.gui.swing.lafmanager;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import de.schipplock.gui.swing.lafmanager.exceptions.MetalThemeException;
import de.schipplock.gui.swing.sthemes.MedicTheme;
import de.schipplock.gui.swing.sthemes.BlindedTheme;
import de.schipplock.gui.swing.sthemes.OceanTheme;
import de.schipplock.gui.swing.sthemes.SteelTheme;

public class LAFManager {

    public static final String DEFAULT_LOOK_AND_FEEL = SteelTheme.NAME;

    public static final String NIMBUS = "Nimbus";

    public static final String MOTIF = "Motif";

    public static final String WINDOWS = "Windows";

    public static final String WINDOWS_CLASSIC = "Windows Classic";

    public static final String METAL_CLASS = "javax.swing.plaf.metal.MetalLookAndFeel";

    private record LookAndFeelEntry(String name, String className) {}

    private final Map<String, LookAndFeelEntry> lafs = Map.ofEntries(
            Map.entry(OceanTheme.NAME, new LookAndFeelEntry(OceanTheme.NAME, OceanTheme.class.getName())),
            Map.entry(SteelTheme.NAME, new LookAndFeelEntry(SteelTheme.NAME, SteelTheme.class.getName())),
            Map.entry(MedicTheme.NAME, new LookAndFeelEntry(MedicTheme.NAME, MedicTheme.class.getName())),
            Map.entry(BlindedTheme.NAME, new LookAndFeelEntry(BlindedTheme.NAME, BlindedTheme.class.getName())),
            Map.entry(NIMBUS, new LookAndFeelEntry(NIMBUS, "javax.swing.plaf.nimbus.NimbusLookAndFeel")),
            Map.entry(MOTIF, new LookAndFeelEntry(MOTIF, "com.sun.java.swing.plaf.motif.MotifLookAndFeel")),
            Map.entry(WINDOWS, new LookAndFeelEntry(WINDOWS, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel")),
            Map.entry(WINDOWS_CLASSIC, new LookAndFeelEntry(WINDOWS_CLASSIC, "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel")),
            Map.entry(FlatLightLaf.NAME, new LookAndFeelEntry(FlatLightLaf.NAME, FlatLightLaf.class.getName())),
            Map.entry(FlatDarkLaf.NAME, new LookAndFeelEntry(FlatDarkLaf.NAME, FlatDarkLaf.class.getName())),
            Map.entry(FlatMonokaiProIJTheme.NAME, new LookAndFeelEntry(FlatMonokaiProIJTheme.NAME, FlatMonokaiProIJTheme.class.getName())),
            Map.entry(FlatDarculaLaf.NAME, new LookAndFeelEntry(FlatDarculaLaf.NAME, FlatDarculaLaf.class.getName())),
            Map.entry(FlatIntelliJLaf.NAME, new LookAndFeelEntry(FlatIntelliJLaf.NAME, FlatIntelliJLaf.class.getName())),
            Map.entry(FlatArcIJTheme.NAME, new LookAndFeelEntry(FlatArcIJTheme.NAME, FlatArcIJTheme.class.getName())),
            Map.entry(FlatArcOrangeIJTheme.NAME, new LookAndFeelEntry(FlatArcOrangeIJTheme.NAME, FlatArcOrangeIJTheme.class.getName())),
            Map.entry(FlatArcDarkIJTheme.NAME, new LookAndFeelEntry(FlatArcDarkIJTheme.NAME, FlatArcDarkIJTheme.class.getName())),
            Map.entry(FlatGrayIJTheme.NAME, new LookAndFeelEntry(FlatGrayIJTheme.NAME, FlatGrayIJTheme.class.getName())),
            Map.entry(FlatGruvboxDarkSoftIJTheme.NAME, new LookAndFeelEntry(FlatGruvboxDarkSoftIJTheme.NAME, FlatGruvboxDarkSoftIJTheme.class.getName())),
            Map.entry(FlatXcodeDarkIJTheme.NAME, new LookAndFeelEntry(FlatXcodeDarkIJTheme.NAME, FlatXcodeDarkIJTheme.class.getName()))
    );

    private final Map<String, String> metalThemes = Map.ofEntries(
            Map.entry(SteelTheme.NAME, SteelTheme.class.getName()),
            Map.entry(OceanTheme.NAME, OceanTheme.class.getName()),
            Map.entry(MedicTheme.NAME, MedicTheme.class.getName()),
            Map.entry(BlindedTheme.NAME, BlindedTheme.class.getName())
    );
    
    private LAFManager() {
        lafs.values().forEach(entry -> installLookAndFeel(entry.name(), entry.className()));
    }

    public static LAFManager create() {
        return new LAFManager();
    }

    /**
     * This method returns all Look &amp; Feels that were installed by LAFManager.
     * To get a list of all Look &amp; Feels use UIManager.getInstalledLookAndFeels().
     *
     * @return string array of look and feel names
     */
    public String[] getInstalledLookAndFeelNames() {
        List<String> sortedThemes = new ArrayList<>(lafs.keySet().stream().toList());
        Collections.sort(sortedThemes);
        return sortedThemes.toArray(new String[0]);
    }

    public LAFManager decorateWindowBorders() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        return this;
    }

    public void redraw() {
        FlatLaf.updateUI();
        FlatLaf.updateUILater();
    }

    public LAFManager setLookAndFeelByName(String name) {
        if (metalThemes.containsKey(name)) {
            setMetalTheme(name);
            setLookAndFeelByClassName(METAL_CLASS);
            return this;
        }
        setLookAndFeelByClassName(lafs.get(name).className());
        return this;
    }

    private void setLookAndFeelByClassName(String className) {
        try {
            UIManager.setLookAndFeel(className);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void setMetalTheme(String name) {
        String className = metalThemes.get(name);
        try {
            Class<?> clazz = Class.forName(className);
            MetalLookAndFeel.setCurrentTheme((MetalTheme) clazz.getDeclaredConstructor().newInstance());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new MetalThemeException(e);
        }
    }

    private void installLookAndFeel(String name, String className) {
        UIManager.installLookAndFeel(new UIManager.LookAndFeelInfo(name, className));
    }
}
