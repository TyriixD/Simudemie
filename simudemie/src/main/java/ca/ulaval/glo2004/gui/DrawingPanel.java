/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domain.SimudemieController;
import ca.ulaval.glo2004.domain.drawing.CarteDrawer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import javax.swing.JPanel;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class DrawingPanel extends JPanel implements Serializable {

    private SimudemieGUI mainWindow;

    public DrawingPanel() {
    }

    public DrawingPanel(SimudemieGUI mainWindow) {
        this.mainWindow = mainWindow;
        // setBorder(new javax.swing.border.BevelBorder(BevelBorder.LOWERED));
        // int width = (int)
        // (java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
        // setPreferredSize(new Dimension(width,1));
        // setVisible(true);
        // int height = (int)(width*0.5);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (mainWindow != null) {
            super.paintComponent(g);
            CarteDrawer mainDrawer = new CarteDrawer(mainWindow.controller,
                    this.mainWindow.controller.getCarteDuMonde().getPanelSize());
            mainDrawer.draw(g);
        }
    }

    public SimudemieGUI getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(SimudemieGUI mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void setInitialDimension() {

    }

    public void saveImage(String name, String type) {
        SimudemieController.TalkToUser("Capture de la carte est appelle");
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        paint(g2);

        try {
            ImageIO.write(image, type, new File(name + this.chooseFileSave() + "." + type));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String chooseFileSave() {
        JFileChooser fileChooser = new JFileChooser(new File("."));
        File fileToSave;
        fileChooser.setDialogTitle("Specify a file to save");
        String patch = "";

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            fileToSave = fileChooser.getSelectedFile();
            patch = fileToSave.getAbsolutePath();
        }

        return patch;
    }

}
