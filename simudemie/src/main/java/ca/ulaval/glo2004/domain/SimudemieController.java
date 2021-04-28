/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain;


import ca.ulaval.glo2004.domain.dto.*;
import ca.ulaval.glo2004.domain.drawing.CarteDrawer;
import ca.ulaval.glo2004.domain.entites.CarteDuMonde;
import ca.ulaval.glo2004.domain.entites.Pays;
import ca.ulaval.glo2004.domain.entites.Region;
import ca.ulaval.glo2004.domain.entites.VoiesDeLiaisons;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.awt.Point;
import java.awt.Polygon;
import java.util.*;

/**
 *
 * @author guillaume
 */
public class SimudemieController {

    private CarteDrawer drawer;
    private CarteDuMonde m_carte;
    private boolean m_inPanel;
    private Point m_mousePosition;
    private Point m_mouseClicked;
    private Point m_mouseReleased;
    private Boolean m_addRegionSelected;
    private boolean m_validerFormeTerminer;
    private Boolean m_isAddPaysTabSelected;
    private Boolean m_isModifierPaysTabSelected;
    private Boolean m_isModifierRegionTabSelected;
    private ArrayList<Point> m_listePoints;
    private ArrayList<Point> m_listePointsPolyIrr;
    private ArrayList<Point> m_listePointsBoundPoints;
    private ArrayList<Point> m_listePointsBoundPointsReg;
    int m_indexVertexToBeMove;
    int m_indexVertexToBeMoveReg;
    private Pays m_payshover;
    private Region m_regionhover;
    private Point m_oldPosition;
    private boolean m_oldPosIsSet;
    private boolean m_modeZoom;
    private boolean m_showAllStats;
    private boolean m_ctrlDown;
    private boolean m_BoundPointsClicked;
    private boolean m_BoundPointsClickedReg;
    private boolean m_validMove;
    public static ArrayList<String> m_msgToUser;
    private Stack<byte[]> stackRetour = new Stack<>();
    private Stack<byte[]> stackAvance = new Stack<>();
    private int compteurDEtat = 0;
    private int compteurRetour = 0;
    private ArrayList<CarteDuMonde> m_listeCartes =new ArrayList<>();  
    private Pays paysMouseOver;


    public SimudemieController() {

        // Message par défaut à l'ouverture de l'app
        m_msgToUser = new ArrayList<>();
        m_msgToUser.add("Bonjour !");
        m_msgToUser.add("Bienvenue dans Simudémie !");
        m_msgToUser.add("Veuillez Toujours consulter ces messages pour avoir plus d'information !");
        m_msgToUser.add("Bonne simulation !");
        m_msgToUser.add("Veuillez débuter en important une simulation ou en ajoutant un premier pays.");
        m_carte = new CarteDuMonde();

        m_listePoints = new ArrayList<>();
        m_listePointsPolyIrr = new ArrayList<>();
        m_listePointsBoundPoints = new ArrayList<>();
        m_listePointsBoundPointsReg = new ArrayList<>();
        m_addRegionSelected = false;
        m_oldPosIsSet = false;
        m_modeZoom = false;
        m_showAllStats = false;
        m_BoundPointsClicked = false;
        m_BoundPointsClickedReg = false;
         m_validMove = true;
         m_carte.setTranslate(new Dimension(-500, -200));
    }

    public CarteDuMonde getCarteDuMonde() {
        return m_carte;
    }
    
    
        
    public Pays isMouseInPays() {
        // Définition de quel pays ou région est sous la souris !
        for (Pays pays : m_carte.getListePays()) {
            if (pays.getForme().getPolygonForme().contains(m_mousePosition))
            {
                return pays;
            }    
        }
        return null;
    }

    // besoin de faire une assignation en profondeur ??
    public void setCarteDuMonde(CarteDuMonde p_carte) {
        m_carte = p_carte;
    }

    public ArrayList<Point> getListePoints() {
        return m_listePoints;
    }

    public void addPoint(int x, int y) {
        m_listePoints.add(new Point(x,y));
        SimudemieController.TalkToUser("Point Ajouté : " + x + ", " + y + ")" );
    }
    
    public ArrayList<Point> getListePointsIrr() {
        return m_listePointsPolyIrr;
    }

    public void addPointIrr(int x, int y) {
        m_listePointsPolyIrr.add(new Point(x,y));
        SimudemieController.TalkToUser("Point Ajouté : " + m_listePoints.size() + " : (" + x + ", " + y + ")" );
    }
    
    
    public void initListePointBoundPoints() {
        m_listePointsBoundPoints = new ArrayList<>();
        m_listePointsBoundPointsReg = new ArrayList<>();
    }
    public ArrayList<Point> getListePointsBoundPoints() {
        return m_listePointsBoundPoints;
    }
    public ArrayList<Point> getListePointsBoundPointsReg() {
        return m_listePointsBoundPointsReg;
    }
    public void setPointBoundPointsPaysHover() {
        Polygon mouseClickPoly = new Polygon();//Cible pour le clic
        if (m_payshover != null && m_payshover.getForme().getFormeIrr())
        {
            mouseClickPoly.addPoint(m_mousePosition.x - 15, m_mousePosition.y + 15);
            mouseClickPoly.addPoint(m_mousePosition.x + 15, m_mousePosition.y + 15);
            mouseClickPoly.addPoint(m_mousePosition.x + 15, m_mousePosition.y - 15);
            mouseClickPoly.addPoint(m_mousePosition.x - 15, m_mousePosition.y - 15);
        }
        Polygon mouseClickPolyReg = new Polygon();//Cible pour le clic
        if (m_regionhover != null && m_payshover.getForme().getFormeIrr())
        {
            mouseClickPolyReg.addPoint(m_mousePosition.x - 15, m_mousePosition.y + 15);
            mouseClickPolyReg.addPoint(m_mousePosition.x + 15, m_mousePosition.y + 15);
            mouseClickPolyReg.addPoint(m_mousePosition.x + 15, m_mousePosition.y - 15);
            mouseClickPolyReg.addPoint(m_mousePosition.x - 15, m_mousePosition.y - 15);
        }
        boolean PaysUnder = false;
        boolean RegionUnder = false;
        for(Pays pays : m_carte.getListePays())
        {
            if (pays.getForme().getPolygonForme().contains(m_mousePosition))
            {
                PaysUnder = true;
                for (Point points : pays.getForme().getListeSommet())
                {
                    m_listePointsBoundPoints.add(points);
                    m_payshover = pays;
                }
                for (int i = 0; i < m_listePointsBoundPoints.size(); i++)
                {
                    if (mouseClickPoly.contains(m_listePointsBoundPoints.get(i)))
                    {
                        m_indexVertexToBeMove = i;
                    }
                }
                
                for (Region regions:pays.getListeRegion())
                { 
                    if (regions.getForme().getPolygonForme().contains(m_mousePosition))
                    {
                        RegionUnder = true;
                        for (Point points : regions.getForme().getListeSommet())
                        {
                            m_listePointsBoundPointsReg.add(points);
                            m_regionhover = regions;
                        }
                        for (int y = 0; y < m_listePointsBoundPointsReg.size(); y++)
                        {
                            if (mouseClickPolyReg.contains(m_listePointsBoundPointsReg.get(y)))
                            {
                                m_indexVertexToBeMoveReg = y;
                            }
                        }
                    }
                }
                
            }
        }
        if (!PaysUnder)    
        {
            m_payshover = null;
        }
        if (!RegionUnder)    
        {
            m_regionhover = null;
        }
    }
    public void setPointBoundPointsClicked()
    { 
        Polygon mouseClickPoly = new Polygon();//Cible pour le clic
        if (m_payshover != null && m_payshover.getForme().getFormeIrr())
        {
            mouseClickPoly.addPoint(m_mousePosition.x - 15, m_mousePosition.y + 15);
            mouseClickPoly.addPoint(m_mousePosition.x + 15, m_mousePosition.y + 15);
            mouseClickPoly.addPoint(m_mousePosition.x + 15, m_mousePosition.y - 15);
            mouseClickPoly.addPoint(m_mousePosition.x - 15, m_mousePosition.y - 15);
        }
        for (int i = 0; i < m_listePointsBoundPoints.size(); i++)
                {
                    if (mouseClickPoly.contains(m_listePointsBoundPoints.get(i)))
                    {
                        m_indexVertexToBeMove = i;
                        m_BoundPointsClicked = true;
                    }
                }
    }
    public void setPointBoundPointsClickedReg()
    { 
        Polygon mouseClickPolyReg = new Polygon();//Cible pour le clic
        if (m_regionhover != null && m_regionhover.getForme().getFormeIrr())
        {
            mouseClickPolyReg.addPoint(m_mousePosition.x - 15, m_mousePosition.y + 15);
            mouseClickPolyReg.addPoint(m_mousePosition.x + 15, m_mousePosition.y + 15);
            mouseClickPolyReg.addPoint(m_mousePosition.x + 15, m_mousePosition.y - 15);
            mouseClickPolyReg.addPoint(m_mousePosition.x - 15, m_mousePosition.y - 15);
        }
        for (int y = 0; y < m_listePointsBoundPointsReg.size(); y++)
                {
                    if (mouseClickPolyReg.contains(m_listePointsBoundPointsReg.get(y)))
                    {
                        m_indexVertexToBeMoveReg = y;
                        m_BoundPointsClickedReg = true;
                    }
                }
    }
    public void addNewMovedVertex()
    {
        if (m_payshover != null && m_BoundPointsClicked && m_payshover.getForme().getFormeIrr())
        {
            m_listePointsBoundPoints.set(m_indexVertexToBeMove, m_mouseReleased);
            m_payshover.getForme().setPolygonForme(m_listePointsBoundPoints);
        }
    }
    public void addNewMovedVertexReg()
    {
        if (m_regionhover != null && m_BoundPointsClickedReg && m_regionhover.getForme().getFormeIrr())
        {
            m_listePointsBoundPointsReg.set(m_indexVertexToBeMoveReg, m_mouseReleased);
            m_regionhover.getForme().setPolygonForme(m_listePointsBoundPointsReg);
        }
    }
    
    public void addPointBoundPoints(int x, int y) {
        m_listePointsBoundPoints.add(new Point(x,y));
    }
    public void addPointBoundPointsReg(int x, int y) {
        m_listePointsBoundPointsReg.add(new Point(x,y));
    }
    public void setBoundPointClicked(boolean p_bool)
    {
        m_BoundPointsClicked = p_bool;
    }
    public void setBoundPointClickedReg(boolean p_bool)
    {
        m_BoundPointsClickedReg = p_bool;
    }
    public boolean getBoundPointClicked()
    {
        return m_BoundPointsClicked;
    }
    public boolean getBoundPointClickedReg()
    {
        return m_BoundPointsClickedReg;
    }

    public boolean getValiderFormeTerminer() {
        return m_validerFormeTerminer;
    }

    public void setValiderFormeTerminer(boolean p_validite) {
        m_validerFormeTerminer = p_validite;
    }

    public int getIndexVertexToBeMove() {
        return m_indexVertexToBeMove;
    }
    
    public int getIndexVertexToBeMoveReg() {
        return m_indexVertexToBeMoveReg;
    }


    public Point getMouseClicked() {
        return m_mouseClicked;
    }

    public void setMouseclicked(int x, int y) {
        m_mouseClicked = new Point(x, y);
    }

    public Point getMouseReleased() {
        return m_mouseReleased;
    }

    public void setMouseReleased(int x, int y) {
        m_mouseReleased = new Point(x, y);
    }
    
    public boolean getCtrlDown() {
        return m_ctrlDown;
    }
    public void setCtrlDownPressed() {
        m_listePointsPolyIrr = new ArrayList<>();  //Init du Array qui recois les coordonnées
        m_ctrlDown = true;
    }
    public void setCtrlDownReleased(int p_taille, int p_infecter, double p_taux) {
        m_ctrlDown = false;
        String nom = "ZIMBAIRRR" +(int) (Math.random() * ((100 - 1) + 1)) + 1;
        String nomRegion = "Quebec" +(int) (Math.random() * ((100 - 1) + 1)) + 1;
        boolean inPays = false;
        Pays paysIn = new Pays();
//        boolean inRegion = false;
        if (m_listePointsPolyIrr.size() > 2)
        {
            
            for (Pays pays : m_carte.getListePays()) {
                if(pays.getForme().getPolygonForme().contains(m_listePointsPolyIrr.get(0)))
                {
                    inPays = true;
                    paysIn = pays;
                }
                
            }       
            if (!inPays)
            {
                if(!m_addRegionSelected)
                {
                    m_carte.addPaysToList(nom, m_listePointsPolyIrr.get(0), m_listePointsPolyIrr, new Color(0, 60, 60), p_taille, p_infecter, p_taux);
                    TalkToUser("Vous avez ajouter le Pays : " + nom);
                }
            }
            else
            {
                if (m_addRegionSelected) {
                   m_carte.getPaysFromList(paysIn.getNom()).addRegion(nomRegion, m_listePointsPolyIrr.get(0), m_listePointsPolyIrr, new Color(0, 60, 60));
                   TalkToUser("Vous avez ajouté la région : " + nomRegion);
                }
            }
            
        }
        m_listePointsPolyIrr = new ArrayList<>(); // Reset des sommets en cours de traitement après avoir ajouter le payrs
                                                // - NOK
    }
    public Point getMousePosition() {
        return m_mousePosition;
    }

    public void setMousePosition(int x, int y ) {
        m_mousePosition = new Point (x,y);
        // Définition de quel pays ou région est sous la souris !
        setPaysUnderMouse();
    }
     
     
    public void setPaysUnderMouse() {
        // Définition de quel pays ou région est sous la souris !
        for (Pays pays : m_carte.getListePays()) {
            if (pays.getForme().getPolygonForme().contains(m_mousePosition)) {
                pays.getForme().setMouseOver(true);
                for (Region regions : pays.getListeRegion()) {
                    if (regions.getForme().getPolygonForme().contains(m_mousePosition)) {
                        regions.getForme().setMouseOver(true);
                    } else {
                        regions.getForme().setMouseOver(false);
                    }
                }
            } else {

                pays.getForme().setMouseOver(false);
                for (Region regions : pays.getListeRegion()) {
                    regions.getForme().setMouseOver(false);
                }
            }
        }
    }

    public boolean getInPanel() // ce ne sera surement pas nécessaire - NOK
    {
        return m_inPanel;
    }

    public void setInPanel(boolean p_inPanel) {
        m_inPanel = p_inPanel;
    }

    public boolean getAddRegionSelected() {
        return m_addRegionSelected;
    }

    public void setAddRegionSelected(boolean p_addRegionSelected) {
        m_addRegionSelected = p_addRegionSelected;
    }

    public void UpdateSelectedTab(int selectedTabIndex) {
        switch (selectedTabIndex) {
            case 0:
                m_isAddPaysTabSelected = true;
                m_addRegionSelected = false;
                m_isModifierPaysTabSelected = false;
                m_isModifierRegionTabSelected = false;
                break;
            case 1:
                m_isAddPaysTabSelected = false;
                m_addRegionSelected = true;
                m_isModifierPaysTabSelected = false;
                m_isModifierRegionTabSelected = false;
                break;
            case 2:
                m_isAddPaysTabSelected = false;
                m_addRegionSelected = false;
                m_isModifierPaysTabSelected = true;
                m_isModifierRegionTabSelected = false;
                break;
            case 3:
                m_isAddPaysTabSelected = false;
                m_addRegionSelected = false;
                m_isModifierPaysTabSelected = false;
                m_isModifierRegionTabSelected = true;
                break;
            default:
                break;
        }

    }

    public void ajouterPaysOuRegion(int x, int y , String p_nom, String p_nomRegion, int p_taille, int p_nbInfecter, double p_taux) {
        Point p_point = new Point (x,y);
        boolean estInclus = false;
        boolean estInclusReg = false;
        for (Pays pays : m_carte.getListePays()) {
            for (Point points : m_listePoints) {
                if (pays.getForme().getPolygonForme().contains(points)) {
                    estInclus = true;
                    TalkToUser("IMPOSSIBLE DE DESSINER UNE PAYS SUR UN AUTRE");
                }
            }
            if (pays.getForme().getPolygonForme().contains(m_mouseClicked) && m_addRegionSelected == true && !m_ctrlDown) {
                for (Region regions : m_carte.getPaysFromList(pays.getNom()).getListeRegion()) {
                    for (Point points : m_listePoints) {
                        if (regions.getForme().getPolygonForme().contains(points)) {
                                estInclusReg = true;
                                TalkToUser("IMPOSSIBLE DE DESSINER UNE REGION SUR UN AUTRE");
                            }
                        }
                    }
                    if (!estInclusReg && m_addRegionSelected) {
                        m_carte.getPaysFromList(pays.getNom()).addRegion(p_nomRegion, m_mouseClicked, m_listePoints, new Color(0, 60, 60));
                        TalkToUser("Vous avez ajouté la région : " + p_nomRegion);

        }
        m_listePoints = new ArrayList<>();
            }
        }
        if (!estInclus && !m_addRegionSelected) {
            m_carte.addPaysToList(p_nom, p_point, m_listePoints, new Color(0, 60, 60), p_taille, p_nbInfecter, p_taux);
            TalkToUser("Vous avez ajouter le Pays : " + p_nom);
        }
        m_listePoints = new ArrayList<>(); // Reset des sommets en cours de traitement après avoir ajouter le payrs
                                                // - NOK
                                                
        setPopInitTotal();
    }

    public void supprimerPays(String nom) {
        saveEtatCourantToList();
        this.getCarteDuMonde().deletePaysFromList(nom);
        SimudemieController.TalkToUser("Vous avez supprimé le pays : " + nom);
    }

    public void modifierPays(String ancienNom, String nouveauNom, int taillePop, int nbIfecter, double p_taux) {
        saveEtatCourantToList();
        this.getCarteDuMonde().modifierPaysInList(ancienNom, nouveauNom, taillePop, nbIfecter, p_taux);
        SimudemieController.TalkToUser("Vous avez MODIFIER le pays : " + ancienNom);
        SimudemieController.TalkToUser("Nouveaux attributs : " + nouveauNom + ", " + taillePop + ", " + nbIfecter + ", " + p_taux);
    }

    public void ajoutFront(String nomPays1, String nomPays2, String p_type, double p_seuil, double p_transmission, double p_adhesion, boolean aUneMesure) {

        for (Pays t : getCarteDuMonde().getListePays()) {
            for (Pays i : getCarteDuMonde().getListePays()) {
                if (!nomPays1.equals(nomPays2)) {
                    if (t.getNom().equals(nomPays1) && i.getNom().equals(nomPays2)) {
                        saveEtatCourantToList();
                        this.getCarteDuMonde().addVoieToList(t, i, p_seuil , p_transmission, p_adhesion, p_type, aUneMesure); 
                        TalkToUser("Vous avez ajouté une frontière entre : " + t.getNom() + " ET " + i.getNom());
                        TalkToUser("Détails : " + t.getNom() + ",  " + i.getNom() + ",  seuil : " + p_seuil + ",  adhésion : " + p_adhesion + ",   type :" + p_type);  
                    }
                }
            }
        }
        if (nomPays1.equals(nomPays2)) {
            TalkToUser("On ne peut pas lier un pays avec lui-meme");
        }
    }

    public void supprimerFrontiereDuPays(String nomPays) {
        ArrayList<String> nomFrontToDel = new ArrayList<>();
        for (VoiesDeLiaisons voie : getCarteDuMonde().getListeVoies()) {
            if (nomPays.equals(voie.getPays1().getNom()) || nomPays.equals(voie.getPays2().getNom())) {
                nomFrontToDel.add(voie.getNom());
            }
        }
        for (String nomFront : nomFrontToDel) {
            supprimerFront(nomFront);
        }
        
    }

    public void supprimerFront(String nomFrontiere) {
        saveEtatCourantToList();
        this.getCarteDuMonde().deleteVoieFromList(nomFrontiere);
        TalkToUser("La frontière : " + nomFrontiere + " à été supprimée.");
    }

    // on peut seulement modifier l achalandage et le fait qu une frontiere soit
    // ouvert ou non
    public void modifierFront(String nomFrontiere,  double p_transmission, double p_adhesion, double p_seuil, boolean aUneMesure) 
    {    
        saveEtatCourantToList();
        this.getCarteDuMonde().modifierVoieInList(nomFrontiere,  p_transmission, p_adhesion, p_seuil, aUneMesure);
        TalkToUser("La frontière : " + nomFrontiere + " à été Modifiée.");
    }

    public void ajoutMaladie(String nomMaladie, double p_mortalite, double p_reproduction, double p_guerison) {
        saveEtatCourantToList();
        this.getCarteDuMonde().addMaladieToList(nomMaladie, p_mortalite, p_reproduction, p_guerison);
        TalkToUser("Vous avez ajouté la Maladie : " + nomMaladie);
    }

    public void supprimerMaladie(String p_nomMaladie) {
        saveEtatCourantToList();
        this.getCarteDuMonde().deleteMaladieFromList(p_nomMaladie);
        TalkToUser("Vous avez supprimé la Maladie : " + p_nomMaladie);
    }

    public void modifierMaladie(String ancienNom, String nouveauNom, double p_mortalite, double p_reproduction, double p_guerison) {
        saveEtatCourantToList();
        this.getCarteDuMonde().modifierMaladieInList(ancienNom, nouveauNom, p_mortalite, p_reproduction, p_guerison);
        TalkToUser("Vous avez Modifié la Maladie : " + ancienNom + " ---> " + nouveauNom);
    }

    public void importSim() {
        Serializable.openCarteDuMonde(this);
        TalkToUser("La carte à été importée");
    }

    public void newSim() {
        m_carte.reinitialiserCarte();
        TalkToUser("NOUVELLE SIMULATION!");
    }

    public void saveSim() {
        Serializable.save(m_carte);
        TalkToUser("SAUVEGARDE EFFECTUÉE !");
    }
    

    public void saveCSV() {
        Serializable.saveStatsToCSV(m_carte);
        TalkToUser("SAUVEGARDE de stats.CSV EFFECTUÉE !");
    }

       public void play(int nbJourEcouler) {
        saveEtatCourantDansSimulation(m_carte);
        this.getCarteDuMonde().avanceDeUnJour(nbJourEcouler);
        TalkToUser("SIMULATION EN COUR ...");
        if (this.getCarteDuMonde().getNbInfecteSimulation() == 0) {
            TalkToUser("LA MALADIE EST ÉRADIQUÉE, il n'y a plus de personne infecté pour transmettre le virus!");
        }
    }

    public void saveEtatCourantToList() {
        if (compteurRetour != compteurDEtat) {
            stackAvance.clear();
            stackRetour.push(Serializable.TransformToStream(m_carte));
            compteurRetour = compteurDEtat;
        } else {
            stackRetour.push(Serializable.TransformToStream(m_carte));
            compteurDEtat = compteurDEtat + 1;
            compteurRetour = compteurDEtat;
        }
    }

    public void retourDEtat() {
        TalkToUser("UNDO effectué");
        if (!stackRetour.isEmpty()) {
            stackAvance.push(Serializable.TransformToStream(m_carte));
            byte[] carteByte = stackRetour.pop();
            CarteDuMonde carteCharger = Serializable.TransformToCarte(carteByte);           
            this.setCarteDuMonde(carteCharger);
            compteurRetour = compteurRetour - 1;
        } else {
            TalkToUser("Il n'existe pas d'etat precedent celle-ci");
        }
    }

    public void avanceDEtat() {
        TalkToUser("REDO effectué");
        if (!stackAvance.isEmpty()) {
            stackRetour.push(Serializable.TransformToStream(m_carte));
            this.setCarteDuMonde(Serializable.TransformToCarte(stackAvance.pop()));
            compteurRetour = compteurRetour + 1;
        } else {
            TalkToUser("Il n'existe pas d'etat apres celle-ci");
        }
    }
    
    public void getEtatDansSimulationCourante(int index)
    {
        if (!m_listeCartes.isEmpty())
        {
        this.setCarteDuMonde(m_listeCartes.get(index));
        }
    }
    
    public void saveEtatCourantDansSimulation(CarteDuMonde carte) {
           byte[] carteSerializer = Serializable.TransformToStream(carte);
           m_listeCartes.add(Serializable.TransformToCarte(carteSerializer));
    }
    
    public void reinitialiserArrayEtatsCourant()
    {
        m_listeCartes.clear();
    }
    
    public void sauverSimulationPasser(String p_nom) {
       if (!m_listeCartes.isEmpty())
        { 
            getCarteDuMonde().addSimulationToList (p_nom, m_listeCartes);
            reinitialiserArrayEtatsCourant();
        }

    }
    
    public void chargerSimulationPasser(String p_nom) {
        for (int i=0;i<getCarteDuMonde().getSimulationPasserFromList(p_nom).getEtatsPasserSimulation().size();i++)
        {
           m_listeCartes.add(getCarteDuMonde().getSimulationPasserFromList(p_nom).getEtatsPasserSimulation().get(i));
        }
        this.setCarteDuMonde(m_listeCartes.get(0));
    }
    
    public void deleteSimulationPasser(String p_nom) {
        this.getCarteDuMonde().deleteSimulationFromList(p_nom);
        this.setCarteDuMonde(Serializable.TransformToCarte(stackRetour.pop()));
    }
    
    public void modifieNomSimulationPasser(String ancienNom, String nouveauNom) {
        this.getCarteDuMonde().modifierSimlulationInList(ancienNom ,nouveauNom);
    }

    public void aide() {
    }

    public void zoomInScroll() {
        // Repositionnement en fonctione de la position de la souris
        int newPosX = -(m_mousePosition.x) + (this.getCarteDuMonde().getPanelSize().width / 2);
        int newPosY = -(m_mousePosition.y) + (this.getCarteDuMonde().getPanelSize().height / 2);
        this.getCarteDuMonde().setNewTranslate(new Dimension(newPosX, newPosY));
        this.getCarteDuMonde().setScale((float) (this.getCarteDuMonde().getScale() + 0.05));
    }

    public void zoomOutScroll() {
        // Repositionnement en fonctione de la position de la souris
        int newPosX = -(m_mousePosition.x) + (this.getCarteDuMonde().getPanelSize().width / 2);
        int newPosY = -(m_mousePosition.y) + (this.getCarteDuMonde().getPanelSize().height / 2);
        this.getCarteDuMonde().setNewTranslate(new Dimension(newPosX, newPosY));
        this.getCarteDuMonde().setScale((float) (this.getCarteDuMonde().getScale() - 0.1));
    }

    public void zoomIn() {
        this.getCarteDuMonde().setScale((float) (this.getCarteDuMonde().getScale() + 0.1));
    }

    public void zoomOut() {
        this.getCarteDuMonde().setScale((float) (this.getCarteDuMonde().getScale() - 0.1));
    }

    public void setModeZoom(boolean p_modeZoom) {
        m_modeZoom = p_modeZoom;
    }

    public boolean getModeZoom() {
        return m_modeZoom;
    }

    public void setShowAllStats(boolean p_showAllStats) {
        m_showAllStats = p_showAllStats;
    }

    public boolean getShowAllStats() {
        return m_showAllStats;
    }


 
 
    public boolean IsMoveValid()
    {
        m_validMove = true;
        
        //Vérification si u pays est sur un autre
        for (int i = 0; i < m_carte.getListePays().size(); i++)
        {
            for (int j = 0; j < m_carte.getListePays().size(); j++)
            {
                for (Point points: m_carte.getListePays().get(j).getForme().getListeSommet())
                {
                    if (j != i && m_carte.getListePays().get(i).getForme().getPolygonForme().contains(points))
                    {
                        m_validMove = false;
                        TalkToUser("Cette opération est invalide, SVP recommencer - Un pays est sur un autre!"); 
                    }
                }
            }
        }
        
        //Vérification si une région est sur une autre
        for (Pays pays : m_carte.getListePays())
        {
            
            for (int i = 0; i < pays.getListeRegion().size(); i++)
            {
                for (int j = 0; j < pays.getListeRegion().size(); j++)
                {
                    for (Point points: pays.getListeRegion().get(j).getForme().getListeSommet())
                    {
                        if (j != i && pays.getListeRegion().get(i).getForme().getPolygonForme().contains(points))
                        {
                            m_validMove = false;
                        TalkToUser("Cette opération est invalide, SVP recommencer - Une région est sur une autre !"); 
                            
                        }
                    }
                }
            }
        }
        
        //Vérification si une région sort d'un pays
        
        for (Pays pays : m_carte.getListePays())
        {
            for (Region regions : pays.getListeRegion())
            {
                for (Point points : regions.getForme().getListeSommet())
                {
                    if (!pays.getForme().getPolygonForme().contains(points))
                    {
                        m_validMove = false;
                        TalkToUser("Cette opération est invalide, SVP recommencer - Une région sort de son pays !"); 
                    }
                }
            }
        }
        
        
        
        
        
        return m_validMove;
    }
    public void movePays(int p_posX, int p_posY) {
        for (Pays pays : m_carte.getListePays()) {
            if (pays.getForme().getPolygonForme().contains(m_mouseClicked)) {
                // Détecter si le clic est dans une région.
                boolean clickInRegion = false;
                for (Region regions : pays.getListeRegion()) {
                    if (regions.getForme().getPolygonForme().contains(m_mouseClicked)) {
                        clickInRegion = true;

                    }
                }
                if (!clickInRegion) {

                    // Vérification si nous sommes en mode déplacement ou redimension(Est-ce que le
                    // clic est dans la zone de redim?)
                    Polygon polyRedim = new Polygon();
                    int posXPolyRedim = pays.getPosition().x + pays.getForme().getPolygonForme().getBounds().width - 20;
                    int posYPolyRedim = pays.getPosition().y + pays.getForme().getPolygonForme().getBounds().height
                            - 20;
                    polyRedim.addPoint(posXPolyRedim, posYPolyRedim);
                    polyRedim.addPoint(posXPolyRedim + 20, posYPolyRedim);
                    polyRedim.addPoint(posXPolyRedim + 20, posYPolyRedim + 20);
                    polyRedim.addPoint(posXPolyRedim, posYPolyRedim + 20);
                    // Si nous sommes dans la boite de redim.
                    if (polyRedim.contains(m_mouseClicked)) {
                        redimPays(p_posX, p_posY, pays.getNom());

                    } else {
                        // Nous effectons le déplacement si nous ne sommes pas dans une région et que le
                        // redim nest pas cliqué
                        pays.getForme().setMoveEnCours(true);
                        if (m_oldPosIsSet == false) {
                            m_oldPosition = pays.getPosition();
                            m_oldPosIsSet = true;
                        }
                        // Positionnement du pays et déplacement des régions qui sont dans le pays
                        pays.setPosition(new Point(p_posX - (m_mouseClicked.x - m_oldPosition.x),
                                p_posY - (m_mouseClicked.y - m_oldPosition.y)));
                        for (Region regions : pays.getListeRegion()) {
                            regions.getForme().setMoveEnCours(true);
                            regions.setPositionRegion(new Point(p_posX - (m_mouseClicked.x - m_oldPosition.x) + regions.getRelativePositionRegion().x, p_posY - (m_mouseClicked.y - m_oldPosition.y) + regions.getRelativePositionRegion().y));
                        
                        }
                    }

                }
                clickInRegion = false;
            }
        }
    }

    public void redimPays(int p_posX, int p_posY, String p_nomPays) {
        Pays pays = m_carte.getPaysFromList(p_nomPays);
        TalkToUser("Vous redimensionnez le pays : " + p_nomPays);
        int newWidth = pays.getForme().getOriginalWidthBox() + m_mousePosition.x - m_mouseClicked.x;
        int newHeight = pays.getForme().getOriginalHeightBox() + m_mousePosition.y - m_mouseClicked.y;
        pays.getForme().setWidthSelectBox(newWidth);
        pays.getForme().setHeightSelectBox(newHeight);

    }

    public void redimRegion(int p_posX, int p_posY, Region p_region) {
        TalkToUser("Vous redimensionnez la région : " + p_region.getNom());
        int newWidth = p_region.getForme().getOriginalWidthBox() + m_mousePosition.x - m_mouseClicked.x;
        int newHeight = p_region.getForme().getOriginalHeightBox() + m_mousePosition.y - m_mouseClicked.y;
        p_region.getForme().setWidthSelectBox(newWidth);
        p_region.getForme().setHeightSelectBox(newHeight);

    }

    public void RecalculPolygonPays(int p_posX, int p_posY) {
        for (Pays pays : m_carte.getListePays()) {
            if (pays.getForme().getPolygonForme().contains(m_mouseClicked)) {
                boolean clickInRegion = false;
                for (Region regions : pays.getListeRegion()) {
                    if (regions.getForme().getPolygonForme().contains(m_mouseClicked)) {
                        clickInRegion = true;
                    }
                }
                if (!clickInRegion) {
                    // Vérification si nous sommes en mode déplacement ou redimension(Est-ce que le
                    // clic est dans la zone de redim?)
                    Polygon polyRedim = new Polygon();
                    int posXPolyRedim = pays.getPosition().x + pays.getForme().getPolygonForme().getBounds().width - 20;
                    int posYPolyRedim = pays.getPosition().y + pays.getForme().getPolygonForme().getBounds().height
                            - 20;
                    polyRedim.addPoint(posXPolyRedim, posYPolyRedim);
                    polyRedim.addPoint(posXPolyRedim + 20, posYPolyRedim);
                    polyRedim.addPoint(posXPolyRedim + 20, posYPolyRedim + 20);
                    polyRedim.addPoint(posXPolyRedim, posYPolyRedim + 20);
                    if (polyRedim.contains(m_mouseClicked)) {
                        // redimPays(p_posX, p_posY, pays.getNom());METTRE ICI LE RECALCUL POLYGONE
                        // APRES REDIM
                        m_listePoints = new ArrayList<>();
                        m_listePoints.add(new Point(pays.getPosition().x, pays.getPosition().y));
                        m_listePoints.add(new Point(pays.getPosition().x + pays.getForme().getWidthSelectBox(),
                                pays.getPosition().y));
                        m_listePoints.add(new Point(pays.getPosition().x + pays.getForme().getWidthSelectBox(),
                                pays.getPosition().y + pays.getForme().getHeightSelectBox()));
                        m_listePoints.add(new Point(pays.getPosition().x,
                                pays.getPosition().y + pays.getForme().getHeightSelectBox()));

                        pays.getForme().setPolygonForme(m_listePoints);
                        m_listePoints = new ArrayList<>();

                        pays.getForme().setSelectBox();
                    } else {                //DÉPLACEMENT
                        int posXChange = pays.getPosition().x - m_oldPosition.x;
                        int posYChange = pays.getPosition().y - m_oldPosition.y;

                        m_listePoints = new ArrayList<>();
                        for (Point points : pays.getForme().getListeSommet()) {
                            m_listePoints.add(new Point(points.x + posXChange, points.y + posYChange));
                        }
                        pays.getForme().setPolygonForme(m_listePoints);
                        m_listePoints = new ArrayList<>();
                        m_oldPosIsSet = false;

                        // Recalcul des polygone de régions car il se déplace avec le pays.
                        for (Region regions : pays.getListeRegion()) {
                            m_listePoints = new ArrayList<>();
                            for (Point points : regions.getForme().getListeSommet()) {
                                m_listePoints.add(new Point(points.x + posXChange, points.y + posYChange));
                            }
                            regions.getForme().setPolygonForme(m_listePoints);
                            m_listePoints = new ArrayList<>();

                        }
                    }
                }
            }
        }
    }

    public void moveRegion(int p_posX, int p_posY) {
        for (Pays pays : m_carte.getListePays()) {
            if (pays.getForme().getPolygonForme().contains(m_mouseClicked)) {
                for (Region regions : pays.getListeRegion()) {
                    // Vérification du clic dans la zone de REDIM...si nous y sommes on redim sinon
                    // on activie le booléean de deplacement
                    Polygon polyRedimRegion = new Polygon();
                    int posXPolyRedim = regions.getPositionRegion().x
                            + regions.getForme().getPolygonForme().getBounds().width - 20;
                    int posYPolyRedim = regions.getPositionRegion().y
                            + regions.getForme().getPolygonForme().getBounds().height - 20;
                    polyRedimRegion.addPoint(posXPolyRedim, posYPolyRedim);
                    polyRedimRegion.addPoint(posXPolyRedim + 20, posYPolyRedim);
                    polyRedimRegion.addPoint(posXPolyRedim + 20, posYPolyRedim + 20);
                    polyRedimRegion.addPoint(posXPolyRedim, posYPolyRedim + 20);
                    // Si nous sommes dans la boite de redim.
                    if (polyRedimRegion.contains(m_mouseClicked)) {
                        redimRegion(p_posX, p_posY, regions);

                    } else // Sinon on déplace plutot que redim.
                    {
                        regions.getForme().setMoveEnCours(true);
                        if (regions.getForme().getPolygonForme().contains(m_mouseClicked)) {
                            regions.setPositionRegion(new Point(
                                    p_posX - (m_mouseClicked.x - regions.getForme().getListeSommet().get(0).x),
                                    p_posY - (m_mouseClicked.y - regions.getForme().getListeSommet().get(0).y)));
                            regions.setRelativePositionRegion(new Point(
                                    p_posX - pays.getPosition().x
                                            - (m_mouseClicked.x - regions.getForme().getListeSommet().get(0).x),
                                    p_posY - pays.getPosition().y
                                            - (m_mouseClicked.y - regions.getForme().getListeSommet().get(0).y)));
                        }
                    }

                }
            }
        }
    }

    public void RecalculPolygonRegion(int p_posX, int p_posY) {
        for (Pays pays : m_carte.getListePays()) {
            if (pays.getForme().getPolygonForme().contains(m_mouseClicked)) {
                for (Region regions : pays.getListeRegion()) {
                    if (regions.getForme().getPolygonForme().contains(m_mouseClicked)) {
                        Polygon polyRedim = new Polygon();
                        int posXPolyRedim = regions.getPositionRegion().x
                                + regions.getForme().getPolygonForme().getBounds().width - 20;
                        int posYPolyRedim = regions.getPositionRegion().y
                                + regions.getForme().getPolygonForme().getBounds().height - 20;
                        polyRedim.addPoint(posXPolyRedim, posYPolyRedim);
                        polyRedim.addPoint(posXPolyRedim + 20, posYPolyRedim);
                        polyRedim.addPoint(posXPolyRedim + 20, posYPolyRedim + 20);
                        polyRedim.addPoint(posXPolyRedim, posYPolyRedim + 20);
                        if (polyRedim.contains(m_mouseClicked)) {
                            // redimPays(p_posX, p_posY, pays.getNom());METTRE ICI LE RECALCUL POLYGONE
                            // APRES REDIM
                            m_listePoints = new ArrayList<>();
                            m_listePoints.add(new Point(regions.getPositionRegion().x, regions.getPositionRegion().y));
                            m_listePoints.add(
                                    new Point(regions.getPositionRegion().x + regions.getForme().getWidthSelectBox(),
                                            regions.getPositionRegion().y));
                            m_listePoints.add(
                                    new Point(regions.getPositionRegion().x + regions.getForme().getWidthSelectBox(),
                                            regions.getPositionRegion().y + regions.getForme().getHeightSelectBox()));
                            m_listePoints.add(new Point(regions.getPositionRegion().x,
                                    regions.getPositionRegion().y + regions.getForme().getHeightSelectBox()));

                            regions.getForme().setPolygonForme(m_listePoints);
                            m_listePoints = new ArrayList<>();

                            regions.getForme().setSelectBox();
                        } else {
                            int posXChange = regions.getPositionRegion().x
                                    - regions.getForme().getListeSommet().get(0).x;
                            int posYChange = regions.getPositionRegion().y
                                    - regions.getForme().getListeSommet().get(0).y;
                            m_listePoints = new ArrayList<>();
                            for (Point points : regions.getForme().getListeSommet()) {
                                m_listePoints.add(new Point(points.x + posXChange, points.y + posYChange));
                            }
                            regions.getForme().setPolygonForme(m_listePoints);
                            m_listePoints = new ArrayList<>();
                        }

                    }
                }
            }
        }

    }

    static public void TalkToUser(String p_NewMessage) {
        if (p_NewMessage != m_msgToUser.get(4)) {
            CarteDrawer.cptTempsCourt = 0;
            m_msgToUser.add(p_NewMessage);
            m_msgToUser.remove(0);
        }

    }

    public void setMsgToUser() {

        m_msgToUser.add("Bonjour !");
        m_msgToUser.add("Bienvenu dans Simudémie !");
        m_msgToUser.add("Veuillez débuter en important une simulation ou en ajoutant un premier pays.");

    }

    public ArrayList<String> getMsgToUser() {
        return m_msgToUser;
    }

    // Méthode afin de revalider tous les polygone de la vue et êtr certain que tout
    // erst toujours bien enligné dans la GRID.
    //A REFAIRE POUR FONCTINNER AVEC IRRÉGULIERE
    public void updateAllPolygon() {
        for (Pays pays : m_carte.getListePays()) {
            
            if (pays.getForme().getFormeIrr())
            {
                for(Point points : pays.getForme().getListeSommet())
                    {
                        m_listePoints.add(points);
                    }
            }
            else
            {
                m_listePoints = new ArrayList<>();
                m_listePoints.add(new Point(pays.getPosition().x, pays.getPosition().y));
                m_listePoints
                        .add(new Point(pays.getPosition().x + pays.getForme().getWidthSelectBox(), pays.getPosition().y));
                m_listePoints.add(new Point(pays.getPosition().x + pays.getForme().getWidthSelectBox(),
                        pays.getPosition().y + pays.getForme().getHeightSelectBox()));
                m_listePoints
                        .add(new Point(pays.getPosition().x, pays.getPosition().y + pays.getForme().getHeightSelectBox()));
            }
            

            pays.getForme().setPolygonForme(m_listePoints);
            m_listePoints = new ArrayList<>();

            pays.getForme().setSelectBox();
            for (Region regions : pays.getListeRegion()) {
                m_listePoints = new ArrayList<>();
                
                if (regions.getForme().getFormeIrr())
                {
                    for(Point points : regions.getForme().getListeSommet())
                    {
                        m_listePoints.add(points);
                    }
                }
                else
                {
                    m_listePoints.add(new Point(regions.getPositionRegion().x, regions.getPositionRegion().y));
                    m_listePoints.add(new Point(regions.getPositionRegion().x + regions.getForme().getWidthSelectBox(),
                            regions.getPositionRegion().y));
                    m_listePoints.add(new Point(regions.getPositionRegion().x + regions.getForme().getWidthSelectBox(),
                            regions.getPositionRegion().y + regions.getForme().getHeightSelectBox()));
                    m_listePoints.add(new Point(regions.getPositionRegion().x,
                            regions.getPositionRegion().y + regions.getForme().getHeightSelectBox()));
                }
                   
                regions.getForme().setPolygonForme(m_listePoints);
                m_listePoints = new ArrayList<>();
                regions.getForme().setSelectBox();

            }
        }

    }
    
    public void setPanelCarte(int width, int height){
        this.getCarteDuMonde().setPanelSize(new Dimension(width, height));
    }
    
    public MaladieDto getDtoMaladieCourante()
    {
        return new MaladieDto(this.getCarteDuMonde().getMaladieCourante());
    }
    
    public CarteDuMondeDto getDtoCarteDuMonde()
    {
        return new CarteDuMondeDto(this.getCarteDuMonde());
    }
    
    public PaysDto getPaysdeCarteDto(String nom)
    {
        Pays paysSelectionner = this.getCarteDuMonde().getPaysFromList(nom);
        return new PaysDto(paysSelectionner);
    }
    
    public void modifierNomRegionDeCarte(String nomPays, String ancienNomRegion, String newNomRegion)
    {
        this.getCarteDuMonde().modifierNomRegion(nomPays, ancienNomRegion, newNomRegion);
    }

    public RegionDto getRegionDeCarteDto(String nomPays, String nomRegion)
    {
        Pays paysSelectionner = this.getCarteDuMonde().getPaysFromList(nomPays);
        return new RegionDto(paysSelectionner.getRegionFromList(nomRegion));
    }
   
    
    public MesureSanitaireDto getMesureDePaysDto(String nomPay, String nomMesure)
    {   
        Pays pays = this.getCarteDuMonde().getPaysFromList(nomPay);  
        return new MesureSanitaireDto(pays.getMesureFromList(nomMesure));
    }
    
    public MaladieDto getDtoMaladieSelectionner(String nom)
    {
        return new MaladieDto(this.getCarteDuMonde().getMaladieFromList(nom));
    }
    
    public VoiesDeLiaisonsDto getDtoVoie(String nom)
    {
        return new VoiesDeLiaisonsDto(this.getCarteDuMonde().getVoieFromList(nom));
    }
    
    public void modifierMesurePaysDeCarte(String nomPays, String nomMesure, String txtNomMesurePays, double p_seuil, double adhesion, double transmission, double reproduction)
    {
        saveEtatCourantToList();
        this.getCarteDuMonde().getPaysFromList(nomPays).modifierMesureInList(nomMesure, txtNomMesurePays, p_seuil,adhesion, transmission, reproduction);
        TalkToUser("Modificication de la mesure : " + nomMesure + ", Nom Pays : " + txtNomMesurePays + ", Seuil : " + p_seuil + ", Trans : " + transmission + ", reprod : " + reproduction );
    }
    
    public void resetScaleCarte()
    {
        this. getCarteDuMonde().setScale(1);
    }
    
    public void translateNewCarte(int x,int y)
    {
        this. getCarteDuMonde().setNewTranslate(new Dimension(x, y));
    }
    
    public void translateCarte(int x,int y)
    {
        this. getCarteDuMonde().setTranslate(new Dimension(x, y));
    }
    
    public void setMaladieDeCarte(String m_nom)
    {
        saveEtatCourantToList();
        this.getCarteDuMonde().setMaladieCourante(m_nom);
        TalkToUser("La maladie : " + m_nom + " à été activée !");
    }
    public void addMesureToListMesurePays(String nomPays, String nom, double p_seuil, double adhesion, double transmission, double reproduction)
    {      
        saveEtatCourantToList();
        this.getCarteDuMonde().getPaysFromList(nomPays).addMesureActive(nom, p_seuil,adhesion, transmission, reproduction);
        TalkToUser("AJout de la mesure : " + nom + ", Nom Pays : " + nomPays + ", Seuil : " + p_seuil + ", Trans : " + transmission + ", reprod : " + reproduction );
        
    }
    
    public void deleteMesureToListMesurePays(String nomPays, String nom)
    {
        saveEtatCourantToList();
        this.getCarteDuMonde().getPaysFromList(nomPays).deleteMesureFromList(nom);
        TalkToUser("La mesure : " + nom + "du pays : " + nomPays + "à été ajoutée.");
    }
    
       
    public void deleteRegion(String nomPays, String nomRegion)
    {
        saveEtatCourantToList();
        this.getCarteDuMonde().getPaysFromList(nomPays).deleteRegionFromList(nomRegion);
        this.getCarteDuMonde().getPaysFromList(nomPays).calculPourcentageEquivalentRegion();
        this.getCarteDuMonde().getPaysFromList(nomPays).calculUpdateStatsRegion();
        TalkToUser("La région : " + nomRegion + "du pays : " + nomPays + "à été supprimée.");
    }
    
    public void modifierPourcentageRegion(String nomPays, String nomRegion, double pourcentage)
    {
        saveEtatCourantToList();
        this.getCarteDuMonde().getPaysFromList(nomPays).getRegionFromList(nomRegion).setPourcentPop(pourcentage);
        TalkToUser("La région : " + nomRegion + "du pays : " + nomPays + " à un nouveau % de population : " + pourcentage);
    }
    
    public void updateStatsPays(String nomPays)
    {
        saveEtatCourantToList();
        getCarteDuMonde().getPaysFromList(nomPays).calculUpdateStatsRegion();
    }
    
    public SimulationPasserDto getDtoSimulationSelectionner(String nom)
    {
        return (new SimulationPasserDto(this.getCarteDuMonde().getSimulationPasserFromList(nom)));
    }
    
    public void reinitialiserArrayStatsCarte()
    {
        getCarteDuMonde().reinitialiserArrayStats();
    }
    
    public void reinitialiserArrayStatsPaysDeCarte()
    {
        getCarteDuMonde().reinitialiserArrayStatsPays();
    }
    
    public void setMoveEnCoursFalse()
    {
        for (Pays pays:m_carte.getListePays())
        {
            pays.getForme().setMoveEnCours(false);
            for (Region regions:pays.getListeRegion())
            {
                regions.getForme().setMoveEnCours(false);
            }
        }
    }
    public void setPaysOverToNull()
    {
        m_payshover = null;
    }

    public String getPaysOver()
    {
        
        if(m_payshover != null)
        {
            return m_payshover.getNom();
        }
        else
        {
          return "Aucun";  
        }
        
    }
    
     public void setPopInitTotal() {
        m_carte.setPopInitTotal();
    }
        
}
