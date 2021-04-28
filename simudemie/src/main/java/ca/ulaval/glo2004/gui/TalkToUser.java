/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domain.drawing.CarteDrawer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import javax.swing.JPanel;
import java.awt.*;

public class TalkToUser extends JPanel implements Serializable {

  private SimudemieGUI mainWindow;

  public TalkToUser() {
  }

  public TalkToUser(SimudemieGUI mainWindow) {
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
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    
     g2.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    // You can also enable antialiasing for text:

        g2.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    String msg1 = mainWindow.controller.getMsgToUser().get(0);
    String msg2 = mainWindow.controller.getMsgToUser().get(1);
    String msg3 = mainWindow.controller.getMsgToUser().get(2);
    String msg4 = mainWindow.controller.getMsgToUser().get(3);
    String msg5 = mainWindow.controller.getMsgToUser().get(4);

//    if (!mainWindow.controller.getModeZoom()) // SI nous ne sommes pas en mode ZOOM
//    {
      // TALK TO USER - Affichage des messages à l'utilisateur.
      g2.setColor(new Color(20, 20, 20));
      g2.fillRoundRect(0, 0, 650, 120, 10, 10);
      g.setColor(new Color(50, 220, 50));
      g.drawRoundRect(0, 0, 649, 119, 10, 10);
      g.setColor(new Color(20, 100, 20));
      g.drawString(msg1, 5, 20);
      g.setColor(new Color(40, 120, 40));
      g.drawString(msg2, 5, 40);
      g.setColor(new Color(60, 150, 60));
      g.drawString(msg3, 5, 60);
      g.setColor(new Color(70, 200, 70));
      g.drawString(msg4, 5, 80);
      g.setColor(new Color( 255 - CarteDrawer.cptTempsCourt , CarteDrawer.cptTempsCourt , 80));
      g.drawString(msg5, 5, 100);
      g.drawString("MESSAGES", 5, 0);
//    } 
//    else // Message spécial pour le mode ZOOM
//    {
//      g2.setColor(new Color(20, 20, 20));
//      g2.setFont(new Font("TimesRoman", Font.PLAIN, 50));
//      g2.fillRoundRect(0, 0, 650, 70, 10, 10);
//      g.setColor(new Color(50, 220, 50));
//      g.drawRoundRect(0, 0, 649, 69, 10, 10);
//      g.setColor(new Color(90, 90, 220));
//      int newScaleParse = (int) (mainWindow.controller.getCarteDuMonde().getScale() * 100);
//      float newScaleFloat = (float) newScaleParse / 100;
//      g.drawString(" +/- MODE ZOOM : " + newScaleFloat, 10, 59);
//      g2.setFont(new Font("TimesRoman", Font.BOLD, 14));
//      g.drawString("Cliquez dans la carte pour revenir en mode EDITION", 100, 15);
//
//    }
  }

}
