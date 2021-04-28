/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domain.SimudemieController;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import javax.swing.JPanel;

import java.awt.*;

public class DrawingStats extends JPanel implements Serializable {

  private SimudemieGUI mainWindow;

  public DrawingStats() {
  }

  public DrawingStats(SimudemieGUI mainWindow) {
    this.mainWindow = mainWindow;
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
    
    g2.setColor(new Color(20, 20, 20));
    g2.fillRoundRect((int) (0), (int) (0), (int) (930), (int) (110), 10, 10);
    g2.setColor(new Color(90, 90, 220));
    g2.drawRoundRect((int) (0), (int) (0), (int) (929), (int) (109), 10, 10);
    g2.setFont(new Font("TimesRoman", Font.PLAIN, 22));

    // Responsiveness pour gros chiffre de populasse
    if (mainWindow.controller.getCarteDuMonde().getPopulationTotale() > 99000) {
      g2.setFont(new Font("TimesRoman", Font.PLAIN, 15));

    }

    g2.drawString("Pop Totale : " + String.valueOf(mainWindow.controller.getCarteDuMonde().getPopulationTotale()),
        200, 30);
    g2.drawString("Pop Initial : " + String.valueOf(mainWindow.controller.getCarteDuMonde().getPopInitTotal()),
        200, 55);
    g2.drawString("NB Guéris : " + String.valueOf(mainWindow.controller.getCarteDuMonde().getNbGuerieSimulation()), 200,
        80);
    
    g2.drawString("NB Infects : " + String.valueOf(mainWindow.controller.getCarteDuMonde().getNbInfecteSimulation()),
        450, 30);

    g2.drawString("NB Décès  : " + String.valueOf(mainWindow.controller.getCarteDuMonde().getNbDecedeSimulation()), 450,
        65);
    
    
    g2.setColor(new Color(120, 120, 0));
    g2.setFont(new Font("TimesRoman", Font.PLAIN, 50));
    g2.drawString("jour " + mainWindow.compteurJour, 15, 50);

    
    
    g2.setFont(new Font("TimesRoman", Font.PLAIN, 12));
    g2.drawString("Frontiere terrestre", 320, 100);
    g2.setColor(Color.WHITE);
    g2.setStroke(new BasicStroke(7f));
    g2.drawLine(280, 95, 310, 95);

    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(6f));
    g2.drawLine(280, 95, 310, 95);
    float[] dashingPattern1b = {5f, 2f};
    Stroke stroke1 = new BasicStroke(1f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 2.0f, dashingPattern1b, 4.0f);
    g2.setStroke(stroke1);
    g2.setColor(Color.YELLOW);
    g2.drawLine(280, 95, 310, 95);
    
    
    g2.setColor(new Color(120, 120, 0));
    g2.drawString("Frontiere Maritime", 520, 100);
     float[] dashingPattern2 = {Math.abs(10f), 7f, 5f, 4f};
                Stroke stroke2 = new BasicStroke(4f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 1.0f, dashingPattern2, 0.0f);
    g2.setColor(new Color(200, 200, 250));
    g2.setStroke(new BasicStroke(6f));
    g2.drawLine(480, 95, 510, 95);
    g2.setColor(new Color(100, 100, 200));
    g2.setStroke(stroke2);
    g2.drawLine(480, 95, 510, 95);
    
    
    g2.setColor(new Color(120, 120, 0));
    g2.drawString("Frontiere Aerienne", 120, 100);
    g2.setColor(new Color(220, 100, 20));
    float[] dashingPattern3 = {Math.abs(10f), 5f, 1f, 5f};
    Stroke stroke3 = new BasicStroke(2f, BasicStroke.CAP_SQUARE,
            BasicStroke.JOIN_MITER, 1.0f, dashingPattern3, 0.0f);
    g2.setStroke(stroke3);
    g2.drawLine(80, 95, 110, 95);
    
    
    
    //STATS EN BAR
    //Pop Infect-------------------------------------
                   
        g2.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        g2.setColor(new Color(250, 200, 50));

        g2.drawString("infec : ", 650, 30);
    //
        g2.setColor(new Color(150, 100, 0));
    //                    //ligne stat de fond

        g2.setStroke(new BasicStroke(10));
        g2.drawLine(705,25, 905, 25);

        g2.setColor(new Color(250, 200, 50));
        float pourInfect = 0;
        if (mainWindow.controller.getCarteDuMonde().getPopulationTotale() != 0)
        {
            pourInfect = ((float)mainWindow.controller.getCarteDuMonde().getNbInfecteSimulation() / (float)mainWindow.controller.getCarteDuMonde().getPopulationTotale());
        }
        g2.drawLine(705, 25, 705 + (int)(200 * pourInfect),25);

        g2.setColor(new Color(50, 25, 0));
        g2.drawString(String.valueOf(mainWindow.controller.getCarteDuMonde().getNbInfecteSimulation()),705,
                30);
        
        
        //Pop saine-------------------------------------
                   
        g2.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        g2.setColor(new Color(50, 250, 50));

        g2.drawString("Sains : ", 650, 45);
    //
        g2.setColor(new Color(0, 150, 0));
    //                    //ligne stat de fond

        g2.setStroke(new BasicStroke(10));
        g2.drawLine(705,40, 905, 40);

        g2.setColor(new Color(50, 250, 50));
        float pourSain = 0;
        if (mainWindow.controller.getCarteDuMonde().getPopulationTotale() != 0)
        {
            pourSain = ((float)mainWindow.controller.getCarteDuMonde().getNbSainsSimulation()/ (float)mainWindow.controller.getCarteDuMonde().getPopulationTotale());
        }
        g2.drawLine(705, 40, 705 + (int)(200 * pourSain),40);

        g2.setColor(new Color(0, 40, 0));
        g2.drawString(String.valueOf(mainWindow.controller.getCarteDuMonde().getNbSainsSimulation()),705,
                45);    
        
        //Pop Guéri-------------------------------------
                   
        g2.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        g2.setColor(new Color(50, 250, 250));

        g2.drawString("Guéri : ", 650, 60);
    //
        g2.setColor(new Color(0, 150, 150));
    //                    //ligne stat de fond

        g2.setStroke(new BasicStroke(10));
        g2.drawLine(705,55, 905, 55);

        g2.setColor(new Color(50, 250, 250));
        float pourGueri = 0;
        if (mainWindow.controller.getCarteDuMonde().getPopulationTotale() != 0)
        {
            pourGueri = (float)mainWindow.controller.getCarteDuMonde().getNbGuerieSimulation() / ((float)mainWindow.controller.getCarteDuMonde().getNbGuerieSimulation() + (float)mainWindow.controller.getCarteDuMonde().getNbDecedeSimulation());
        }
        g2.drawLine(705, 55, 705 + (int)(200 * pourGueri),55);

        g2.setColor(new Color(0, 70, 70));
        g2.drawString(String.valueOf(mainWindow.controller.getCarteDuMonde().getNbGuerieSimulation()),705,
                60);
        
        //Pop Décédé-------------------------------------
                   
        g2.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        g2.setColor(new Color(250, 50, 50));

        g2.drawString("Décès : ", 650, 75);
    //
        g2.setColor(new Color(150, 0, 0));
    //                    //ligne stat de fond

        g2.setStroke(new BasicStroke(10));
        g2.drawLine(705,70, 905, 70);

        g2.setColor(new Color(250, 50, 50));
        float pourDeces = 0;
        if (mainWindow.controller.getCarteDuMonde().getPopulationTotale() != 0)
        {
            pourDeces = (float)mainWindow.controller.getCarteDuMonde().getNbDecedeSimulation() / ((float)mainWindow.controller.getCarteDuMonde().getNbGuerieSimulation() + (float)mainWindow.controller.getCarteDuMonde().getNbDecedeSimulation());
        }
        g2.drawLine(705, 70, 705 + (int)(200 * pourDeces),70);

        g2.setColor(new Color(50, 0, 20));
        g2.drawString(String.valueOf(mainWindow.controller.getCarteDuMonde().getNbDecedeSimulation()),705,
                75);
        
        //Pop Total-------------------------------------
                   
        g2.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        g2.setColor(new Color(130, 50, 250));

        g2.drawString("PopTo : ", 650, 90);
    //
    //                    //ligne stat de fond

        g2.setStroke(new BasicStroke(10));
        g2.drawLine(705,85, 905, 85);

        g2.setColor(new Color(0, 78, 150));
        float pourPopTotal = 0;
        if (mainWindow.controller.getCarteDuMonde().getPopulationTotale() != 0 && mainWindow.controller.getCarteDuMonde().getPopInitTotal() != 0)
        {
          pourPopTotal = (float)mainWindow.controller.getCarteDuMonde().getPopulationTotale() / (float)mainWindow.controller.getCarteDuMonde().getPopInitTotal();
        }
        g2.drawLine(705, 85, 705 + (int)(200 * pourPopTotal),85);

        g2.setColor(new Color(50, 0, 20));
        g2.drawString(String.valueOf(mainWindow.controller.getCarteDuMonde().getPopulationTotale()+ " / " + String.valueOf(mainWindow.controller.getCarteDuMonde().getPopInitTotal())),705,
                90);


    
    
    
  }

}
