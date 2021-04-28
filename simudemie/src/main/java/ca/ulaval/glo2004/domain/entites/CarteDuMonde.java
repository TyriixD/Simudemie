/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.entites;

import ca.ulaval.glo2004.domain.SimudemieController;
import ca.ulaval.glo2004.helpers.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.io.Serializable;

/**
 *
 * @author Monique
 */
public class CarteDuMonde implements Serializable {

    private Maladie m_maladie;
    private Pays m_paySelectionner;
    private Region m_regionSelectionner;
    private Pays m_payHovered;
    private Region m_regionHovered;
    private ArrayList<Pays> m_listePays = new ArrayList<>();
    private ArrayList<VoiesDeLiaisons> m_listeVoies = new ArrayList<>();

    private ArrayList<Maladie> m_listeMaladie = new ArrayList<>();
    private ArrayList<SimulationPasser> m_simulationSauvegarder = new ArrayList<>();
    private float m_scale; // Conserve la valeur du zoom - NOK
    private Dimension m_translation; // Conserve la valeur du déplacement - NOK
    private Dimension m_panel_size_to_move;
    private Integer nbMalade;
    private Integer nbGuerie;
    private Integer nbPersonneDecede;
    private Integer population;
    private Integer populationInit;
    private Integer nbSain;
    
    private ArrayList<Integer> statsMalade = new ArrayList<>();
    private ArrayList<Integer> statsGueri = new ArrayList<>();
    private ArrayList<Integer> statsDeces = new ArrayList<>();
    private ArrayList<Integer> statsPop = new ArrayList<>();
    private ArrayList<String> statsFrontiereOuvert = new ArrayList<>();
            

    public CarteDuMonde() 
    {
        m_scale = 1;
        m_translation = new Dimension(0, 0);
        initialiserMaladieDeBase();
        populationInit = 0;
    }

    private void initialiserMaladieDeBase() {
        m_listeMaladie.add(new Maladie("Covid", 2, 1.1, 3));
        m_listeMaladie.add(new Maladie("Lepre", 100, 2, 0));
        m_listeMaladie.add(new Maladie("Peste noire", 1, 2, 5));
        m_maladie = getMaladieFromList("Peste noire");
    }
    
    public ArrayList<SimulationPasser> getListeSimulationPasser()
    {
        return m_simulationSauvegarder;
    }
     public void addSimulationToList(String p_nom, ArrayList<CarteDuMonde> listeEtat) {
        if (!simulationExiste(p_nom)) {
            m_simulationSauvegarder.add(new SimulationPasser(p_nom, listeEtat));
            SimudemieController.TalkToUser("Simulation ajouter avec succes");
        } else {
            SimudemieController.TalkToUser("Cette Simulation existe deja");
        }
    }

    public void deleteSimulationFromList(String p_nom) {
        getListeSimulationPasser().remove(getSimulationPasserFromList(p_nom));
        SimudemieController.TalkToUser("Simulation Supprimer avec Succes");
    }

    public void modifierSimlulationInList(String ancienNom, String nouveauNom) {
        getSimulationPasserFromList(ancienNom).setNom(nouveauNom);
    }    
        
    public boolean simulationExiste(String nomSimulation) {
        for (SimulationPasser i : getListeSimulationPasser()) {
            if (i.getNom().equals(nomSimulation)) {
                return true;
            }
        }
        return false;
    }
    
    public SimulationPasser getSimulationPasserFromList(String p_nom) {
        for (SimulationPasser i : getListeSimulationPasser()) {
            if (i.getNom().equals(p_nom)) {
                return i;
            }
        }
        return null;
    }
      
    public Maladie getMaladieCourante() {
        return m_maladie;
    }

    public void setMaladieCourante(String p_nomMaladie) {
        for (Maladie i : getListeMaladie()) {
            if (i.getNom().equals(getMaladieFromList(p_nomMaladie).getNom())) {
                m_maladie = getMaladieFromList(p_nomMaladie);
            }
        }
    }

    public Pays getPaysSelectionner() {
        return m_paySelectionner;
    }

    public void setPaySelectionner(Pays p_pays) {
        m_paySelectionner = p_pays;
    }

    public Region getRegionSelectionner() {
        return m_regionSelectionner;
    }

    public void setRegionSelectionner(Region p_region) {
        m_regionSelectionner = p_region;
    }

    public Pays getPaysHovered() {
        return m_payHovered;
    }

    public void setScale(float p_scale) {
        m_scale = p_scale;
        if (m_scale < 0.4) {
            m_scale = 0.4f;
            setNewTranslate(new Dimension(-getPanelSize().height / 2, -getPanelSize().width / 2 + 600));
        }
        if (m_scale > 5) {
            m_scale = 5f;
        }
    }

    public float getScale() {
        return m_scale;
    }

    public void setTranslate(Dimension p_translation) {
        m_translation = new Dimension(p_translation.width + m_translation.width,
                p_translation.height + m_translation.height);
        
                if (getScale() > 1.5)
                {
                    if (getTranslate().width > 2000) {
                        m_translation = new Dimension(2000, m_translation.height);
                    }
                    if (getTranslate().width < -3500) {
                        m_translation = new Dimension(-3500, m_translation.height);
                    }
                    if (m_translation.height > 500) {
                        m_translation = new Dimension(m_translation.width, 500);
                    }
                    if (m_translation.height < -3500) {
                        m_translation = new Dimension(m_translation.width, -3500);
                    }
        
                }
                
                if (getScale() <= 1.5 && getScale() > 0.8)
                {
                    if (getTranslate().width > 50) {
                        m_translation = new Dimension(50, m_translation.height);
                    }
                    if (getTranslate().width < -1800) {
                        m_translation = new Dimension(-1800, m_translation.height);
                    }
                    if (m_translation.height > 50) {
                        m_translation = new Dimension(m_translation.width, 50);
                    }
                    if (m_translation.height < -1400) {
                        m_translation = new Dimension(m_translation.width, -1400);
                    }
        
                }
                
                if (getScale() <= 0.8)
                {
                    if (getTranslate().width > -150) {
                        m_translation = new Dimension(-150, m_translation.height);
                    }
                    if (getTranslate().width < -600) {
                        m_translation = new Dimension(-600, m_translation.height);
                    }
                    if (m_translation.height > -150) {
                        m_translation = new Dimension(m_translation.width, -150);
                    }
                    if (m_translation.height < -600) {
                        m_translation = new Dimension(m_translation.width, -600);
                    }
        
                }
                
        
    }

    public void setNewTranslate(Dimension p_translation) {
        m_translation = new Dimension(p_translation.width, p_translation.height);
        
         if (getScale() > 1.5)
                {
                    if (getTranslate().width > 2000) {
                        m_translation = new Dimension(2000, m_translation.height);
                    }
                    if (getTranslate().width < -3500) {
                        m_translation = new Dimension(-3500, m_translation.height);
                    }
                    if (m_translation.height > 500) {
                        m_translation = new Dimension(m_translation.width, 500);
                    }
                    if (m_translation.height < -3500) {
                        m_translation = new Dimension(m_translation.width, -3500);
                    }
        
                }
                
                if (getScale() <= 1.5 && getScale() > 0.8)
                {
                    if (getTranslate().width > 50) {
                        m_translation = new Dimension(50, m_translation.height);
                    }
                    if (getTranslate().width < -1800) {
                        m_translation = new Dimension(-1800, m_translation.height);
                    }
                    if (m_translation.height > 50) {
                        m_translation = new Dimension(m_translation.width, 50);
                    }
                    if (m_translation.height < -1400) {
                        m_translation = new Dimension(m_translation.width, -1400);
                    }
        
                }
                
                if (getScale() <= 0.8)
                {
                    if (getTranslate().width > -150) {
                        m_translation = new Dimension(-150, m_translation.height);
                    }
                    if (getTranslate().width < -600) {
                        m_translation = new Dimension(-600, m_translation.height);
                    }
                    if (m_translation.height > -150) {
                        m_translation = new Dimension(m_translation.width, -150);
                    }
                    if (m_translation.height < -600) {
                        m_translation = new Dimension(m_translation.width, -600);
                    }
        
                }

    }

    public Dimension getTranslate() {
        return m_translation;
    }

    public Dimension getPanelSize() {
        return m_panel_size_to_move;
    }

    public void setPanelSize(Dimension p_panel_size) {
        m_panel_size_to_move = p_panel_size;
    }

    public void setPayshovered(Pays p_pays) {
        m_payHovered = p_pays;
    }

    public Region getRegionHovered() {
        return m_regionHovered;
    }

    public void setRegionHovered(Region p_region) {
        m_regionHovered = p_region;
    }

    public ArrayList<VoiesDeLiaisons> getListeVoies() {
        return m_listeVoies;
    }

    public boolean voieExiste(Pays p_pays1, Pays p_pays2, String p_type) {
        for (VoiesDeLiaisons i : getListeVoies()) {
            if (i.getPays1().getNom().equals(p_pays1.getNom()) && i.getPays2().getNom().equals(p_pays2.getNom())
                    && i.getType().equals(p_type)
                    || i.getPays2().getNom().equals(p_pays1.getNom()) && i.getPays1().getNom().equals(p_pays2.getNom())
                            && i.getType().equals(p_type)) {
                SimudemieController.TalkToUser("Cette voie existe");
                return true;
            }
        }
        SimudemieController.TalkToUser("Cette voie n'existe pas");
        return false;
    }

    public void addVoieToList(Pays p_pays1, Pays p_pays2, double p_seuil, double p_transmission, double p_adhesion, String p_type, boolean aMesure) 
    {
        if (!voieExiste(p_pays1, p_pays2, p_type)) {
            VoiesDeLiaisons nouveauVoie = new VoiesDeLiaisons(p_pays1, p_pays2, p_seuil, p_transmission, p_adhesion, p_type, aMesure);         
            m_listeVoies.add(nouveauVoie);
            SimudemieController.TalkToUser("Nouveau Voie de liaison " + nouveauVoie.getType() + " ajouter entre  : "
                    + nouveauVoie.getPays1().getNom() + " et " + nouveauVoie.getPays2().getNom());
            setOuvertureFrontieres(0);
        }
    }

    public void deleteVoieFromList(String nomFrontiere) {
        int pos = 0;
        VoiesDeLiaisons voieASupprimer = getVoieFromList(nomFrontiere);
        for (VoiesDeLiaisons i : getListeVoies()) {
            if (i.getNom().equals(voieASupprimer.getNom())) {
                pos = getListeVoies().indexOf(i);
            }
        }
        getListeVoies().remove(pos);
        SimudemieController.TalkToUser("Lien Supprimer");
    }

    public void modifierVoieInList(String nomFrontiere, double p_transmission, double p_adhesion, double p_seuil, boolean aMesure) {
        VoiesDeLiaisons voieAModifier = getVoieFromList(nomFrontiere);
        for (VoiesDeLiaisons i : getListeVoies()) {
            if (i.getNom().equals(voieAModifier.getNom())) {
                i.setSeuil(p_seuil/100);
                i.setTauxAdhesion(p_adhesion/100);
                i.setTauxTransmission(p_transmission/100);
                i.setAUneMesure(aMesure);
                SimudemieController.TalkToUser("Lien Modifier avec Succes");
                setOuvertureFrontieres(0);
            }
        }
    }

    public VoiesDeLiaisons getVoieFromList(String nomFrontiere) {
        for (VoiesDeLiaisons i : getListeVoies()) {
            if (i.getNom().equals(nomFrontiere)) {
                return i;
            }
        }
        return null;
    }

    public ArrayList<Maladie> getListeMaladie() {
        return m_listeMaladie;
    }

    public boolean maladieExiste(String nomMaladie) {
        for (Maladie i : getListeMaladie()) {
            if (i.getNom().equals(nomMaladie)) {
                return true;
            }
        }
        return false;
    }

    public void addMaladieToList(String nomMaladie, double p_mortalite, double p_reproduction, double p_guerison) {
        if (!maladieExiste(nomMaladie)) {
            m_listeMaladie
                    .add(new Maladie(nomMaladie, (double) p_mortalite, (double) p_reproduction, (double) p_guerison));
            SimudemieController.TalkToUser("Maladie ajouter avec succes");
        } else {
            SimudemieController.TalkToUser("Cette maladie existe deja");
        }
    }

    public void modifierMaladieInList(String ancienNom, String nouveauNom, double p_mortalite, double p_reproduction,double p_guerison) {
        getMaladieFromList(ancienNom).setTauxMortalite(p_mortalite);
        getMaladieFromList(ancienNom).setTauxReproduction(p_reproduction);
        getMaladieFromList(ancienNom).setTauxGuerison(p_guerison);
        getMaladieFromList(ancienNom).setNom(nouveauNom);

    }

    public void deleteMaladieFromList(String p_nom) {
        getListeMaladie().remove(getMaladieFromList(p_nom));
        SimudemieController.TalkToUser("Maladie Supprimer avec Succes");
    }

    public Maladie getMaladieFromList(String p_nom) {
        for (Maladie i : getListeMaladie()) {
            if (i.getNom().equals(p_nom)) {
                return i;
            }
        }
        return null;
    }

    public ArrayList<Pays> getListePays() {
        return m_listePays;
    }

    public boolean paysExiste(String nomPays) {
        for (Pays i : getListePays()) {
            if (i.getNom().equals(nomPays)) {
                return true;
            }
        }
        return false;
    }

    public boolean aucuneRegionExiste() {
        boolean empty = true;
        for (Pays i : getListePays()) {
            for (Region j : i.getListeRegion()) {
                if (!i.getListeRegion().isEmpty()) {
                    empty = false;
                }
            }
        }
        return empty;

    }

    public void addPaysToList(String p_nom, Point p_position, ArrayList<Point> p_points, Color p_couleur, int p_taille,
            int p_nbInfecter, double p_taux) 
    {
        if (!paysExiste(p_nom)) {
            Color nouvelleCouleur = calculerCouleur(p_taille, p_nbInfecter);
            this.m_listePays.add(new Pays(p_nom, p_position, p_points, nouvelleCouleur, p_taille, p_nbInfecter, p_taux));

        } else {
            SimudemieController.TalkToUser("Ce Pays existe deja");
        }
    }

    public void deletePaysFromList(String nomPays) {
        this.m_listePays.remove(getPaysFromList(nomPays));
    }

    public void modifierPaysInList(String ancienNom, String nouveauNom, int taillePop, int nbIfecter, double p_taux) {

        Color nouvelleCouleur = calculerCouleur(taillePop, nbIfecter);
        this.getPaysFromList(ancienNom).getForme().setCouleur(nouvelleCouleur);
        getPaysFromList(ancienNom).setCouleur(nouvelleCouleur);
        getPaysFromList(ancienNom).setTaillePop(taillePop);
        getPaysFromList(ancienNom).setNbInfecter(nbIfecter);
        getPaysFromList(ancienNom).setNbSaine(taillePop - nbIfecter);
        getPaysFromList(ancienNom).setTauxTransmissionRegion(p_taux/250);
        getPaysFromList(ancienNom).setNom(nouveauNom);
        if (!getPaysFromList(nouveauNom).getListeRegion().isEmpty()) {
            getPaysFromList(nouveauNom).calculUpdateStatsRegion();
        }

    }

    public Pays getPaysFromList(String p_nomPays) {
        for (Pays i : getListePays()) {
            if (i.getNom().equals(p_nomPays)) {
                return i;
            }
        }
        return null;
    }

    public void addPaysToListForTest(Pays pays) {
        m_listePays.add(pays);
    }

    public double trouverPourcentageMalade(int p_taille, int p_nbInfecter) {
        if (p_taille < 1)
        {
            return 100;
        }
        else
        { 
            return ((double) p_nbInfecter / (double) p_taille) * 100;
        }
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

    public void avanceDeUnJour(int nbJourEcouler) {
        
        // Calcul du nombre d'infection interne du pays
        for (Pays pays : m_listePays) {

            if (pays.getListeRegion().isEmpty()) {
                // Calcul des nouvells stats de personne infecte, morte, gueris et saine

                if (pays.getTaillePop() != 0) {
                    int nbInfecterPays = pays.getNbInfecter();              
                    int nbPersonneSaine = pays.getNbSaine();              

                    pays.calculStatPaysSansRegionParJour(m_maladie,nbPersonneSaine,nbInfecterPays);

                }

                Color nouvelleCouleur = calculerCouleur(pays.getTaillePop(), pays.getNbInfecter());
                pays.setCouleur(nouvelleCouleur);
                pays.getForme().setCouleur(nouvelleCouleur);

            }
            else {
                //Calcul tranmission inter-region
                if(pays.chercherSiMesureActive()){
                    int infecterInterRegion = 0;
                    //On additionne les effets de chaque mesure pour les différentes réduction
                    double totalReductionReproduction = 0;
                    for (MesureSanitaire mesure : pays.getListeMesureActive()){
                        if(mesure.getEstActive()){
                            totalReductionReproduction += mesure.getReproduction();
                        }
                    }
                    if (totalReductionReproduction > 1){
                        totalReductionReproduction = 1;
                    }
                    double totalReductionTranmission = 0;
                    for (MesureSanitaire mesure : pays.getListeMesureActive()){
                        if(mesure.getEstActive()){
                            totalReductionTranmission += mesure.getTransmission();
                        }       
                    }
                    if(totalReductionTranmission > 1){
                        totalReductionTranmission = 1;
                    }
                    //Calcul des nouveaux taux de reproduction et transmission
                    double tauxReproductionReduit = m_maladie.reductionTauxReproduction(m_maladie.getTauxReproduction(), totalReductionReproduction);
                    double tauxTranmissionReduit = m_maladie.reductionTauxTransmission(pays.getTauxTransmissionRegion(), totalReductionTranmission);


                    //On calcul le nombre de personne qui ne respecte pas les mesures 
                    int personneNeRespectantPasLaMesure = pays.getNbInfecter();
                    for (MesureSanitaire mesure : pays.getListeMesureActive()){
                        if (mesure.getEstActive()){
                             double tauxRespectantPasLaMesure = 1 - mesure.getAdhesion();
                             personneNeRespectantPasLaMesure = (int)Math.round(personneNeRespectantPasLaMesure * tauxRespectantPasLaMesure);
                        }
                    }
                    int personneRespectantLesMesure = pays.getNbInfecter() - personneNeRespectantPasLaMesure;
                    //Calcul de la premiere loi binomial pour les personnes ne respectant pas les mesures
                    
                    infecterInterRegion += StatistiqueHelper.calculerNombreDePersonneInfectees(personneNeRespectantPasLaMesure, pays.getTauxTransmissionRegion(),m_maladie.getTauxReproduction());

                    //Calcul de la deuxieme loi binomial pour les personnes respectant les mesures
                    infecterInterRegion += StatistiqueHelper.calculerNombreDePersonneInfectees(personneRespectantLesMesure, tauxTranmissionReduit, tauxReproductionReduit);
                    
                    pays.repartirInfecterParRegion(infecterInterRegion);
                }
                 
                else{
                    if(pays.getListeRegion().size() > 1){
                        int infecterInterRegion = StatistiqueHelper.calculerNombreDePersonneInfectees(pays.getNbInfecter(), pays.getTauxTransmissionRegion(), m_maladie.getTauxReproduction());
                        pays.repartirInfecteInterRegion(infecterInterRegion);
                    }
                }
                
                int nbNouvelleInfectionTotal = 0;
                int nbNouveauGuerisTotal = 0;
                int nbNouveauDecesTotal = 0;
                int nbNouveauPersonneSaine = 0;
                // pays.setNbInfecter(0);

                for (Region region : pays.getListeRegion()) {
                    // Calcul des nouvelles personnes dead
                    
                    int nbInfecterRegion = region.getNbInfecter();                  
                    int nbPersonneSaineRegion = region.getNbSaine();
                    
                    
                    region.calculerStatParRegionParJour(m_maladie,pays.getTauxTransmissionRegion(),nbInfecterRegion, nbPersonneSaineRegion, pays.getListeMesureActive() );

                    Color nouvelleCouleur = calculerCouleur(region.getTaillePop(), region.getNbInfecter());
                    region.setCouleur(nouvelleCouleur);
                    region.getForme().setCouleur(nouvelleCouleur);

                    nbNouvelleInfectionTotal += region.getNbInfecter();
                    nbNouveauGuerisTotal += region.getNbGueris();
                    nbNouveauDecesTotal += region.getNbDecede();
                    nbNouveauPersonneSaine += region.getNbSaine();

                }
                pays.modifierStats(nbNouvelleInfectionTotal, nbNouveauGuerisTotal, nbNouveauDecesTotal,
                        nbNouveauPersonneSaine);
                
                

                Color nouvelleCouleurPays = calculerCouleur(pays.getTaillePop(), pays.getNbInfecter());
                pays.setCouleur(nouvelleCouleurPays);
                pays.getForme().setCouleur(nouvelleCouleurPays);
            }
        }
        if (!getListeVoies().isEmpty()) {
            //Si la voie est ouverte
            for (VoiesDeLiaisons voiesDeLiaisons : getListeVoies()) {

                    Pays pays1 = voiesDeLiaisons.getPays1();
                    Pays pays2 = voiesDeLiaisons.getPays2();

                    pays1.calculerInfectionDesVoyageur(m_maladie, voiesDeLiaisons.getTauxTransmission(), pays2,voiesDeLiaisons,pays1.getNbInfecter());
                    pays2.calculerInfectionDesVoyageur(m_maladie, voiesDeLiaisons.getTauxTransmission(), pays1, voiesDeLiaisons,pays2.getNbInfecter());

                    Color nouvelleCouleurPays1 = calculerCouleur(pays1.getTaillePop(), pays1.getNbInfecter());
                    pays1.setCouleur(nouvelleCouleurPays1);
                    pays1.getForme().setCouleur(nouvelleCouleurPays1);

                    Color nouvelleCouleurPays2 = calculerCouleur(pays2.getTaillePop(), pays2.getNbInfecter());
                    pays2.setCouleur(nouvelleCouleurPays2);
                    pays2.getForme().setCouleur(nouvelleCouleurPays2);

            }
        }      
        setActivationMesures(nbJourEcouler);
        setOuvertureFrontieres(nbJourEcouler);
        MettreAJourArrayStats();
    }
    
    
    public void setOuvertureFrontieres(int nbJourEcouler)
    {
        for(VoiesDeLiaisons i : getListeVoies())
        {
            if (i.getAUneMesure() == true)
            {
                if ((i.getPays1().getNbInfecter()>(i.getSeuil()*i.getPays1().getTaillePop()) || i.getPays2().getNbInfecter()>(i.getSeuil()*i.getPays2().getTaillePop())) && i.getEstOuvert()==true)
                {
                    statsFrontiereOuvert.add (i.getNom() + " fermer a " + (nbJourEcouler));
                     i.setEstOuvert(false);
                }
                if (i.getPays1().getNbInfecter()>(i.getSeuil()*i.getPays1().getTaillePop()) || i.getPays2().getNbInfecter()>(i.getSeuil()*i.getPays2().getTaillePop()))
                {
                     i.setEstOuvert(false);
                }
                else if ((i.getPays1().getNbInfecter()<(i.getSeuil()*i.getPays1().getTaillePop()) || i.getPays2().getNbInfecter()<(i.getSeuil()*i.getPays2().getTaillePop())) && i.getEstOuvert()==false)
                {
                    statsFrontiereOuvert.add (i.getNom() + " ouvert a " + (nbJourEcouler));
                     i.setEstOuvert(true);
                }
                else
                {
                    i.setEstOuvert(true);                 
                }
            }
        }
    }
    
    public void setActivationMesures(int nbJourEcouler)
    {
        for(Pays i : getListePays())
        {
            i.setIfMesureActiver(nbJourEcouler);
        }
    }

    public int getNbInfecteSimulation() {
        int nbMaladeTotal = 0;
        for (Pays pays : m_listePays) {
            nbMaladeTotal += pays.getNbInfecter();
        }
        nbMalade = nbMaladeTotal;
        return nbMaladeTotal;
    }

    public int getNbGuerieSimulation() {
        int nbGuerieTotal = 0;
        for (Pays pays : m_listePays) {
            nbGuerieTotal += pays.getNbGueris();
        }
        nbGuerie = nbGuerieTotal;
        return nbGuerieTotal;
    }
    
    
    public Integer getNbSainsSimulation() {
        int nbSainTotal = 0;
        for (Pays pays : m_listePays) {
            nbSainTotal += pays.getNbSaine();
        }
        nbSain = nbSainTotal;
        return nbSainTotal;
    }
        

    public int getNbDecedeSimulation() {
        int nbPersonneDecedeTotal = 0;
        for (Pays pays : m_listePays) {
            nbPersonneDecedeTotal += pays.getNbDecede();
        }
        nbPersonneDecede = nbPersonneDecedeTotal;
        return nbPersonneDecedeTotal;
    }

    public void modifierNomRegion(String nomPays, String ancienNomRegion, String newNomRegion) {
        getPaysFromList(nomPays).getRegionFromList(ancienNomRegion).setNom(newNomRegion);
    }

    public void reinitialiserCarte() {
        m_listePays = new ArrayList<>();
        m_scale = 1;
        m_translation = new Dimension(0, 0);
        m_listeVoies = new ArrayList<>();
        m_listeMaladie = new ArrayList<>();
        initialiserMaladieDeBase();
        populationInit = 0;
        reinitialiserArrayStats();
    }

    public void setMaladieForTest(Maladie maladie) {
        m_maladie = maladie;
    }

    public int getPopulationTotale() {
        int popTotal = 0;
        for (Pays pays : m_listePays) {
            popTotal += pays.getTaillePop();
        }
        population = popTotal;
        return popTotal;
    }

    public int setPopInitTotal() {
            populationInit = getPopulationTotale();
            return populationInit;
    }

    public int getPopInitTotal(){
            return populationInit;
        
    
    }
    public void MettreAJourArrayStats()
    {
        statsMalade.add(nbMalade);
        statsGueri.add(nbSain);
        statsDeces.add(nbPersonneDecede);
        statsPop.add(population);
    }
    

    public  ArrayList<String> getArrayFrontiereOuvert ()
    {
        return statsFrontiereOuvert;
    }
            
    public ArrayList<Integer> getArrayMalade ()
    {
       return statsMalade;
    }
    
    public ArrayList<Integer> getArrayGuerie ()
    {
       return statsGueri;
    }
        
    public ArrayList<Integer> getArrayDeces ()
    {
       return statsDeces;
    }
            
    public ArrayList<Integer> getArrayPopTot ()
    {
       return statsPop;
    }
    public void reinitialiserArrayStats()
    {
        statsMalade.clear();
        statsDeces.clear();
        statsPop.clear();
        statsGueri.clear();
        statsFrontiereOuvert.clear();
    }
    public void reinitialiserArrayStatsPays()
    {
        for (Pays i : getListePays())
        {
            i.reinitialiserArrayStats();
        }
    }

}