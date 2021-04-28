/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.domain.entites;

import ca.ulaval.glo2004.domain.SimudemieController;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;
import ca.ulaval.glo2004.helpers.StatistiqueHelper;

import java.io.Serializable;

/**
 *
 * @author Monique
 */
public class Pays implements Serializable {

    private int m_taillePop;
    private int m_nbSaine;
    private int m_nbInfecter;
    private int m_nbGueris;
    private int m_nbDecede;
    private double m_tauxTransmissionRegion;
    private Color m_couleur;
    private String m_nom;
    private Forme m_forme;
    private Point m_positionAjouter;
    private ArrayList<Region> m_listeRegion = new ArrayList<Region>();
    private ArrayList<MesureSanitaire> m_listeMesureActive = new ArrayList<MesureSanitaire>();
    
    private ArrayList<Integer> statsMalade = new ArrayList<>();
    private ArrayList<Integer> statsGueri = new ArrayList<>();
    private ArrayList<Integer> statsDeces = new ArrayList<>();
    private ArrayList<Integer> statsPop = new ArrayList<>();
    private ArrayList<String> statsMesureActiver = new ArrayList<>();

    public Pays() {
    }

    // initialiser les int a 0 par defaut ?
    public Pays(String p_nom, Point p_position, ArrayList<Point> p_points, Color p_Color, int p_taille,
            int p_nbInfecter, double p_tauxTransmission) // J'ai enlever forme pour le point de position la forme nest pas accessible
                              // qwuand on appel ajouter pays NOK
    {
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
        m_positionAjouter = new Point(minX, minY);
        
        
        m_taillePop = p_taille;
        m_nbInfecter = p_nbInfecter;
        m_nbSaine = p_taille - p_nbInfecter;
        m_tauxTransmissionRegion = p_tauxTransmission /250;
        m_nbDecede = 0;
        m_nbGueris = 0;
        boolean formeIrr = false;
        if (p_points.size() != 4 || p_points.get(0).y != p_points.get(1).y)
        {
            formeIrr = true;
        }
        m_forme = new Forme(p_points, p_Color, formeIrr);
        SimudemieController.TalkToUser("Nouveau Pays ajouté: " + m_nom + " Position : " + m_positionAjouter);
        

    }
    
    

    public Integer getTaillePop() {
        return m_taillePop;
    }

    public void setTaillePop(int p_taillePop) {
        m_taillePop = p_taillePop;
    }
    
    public Double getTauxTransmissionRegion() {
        return m_tauxTransmissionRegion;
    }

    public void setTauxTransmissionRegion(double p_taux) {
        m_tauxTransmissionRegion = p_taux;
    }
    

    public Integer getNbInfecter() {
        return m_nbInfecter;
    }

    public void setNbInfecter(int p_nbInfecter) {
        if (p_nbInfecter >= getTaillePop())
        {
            m_nbInfecter = getTaillePop();
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
        return m_nbDecede;
    }

    public Integer getNbSaine() {
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

    public void setNbDecede(int p_nbDecede) {
        m_nbDecede = p_nbDecede;
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

    public Point getPosition() {
        return m_positionAjouter;
    }

    public void setPosition(Point p_point) {
        m_positionAjouter = p_point;
    }

    public void setForme(ArrayList<Point> p_forme)// MODIF NOK - On recois le vecteur de point et on créer la forme dans
                                                  // la clase pays...on na pas acces a forme dans le controleur mais
                                                  // jsute au vecteur de poit.
    {

    }

    public ArrayList<Region> getListeRegion() {
        return m_listeRegion;
    }

    public void addRegion(String p_nom, Point p_position, ArrayList<Point> p_points, Color p_couleur) {
        if (!regionExiste(p_nom)) {
            Point relPoint = new Point(p_position.x - m_positionAjouter.x, p_position.y - m_positionAjouter.y);
            this.m_listeRegion.add(new Region(p_nom, p_position, relPoint, p_points, p_couleur));
            calculPourcentageEquivalentRegion();

        } else {
            SimudemieController.TalkToUser("Cette Region existe deja");
        }
    }

    // Méthode existante seulement pour faciliter les tests NE PAS UTILISER DANS LE
    // CODE
    public void addRegionForTest(Region region) {
        this.m_listeRegion.add(region);
    }

    public void calculPourcentageEquivalentRegion() {
        int nbRegionDuPays = (m_listeRegion.size());
        double pourcentageParRegion = (100 / nbRegionDuPays);
        for (Region region : getListeRegion()) {
            region.setPourcentPop(pourcentageParRegion);
        }
        calculUpdateStatsRegion();

    }

    public boolean regionExiste(String nomRegion) {
        for (Region i : getListeRegion()) {
            if (i.getNom().equals(nomRegion)) {
                return true;
            }
        }
        return false;
    }

    public void deleteRegionFromList(String nomRegion) {
        this.m_listeRegion.remove(getRegionFromList(nomRegion));
    }

    public void modifierRegionInList(String ancienNom, String nouveauNom, int pourcentPop) {
        getRegionFromList(ancienNom).setNom(nouveauNom);
    }

    public Region getRegionFromList(String p_nomRegion) {
        for (Region i : getListeRegion()) {
            if (i.getNom().equals(p_nomRegion)) {
                return i;
            }
        }
        return null;
    }

    public ArrayList<MesureSanitaire> getListeMesureActive() {
        return m_listeMesureActive;
    }

    public boolean mesureExiste(String p_mesure) {
        for (MesureSanitaire i : getListeMesureActive()) {
            if (i.getNom().equals(p_mesure)) {
                return true;
            }
        }
        return false;
    }

    public void addMesureActive(String p_nom, double p_seuil, double adhesion, double transmission, double reproduction) {
        if (!mesureExiste(p_nom)) {
            m_listeMesureActive.add(new MesureSanitaire(p_nom, p_seuil, adhesion, transmission, reproduction));
            SimudemieController.TalkToUser("Mesure ajouter avec succes");
        } else {
            SimudemieController.TalkToUser("Cette mesure existe deja");
        }
    }

    public void deleteMesureFromList(String p_nomMesure) {
        getListeMesureActive().remove(getMesureFromList(p_nomMesure));
        SimudemieController.TalkToUser("Mesure Supprimer avec Succes");
    }

    public void modifierMesureInList(String ancienNom, String nouveauNom, double p_seuil, double adhesion, double transmission, double reproduction) {
        getMesureFromList(ancienNom).setSeuil(p_seuil/100);
        getMesureFromList(ancienNom).setAdhesion(adhesion/100);
        getMesureFromList(ancienNom).setTransmission(transmission/100);
        getMesureFromList(ancienNom).setReproduction(reproduction/100);
        getMesureFromList(ancienNom).setNom(nouveauNom);
        SimudemieController.TalkToUser("Mesure modifier avec succes");
    }

    public MesureSanitaire getMesureFromList(String p_nom) {
        for (MesureSanitaire i : getListeMesureActive()) {
            if (i.getNom().equals(p_nom)) {
                return i;
            }
        }
        return null;
    }

    public void modifierStats(int p_nbNouveauInfecte, int p_nbNouveauGueries, int p_nbNouveauDecedes, int p_nbSaine) {
        setNbDecede(p_nbNouveauDecedes);
        setNbGueris(p_nbNouveauGueries);
        setNbInfecter(p_nbNouveauInfecte);
        setNbSaine(p_nbSaine);
        setTaillePop(m_nbInfecter + m_nbSaine);
        statsMalade.add(getNbInfecter());
        statsPop.add(getTaillePop());
        statsDeces.add(getNbDecede());
        statsGueri.add(getNbSaine());
    }

    public void modifierNouveauDeces(int p_nbNouveauDecedes) {
        setNbDecede(m_nbDecede + p_nbNouveauDecedes);
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

    public void incrementePersonneSaine(int p_nbNouveauGueris) {
        setNbSaine(m_nbSaine + p_nbNouveauGueris);
    }

    public void decrementePersonneSaine(int p_nbNouveauInfecte) {
        setNbSaine(Math.max(0, m_nbSaine - p_nbNouveauInfecte));
    }

    public void calculUpdateStatsRegion() {
        for (Region region : getListeRegion()) {
            region.setTaillePop((int) ((region.getPourcentPop() * (double) m_taillePop) / 100));

            // region.setNbInfecter((int)(( region.getPourcentPop() * (double)
            // m_nbInfecter)/100));
            // region.setNbSaine(region.getTaillePop() - region.getNbInfecter());
        }
        repartirInfecterParRegion(m_nbInfecter);

    }

    public String getStatPaysFormate() {
        String information;
        information = "PAYS : " + m_nom + " --- > Nb personne Total : " + getTaillePop() + " Nb Santé " + getNbSaine()
                + " Nb infectés : " + getNbInfecter() + " Nb deces : " + getNbDecede() + "Nb gueris : " + getNbGueris();
        return information;
    }

    public void calculStatPaysSansRegionParJour(Maladie p_maladie, int nbSaine, int nbInfecte) {


        // Calcul des nouvelles infections

        if(chercherSiMesureActive()){
            calculerInfecterAvecMesureSanitaire(p_maladie, nbSaine, nbInfecte, m_listeMesureActive);
        }
        else{
                int nbNouvelleInfection = StatistiqueHelper.calculerNombreDePersonneInfectees(nbInfecte,
                m_tauxTransmissionRegion, p_maladie.getTauxReproduction());
                if (nbNouvelleInfection >= nbSaine) {
                    m_nbInfecter = nbInfecte + m_nbSaine;
                    m_nbSaine = 0;
                    m_taillePop = m_nbInfecter;
                } 
                else {
                        modifierNouveauInfecte(nbNouvelleInfection);
                        decrementePersonneSaine(nbNouvelleInfection);
                    }           
        }
            
        
        // Calcul des nouvelles personnes gueris
        int nbNouveauGueris = StatistiqueHelper.calculerNombrePersonneGueries(nbInfecte,
                p_maladie.getTauxGuerison());
        // Ajoute nb de guéris total et decrémente le nombre d'infecter car ils sont
        // guéris
        modifierNouveauGuerie(nbNouveauGueris);
        incrementePersonneSaine(nbNouveauGueris);
        // Calcul des nouvelles personnes dead

        int nbNouveauDeces = StatistiqueHelper.calculerNombePersonneDecedees(nbInfecte,
                p_maladie.getTauxMortalite());

        // Ajoute nb de déces total et decrémente le nombre d'infecter car ils sont dead
        modifierNouveauDeces(nbNouveauDeces);
        // Nouvelle population est les personnes saines + personne infecté
        modifierNouvelleTaillePopulation(nbNouveauDeces);
        
        Color nouvelleCouleurPays = calculerCouleur(getTaillePop(),getNbInfecter());
        setCouleur(nouvelleCouleurPays);
        getForme().setCouleur(nouvelleCouleurPays);
        
        // Ajuste nb de personne infecte et le nombre de personne saine
        statsMalade.add(getNbInfecter());
        statsPop.add(getTaillePop());
        statsDeces.add(getNbDecede());
        statsGueri.add(getNbSaine());

    }
    public boolean chercherSiMesureActive(){
        boolean estActive = false;
        for(MesureSanitaire mesure : m_listeMesureActive){
            if (mesure.getEstActive()){
                estActive = true;
            }
        }
        return estActive;
        
    }
    
    public void calculerInfecterAvecMesureSanitaire(Maladie p_maladie, int nbSaine, int nbInfecte, ArrayList<MesureSanitaire> listeMesure){
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
        double tauxTranmissionReduit = p_maladie.reductionTauxTransmission(m_tauxTransmissionRegion, totalReductionTranmission);
        
        
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
        nouveauInfecte += StatistiqueHelper.calculerNombreDePersonneInfectees(personneNeRespectantPasLaMesure, m_tauxTransmissionRegion, p_maladie.getTauxReproduction());
        
        //Calcul de la deuxieme loi binomial pour les personnes respectant les mesures
        nouveauInfecte += StatistiqueHelper.calculerNombreDePersonneInfectees(personneRespectantLesMesure, tauxTranmissionReduit, tauxReproductionReduit);
        
        if (nouveauInfecte >= nbSaine) {
            m_nbInfecter = m_nbInfecter + m_nbSaine;
            m_nbSaine = 0;
            m_taillePop = m_nbInfecter;
        } 
        else {
            modifierNouveauInfecte(nouveauInfecte);
            decrementePersonneSaine(nouveauInfecte);
        }
        Color nouvelleCouleurPays = calculerCouleur(getTaillePop(),getNbInfecter());
        setCouleur(nouvelleCouleurPays);
        getForme().setCouleur(nouvelleCouleurPays);
        
    }
    
    

    public void calculerInfectionDesVoyageur(Maladie p_maladie, Double transmission, Pays p_paysVoisin,VoiesDeLiaisons voiesDeLiaisons, int nbInfecte) {
        
        //Calcul pour pays sans régions
        if (p_paysVoisin.getTaillePop() != 0 && p_paysVoisin.getListeRegion().isEmpty()) {
            //Frontière est ouverte
            if (voiesDeLiaisons.getEstOuvert() || voiesDeLiaisons.getAUneMesure()==false){
                int voyageurInfecte = StatistiqueHelper.calculerNombreDePersonneInfectees(nbInfecte, voiesDeLiaisons.getTauxTransmission(), p_maladie.getTauxReproduction());
                if(voyageurInfecte >= p_paysVoisin.getNbSaine()){
                    int nbInfecter = p_paysVoisin.getNbInfecter() + p_paysVoisin.getNbSaine();
                    p_paysVoisin.setNbInfecter(nbInfecter);
                    p_paysVoisin.setNbSaine(0);
                    p_paysVoisin.setTaillePop(nbInfecter);
                }
                else{
                    p_paysVoisin.modifierNouveauInfecte(voyageurInfecte);
                    p_paysVoisin.decrementePersonneSaine(voyageurInfecte);
                }
                
            }
            else{
                int voyageurInfecte = calculerInfectionSurFrontiereFerme(voiesDeLiaisons, p_maladie, nbInfecte);
                if (voyageurInfecte >= p_paysVoisin.getNbSaine()) {
                    int nbInfecter = p_paysVoisin.getNbInfecter() + p_paysVoisin.getNbSaine();
                    p_paysVoisin.setNbInfecter(nbInfecter);
                    p_paysVoisin.setNbSaine(0);
                    p_paysVoisin.setTaillePop(nbInfecter);
            } 
                else {
                    p_paysVoisin.modifierNouveauInfecte(voyageurInfecte);
                    p_paysVoisin.decrementePersonneSaine(voyageurInfecte);
                }
                
            }

            Color nouvelleCouleurPays1 = calculerCouleur(p_paysVoisin.getTaillePop(), p_paysVoisin.getNbInfecter());
            p_paysVoisin.setCouleur(nouvelleCouleurPays1);
            p_paysVoisin.getForme().setCouleur(nouvelleCouleurPays1);
            
        //Calcul pays avec région
        } else {
            //Si la voie de liaison est ouverte, on traite les nouveaux infecté comme avant
            if(voiesDeLiaisons.getEstOuvert()){
                int nbNouvelleInfectionTotal = 0;
                int nbNouvellePersonneSaine = 0;
                int voyageurInfecte = StatistiqueHelper.calculerNombreDePersonneInfectees(nbInfecte, voiesDeLiaisons.getTauxTransmission(), p_maladie.getTauxReproduction());

                for (Region region : p_paysVoisin.getListeRegion()) {
                    int nbNouvelleInfection = StatistiqueHelper.calculerNombreDePersonneInfectees(voyageurInfecte,voiesDeLiaisons.getTauxTransmission(), p_maladie.getTauxReproduction());
                    region.modifierNouveauInfecte(nbNouvelleInfection);
                    region.decrementPersonneSaine(nbNouvelleInfection);

                    nbNouvelleInfectionTotal += region.getNbInfecter();
                    nbNouvellePersonneSaine += region.getNbSaine();

                    Color nouvelleCouleur = calculerCouleur(region.getTaillePop(), region.getNbInfecter());
                    region.setCouleur(nouvelleCouleur);
                    region.getForme().setCouleur(nouvelleCouleur);

                    }
                    p_paysVoisin.setNbInfecter(nbNouvelleInfectionTotal);
                    p_paysVoisin.setNbSaine(nbNouvellePersonneSaine);

                    Color nouvelleCouleurPays1 = calculerCouleur(p_paysVoisin.getTaillePop(), p_paysVoisin.getNbInfecter());
                    p_paysVoisin.setCouleur(nouvelleCouleurPays1);
                    p_paysVoisin.getForme().setCouleur(nouvelleCouleurPays1);
                }
            else{
                int infectionDesVoyageurs = calculerInfectionSurFrontiereFerme(voiesDeLiaisons, p_maladie, nbInfecte);
                repartirInfecteInterRegion(infectionDesVoyageurs);
                
                Color nouvelleCouleurPays1 = calculerCouleur(p_paysVoisin.getTaillePop(), p_paysVoisin.getNbInfecter());
                p_paysVoisin.setCouleur(nouvelleCouleurPays1);
                p_paysVoisin.getForme().setCouleur(nouvelleCouleurPays1);
            }
        }
    }
    public int calculerInfectionSurFrontiereFerme(VoiesDeLiaisons voiesDeLiaisons, Maladie maladie, int nbInfecte){
        int nouveauInfecte = 0;
        
        // Calcul du nombre d'infecte selon ceux qui ne respecte pas la mesure
        double tauxRespectantPasLaMesure = 1 - voiesDeLiaisons.getTauxAdhesion();
        int nbPersonneRespectantPasLaMesure = (int)Math.round(nbInfecte * tauxRespectantPasLaMesure);
        nouveauInfecte += StatistiqueHelper.calculerNombreDePersonneInfectees(nbPersonneRespectantPasLaMesure, m_tauxTransmissionRegion, maladie.getTauxReproduction());
                
        //Calcul du nombre d'infecte selon ceux qui respecte la mesure
        int nbPersonneRespectantMesure = (int)Math.round(nbInfecte * voiesDeLiaisons.getTauxTransmission());
        double tauxTransmissionInitial = voiesDeLiaisons.getTauxTransmission();
        double nouveauTauxTransmission = (1 - voiesDeLiaisons.getTauxAdhesion()) * tauxTransmissionInitial;
        
        nouveauInfecte += StatistiqueHelper.calculerNombreDePersonneInfectees(nbPersonneRespectantMesure, nouveauTauxTransmission, maladie.getTauxReproduction());
        return nouveauInfecte;
    }

    public void repartirInfecterParRegion(int p_nbInfecte) {
        for (Region region : getListeRegion()) {
            region.setNbInfecter(0);
        }

        int infecterPourRepartir = p_nbInfecte;
        while (infecterPourRepartir != 0) {
            for (Region region : getListeRegion()) {
                if (region.getNbInfecter() < region.getTaillePop()) {
                    if (region.getTaillePop() < infecterPourRepartir) {
                        Random random = new Random();
                        int infecterPourRegion = random.nextInt((region.getTaillePop()) + 1);
                        if (infecterPourRegion + region.getNbInfecter() > getTaillePop()) {
                            infecterPourRegion = region.getTaillePop() - region.getNbInfecter();
                        }
                        region.setNbInfecter(region.getNbInfecter() + infecterPourRegion);
                        infecterPourRepartir -= infecterPourRegion;
                    } else {
                        Random random = new Random();
                        int infecterPourRegion = random.nextInt((infecterPourRepartir) + 1);
                        if(infecterPourRegion + region.getNbInfecter() > getTaillePop()){
                            infecterPourRegion = region.getTaillePop() - region.getNbInfecter();
                        }
                        region.setNbInfecter(region.getNbInfecter() + infecterPourRegion);
                        
                        infecterPourRepartir -= infecterPourRegion;
                    }
                }

            }
        }
        for (Region region : getListeRegion()) {
  
            region.setNbSaine(region.getTaillePop() - region.getNbInfecter());
            if(region.getNbSaine() < 0 || region.getNbInfecter() < 0){
                repartirInfecterParRegion(p_nbInfecte);
            }
            Color nouvelleCouleurRegion = calculerCouleur(region.getTaillePop(),region.getNbInfecter());
            region.setCouleur(nouvelleCouleurRegion);
            region.getForme().setCouleur(nouvelleCouleurRegion);
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

    public double trouverPourcentageMalade(int p_taille, int p_nbInfecter) {
        return ((double) p_nbInfecter / (double) p_taille) * 100;
    }
    
    public void reinitialiserArrayStats()
    {
        statsMalade.clear();
        statsDeces.clear();
        statsPop.clear();
        statsGueri.clear();
        statsMesureActiver.clear();
        for (MesureSanitaire j : getListeMesureActive())
        { 
            j.setEstActive(false);

        }
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
    
    public void repartirInfecteInterRegion(int p_nbInfecte) {       
        int infecterPourRepartir = p_nbInfecte;
        while (infecterPourRepartir != 0) {
           
            for (Region region : getListeRegion()) {
                if (region.getNbInfecter() + p_nbInfecte < region.getTaillePop()) {
                    if (region.getTaillePop() < infecterPourRepartir) {
                        Random random = new Random();
                        int infecterPourRegion = random.nextInt((region.getTaillePop()) + 1);
                        if (infecterPourRegion + region.getNbInfecter() > getTaillePop()) {
                            infecterPourRegion = region.getTaillePop() - region.getNbInfecter();
                        }
                        region.setNbInfecter(region.getNbInfecter() + infecterPourRegion);
                        infecterPourRepartir -= infecterPourRegion;
                        if(infecterPourRepartir < 0){
                        infecterPourRepartir = 0;
            }
                    } else {
                        Random random = new Random();
                        int infecterPourRegion = random.nextInt((infecterPourRepartir) + 1);
                        region.setNbInfecter(region.getNbInfecter() + infecterPourRegion);
                        infecterPourRepartir -= infecterPourRegion;
                        if(infecterPourRepartir < 0){
                        infecterPourRepartir = 0;
            }
                    }
                }
                else{
                    region.setNbInfecter(region.getTaillePop());
                    region.setNbSaine(0);
                    infecterPourRepartir -= (region.getTaillePop()- p_nbInfecte);
                    if(infecterPourRepartir < 0){
                    infecterPourRepartir = 0;
                    }
                }

            }
        }
        for (Region region : getListeRegion()) {
            region.setNbSaine(region.getTaillePop() - region.getNbInfecter());
            
            Color nouvelleCouleurRegion = calculerCouleur(region.getTaillePop(),region.getNbInfecter());
            region.setCouleur(nouvelleCouleurRegion);
            region.getForme().setCouleur(nouvelleCouleurRegion);
        }
        

    }
    
    public ArrayList<String> getArrayMesureActive()
    {
        return statsMesureActiver;
    }
    
    public void setIfMesureActiver(int nbJourEcouler)
    {
        for (MesureSanitaire j : getListeMesureActive())
        {
            if (getNbInfecter()> (j.getSeuil()*getTaillePop()) &&j.getEstActive()==false)
            {   
                statsMesureActiver.add (j.getNom() + " est activer au jour " + (nbJourEcouler));
                j.setEstActive(true);
            }
            if (getNbInfecter()> (j.getSeuil()*getTaillePop()))
            {   
                j.setEstActive(true);
            }
            else if (getNbInfecter()< (j.getSeuil()*getTaillePop()) && j.getEstActive()==true)
            {
                statsMesureActiver.add (j.getNom() + " est desactiver au jour " + (nbJourEcouler));
                j.setEstActive(false);                    
            }
            else
            {
                j.setEstActive(false); 
            }
        } 
    }

}
