/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.entites;

import ca.ulaval.glo2004.domain.SimudemieController;
import ca.ulaval.glo2004.helpers.StatistiqueHelper;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.Point;
import java.io.Serializable;

/**
 *
 * @author Monique
 */
public class Region implements Serializable {

    private int m_taillePop;
    private double m_pourcentagePop;
    private int m_nbSaine;
    private int m_nbInfecter;
    private int m_nbGueris;
    private int m_nbDecedes;
    private Color m_couleur;
    private String m_nom;
    private Forme m_forme;
    private ArrayList<Point> m_points;
    private Point m_positionRegion;
    private Point m_positionRelativeRegion;

    // initialiser les int a 0 par defaut ?
    public Region(String p_nom, Point p_position, Point p_positionRelative, ArrayList<Point> p_points, Color p_Color) {
        m_nom = p_nom;
        
        int minX = 999999999;
        int minY = 999999999;
        for (Point points : p_points)
        {
            if (points.x < minX)
            {
                minX = points.x;
            }
            if (points.y < minY)
            {
                minY = points.y;
            }
        }
        m_positionRegion = new Point(minX, minY);
        
        
        m_nbDecedes = 0;
        m_nbGueris = 0;
        m_pourcentagePop = 0;
        m_positionRelativeRegion = p_positionRelative;
        boolean formeIrr = false;
        if (p_points.size() != 4 || p_points.get(0).y != p_points.get(1).y)
        {
            formeIrr = true;
        }
        m_forme = new Forme(p_points, p_Color, formeIrr);
        SimudemieController.TalkToUser("Nouvelle région ajoutée: " + m_nom);

        m_points = p_points;
    }

    public Integer getTaillePop() {
        return m_taillePop;
    }

    public void setTaillePop(int p_taillePop) {
        m_taillePop = p_taillePop;
    }

    public Point getPositionRegion() {
        return m_positionRegion;
    }

    public Double getPourcentPop() {
        return m_pourcentagePop;
    }

    public void setPourcentPop(double p_pourcentPop) {
        m_pourcentagePop = p_pourcentPop;
    }

    public Integer getNbInfecter() {
        return m_nbInfecter;
    }

    public void setNbInfecter(int p_nbInfecter) {
         if (p_nbInfecter > getTaillePop())
        {
            m_nbInfecter = m_taillePop;
        }
         else
         {
             m_nbInfecter = p_nbInfecter;
         }
    }

    public Integer getNbGueris() {
        return m_nbGueris;
    }

    public void setNbGueris(int p_nbGueris) {
        m_nbGueris = p_nbGueris;
    }

    public Integer getNbDecede() {
        return m_nbDecedes;
    }

    public int getNbSaine() {
        return m_nbSaine;
    }

    public void setNbSaine(int p_NbSaine) {
        if (p_NbSaine >= getTaillePop())
        {
            m_nbSaine = getTaillePop();
        }
        else
        {
            if(p_NbSaine < 1)
            {
                m_nbSaine = 1;
            }
            else
            {
                m_nbSaine = p_NbSaine;
            }
        }
    }

    public void setNbDecede(int p_nbDecedes) {
        m_nbDecedes = p_nbDecedes;
    }

    public Color getCouleur() {
        return m_couleur;
    }

    public void setCouleur(Color p_couleur) {
        m_couleur = p_couleur;
    }

    public String getNom() {
        return m_nom;
    }

    public void setNom(String p_nom) {
        m_nom = p_nom;
    }

    public Forme getForme() {
        return m_forme;
    }

    // besoin de faire une assignation en profondeur ??
    public void setForme(Forme p_forme) {
        m_forme = p_forme;
    }

    public ArrayList<Point> getPoints() {
        return m_points;
    }

    // besoin de faire une assignation en profondeur ??
    public void setPoints(ArrayList<Point> p_points) {
        m_points = p_points;
    }

    public void setRelativePositionRegion(Point p_point) {
        m_positionRelativeRegion = p_point;
    }

    public Point getRelativePositionRegion() {
        return m_positionRelativeRegion;
    }

    public void setPositionRegion(Point p_point) {
        m_positionRegion = p_point;
    }

    public void modifierStatsRegion(int p_nbNouveauInfecte, int p_nbNouveauGueries, int p_nbNouveauDecedes) {
        setNbDecede(m_nbDecedes + p_nbNouveauDecedes);
        setNbGueris(m_nbGueris + p_nbNouveauGueries);
        setNbInfecter(m_nbInfecter + p_nbNouveauInfecte - p_nbNouveauGueries);
        setTaillePop(m_taillePop - p_nbNouveauDecedes);

    }

    public String getStatRegionFormate() {
        String information;
        information = "RÉGION : " + m_nom + " --- > Nb personne Total : " + getTaillePop() + " Nb Santé " + getNbSaine()
                + " Nb infectés : " + getNbInfecter() + " Nb deces : " + getNbDecede() + "Nb gueris : " + getNbGueris();
        return information;
    }

    public void modifierNouveauDeces(int p_nbNouveauDecedes) {
        setNbDecede(m_nbDecedes + p_nbNouveauDecedes);
        setNbInfecter(Math.max(0, m_nbInfecter - p_nbNouveauDecedes));
    }

    public void modifierNouveauGuerie(int p_nbNouveauGueries) {
        setNbGueris(m_nbGueris + p_nbNouveauGueries);
        setNbInfecter(Math.max(0, m_nbInfecter - p_nbNouveauGueries));
    }

    public void modifierNouveauInfecte(int p_nbNouveauInfecte) {
        setNbInfecter(m_nbInfecter + p_nbNouveauInfecte);
    }

    public void modifierNouvelleTaillePopulation(int p_nbNouveauDecedes) {
        setTaillePop(m_nbSaine + m_nbInfecter);
    }

    public void incrementPersonneSaine(int p_nbNouveauGueris) {
        setNbSaine(m_nbSaine + p_nbNouveauGueris);
    }

    public void decrementPersonneSaine(int p_nbNouveauInfecte) {
        setNbSaine(Math.max(0, m_nbSaine - p_nbNouveauInfecte));
    }

    public void calculerStatParRegionParJour(Maladie p_maladie, double p_tauxTransmission, int nbInfecte, int nbSaine, ArrayList<MesureSanitaire> listeMesure) {
        // Calcul des nouvelles infections
        //On vient voir si il y a une mesure active
        
        if(chercherSiMesureActive(listeMesure)){
            for (MesureSanitaire mesureSanitaire : listeMesure)
                if(mesureSanitaire.getEstActive()){
                    calculerInfecterAvecMesureSanitaire(p_maladie, p_tauxTransmission, nbSaine, nbInfecte,listeMesure);
                }
        }
        else{
            int nbNouvelleInfection = StatistiqueHelper.calculerNombreDePersonneInfectees(nbInfecte,
                p_tauxTransmission, p_maladie.getTauxReproduction());
            if (nbNouvelleInfection >= getNbSaine()) {
                m_nbInfecter = m_nbInfecter + m_nbSaine;
                m_nbSaine = 0;
                m_taillePop = m_nbInfecter;
        }   else {
                modifierNouveauInfecte(nbNouvelleInfection);
                decrementPersonneSaine(nbNouvelleInfection);
        }
        }
        
        int nbNouveauGueris = StatistiqueHelper.calculerNombrePersonneGueries(nbInfecte,
                p_maladie.getTauxGuerison());
        // Ajoute nb de guéris total et decrémente le nombre d'infecter car ils sont
        // guéris
        modifierNouveauGuerie(nbNouveauGueris);
        incrementPersonneSaine(nbNouveauGueris);
        
        int nbNouveauDeces = StatistiqueHelper.calculerNombePersonneDecedees(nbInfecte,
        p_maladie.getTauxMortalite());

        // Ajoute nb de déces total et decrémente le nombre d'infecter car ils sont dead
        modifierNouveauDeces(nbNouveauDeces);
        // Nouvelle population est les personnes saines + personne infecté
        modifierNouvelleTaillePopulation(nbNouveauDeces);

    }
    
    public void calculerInfecterAvecMesureSanitaire(Maladie p_maladie, double tauxTransmissionRegion, int nbSaine, int nbInfecte, ArrayList<MesureSanitaire> listeMesure){
        //On additionne les effets de chaque mesure pour les différentes réduction
        double totalReductionReproduction = 0;
        for (MesureSanitaire mesure : listeMesure){
            if(mesure.getEstActive()){
                totalReductionReproduction += mesure.getReproduction();
            }
        }
        if (totalReductionReproduction > 1){
            totalReductionReproduction = 1;
        }
        double totalReductionTranmission = 0;
        for (MesureSanitaire mesure : listeMesure){
            if(mesure.getEstActive()){
                totalReductionTranmission += mesure.getTransmission();
            }       
        }
        if(totalReductionTranmission > 1){
            totalReductionTranmission = 1;
        }
        //Calcul des nouveaux taux de reproduction et transmission
        double tauxReproductionReduit = p_maladie.reductionTauxReproduction(p_maladie.getTauxReproduction(), totalReductionReproduction);
        double tauxTranmissionReduit = p_maladie.reductionTauxTransmission(tauxTransmissionRegion, totalReductionTranmission);
        
        
        //On calcul le nombre de personne qui ne respecte pas les mesures 
        int personneNeRespectantPasLaMesure = nbInfecte;
        for (MesureSanitaire mesure : listeMesure){
            if (mesure.getEstActive()){
                 double tauxRespectantPasLaMesure = 1 - mesure.getAdhesion();
                 personneNeRespectantPasLaMesure = (int)Math.round(personneNeRespectantPasLaMesure * tauxRespectantPasLaMesure);
            }
        }
        int personneRespectantLesMesure = nbInfecte - personneNeRespectantPasLaMesure;
        //Calcul de la premiere loi binomial pour les personnes ne respectant pas les mesures
        int nouveauInfecte = 0;
        nouveauInfecte += StatistiqueHelper.calculerNombreDePersonneInfectees(personneNeRespectantPasLaMesure, tauxTransmissionRegion, p_maladie.getTauxReproduction());
        
        //Calcul de la deuxieme loi binomial pour les personnes respectant les mesures
        nouveauInfecte += StatistiqueHelper.calculerNombreDePersonneInfectees(personneRespectantLesMesure, tauxTranmissionReduit, tauxReproductionReduit);
        
        if (nouveauInfecte >= nbSaine) {
            m_nbInfecter = m_nbInfecter + m_nbSaine;
            m_nbSaine = 0;
            m_taillePop = m_nbInfecter;
        } 
        else {
            modifierNouveauInfecte(nouveauInfecte);
            decrementPersonneSaine(nouveauInfecte);
        }
        
    }

    public double trouverPourcentageMalade(int p_taille, int p_nbInfecter) {
        return ((double) p_nbInfecter / (double) p_taille) * 100;
    }

    public Color calculerCouleur(int p_taille, int p_nbInfecter) {
        if (trouverPourcentageMalade(p_taille, p_nbInfecter) <= 100
                && trouverPourcentageMalade(p_taille, p_nbInfecter) >= 0) {
            int colInfect = (int) (250 * ((trouverPourcentageMalade(p_taille, p_nbInfecter) / 100)));
            return new Color(colInfect, (255 - colInfect), 50);
        } else {
            return Color.BLACK;
        }
    }
    
    public boolean chercherSiMesureActive(ArrayList<MesureSanitaire> mesureSanitaires){
    boolean estActive = false;
    for(MesureSanitaire mesure : mesureSanitaires){
        if (mesure.getEstActive()){
            estActive = true;
        }
        }
        return estActive;
        
    }

}
