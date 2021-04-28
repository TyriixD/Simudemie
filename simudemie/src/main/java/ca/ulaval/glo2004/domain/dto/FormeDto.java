/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.dto;
import ca.ulaval.glo2004.domain.entites.Forme;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
/**
 *
 * @author Monique
 */
public class FormeDto {
    public Color m_couleur;
    public Polygon m_polygon;
    public Integer m_widthSelectBox;
    public Integer m_heightSelectBox;
    public Integer m_OriginalWidthBox;
    public Integer m_OriginalHeightBox;
    public ArrayList<Point> m_listeSommets = new ArrayList<>();
    public boolean m_moveEnCours;
    public boolean m_mouseOver;
    public boolean m_formeIrr;


    public FormeDto(Forme p_forme) 
    {
        m_couleur = p_forme.getCouleur();
        m_polygon = p_forme.getPolygonForme();
        m_widthSelectBox = p_forme.getWidthSelectBox();
        m_heightSelectBox =  p_forme.getHeightSelectBox();
        m_OriginalWidthBox = p_forme.getOriginalWidthBox();
        m_OriginalHeightBox = p_forme.getOriginalHeightBox();
        m_listeSommets = p_forme.getListeSommet();
        m_moveEnCours = p_forme.getMoveEnCours();
        m_mouseOver = p_forme.getMouseOver();
        m_formeIrr = p_forme.getFormeIrr();

    }
}
