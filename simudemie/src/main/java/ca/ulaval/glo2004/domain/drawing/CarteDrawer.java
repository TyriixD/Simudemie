/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.drawing;

import ca.ulaval.glo2004.domain.SimudemieController;
import ca.ulaval.glo2004.domain.entites.Pays;
import ca.ulaval.glo2004.domain.entites.Region;
import ca.ulaval.glo2004.domain.entites.VoiesDeLiaisons;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.QuadCurve2D;

public class CarteDrawer {

    private final SimudemieController controller;
    private Dimension initialDimension;
    ActionListener timerlistener;
    public static int cptTemps = -1000;
    public static int cptTempsCourt = 0;
    

    public CarteDrawer(SimudemieController controller, Dimension initialDimension) {
        this.controller = controller;
        this.initialDimension = initialDimension;
        if (CarteDrawer.cptTemps > 1000)
        {
            CarteDrawer.cptTemps  = -1000;
        }
        
        if (CarteDrawer.cptTempsCourt > 200)
        {
            CarteDrawer.cptTempsCourt  = 0;
        }
    }

    public void draw(Graphics g) {
        drawFormeReg(g);
        drawVoiesDeLiaison(g);
    }

    private void drawFormeReg(Graphics g) {//Dras ALL form changement de structure en cours de route.
        
        List<Pays> pays = controller.getCarteDuMonde().getListePays();
        float scale = controller.getCarteDuMonde().getScale();

        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int posXCorect = this.controller.getCarteDuMonde().getTranslate().width;
        int posYCorect = this.controller.getCarteDuMonde().getTranslate().height;
        g2.translate(posXCorect, posYCorect);

        g2.translate(controller.getCarteDuMonde().getPanelSize().width / 2,
                controller.getCarteDuMonde().getPanelSize().height / 2); // Pas sure si je dois le
        g2.scale(scale, scale);

        g2.translate(-controller.getCarteDuMonde().getPanelSize().width / 2,
                -controller.getCarteDuMonde().getPanelSize().height / 2);

            //--------------CODE FORME IRR
        //Affichage des ancrage de la nouvelle forme IRR
        g2.setStroke(new BasicStroke(2));
        g2.setFont(new Font("TimesRoman", Font.PLAIN, 9));
        //REmpliassge des point pour forme irr
        int cptPoint = 1;
        for (Pays i : pays) {
            Point position = i.getPosition();
                g.setColor(new Color(50, 100, 50));
                Polygon PolyIrrTmp = new Polygon();

                for(Point points : i.getForme().getListeSommet())
                   {
                      PolyIrrTmp.addPoint(points.x, points.y);
                   }

                g2.setStroke(new BasicStroke(4));
                g2.drawPolygon(PolyIrrTmp);
                g2.setColor(i.getForme().getCouleur());
                g2.fillPolygon(i.getForme().getPolygonForme());
                g2.setColor(new Color(200, 50, 255));
                g2.drawString(i.getNom(), i.getForme().getListeSommet().get(0).x,
                    i.getForme().getListeSommet().get(0).y);
                
                for (Region regions:  i.getListeRegion())
                {
                    Polygon PolyIrrTmpReg = new Polygon();

                    for(Point points : regions.getForme().getListeSommet())
                       {
                          PolyIrrTmpReg.addPoint(points.x, points.y);
                       }

                    g2.setStroke(new BasicStroke(4));
                    g2.setColor(new Color(100, 100, 250));
                    g2.drawPolygon(PolyIrrTmpReg);
                    g2.setColor(regions.getForme().getCouleur());
                    g2.fillPolygon(regions.getForme().getPolygonForme());
                    g2.setColor(new Color(50, 20, 250));
                    g2.drawString(regions.getNom(), regions.getForme().getListeSommet().get(0).x,
                        regions.getForme().getListeSommet().get(0).y);
                    
                    
                
                }
                //-------------------CODE FORME IRR-FIN
                
            // Si le pays est en cour de mouvement.
            if (i.getForme().getMoveEnCours()) {
                // DEssion avec les coordo de Forme qui apparait lors du déplacement et polygone
                g.setColor(new Color(50, 100, 50));
                ArrayList<Point> pointsTmp = controller.getListePointsBoundPoints();
                Polygon PolyMoveEnCours = new Polygon();
                
                for (Point points : pointsTmp) {
                    PolyMoveEnCours.addPoint(points.x, points.y);
                }
                g.drawPolygon(PolyMoveEnCours);
                g.setColor(new Color(20, 20, 20));
                g.fillPolygon(PolyMoveEnCours);
                g.setColor(new Color(200, 200, 100));
                g.drawString(i.getNom(), i.getForme().getListeSommet().get(0).x,
                        i.getForme().getListeSommet().get(0).y);
                g.setColor(new Color(50, 50, 180));
                g.drawString("Déplacement", i.getForme().getListeSommet().get(0).x + 15,
                        i.getForme().getListeSommet().get(0).y + 25);
                g.drawString("en cours...", i.getForme().getListeSommet().get(0).x + 15,
                        i.getForme().getListeSommet().get(0).y + 45);
                g.drawString("Déplacement", pointsTmp.get(0).x + 15,
                        pointsTmp.get(0).y + 25);
                g.drawString("en cours...", pointsTmp.get(0).x + 15,
                        pointsTmp.get(0).y + 45);
            }
            
        }
        
        for(Point points : controller.getListePointsIrr())
            {
               g.setColor(new Color(50, 100, 50));
               g2.drawOval(points.x, points.y, 15, 15);
               g.setColor(new Color(220, 220, 220));
               g2.drawString(String.valueOf(cptPoint), points.x + 5, points.y + 10);
               cptPoint ++;
            }

        // Traitement de la redimension
        for (Pays i : pays) {
            
            
            if (!i.getForme().getFormeIrr())
            {
                g.setColor(new Color(200, 200, 50));
                g.drawRoundRect(i.getPosition().x + i.getForme().getWidthSelectBox() - 20,
                        i.getPosition().y + i.getForme().getHeightSelectBox() - 20, 20, 20, 10, 10);
                g.drawLine(i.getPosition().x + i.getForme().getWidthSelectBox() - 15,
                        i.getPosition().y + i.getForme().getHeightSelectBox() - 15,
                        i.getPosition().x + i.getForme().getWidthSelectBox() - 5,
                        i.getPosition().y + i.getForme().getHeightSelectBox() - 5);

                List<Region> region = i.getListeRegion();
                for (Region y : region) {
                    if (!y.getForme().getFormeIrr())
                    {
                        g.setColor(new Color(200, 200, 50));
                        g.drawRoundRect(y.getPositionRegion().x + y.getForme().getWidthSelectBox() - 20,
                                y.getPositionRegion().y + y.getForme().getHeightSelectBox() - 20, 20, 20, 10, 10);
                        g.drawLine(y.getPositionRegion().x + y.getForme().getWidthSelectBox() - 15,
                                y.getPositionRegion().y + y.getForme().getHeightSelectBox() - 15,
                                y.getPositionRegion().x + y.getForme().getWidthSelectBox() - 5,
                                y.getPositionRegion().y + y.getForme().getHeightSelectBox() - 5);
                    }
                }

            }
            else //SI le pays est régulier
            { 
                List<Region> region = i.getListeRegion();
                for (Region y : region) {
                    if (!y.getForme().getFormeIrr())
                    {
                        g.setColor(new Color(200, 200, 50));
                        g.drawRoundRect(y.getPositionRegion().x + y.getForme().getWidthSelectBox() - 20,
                                y.getPositionRegion().y + y.getForme().getHeightSelectBox() - 20, 20, 20, 10, 10);
                        g.drawLine(y.getPositionRegion().x + y.getForme().getWidthSelectBox() - 15,
                                y.getPositionRegion().y + y.getForme().getHeightSelectBox() - 15,
                                y.getPositionRegion().x + y.getForme().getWidthSelectBox() - 5,
                                y.getPositionRegion().y + y.getForme().getHeightSelectBox() - 5);
                    }
                }
                
            }
        }

        // Taitement du HOVER pays et Région si le mode zoom est off
        if (!controller.getModeZoom() && !controller.getShowAllStats()) {
            for (Pays i : pays) {
                
                drawStats(g2, pays);
                List<Region> region = i.getListeRegion();
                for (Region y : region) {

                    // SI la souris est au dessus de la région.
                    if (y.getForme().getMouseOver()) {
                        
                        g2.setStroke(new BasicStroke(2));
                        
                        g.setColor(new Color(20, 20, 20));
                        g.fillRoundRect(controller.getMousePosition().x - 280, controller.getMousePosition().y - 65,
                                750, 30, 10, 10);
                        g.setColor(new Color(150, 150, 50));
                        g.drawRoundRect(controller.getMousePosition().x - 280, controller.getMousePosition().y - 65,
                                750 , 30, 10, 10);

                        g2.setFont(new Font("TimesRoman", Font.PLAIN, 12));
                        if (y.getTaillePop() > 100000) {
                            g2.setFont(new Font("TimesRoman", Font.PLAIN, 9));
                        }
                        g.drawString(y.getStatRegionFormate(), controller.getMousePosition().x - 260,
                                controller.getMousePosition().y + 20 - 65);
                        g.drawString(Double.toString(y.getPourcentPop()), controller.getMousePosition().x,
                                controller.getMousePosition().y);
                        g.setColor(new Color(50, 50, 180));
                        g.setColor(new Color(200, 200, 25));
                        g2.setStroke(new BasicStroke(2));
                        if (!y.getForme().getFormeIrr())
                        {
                            g.drawRoundRect(y.getPositionRegion().x - 2, y.getPositionRegion().y - 2,
                            y.getForme().getWidthSelectBox() + 4, y.getForme().getHeightSelectBox() + 4, 10, 10);
                        }
                        g2.setStroke(new BasicStroke(1));
                    }
                }
                // SI la souris est au dessus du pays.
                
                
                if (i.getForme().getMouseOver()) {
                    
                    for (int z = 0; z < controller.getListePointsBoundPoints().size(); z++ )
                    {
                        
                        
                        g2.setStroke(new BasicStroke(1));
                        g.setColor(new Color(180,255, 200)); 
                        g2.setFont(new Font("TimesRoman", Font.PLAIN, 8));
                        
                        if(i.getForme().getFormeIrr()){
                            if (controller.getIndexVertexToBeMove() == z)
                           {
                               g2.setStroke(new BasicStroke(3));
                               g.setColor(new Color(255, 255, 20)); 
                               g2.drawOval(controller.getListePointsBoundPoints().get(z).x - 15, controller.getListePointsBoundPoints().get(z).y - 15, 30, 30);
                               g2.drawOval(controller.getListePointsBoundPoints().get(z).x - 7, controller.getListePointsBoundPoints().get(z).y - 7, 14, 14);
                           }
                           else
                           {
                               g.setColor(new Color(235, 235, 20)); 
                               g2.drawOval(controller.getListePointsBoundPoints().get(z).x - 5, controller.getListePointsBoundPoints().get(z).y - 5, 10, 10);
                           }
                           g.setColor(new Color(180,255, 200)); 
//                        g2.drawString(String.valueOf(z), controller.getListePointsBoundPoints().get(z).x - 2, controller.getListePointsBoundPoints().get(z).y + 3);
                        }



                    }
                    g.setColor(new Color(180,255, 200)); 
                    g.drawPolygon(i.getForme().getPolygonForme());
                    g2.setStroke(new BasicStroke(1));
                    
                    //Pour les régions irrégulière 
                    for (Region regions : i.getListeRegion())
                    {
                        if (regions.getForme().getMouseOver()) {
                            
                            g2.setStroke(new BasicStroke(2));
                            g2.setColor(new Color(255, 100, 50));
                            g2.drawPolygon(regions.getForme().getPolygonForme());
                    
                            for (int x = 0; x < controller.getListePointsBoundPointsReg().size(); x++ )
                            {

                                g2.setStroke(new BasicStroke(1));
                                g.setColor(new Color(100, 100, 50));
                                g2.setFont(new Font("TimesRoman", Font.PLAIN, 8));

                                if(regions.getForme().getFormeIrr())
                                {
                                   if(controller.getIndexVertexToBeMoveReg()== x)
                                   {
                                       g2.setStroke(new BasicStroke(3));
                                       g.setColor(new Color(255, 255, 75)); 
                                       g2.drawOval(controller.getListePointsBoundPointsReg().get(x).x - 15, controller.getListePointsBoundPointsReg().get(x).y - 15, 30, 30);
                                       g2.drawOval(controller.getListePointsBoundPointsReg().get(x).x - 7, controller.getListePointsBoundPointsReg().get(x).y - 7, 14, 14);
                                   }
                                   else
                                   {
                                       g.setColor(new Color(235, 235, 75)); 
                                       g2.drawOval(controller.getListePointsBoundPointsReg().get(x).x - 5, controller.getListePointsBoundPointsReg().get(x).y - 5, 10, 10);
                                   }
                                   g.setColor(new Color(180,255, 200)); 
        //                        g2.drawString(String.valueOf(z), controller.getListePointsBoundPoints().get(z).x - 2, controller.getListePointsBoundPoints().get(z).y + 3);
                                }
                            }
                        }
                    }
                }
            }
        } else // SI modeZoom, on affiche tous les stats en format responsiveé ou mode StatsAll
               // activé
        {
            drawStats(g2, pays);
        }

    }

    private void drawStats(Graphics2D g2, List<Pays> pays) {
        
            
                int posDepartBar;
                int posFinBar;
                int largeurBar;
            for (Pays i : pays) {
                
                
                g2.setStroke(new BasicStroke(1));
                List<Region> region = i.getListeRegion();
                for (Region y : region) {

                    float ratioPaysReg = 1.0f;
                    float ratioFontReg = 1.0f;
                    float ratioSpaceXReg = 1.0f;
                    float ratioSpaceYReg = 1.0f;
                    if (y.getForme().getHeightSelectBox() > 100) {
                        ratioPaysReg = 0.5f;
                        ratioFontReg = 2;
                        ratioSpaceXReg = 2f;
                        ratioSpaceYReg = 2f;
                    }
                    g2.setFont(new Font("TimesRoman", Font.PLAIN, (int) (6 * ratioFontReg)));
                    g2.setColor(new Color(250, 200, 50));
                    if (y.getTaillePop() > 100000) {
                        g2.setFont(new Font("TimesRoman", Font.PLAIN, (int) (5 * ratioFontReg)));
                    }

                }
                
                g2.setStroke(new BasicStroke(1));

                float ratioPays;
                float ratioFont;
                float ratioSpaceX;
                float ratioSpaceY;
                float hauteurBoxStats;
                float largeurBarStats;
                //RESPONSIVENESS des BAR STATS
                float ratioHautLarg = i.getForme().getWidthSelectBox();
                if (ratioHautLarg <= 50) {
                        ratioPays = 1.2f;
                        ratioFont = 1;
                        ratioSpaceX = 1f;
                        ratioSpaceY = 1f;
                        hauteurBoxStats = 60;
                        largeurBarStats = 0.1f;
                } else {
                    if (ratioHautLarg > 50 && ratioHautLarg <= 120) {
                        ratioPays = 0.8f;
                        ratioFont = 2;
                        ratioSpaceX = 1f;
                        ratioSpaceY = 1.4f;
                        hauteurBoxStats = 85;
                        largeurBarStats = 0.1f;
                    } else {
                        if(ratioHautLarg > 120 && ratioHautLarg <= 150)
                        {
                            ratioPays = 0.4f;
                            ratioFont = 2.2f;
                            ratioSpaceX = 2f;
                            ratioSpaceY = 1.4f;
                        hauteurBoxStats = 100;
                        largeurBarStats = 0.15f;
                        }
                        else
                             if(ratioHautLarg > 150 && ratioHautLarg <= 250)
                                {
                                    ratioPays = 0.4f;
                                    ratioFont = 2.2f;
                                    ratioSpaceX = 2f;
                                    ratioSpaceY = 1.4f;
                                    hauteurBoxStats = 100;
                                    largeurBarStats = 0.25f;
                                }
                        else
                             if(ratioHautLarg > 250 && ratioHautLarg <= 300)
                                {
                                    ratioPays = 0.4f;
                                    ratioFont = 2.2f;
                                    ratioSpaceX = 2f;
                                    ratioSpaceY = 1.4f;
                                    hauteurBoxStats = 100;
                                    largeurBarStats = 0.45f;
                                }
                        else
                             if(ratioHautLarg > 300 && ratioHautLarg <= 350)
                                {
                                    ratioPays = 0.4f;
                                    ratioFont = 2.2f;
                                    ratioSpaceX = 2f;
                                    ratioSpaceY = 1.4f;
                                    hauteurBoxStats = 100;
                                    largeurBarStats = 0.55f;
                                }
                        else
                             if(ratioHautLarg > 350 && ratioHautLarg <= 450)
                                {
                                    ratioPays = 0.4f;
                                    ratioFont = 2.2f;
                                    ratioSpaceX = 2f;
                                    ratioSpaceY = 1.4f;
                                    hauteurBoxStats = 100;
                                    largeurBarStats = 0.65f;
                                }
                        else
                             if(ratioHautLarg > 450 && ratioHautLarg <= 500)
                                {
                                    ratioPays = 0.4f;
                                    ratioFont = 2.2f;
                                    ratioSpaceX = 2f;
                                    ratioSpaceY = 1.4f;
                                    hauteurBoxStats = 100;
                                    largeurBarStats = 0.75f;
                                }
                        else
                             if(ratioHautLarg > 500 && ratioHautLarg <= 550)
                                {
                                    ratioPays = 0.4f;
                                    ratioFont = 2.2f;
                                    ratioSpaceX = 2f;
                                    ratioSpaceY = 1.4f;
                                    hauteurBoxStats = 100;
                                    largeurBarStats = 0.85f;
                                }
                        else
                             if(ratioHautLarg > 550 && ratioHautLarg <= 650)
                                {
                                    ratioPays = 0.4f;
                                    ratioFont = 2.2f;
                                    ratioSpaceX = 2f;
                                    ratioSpaceY = 1.4f;
                                    hauteurBoxStats = 100;
                                    largeurBarStats = 0.95f;
                                }
                        else
                             if(ratioHautLarg > 650 && ratioHautLarg <= 850)
                                {
                                    ratioPays = 0.4f;
                                    ratioFont = 2.2f;
                                    ratioSpaceX = 2f;
                                    ratioSpaceY = 1.4f;
                                    hauteurBoxStats = 100;
                                    largeurBarStats = 1.2f;
                                }
                             
                        else{
                                ratioPays = 0.4f;
                                ratioFont = 2.2f;
                                ratioSpaceX = 2f;
                                ratioSpaceY = 1.4f;
                                hauteurBoxStats = 100;
                                largeurBarStats = 1.5f;
                        }
                    }
                }
                
                 posDepartBar = i.getPosition().x + i.getForme().getWidthSelectBox() - (int)(400 * largeurBarStats);
                 posFinBar = posDepartBar + (int)(400 * largeurBarStats) - 15;
                 largeurBar = posFinBar - posDepartBar;
                 
                 if(i.getForme().getMouseOver() || controller.getModeZoom())
                 {

                    //Pop Infect-------------------------------------
                    g2.setColor(new Color(20, 20, 60));
                    g2.fillRoundRect(i.getPosition().x, i.getPosition().y + i.getForme().getHeightSelectBox() + 2,
                            i.getForme().getWidthSelectBox(), (int) (hauteurBoxStats),
                            10, 10);
                    g2.setColor(new Color(50, 150, 220));
                    g2.drawRoundRect(i.getPosition().x, i.getPosition().y + i.getForme().getHeightSelectBox() + 2,
                            i.getForme().getWidthSelectBox(), (int) (hauteurBoxStats),
                            10, 10);
                    g2.setFont(new Font("TimesRoman", Font.PLAIN, (int) (6 * ratioFont)));
                    g2.setColor(new Color(250, 200, 50));

                    g2.drawString("infec : ", i.getPosition().x + 1,
                            i.getPosition().y + i.getForme().getHeightSelectBox() + 4 + (int) (10 * ratioSpaceY));
                    if(i.getTaillePop() > 1000000)
                        g2.setFont(new Font("TimesRoman", Font.PLAIN, 6));
                    g2.setFont(new Font("TimesRoman", Font.PLAIN, (int) (6 * ratioFont)));

                    g2.setColor(new Color(150, 100, 0));
                    //ligne stat de fond

                    g2.setStroke(new BasicStroke(10));
                    g2.drawLine(posDepartBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (10 * ratioSpaceY), 
                             posFinBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (10 * ratioSpaceY));

                    g2.setColor(new Color(250, 200, 50));
                    float pourInfect = (i.getNbInfecter().floatValue() / i.getTaillePop().floatValue());

                     g2.drawLine(posDepartBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (10 * ratioSpaceY), 
                             posDepartBar + (int)(largeurBar * pourInfect), 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (10 * ratioSpaceY));

                    g2.setColor(new Color(50, 25, 0));
                    g2.drawString(String.valueOf(i.getNbInfecter()),posDepartBar,
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (10 * ratioSpaceY) + 5);


                    //Pop saine----------------------------------------

                    g2.setColor(new Color(50, 250, 50));
                    g2.drawString("sain : ", i.getPosition().x + 1,
                            i.getPosition().y + i.getForme().getHeightSelectBox() + 4 + (int) (20 * ratioSpaceY));
                    if(i.getTaillePop() > 100000)
                        g2.setFont(new Font("TimesRoman", Font.PLAIN, 8));
                    g2.setFont(new Font("TimesRoman", Font.PLAIN, (int) (6 * ratioFont)));

                    //ligne stat de fond
                     g2.setColor(new Color(0, 150, 0));
                    g2.drawLine(posDepartBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (20 * ratioSpaceY), 
                             posFinBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (20 * ratioSpaceY));
                    g2.setColor(new Color(50, 250, 50));
    //                //ligne stat de dessus
                    float pourSaine = i.getNbSaine().floatValue() / i.getTaillePop().floatValue();
                   g2.drawLine(posDepartBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (20 * ratioSpaceY), 
                             posDepartBar + (int)(largeurBar * pourSaine), 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (20 * ratioSpaceY));
                    g2.setColor(new Color(0, 40, 0));
                   g2.drawString(String.valueOf(i.getNbSaine()),posDepartBar,
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (20 * ratioSpaceY) + 5);



                    //Pop Guéri----------------------------------------
                   g2.setColor(new Color(50, 250, 250));
                    g2.drawString("Guéris : ", i.getPosition().x + 1,
                            i.getPosition().y + i.getForme().getHeightSelectBox() + 2 + (int) (30 * ratioSpaceY));

                    //ligne stat de fond
                     g2.setColor(new Color(0, 150, 150));
                    g2.drawLine(posDepartBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (30 * ratioSpaceY), 
                             posFinBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (30 * ratioSpaceY));
                    g2.setColor(new Color(50, 250, 250));
    //                //ligne stat de dessus
                    float pourGueri = i.getNbGueris().floatValue() / (i.getNbGueris().floatValue() + i.getNbDecede().floatValue());
                   g2.drawLine(posDepartBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (30 * ratioSpaceY), 
                             posDepartBar + (int)(largeurBar * pourGueri), 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (30 * ratioSpaceY));
                    g2.setColor(new Color(0, 70, 70));
                    g2.drawString(String.valueOf(i.getNbGueris()),posDepartBar,
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (30 * ratioSpaceY) + 5);


                     //Pop Décede----------------------------------------
                    g2.setColor(new Color(250, 50, 50));
                    g2.drawString("Décès : ", i.getPosition().x + 1,
                            i.getPosition().y + i.getForme().getHeightSelectBox() + 2 + (int) (40 * ratioSpaceY));
                    if(i.getTaillePop() > 100000)
                        g2.setFont(new Font("TimesRoman", Font.PLAIN, 8));
                    g2.setFont(new Font("TimesRoman", Font.PLAIN, (int) (6 * ratioFont)));
                    //ligne stat de fond
                     g2.setColor(new Color(150, 0, 0));
                    g2.drawLine(posDepartBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (40 * ratioSpaceY), 
                             posFinBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (40 * ratioSpaceY));
                    g2.setColor(new Color(250, 50, 50));
    //                //ligne stat de dessus
                    float pourDeces = i.getNbDecede().floatValue() / (i.getNbGueris().floatValue() + i.getNbDecede().floatValue());
                     g2.drawLine(posDepartBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (40 * ratioSpaceY), 
                             posDepartBar + (int)(largeurBar * pourDeces), 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (40 * ratioSpaceY));
                    g2.setColor(new Color(50, 0, 20));
                    g2.drawString(String.valueOf(i.getNbDecede()),posDepartBar,
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (40 * ratioSpaceY) + 5);

                    //Pop Total----------------------------------------
                    g2.setColor(new Color(130, 50, 250));
                    g2.drawString("Pop tot : ",  i.getPosition().x + 1,
                            i.getPosition().y + i.getForme().getHeightSelectBox() + 2 + (int) (50 * ratioSpaceY));
                    if(i.getTaillePop() > 100000)
                        g2.setFont(new Font("TimesRoman", Font.PLAIN, 8));
                    g2.setFont(new Font("TimesRoman", Font.PLAIN, (int) (6 * ratioFont)));
                    //ligne stat de fond
                   g2.drawLine(posDepartBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (50 * ratioSpaceY), 
                             posFinBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (50 * ratioSpaceY));
                     g2.setColor(new Color(0, 78, 150));
    //                //ligne stat de dessus
                    float pourPopTotal = i.getTaillePop().floatValue() / (float)controller.getCarteDuMonde().getPopulationTotale();
                   g2.drawLine(posDepartBar, 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (50 * ratioSpaceY), 
                             posDepartBar + (int)(largeurBar * pourPopTotal), 
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (50 * ratioSpaceY));
                    g2.setColor(new Color(10, 10, 50));
                    g2.drawString(String.valueOf(i.getTaillePop()),posDepartBar,
                            i.getPosition().y + i.getForme().getHeightSelectBox() + (int) (50 * ratioSpaceY) + 5);

                     
                     
                     
                 }
                     
                     

            }


    }

    private void drawVoiesDeLiaison(Graphics g) {
        
        
        
        Point p1_petit = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point p2_petit = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
         Graphics2D g2d = (Graphics2D) g;
         
         g2d.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    // You can also enable antialiasing for text:

        g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         
         
        int CTRLX = 0;
        int CTRLY = 0;
        QuadCurve2D quadCurve = null;
        QuadCurve2D quadCurve2 = null;
        float pourDist = 0 ;
        int couleurSeuil = 250;

        for (VoiesDeLiaisons i : controller.getCarteDuMonde().getListeVoies()) {
            int distanceInitial = Integer.MAX_VALUE;

            if (i.getType().equals("terrestre")) {
                // je trouve les deux sommets les plus proches
                for (Point p1 : i.getPays1().getForme().getPoints()) {
                    for (Point p2 : i.getPays2().getForme().getPoints()) {
                        if ((p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x) < distanceInitial) {
                            p1_petit = p1;
                            p2_petit = p2;
                            distanceInitial = (p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x);
                        }
                    }
                }
                pourDist = (p2_petit.x - p1_petit.x + p2_petit.y - p1_petit.y) / 2 ; //Distance pour placer le CTRL point responsive
                CTRLX = (p1_petit.x + p2_petit.x) / 2 + 50;
                CTRLY = (p1_petit.y + p2_petit.y) / 2 - 50;
                if(Math.abs(pourDist) < 100)
                {
                    CTRLX = (p1_petit.x + p2_petit.x) / 2 + 10;
                    CTRLY = (p1_petit.y + p2_petit.y) / 2 - 10;
                }

                float terreAnimate = (cptTemps * cptTemps) - 5 * cptTemps - 20000 ;
                if (!i.getEstOuvert())
                {
                    terreAnimate = 1;
                }
                float[] dashingPattern1 = {Math.abs(2f * terreAnimate/100000), Math.abs(4f - 1/terreAnimate),Math.abs(1/terreAnimate)};
                Stroke stroke1 = new BasicStroke(1f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 4.0f, dashingPattern1, 8.0f);
                quadCurve = new QuadCurve2D.Float(p1_petit.x, p1_petit.y, CTRLX,CTRLY, p2_petit.x, p2_petit.y);
                
                float[] dashingPattern1b = {2f, 4f};
                Stroke stroke1b = new BasicStroke(3f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 4.0f, dashingPattern1b, 8.0f);
                quadCurve2 = new QuadCurve2D.Float(p1_petit.x, p1_petit.y, CTRLX,CTRLY, p2_petit.x, p2_petit.y);
                
                    
                    g2d.setColor(new Color((int)(couleurSeuil*i.getTauxTransmission()), (int)(255 - (couleurSeuil*i.getTauxTransmission())), 120));
                    g2d.setStroke(new BasicStroke(7f));
                    g2d.draw(quadCurve);

                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(6f));
                    g2d.draw(quadCurve);

                    g2d.setStroke(stroke1);
                    g2d.setColor(Color.YELLOW);
                    g2d.setStroke(stroke1);
                    g2d.draw(quadCurve);
                
                
//                g.setColor(new Color(255, 255, 0));
//                g2d.setStroke(stroke1b);
//                g2d.draw(quadCurve2);
            }

            if (i.getType().equals("maritime")) {
                
                // je trouve les deux sommets les plus proches
                for (Point p1 : i.getPays1().getForme().getPoints()) {
                    for (Point p2 : i.getPays2().getForme().getPoints()) {
                        if ((p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x) < distanceInitial) {
                            p1_petit = p1;
                            p2_petit = p2;
                            distanceInitial = (p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x);
                        }
                    }
                }
                pourDist = (p2_petit.x - p1_petit.x + p2_petit.y - p1_petit.y) / 2 ; //Distance pour placer le CTRL point responsive
                CTRLX = (p1_petit.x + p2_petit.x) / 2 - 100;
                CTRLY = (p1_petit.y + p2_petit.y) / 2 + 100;
                if(Math.abs(pourDist) < 100)
                {
                    CTRLX = (p1_petit.x + p2_petit.x) / 2 - 45;
                    CTRLY = (p1_petit.y + p2_petit.y) / 2 + 45;
                }
                
                
                float merAnimate = (cptTemps * cptTemps) - 5 * cptTemps - 100 ;
                if(!i.getEstOuvert())
                {
                    merAnimate = 1;
                }
//                float[] dashingPattern1 = {Math.abs(2f * terreAnimate/100000), Math.abs(4f - 1/terreAnimate),Math.abs(1/terreAnimate)};
                float[] dashingPattern2 = {Math.abs(10f * merAnimate/100000), 7f, 5f, 4f};
                Stroke stroke2 = new BasicStroke(4f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 1.0f, dashingPattern2, 0.0f);
                
                
                quadCurve = new QuadCurve2D.Float(p1_petit.x, p1_petit.y, CTRLX,CTRLY, p2_petit.x, p2_petit.y);
            
                g2d.setColor(new Color(200, 200, 250));
                g2d.setStroke(new BasicStroke(6f));
                g2d.draw(quadCurve);


                g2d.setColor(new Color((int)(couleurSeuil*i.getTauxTransmission()), (int)(255 - (couleurSeuil*i.getTauxTransmission())), 200));
                g2d.setStroke(stroke2);
                g2d.draw(quadCurve);
            }

            if (i.getType().equals("aerienne")) {
                // je trouve les deux sommets les plus proches
                for (Point p1 : i.getPays1().getForme().getPoints()) {
                    for (Point p2 : i.getPays2().getForme().getPoints()) {
                        if ((p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x) < distanceInitial) {
                            p1_petit = p1;
                            p2_petit = p2;
                            distanceInitial = (p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x);
                        }
                    }
                }
                
                pourDist = (p2_petit.x - p1_petit.x + p2_petit.y - p1_petit.y) / 2 ; //Distance pour placer le CTRL point responsive
                CTRLX = (p1_petit.x + p2_petit.x) / 2 + 150;
                CTRLY = (p1_petit.y + p2_petit.y) / 2 - 150;
                if(Math.abs(pourDist) < 100)
                {
                    CTRLX = (p1_petit.x + p2_petit.x) / 2 + 70;
                    CTRLY = (p1_petit.y + p2_petit.y) / 2 - 70;
                }
                if(Math.abs(pourDist) < 50)
                {
                    CTRLX = (p1_petit.x + p2_petit.x) / 2 + 40;
                    CTRLY = (p1_petit.y + p2_petit.y) / 2 - 40;
                }
                
                float airAnimate = -5 * (cptTemps * cptTemps) - 5 * cptTemps - 10000 ;
//                float[] dashingPattern1 = {Math.abs(2f * terreAnimate/100000), Math.abs(4f - 1/terreAnimate),Math.abs(1/terreAnimate)};
                
                if(!i.getEstOuvert())
                {
                    airAnimate = 1;
                } 
                  
                g2d.setColor(new Color((int)(couleurSeuil*i.getTauxTransmission()), (int)(255 - (couleurSeuil*i.getTauxTransmission())), 120));
                
                    
                
                float[] dashingPattern3 = {Math.abs(10f * airAnimate/100000), 5f, 1f, 5f};
                Stroke stroke3 = new BasicStroke(2f, BasicStroke.CAP_SQUARE,
                        BasicStroke.JOIN_MITER, 1.0f, dashingPattern3, 0.0f);
                g2d.setStroke(stroke3);
                quadCurve = new QuadCurve2D.Float(p1_petit.x, p1_petit.y, CTRLX,CTRLY, p2_petit.x, p2_petit.y);
                g2d.draw(quadCurve);
            }
            
        }
    }
}
