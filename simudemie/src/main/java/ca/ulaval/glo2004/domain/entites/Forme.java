/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.entites;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.io.Serializable;

/**
 *
 * @author Monique
 */
public class Forme implements Serializable {

    private Color m_couleur; // Changement de type de string a Color NOK
    private Polygon m_polygon;
    private int m_widthSelectBox;
    private int m_heightSelectBox;
    private int m_OriginalWidthBox;
    private int m_OriginalHeightBox;
    private ArrayList<Point> m_listeSommets;
    private boolean m_moveEnCours;
    private boolean m_mouseOver;
    private boolean m_formeIrr;

    // initialiser les int a 0 par defaut ?
    public Forme(ArrayList<Point> p_points, Color p_couleur, boolean p_formeIrr) // J'ai enlever forme pour le point de position la forme
                                                             // nest pas accessible qwuand on appel ajouter pays NOK
    {
        m_polygon = new Polygon();
        m_listeSommets = p_points;
        for (Point points : p_points) {
            m_polygon.addPoint(points.x, points.y);
        }
        m_couleur = p_couleur;
        m_moveEnCours = false;
        m_widthSelectBox = m_polygon.getBounds().width;
        m_heightSelectBox = m_polygon.getBounds().height;
        m_OriginalWidthBox = m_polygon.getBounds().width;
        m_OriginalHeightBox = m_polygon.getBounds().height;
        m_formeIrr = p_formeIrr;
    }

    public Polygon getPolygonForme() {
        return m_polygon;
    }

    public void setPolygonForme(ArrayList<Point> p_points) {
        m_polygon = new Polygon();
        m_listeSommets = p_points;
        for (Point points : p_points) {
            m_polygon.addPoint(points.x, points.y);
        }
//        m_moveEnCours = false;
    }

    public ArrayList<Point> getListeSommet() {
        return m_listeSommets;
    }

    public ArrayList<Point> getPoints() {
        return m_listeSommets;
    }
    
    public boolean getFormeIrr() {
        return m_formeIrr;
    }


    public Color getCouleur() {
        return m_couleur;
    }

    public void setCouleur(Color p_couleur) {
        m_couleur = p_couleur;
    }

    public boolean getMoveEnCours() {
        return m_moveEnCours;
    }

    public void setMoveEnCours(boolean p_moveEnCours) {
        m_moveEnCours = p_moveEnCours;
    }

    public boolean getMouseOver() {
        return m_mouseOver;
    }

    public void setMouseOver(boolean p_mouseOver) {
        m_mouseOver = p_mouseOver;
    }

    public Integer getWidthSelectBox() {
        return m_widthSelectBox;
    }

    public void setWidthSelectBox(int p_width) {
        m_widthSelectBox = p_width;
    }

    public Integer getHeightSelectBox() {
        return m_heightSelectBox;
    }

    public void setHeightSelectBox(int p_height) {
        m_heightSelectBox = p_height;
    }

    public void setSelectBox() {
        m_widthSelectBox = m_polygon.getBounds().width;
        m_heightSelectBox = m_polygon.getBounds().height;
        m_OriginalWidthBox = m_polygon.getBounds().width;
        m_OriginalHeightBox = m_polygon.getBounds().height;
    }

    public int getOriginalWidthBox() {
        return m_OriginalWidthBox;
    }

    public int getOriginalHeightBox() {
        return m_OriginalHeightBox;
    }

}
