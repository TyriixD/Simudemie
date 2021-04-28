/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domain.dto.*;
import ca.ulaval.glo2004.domain.SimudemieController;
import ca.ulaval.glo2004.domain.drawing.CarteDrawer;
import java.awt.BasicStroke;

import javax.swing.DefaultListModel;
import java.awt.Color;
import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.SpinnerNumberModel;

import javax.swing.JSlider;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.*;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 
import java.util.TimerTask;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author guillaume
 */
public class SimudemieGUI extends javax.swing.JFrame {
    public SimudemieController controller;
    int posXCorect;
    int posYCorect; // Correction des mouse_envent en fonction du déplacement / zoom de la carte.
    boolean execution = false;
    boolean changeSpinnerState = true;
    int compteurJour = 0;

    DocumentListener listenerMaladieText;
    ListSelectionListener listenerListMaladie;

    ListSelectionListener listenerListMesurePays;
    DocumentListener listenerTxtMesurePays;

    DocumentListener listenerTextModifPays;
    DocumentListener listenerTexModifRegion;
    ListSelectionListener listenerListModifPays;
    ListSelectionListener listenerTableModifRegion;

    DocumentListener listenerTextFront;
    ListSelectionListener listenerListFront;
    ChangeListener traversableListener;

    ListSelectionListener listenerListSimulationPasser;
    DocumentListener listenerTexModifSimulationPasser;
    ActionListener timerlistener;

    ChangeListener listenerSpeedSlider;
    MouseAdapter listenerforStats;
    
    String ancienneValeurSauver;
    int ancienneIndexSauver;
    int tableRowSelected;
    int tableColSelected;
    String nomRegionSauver;
    String nomMesureSauver;
    String nomMaladieSauver;
    boolean newEntryListePays = true;
    int delay;
    Timer timer = new Timer(delay, timerlistener);

    DefaultTableModel RegionTablemodel = new DefaultTableModel();
    DefaultListModel listeModelPays = new DefaultListModel();
    DefaultComboBoxModel defaultComboBoxModelPays = new DefaultComboBoxModel();
    DefaultComboBoxModel defaultComboBoxModelRegion = new DefaultComboBoxModel();
    DefaultListModel dlmListeMesurePays = new DefaultListModel();
    DefaultListModel dlmListeMaladie = new DefaultListModel();
    DefaultListModel dlmListeFrontiere = new DefaultListModel();
    DefaultListModel dlmListeMesureActive = new DefaultListModel();
    DefaultListModel dlmListefrontiereOuvert = new DefaultListModel();
    DefaultListModel listeModelSimulationPasser = new DefaultListModel();
    boolean updatePaysListenersShouldBeTriggered = false;
    boolean updateRegionsListenersShouldBeTriggered = false;
    boolean updateMesurePaysListenersShouldBeTriggered = false;
    boolean updateMaladieListenersShouldBeTriggered = false;
    boolean updateFrontiereListenersShouldBeTriggered = false;
    boolean updateSimlationPasserListenersShouldBeTriggered = false;
    
    boolean ctrlDown = false;
    
    boolean graphePresent;    
    String nomPaysOver = "Aucun";
    //pour le graphe de stats mondiale
    XYSeries nbInfecter;
    XYSeries nbGueris;
    XYSeries nbDeces;
    XYSeries taillePop;
    ChartPanel chartPanelCarte;
    
    
    XYSeries nbInfecterPays;
    XYSeries nbGuerisPays;
    XYSeries nbDecesPays;
    XYSeries taillePopPays;
    ChartPanel chartPanelPays;        
    /**
     * Creates new form SimudemieGUI
     */

    public SimudemieGUI() {
        initComponents();

        java.util.Timer timerFps = new java.util.Timer();

        TimerTask task = new TimerTask(){
            public void run(){
                CarteDrawer.cptTemps += 2;
                CarteDrawer.cptTempsCourt++;
                
                drawingPanel.repaint();
                talkToUser1.repaint();
                repaint();
                    
            }
        };
        timerFps.scheduleAtFixedRate(task, 0, 50); //1000ms = 1sec
//        
//        
//         java.util.Timer timerFps2 = new java.util.Timer();
//
//        TimerTask task2 = new TimerTask(){
//            public void run(){
//                
//                
//                    
//            }
//        };
//        timerFps2.scheduleAtFixedRate(task2, 0, 5); //1000ms = 1sec
        
        panelMesuresSan.setVisible(false);//j arrive pas a le supprimer
        
        updatePanelVisibility(false, false, false, false , false, false, false, false, false, false);


        controller = new SimudemieController();
        
        //Code pour le keylistener
        drawingPanel.requestFocus();
        drawingPanel.addKeyListener(new MKeyListener());

        btnStop.setVisible(false);
        btnPauseSim.setVisible(false);

        duree.setModel(new SpinnerNumberModel(0, 0, 2000, 1));
        btnEnregistrerSimulation.setVisible(false);
        btnNaviguerSimulation.setVisible(false);

        duree.setValue(300);

        boolean modeAddRegion = false;
        controller.setPanelCarte(drawingPanel.getWidth(), drawingPanel.getHeight());
        jToggleDeplacement.setSelected(false);
        delay = 1000;
        SliderJourPasser.setMinimum(0);
        SliderJourPasser.setEnabled(false);

        SetUpMaladieCourant();

        // pour la section de modifier region
        RegionTablemodel = (DefaultTableModel) tableModifRegion.getModel();
        RegionTablemodel.setRowCount(0);
        if (!controller.getDtoCarteDuMonde().m_listePays.isEmpty()) {
            for (RegionDto n : controller.getPaysdeCarteDto(comboPaysModifRegion.getSelectedItem().toString()).m_listeRegion) {
                Object[] newRow = { n.m_nom, String.valueOf(n.m_pourcentagePop) };
                RegionTablemodel.addRow(newRow);
            }
        }

        tableModifRegion.setModel(RegionTablemodel);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableModifRegion.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableModifRegion.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);


        dlmListeMesurePays = new DefaultListModel();
        listeModifMesurePays.setModel(dlmListeMesurePays);

        dlmListeFrontiere = new DefaultListModel();
        listeModifFrontieres.setModel(dlmListeFrontiere);
        listeModelPays = new DefaultListModel();
        listePays.setModel(listeModelPays);
        dlmListeMaladie = new DefaultListModel();
        listeMaladies.setModel(dlmListeMaladie);
        listeModelSimulationPasser = new DefaultListModel();
        listeSimulations.setModel(listeModelSimulationPasser);
        dlmListeMesureActive = new DefaultListModel();
        listAfficherMesureActiveStats.setModel(dlmListeMesureActive);
        dlmListefrontiereOuvert = new DefaultListModel();
        listAfficherFrontiereOuvertureStats.setModel(dlmListefrontiereOuvert);
        //setup du timer de play
        listenerSpeedSlider = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();  
                txtTime.setText(String.valueOf(source.getValue()) + "ms");
                int delay = source.getValue();
                timer.setDelay(delay);
            }
        };
        speedSlider.addChangeListener(listenerSpeedSlider);
        SliderJourPasser.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting())
                {
                    if (execution == false)
                    {
                        if (source.getValue() == SliderJourPasser.getMaximum())
                        {
                            controller.play(compteurJour);
                        }
                        else
                        {
                         controller.getEtatDansSimulationCourante(source.getValue());
                        }
                    }
                }
            }
        });

                               
        // pour la section de ajouter forme
        ChangeListener changeListenerGererFormesTabbedPane = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                controller.UpdateSelectedTab(sourceTabbedPane.getSelectedIndex());
                UpdateMessageUserFormes(sourceTabbedPane.getSelectedIndex()); 
            }
        };
        panelGererFormes.addChangeListener(changeListenerGererFormesTabbedPane);

        SetupUpdatePaysTabListeners();
        SetupUpdateRegionTabListeners();

        ChangeListener changeListenerGererMesuresTabbedPane = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                UpdateMessageUserMesure(sourceTabbedPane.getSelectedIndex());  
            }
        };
        panelGererMesures.addChangeListener(changeListenerGererMesuresTabbedPane);

        //SetupUpdateMesureBaseTabListeners();
        SetupUpdateMesurePaysListeners();

        jRadiobtnAddMaladie.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                if (jRadiobtnAddMaladie.isSelected()) {
                    btnAjouterMaladie.setEnabled(true);
                    btnSupprimerMaladie.setEnabled(false);
                    btnChoisirMaladie.setEnabled(false);
                    txtTauxReproduction.getDocument().removeDocumentListener(listenerMaladieText);
                    txtTauxMortalite.getDocument().removeDocumentListener(listenerMaladieText);
                    txtTauxGuerison.getDocument().removeDocumentListener(listenerMaladieText);
                    txtNomMaladie.getDocument().removeDocumentListener(listenerMaladieText);
                    listeMaladies.clearSelection();
                }
                if (!jRadiobtnAddMaladie.isSelected()) {
                    btnAjouterMaladie.setEnabled(false);
                    btnSupprimerMaladie.setEnabled(true);
                    btnChoisirMaladie.setEnabled(true);
                    txtNomMaladie.getDocument().addDocumentListener(listenerMaladieText);
                    txtTauxReproduction.getDocument().addDocumentListener(listenerMaladieText);
                    txtTauxMortalite.getDocument().addDocumentListener(listenerMaladieText);
                    txtTauxGuerison.getDocument().addDocumentListener(listenerMaladieText);
                }
            }
        });

        toggleMesureAjouterFrontiere.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                if (toggleMesureAjouterFrontiere.isSelected()) {
                    txtSeuilFermetureFront.setEditable(true);
                    txtTauxAdhesionAjoutFront1.setEditable(true);
                }
                if (!toggleMesureAjouterFrontiere.isSelected()) {
                    txtSeuilFermetureFront.setEditable(false);
                    txtTauxAdhesionAjoutFront1.setEditable(false);
                    txtSeuilFermetureFront.setText("");
                    txtTauxAdhesionAjoutFront1.setText("");
                }
            }
        });
        
        toggleMesureModifierFrontiere.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                if (toggleMesureModifierFrontiere.isSelected()) {
                    txtSeuilModifFront1.setEditable(true);
                    txtTauxAdhesionModifFront1.setEditable(true);
                }
                if (!toggleMesureModifierFrontiere.isSelected()) {
                    txtSeuilModifFront1.setEditable(false);
                    txtTauxAdhesionModifFront1.setEditable(false);
                    txtSeuilModifFront1.setText("");
                    txtTauxAdhesionModifFront1.setText("");
                }
            }
        });
   
        SetupUpdateMaladieListener();

        ChangeListener changeListenerGererFrontieresTabbedPane = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                UpdateMessageUserFrontiere(sourceTabbedPane.getSelectedIndex());
                refreshListeFrontiere();
                refreshListePaysComboBox(comboBoxPays1AjoutFront);
                refreshListePaysComboBox(comboBoxPays2AjoutFront);
                btnSupprimerModifFront.setEnabled(false);
                listeModifFrontieres.clearSelection();   
            }
        };
        panelGererFrontiere.addChangeListener(changeListenerGererFrontieresTabbedPane);
        SetupUpdateFrontiereListeners();
        
        SetupUpdateSimulationPasserListeners();   
    }  

    
    final void SetupUpdatePaysTabListeners() {
        controller.setAddRegionSelected(false); // Nécessaire pour le controleur qui ajouter une région ou un pays
        jToggleDeplacement.setSelected(false);

        listePays.setSelectionBackground(Color.YELLOW);
        listenerTextModifPays = new DocumentListener() {
            public void updateText() {
                if (updatePaysListenersShouldBeTriggered && AddPaysTextFieldsAreValid(txtNomModifPays.getText(),
                txtTaillePopModifPays.getText(), txtNbInfecterModifPays.getText(), txtTransmissionRegionModifPays.getText())) {
                    modfierPays();
                    ancienneValeurSauver = txtNomModifPays.getText();
                    refreshListePaysJList();   
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateText();        
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                    updateText();

            }
        };

        txtNbInfecterModifPays.getDocument().addDocumentListener(listenerTextModifPays);
        txtTaillePopModifPays.getDocument().addDocumentListener(listenerTextModifPays);
        txtNomModifPays.getDocument().addDocumentListener(listenerTextModifPays);
        txtTransmissionRegionModifPays.getDocument().addDocumentListener(listenerTextModifPays);

        listenerListModifPays = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (evt.getValueIsAdjusting()) {
                    updatePaysListenersShouldBeTriggered = false;
                    ancienneValeurSauver = listePays.getSelectedValue();
                    txtNomModifPays.setText(listePays.getSelectedValue());
                    txtTaillePopModifPays.setText(controller.getPaysdeCarteDto(listePays.getSelectedValue()).m_taillePop.toString());
                    txtNbInfecterModifPays.setText(controller.getPaysdeCarteDto(listePays.getSelectedValue()).m_nbInfecter.toString());
                    txtTransmissionRegionModifPays.setText(String.valueOf(controller.getPaysdeCarteDto(listePays.getSelectedValue()).m_tauxTransmisionRegion*250));
                    txtHauteurModifPays.setText(controller.getPaysdeCarteDto(listePays.getSelectedValue()).m_forme.m_heightSelectBox.toString());
                    txtLargeurModifPays.setText(controller.getPaysdeCarteDto(listePays.getSelectedValue()).m_forme.m_widthSelectBox.toString());
                    updatePaysListenersShouldBeTriggered = true;

                }
            }
        };
        listePays.addListSelectionListener(listenerListModifPays);
    }

    boolean AddPaysTextFieldsAreValid(String txtNom, String taillePop, String infecter, String taux) {
        try {
            int taille = Integer.parseInt(taillePop);
            int nbInf = Integer.parseInt(infecter);
            double p_taux = Double.parseDouble(taux);
            return !txtNom.equals("") && taille >= 0 && nbInf >= 0 && taille >= nbInf && p_taux >=0 && p_taux<=100;
        } catch (Exception e) {
        }
        return false;
    }

    void UpdateMessageUserFormes(int sourceTabbedPane) {
        if (sourceTabbedPane == 0) {
            SimudemieController.TalkToUser(
                    "ENTREZ les informations du nouveau pays et CLIQUEZ sur la carte pour ajouter le pays.");
            txtNomAjoutPays.setText("ZIMBABWE" + (int) (Math.random() * ((100 - 1) + 1)) + 1);
        } else if (sourceTabbedPane == 1) {
            SimudemieController.TalkToUser(
                    "ENTREZ les informations de la nouvelle RÉGION et CLIQUEZ DANS UN PAYS pour ajouter la région au pays.");
            txtNomAjoutRegion.setText("Quebec" + (int) (Math.random() * ((100 - 1) + 1)) + 1);
        } else if (sourceTabbedPane == 2) {
            SimudemieController
                    .TalkToUser("Vous pouvez SELECTIONNER le pays que vous desirez modifier et ajuster ses valeurs");
            refreshListePaysJList();
        } else if (sourceTabbedPane == 3) {
            SimudemieController.TalkToUser(
                    "Vous pouvez SELECTIONNER la region du pays que vous desirez modifier et ajuster ses valeurs et ses pourcentages dans le tableau");
            refreshListePaysComboBox(comboPaysModifRegion);
            txtHauteurModifRegion.setEditable(false);
            txtLargeurModifRegion.setEditable(false);
            try {
                refreshTableRegion(comboPaysModifRegion.getSelectedItem().toString());
            } catch (Exception e) {
            }
        }
    }

    final void SetupUpdateRegionTabListeners() {
        refreshListePaysComboBox(comboPaysModifRegion);
        jToggleDeplacement.setSelected(false);
        listenerTexModifRegion = new DocumentListener() {
            public void updateTextRegion() {
                controller.modifierNomRegionDeCarte(comboPaysModifRegion.getSelectedItem().toString(),
                        nomRegionSauver, txtNomModifRegion.getText());
                nomRegionSauver = txtNomModifRegion.getText();
                
                refreshTableRegion(comboPaysModifRegion.getSelectedItem().toString());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (updateRegionsListenersShouldBeTriggered && !txtNomModifRegion.getText().equals("")) {
                    updateTextRegion();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (updateRegionsListenersShouldBeTriggered && !txtNomModifRegion.getText().equals("")) {
                    updateTextRegion();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (updateRegionsListenersShouldBeTriggered && !txtNomModifRegion.getText().equals("")) {
                    updateTextRegion();
                }
            }
        };

        txtNomModifRegion.getDocument().addDocumentListener(listenerTexModifRegion);

        listenerTableModifRegion = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    updateRegionsListenersShouldBeTriggered = false;
                    tableRowSelected = tableModifRegion.getSelectedRow();
                    tableColSelected = tableModifRegion.getSelectedColumn();
                    nomRegionSauver = tableModifRegion.getValueAt(tableRowSelected, 0).toString();
                    ancienneIndexSauver = tableModifRegion.getSelectedRow();
                    txtHauteurModifRegion.setText(String.valueOf(controller.getRegionDeCarteDto(comboPaysModifRegion.getSelectedItem().toString(), nomRegionSauver).m_forme.m_heightSelectBox));
                    txtLargeurModifRegion.setText(String.valueOf(controller.getRegionDeCarteDto(comboPaysModifRegion.getSelectedItem().toString(), nomRegionSauver).m_forme.m_widthSelectBox));
                    txtNomModifRegion.setText(nomRegionSauver);
                    updateRegionsListenersShouldBeTriggered = true;
                }
            }
        };
        tableModifRegion.getSelectionModel().addListSelectionListener(listenerTableModifRegion);
        
        
    }

    void UpdateMessageUserMesure(int sourceTabbedPane) {
        if (sourceTabbedPane == 0) {
            SimudemieController.TalkToUser("ENTREZ les informations de la mesure du pays que vous voulez rajouter au systemes");
            refreshListePaysComboBox(comboBoxPaysAjoutMesurePays);
        } 
        else if (sourceTabbedPane == 1) {
            SimudemieController.TalkToUser(
                    "Vous pouvez modifier les donnees d une mesure existante en le selectionnant dans la liste");
            refreshListePaysComboBox(comboBoxPaysModifMesurePays);
            try {
                refreshListeMesurePays(comboBoxPaysModifMesurePays.getSelectedItem().toString());
            } 
            catch (Exception e) {
            }
        }            
    }
    
    boolean AddMesureTextFieldsAreValid(String t_nom, String p_seuil, String adhesion, String transmission, String reproduction) 
    {
        try {
            double p_adhesion = Double.parseDouble(adhesion); 
            double p_transmission = Double.parseDouble(transmission);
            double p_reproduction = Double.parseDouble(reproduction); 
            Double  seuil = Double.parseDouble(p_seuil);
            return !t_nom.equals("") && seuil >= 0.0 && seuil <= 100.0 
                                    && p_adhesion >= 0.0 && p_adhesion <= 100.0
                                    && p_transmission >= 0.0 && p_transmission <= 100.0
                                    && p_reproduction >= 0.0 && p_reproduction <= 100.0;          
        } 
        catch (Exception e) {
            SimudemieController.TalkToUser("Vous devez rentrer uniquement des chiffres et ca doit etre non vide");
        }
        return false;
    }

    final void SetupUpdateMesurePaysListeners() {
        listeModifMesurePays.setSelectionBackground(Color.YELLOW);
        listenerTxtMesurePays = new DocumentListener() {
            public void updateText() {
                if (updateMesurePaysListenersShouldBeTriggered
                && AddMesureTextFieldsAreValid(txtNomModifMesurePays.getText(),
                                     txtSeuilLimiteModifMesurePays.getText(),
                                     txtAdhesionModifMesurePays.getText(),
                                     txtTransmissionModifMesurePays.getText(),
                                     txtReproductionModifMesurePays.getText()))
                {
                modfierMesurePays();
                nomMesureSauver = txtNomModifMesurePays.getText();
                refreshListeMesurePays(comboBoxPaysModifMesurePays.getSelectedItem().toString());
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateText();       
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateText();     
            }
        };

        txtSeuilLimiteModifMesurePays.getDocument().addDocumentListener(listenerTxtMesurePays);
        txtNomModifMesurePays.getDocument().addDocumentListener(listenerTxtMesurePays);
        txtAdhesionModifMesurePays.getDocument().addDocumentListener(listenerTxtMesurePays);
        txtTransmissionModifMesurePays.getDocument().addDocumentListener(listenerTxtMesurePays);
        txtReproductionModifMesurePays.getDocument().addDocumentListener(listenerTxtMesurePays);

        listenerListMesurePays = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if (evt.getValueIsAdjusting()) {
                    updateMesurePaysListenersShouldBeTriggered = false;
                    nomMesureSauver = listeModifMesurePays.getSelectedValue();
                    String pays = comboBoxPaysModifMesurePays.getSelectedItem().toString();
                    String mesure = listeModifMesurePays.getSelectedValue();                   
                    txtSeuilLimiteModifMesurePays.setText(String.valueOf(Math.round((controller.getMesureDePaysDto(pays, mesure).m_seuil)*100)));
                    txtAdhesionModifMesurePays.setText(String.valueOf(Math.round((controller.getMesureDePaysDto(pays, mesure).m_adhesion)*100)));
                    txtTransmissionModifMesurePays.setText(String.valueOf(Math.round((controller.getMesureDePaysDto(pays, mesure).m_transmission)*100)));
                    txtReproductionModifMesurePays.setText(String.valueOf(Math.round((controller.getMesureDePaysDto(pays, mesure).m_reproduction)*100)));
                    txtNomModifMesurePays.setText(controller.getMesureDePaysDto(pays, mesure).m_nom);
                    updateMesurePaysListenersShouldBeTriggered = true;
                }
            }
        };
        listeModifMesurePays.addListSelectionListener(listenerListMesurePays);

    }

    final void SetupUpdateMaladieListener() {
        txtReprod.setEditable(false);
        txtMorta.setEditable(false);
        txtGuerison.setEditable(false);
        listeMaladies.setSelectionBackground(Color.YELLOW);
        listenerMaladieText = new DocumentListener() {
            public void updateTextMaladie() {
                if (updateMaladieListenersShouldBeTriggered && AddMaladieTextFieldsAreValid()
                && !jRadiobtnAddMaladie.isSelected()) {
                    modfierMaladie();
                    nomMaladieSauver = txtNomMaladie.getText();
                    refreshListeMaladie(); 
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTextMaladie();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                    updateTextMaladie();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTextMaladie();
                
            }
        };
        txtTauxReproduction.getDocument().addDocumentListener(listenerMaladieText);
        txtTauxMortalite.getDocument().addDocumentListener(listenerMaladieText);
        txtTauxGuerison.getDocument().addDocumentListener(listenerMaladieText);
        txtNomMaladie.getDocument().addDocumentListener(listenerMaladieText);

        listenerListMaladie = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if (evt.getValueIsAdjusting()) {
                    jRadiobtnAddMaladie.setSelected(false);
                    updateMaladieListenersShouldBeTriggered = false;
                    nomMaladieSauver = listeMaladies.getSelectedValue();
                    txtNomMaladie.setText(controller.getDtoMaladieSelectionner(nomMaladieSauver).m_nom);
                    txtTauxGuerison.setText(String.valueOf(controller.getDtoMaladieSelectionner(nomMaladieSauver).m_guerison * 100));
                    txtTauxReproduction.setText(String.valueOf(controller.getDtoMaladieSelectionner(nomMaladieSauver).m_reproduction));
                    txtTauxMortalite.setText(String.valueOf(controller.getDtoMaladieSelectionner(nomMaladieSauver).m_mortalite * 100));
                    updateMaladieListenersShouldBeTriggered = true;
                }
            }
        };
        listeMaladies.addListSelectionListener(listenerListMaladie);
    }

    boolean AddMaladieTextFieldsAreValid() {
        try {
            double mortalite = Double.parseDouble(txtTauxMortalite.getText());
            double reproduction = Double.parseDouble(txtTauxReproduction.getText());
            double guerison = Double.parseDouble(txtTauxGuerison.getText());
            return mortalite >= 0.0 && !txtNomMaladie.equals("") && mortalite <= 100.0 && reproduction >= 0.0 && guerison >= 0.0 && guerison <= 100.0;
        } catch (Exception e) {
            SimudemieController
                    .TalkToUser("Vous devez rentrer des pourcentages pour les taux et le nom doit etre non vide");
        }
        return false;
    }

    void UpdateMessageUserFrontiere(int sourceTabbedPane) {
        if (sourceTabbedPane == 0) {
            SimudemieController
                    .TalkToUser("ENTREZ les informations de la frontiere que vous voulez rajouter au systemes");
            refreshListePaysComboBox(comboBoxPays1AjoutFront);
            refreshListePaysComboBox(comboBoxPays2AjoutFront);
            toggleMesureAjouterFrontiere.setSelected(false);
            txtSeuilFermetureFront.setText("");
            txtTauxAdhesionAjoutFront1.setText("");
            txtSeuilFermetureFront.setEditable(false);
            txtTauxAdhesionAjoutFront1.setEditable(false);
        } else if (sourceTabbedPane == 1) {
            SimudemieController.TalkToUser(
                    "Vous pouvez modifier les donnees d une mesure existante en le selectionnant dans la liste");
        }
    }

    final void SetupUpdateFrontiereListeners() {
        toggleMesureAjouterFrontiere.setSelected(false);
        txtSeuilFermetureFront.setEditable(false);
        txtTauxAdhesionAjoutFront1.setEditable(false);
        txtSeuilFermetureFront.setText("");
        txtTauxAdhesionAjoutFront1.setText("");
        listeModifFrontieres.setSelectionBackground(Color.YELLOW);
        listenerTextFront = new DocumentListener() {
            public void updateFrontiere() {
            if (updateFrontiereListenersShouldBeTriggered
                && FrontiereTextFieldsAreValid(txtSeuilModifFront1.getText(),
                                               txtTransmissionModifFront.getText(), 
                                               txtTauxAdhesionModifFront1.getText(), toggleMesureModifierFrontiere.isSelected()))
                {
                    modifierFrontiere();
                }
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFrontiere();                
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFrontiere();      
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFrontiere();    
            }
        };
        txtTransmissionModifFront.getDocument().addDocumentListener(listenerTextFront);
        txtSeuilModifFront1.getDocument().addDocumentListener(listenerTextFront);
        txtTauxAdhesionModifFront1.getDocument().addDocumentListener(listenerTextFront);

        listenerListFront = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (evt.getValueIsAdjusting()) {
                    updateFrontiereListenersShouldBeTriggered = false;
                    String nom = listeModifFrontieres.getSelectedValue();
                    txtTransmissionModifFront.setText(String.valueOf(Math.round((controller.getDtoVoie(nom).m_tauxTransmission)*250)));
                    toggleMesureModifierFrontiere.setSelected(controller.getDtoVoie(nom).aMesure);    
                    if (controller.getDtoVoie(nom).aMesure == true)
                    {
                        txtTauxAdhesionModifFront1.setText(String.valueOf(Math.round((controller.getDtoVoie(nom).m_tauxAdhesion)*100)));
                        txtSeuilModifFront1.setText(String.valueOf(Math.round((controller.getDtoVoie(nom).m_seuil)*100)));                  
                    }
                    if (controller.getDtoVoie(nom).aMesure == false)
                    {
                        txtTauxAdhesionModifFront1.setText("");
                        txtSeuilModifFront1.setText("");                  
                    }

                    btnSupprimerModifFront.setEnabled(true);
                    updateFrontiereListenersShouldBeTriggered = true;
                }
            }
        };
        listeModifFrontieres.addListSelectionListener(listenerListFront);   
    }

    boolean FrontiereTextFieldsAreValid(String p_seuil, String p_transmission, String p_adhesion,  boolean isSelected) {
        try { 
            if (isSelected == true)
            {
                Double  seuil = Double.parseDouble(p_seuil);
                Double  transmission = Double.parseDouble(p_transmission);
                Double  adhesion = Double.parseDouble(p_adhesion);
                return seuil >=0 && seuil <=100 && transmission>=0 && transmission<=100 &&  adhesion>=0 && adhesion<=100;
            }
            if (isSelected == false)
            {
                Double  transmission = Double.parseDouble(p_transmission);
                return transmission>=0 && transmission<=100;
            }
        } 
        catch (NumberFormatException e) {
        }
        return false;
    }
    
    final void SetupUpdateSimulationPasserListeners ()
    {   
        listeSimulations.setSelectionBackground(Color.YELLOW);
        listenerTexModifSimulationPasser = new DocumentListener() {
            public void updateTextSimulation() {
                if (updateSimlationPasserListenersShouldBeTriggered && !txtNomSimulation.equals("")){
                    controller.modifieNomSimulationPasser(ancienneValeurSauver, txtNomSimulation.getText());
                    ancienneValeurSauver = txtNomSimulation.getText();
                    refreshListeSimulation(); 
                }
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTextSimulation();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTextSimulation();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTextSimulation();
            }
        };
        txtNomSimulation.getDocument().addDocumentListener(listenerTexModifSimulationPasser);

        listenerListSimulationPasser = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if (evt.getValueIsAdjusting()) {
                    updateSimlationPasserListenersShouldBeTriggered = false;
                    ancienneValeurSauver = listeSimulations.getSelectedValue();
                    txtNomSimulation.setText(controller.getDtoSimulationSelectionner(ancienneValeurSauver).m_nom);
                    updateSimlationPasserListenersShouldBeTriggered = true;                    
                    btnSupprimerSimulation.setEnabled(true);
                    btnChoisirSimulation.setEnabled(true);
                }
            }
        };
        listeSimulations.addListSelectionListener(listenerListSimulationPasser);
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        duree = new javax.swing.JSpinner();
        lblJours = new javax.swing.JLabel();
        lblTransmi = new javax.swing.JLabel();
        lblGuerison = new javax.swing.JLabel();
        lblMorta = new javax.swing.JLabel();
        lblNomMaladie = new javax.swing.JLabel();
        txtGuerison = new javax.swing.JTextArea();
        txtReprod = new javax.swing.JTextArea();
        txtMorta = new javax.swing.JTextArea();
        btnZoomOut = new javax.swing.JButton();
        btnZoomIn = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnNewSim = new javax.swing.JButton();
        speedSlider = new javax.swing.JSlider();
        txtTime = new javax.swing.JTextField();
        lblTimer = new javax.swing.JLabel();
        btnPlaySIm = new javax.swing.JButton();
        btnPauseSim = new javax.swing.JButton();
        btnSaveSim = new javax.swing.JButton();
        lblMesure1 = new javax.swing.JLabel();
        btnGererFront = new javax.swing.JButton();
        btnParaMaladie = new javax.swing.JButton();
        lblDuree1 = new javax.swing.JLabel();
        btnAjouterPays = new javax.swing.JButton();
        btnAjoutMes = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        btnNaviguerSimulation = new javax.swing.JButton();
        btnEnregistrerSimulation = new javax.swing.JButton();
        btnUndo = new javax.swing.JButton();
        SliderJourPasser = new javax.swing.JSlider();
        btnRedo = new javax.swing.JButton();
        panelGererFormes = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        lblHauteurAjoutPays = new javax.swing.JLabel();
        lblLargeurAjoutPays = new javax.swing.JLabel();
        lblNbInfecterAjoutPays = new javax.swing.JLabel();
        lbllTaillePopAjoutPays = new javax.swing.JLabel();
        lblNom = new javax.swing.JLabel();
        lblTransmessionRegionAjoutPays = new javax.swing.JLabel();
        txtNomAjoutPays = new javax.swing.JTextArea();
        txtLargeurAjoutPays = new javax.swing.JTextArea();
        txtHauteurAjoutPays = new javax.swing.JTextArea();
        txtNbInfecterAjoutPays = new javax.swing.JTextArea();
        txtTaillePopAjoutPays = new javax.swing.JTextArea();
        txtTransmissionRegionAjoutPays = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblHauteurAjoutRegion = new javax.swing.JLabel();
        lblLargeurAjoutRegion = new javax.swing.JLabel();
        lblNomAjoutRegion = new javax.swing.JLabel();
        txtHauteurAjoutRegion = new javax.swing.JTextArea();
        txtLargeurAjoutRegion = new javax.swing.JTextArea();
        txtNomAjoutRegion = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnSupprimerPays = new javax.swing.JButton();
        ListePaysPane = new javax.swing.JScrollPane();
        listePays = new javax.swing.JList<>();
        lblPaysPresent = new javax.swing.JLabel();
        lblNomModifPays = new javax.swing.JLabel();
        lblTauxTransmissionRegionModifPays = new javax.swing.JLabel();
        txtNomModifPays = new javax.swing.JTextArea();
        txtNbInfecterModifPays = new javax.swing.JTextArea();
        txtTaillePopModifPays = new javax.swing.JTextArea();
        txtHauteurModifPays = new javax.swing.JTextArea();
        txtLargeurModifPays = new javax.swing.JTextArea();
        txtTransmissionRegionModifPays = new javax.swing.JTextArea();
        lblHauteurModifPays = new javax.swing.JLabel();
        lblLargeurModifPays = new javax.swing.JLabel();
        lblNbInfecterModifPays = new javax.swing.JLabel();
        lblTaillePopModifPays = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnSupprimerModifRegion = new javax.swing.JButton();
        comboPaysModifRegion = new javax.swing.JComboBox<>();
        tableRegionPourcentPane1 = new javax.swing.JScrollPane();
        tableModifRegion = new javax.swing.JTable();
        lblChoisirRegion = new javax.swing.JLabel();
        btnValiderPourcentageRegion = new javax.swing.JButton();
        lblNomModifRegion = new javax.swing.JLabel();
        txtHauteurModifRegion = new javax.swing.JTextArea();
        txtLargeurModifRegion = new javax.swing.JTextArea();
        lblHauteurModifregion = new javax.swing.JLabel();
        lblLargeurModifRegion = new javax.swing.JLabel();
        txtNomModifRegion = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        panelGererMesures = new javax.swing.JTabbedPane();
        AjoutMesurePays = new javax.swing.JPanel();
        lblNomAjoutMesure = new javax.swing.JLabel();
        lblSeuilLimiteAjoutMesure = new javax.swing.JLabel();
        comboBoxPaysAjoutMesurePays = new javax.swing.JComboBox<>();
        txtNomAjoutMesure = new javax.swing.JTextField();
        txtSeuilLimiteAjoutMesure = new javax.swing.JTextField();
        btnAjouterAjoutMesure = new javax.swing.JButton();
        lbladhesionAjoutMesure = new javax.swing.JLabel();
        txtAdhesionAjoutMesure = new javax.swing.JTextField();
        lblTransmissionAjoutMesure1 = new javax.swing.JLabel();
        txtTransmissionAjoutMesure = new javax.swing.JTextField();
        txtReproductionAjoutMesure = new javax.swing.JTextField();
        lblReproductionAjoutMesure = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ModifierMesurePays = new javax.swing.JPanel();
        lblNomModifMesurePays = new javax.swing.JLabel();
        lblSeuilLimiteModifMesurePays = new javax.swing.JLabel();
        lblMesurePresentesModifMesurePays = new javax.swing.JLabel();
        comboBoxPaysModifMesurePays = new javax.swing.JComboBox<>();
        ListeModifMesurePays = new javax.swing.JScrollPane();
        listeModifMesurePays = new javax.swing.JList<>();
        txtNomModifMesurePays = new javax.swing.JTextField();
        txtSeuilLimiteModifMesurePays = new javax.swing.JTextField();
        btnSupprimerModifMesurePays = new javax.swing.JButton();
        txtTransmissionModifMesurePays = new javax.swing.JTextField();
        txtReproductionModifMesurePays = new javax.swing.JTextField();
        lblTransmissionModifMesurePays = new javax.swing.JLabel();
        lbladhesionModifMesurePays = new javax.swing.JLabel();
        txtAdhesionModifMesurePays = new javax.swing.JTextField();
        lblReproductionModifMesurePays = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        panelGererStatsPays = new javax.swing.JPanel();
        labelAfficherStatsPays = new javax.swing.JLabel();
        panelFrontieresOuvertGraphePays = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listAfficherFrontiereOuvertureStats = new javax.swing.JList<>();
        panelMesureGraphePays = new javax.swing.JPanel();
        labelMesureActive = new javax.swing.JLabel();
        jScrollPaneMesureActive = new javax.swing.JScrollPane();
        listAfficherMesureActiveStats = new javax.swing.JList<>();
        panelGererStatsCarte = new javax.swing.JPanel();
        panelGererMaladie = new javax.swing.JPanel();
        labelTauxMortalite = new javax.swing.JLabel();
        labelTauxReproduction = new javax.swing.JLabel();
        btnAjouterMaladie = new javax.swing.JButton();
        btnChoisirMaladie = new javax.swing.JButton();
        btnSupprimerMaladie = new javax.swing.JButton();
        ScrollPane_Maladie = new javax.swing.JScrollPane();
        listeMaladies = new javax.swing.JList<>();
        labelTauxGuerison = new javax.swing.JLabel();
        pourcentage1 = new javax.swing.JLabel();
        labelPossibilites = new javax.swing.JLabel();
        jRadiobtnAddMaladie = new javax.swing.JRadioButton();
        txtTauxReproduction = new javax.swing.JTextField();
        txtTauxMortalite = new javax.swing.JTextField();
        txtTauxGuerison = new javax.swing.JTextField();
        txtNomMaladie = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        panelGererFrontiere = new javax.swing.JTabbedPane();
        AjouterFrontiere = new javax.swing.JPanel();
        lblNomPaysAjoutFront = new javax.swing.JLabel();
        lblTypeAjoutFront = new javax.swing.JLabel();
        lblSeuilFermetureAjoutFront = new javax.swing.JLabel();
        lblTauxTransmissionAjoutFront = new javax.swing.JLabel();
        lblTauxadhesionAjoutFront1 = new javax.swing.JLabel();
        txtSeuilFermetureFront = new javax.swing.JTextField();
        comboBoxTypeAjoutFront = new javax.swing.JComboBox<>();
        btnAjouterFront = new javax.swing.JButton();
        txtTauxTransmissionAjoutFront = new javax.swing.JTextField();
        txtTauxAdhesionAjoutFront1 = new javax.swing.JTextField();
        comboBoxPays1AjoutFront = new javax.swing.JComboBox<>();
        toggleMesureAjouterFrontiere = new javax.swing.JRadioButton();
        comboBoxPays2AjoutFront = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        ModifierFrontiere = new javax.swing.JPanel();
        lbFrontPresentes = new javax.swing.JLabel();
        lblTransmissionModifFront = new javax.swing.JLabel();
        lblTauxAdhesionModifFront1 = new javax.swing.JLabel();
        lblSeuilModifFront = new javax.swing.JLabel();
        toggleMesureModifierFrontiere = new javax.swing.JRadioButton();
        txtSeuilModifFront1 = new javax.swing.JTextField();
        jScrollPaneModifFront = new javax.swing.JScrollPane();
        listeModifFrontieres = new javax.swing.JList<>();
        txtTransmissionModifFront = new javax.swing.JTextField();
        txtTauxAdhesionModifFront1 = new javax.swing.JTextField();
        btnSupprimerModifFront = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        panelGererSimulations = new javax.swing.JPanel();
        labelNomSimulation = new javax.swing.JLabel();
        btnChoisirSimulation = new javax.swing.JButton();
        btnSupprimerSimulation = new javax.swing.JButton();
        ScrollPane_Simulation = new javax.swing.JScrollPane();
        listeSimulations = new javax.swing.JList<>();
        labelSimulationPossible = new javax.swing.JLabel();
        txtNomSimulation = new javax.swing.JTextField();
        panelMesuresSan = new javax.swing.JPanel();
        moveUp1 = new javax.swing.JButton();
        moveLeft = new javax.swing.JButton();
        moveDown = new javax.swing.JButton();
        moveRight = new javax.swing.JButton();
        jToggleDeplacement = new javax.swing.JToggleButton();
        btnScreenShot = new javax.swing.JButton();
        btnCSVStats = new javax.swing.JButton();
        drawingStats1 = new ca.ulaval.glo2004.gui.DrawingStats(this);
        jLabel2 = new javax.swing.JLabel();
        talkToUser1 = new ca.ulaval.glo2004.gui.TalkToUser(this);
        jLabel3 = new javax.swing.JLabel();
        drawingPanel = new ca.ulaval.glo2004.gui.DrawingPanel(this);
        jLabel1 = new javax.swing.JLabel();
        lblBG = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        duree.setOpaque(false);
        getContentPane().add(duree, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 380, 80, 20));

        lblJours.setFont(new java.awt.Font("URW Bookman", 1, 14)); // NOI18N
        lblJours.setForeground(new java.awt.Color(237, 235, 235));
        lblJours.setText("Jours");
        getContentPane().add(lblJours, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 380, -1, -1));

        lblTransmi.setFont(new java.awt.Font("URW Bookman", 0, 12)); // NOI18N
        lblTransmi.setForeground(new java.awt.Color(237, 235, 235));
        lblTransmi.setText("REPRODUCTION");
        getContentPane().add(lblTransmi, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 860, 120, 20));

        lblGuerison.setFont(new java.awt.Font("URW Bookman", 0, 12)); // NOI18N
        lblGuerison.setForeground(new java.awt.Color(237, 235, 235));
        lblGuerison.setText("TAUX DE GUÉRISON");
        getContentPane().add(lblGuerison, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 890, 140, 30));

        lblMorta.setFont(new java.awt.Font("URW Bookman", 0, 12)); // NOI18N
        lblMorta.setForeground(new java.awt.Color(237, 235, 235));
        lblMorta.setText("TAUX DE MORTALITÉ");
        getContentPane().add(lblMorta, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 930, 150, 20));

        lblNomMaladie.setFont(new java.awt.Font("URW Bookman", 1, 14)); // NOI18N
        lblNomMaladie.setForeground(new java.awt.Color(237, 235, 235));
        lblNomMaladie.setText("MALADIE :");
        getContentPane().add(lblNomMaladie, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 830, 210, 20));

        txtGuerison.setBackground(new java.awt.Color(140, 140, 140));
        txtGuerison.setColumns(20);
        txtGuerison.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtGuerison.setRows(5);
        txtGuerison.setText("%");
        getContentPane().add(txtGuerison, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 900, 40, 20));

        txtReprod.setBackground(new java.awt.Color(140, 140, 140));
        txtReprod.setColumns(20);
        txtReprod.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtReprod.setRows(5);
        getContentPane().add(txtReprod, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 860, 40, 20));

        txtMorta.setBackground(new java.awt.Color(140, 140, 140));
        txtMorta.setColumns(20);
        txtMorta.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtMorta.setRows(5);
        txtMorta.setText("%");
        getContentPane().add(txtMorta, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 930, 40, 20));

        btnZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/zoomout.png"))); // NOI18N
        btnZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomOutActionPerformed(evt);
            }
        });
        getContentPane().add(btnZoomOut, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 140, 30, 30));

        btnZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/zoomin.png"))); // NOI18N
        btnZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomInActionPerformed(evt);
            }
        });
        getContentPane().add(btnZoomIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 110, 30, 30));

        btnImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/importersim.png"))); // NOI18N
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });
        getContentPane().add(btnImport, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, 222, 60));

        btnNewSim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/nouvellessim.png"))); // NOI18N
        btnNewSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewSimActionPerformed(evt);
            }
        });
        getContentPane().add(btnNewSim, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, 220, 60));

        speedSlider.setBackground(new java.awt.Color(139, 139, 10));
        speedSlider.setForeground(java.awt.Color.orange);
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setMaximum(5000);
        speedSlider.setMinimum(50);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setValue(2500);
        speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                speedSliderStateChanged(evt);
            }
        });
        getContentPane().add(speedSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 440, 190, -1));

        txtTime.setBackground(new java.awt.Color(140, 140, 140));
        txtTime.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        txtTime.setText("2500ms");
        txtTime.setPreferredSize(new java.awt.Dimension(40, 17));
        txtTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimeActionPerformed(evt);
            }
        });
        getContentPane().add(txtTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 410, 60, 20));

        lblTimer.setFont(new java.awt.Font("URW Bookman", 1, 14)); // NOI18N
        lblTimer.setForeground(new java.awt.Color(237, 235, 235));
        lblTimer.setText("Timer :");
        getContentPane().add(lblTimer, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 410, -1, -1));

        btnPlaySIm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/play.png"))); // NOI18N
        btnPlaySIm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlaySImActionPerformed(evt);
            }
        });
        getContentPane().add(btnPlaySIm, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 220, 120, 30));

        btnPauseSim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/pause.png"))); // NOI18N
        btnPauseSim.setToolTipText("");
        btnPauseSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseSimActionPerformed(evt);
            }
        });
        getContentPane().add(btnPauseSim, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 260, 120, 30));

        btnSaveSim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/savesimulation.png"))); // NOI18N
        btnSaveSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveSimActionPerformed(evt);
            }
        });
        getContentPane().add(btnSaveSim, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 220, 60));

        lblMesure1.setFont(new java.awt.Font("URW Bookman", 1, 18)); // NOI18N
        lblMesure1.setForeground(new java.awt.Color(237, 235, 235));
        lblMesure1.setToolTipText("");
        getContentPane().add(lblMesure1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 150, -1, -1));

        btnGererFront.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Boutonfrontiere.png"))); // NOI18N
        btnGererFront.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGererFrontActionPerformed(evt);
            }
        });
        getContentPane().add(btnGererFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 640, 220, 59));

        btnParaMaladie.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Boutonparamal.png"))); // NOI18N
        btnParaMaladie.setMaximumSize(new java.awt.Dimension(220, 60));
        btnParaMaladie.setMinimumSize(new java.awt.Dimension(220, 60));
        btnParaMaladie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnParaMaladieActionPerformed(evt);
            }
        });
        getContentPane().add(btnParaMaladie, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 710, 220, 59));

        lblDuree1.setFont(new java.awt.Font("URW Bookman", 1, 14)); // NOI18N
        lblDuree1.setForeground(new java.awt.Color(237, 235, 235));
        lblDuree1.setText("Durée de la simulation");
        getContentPane().add(lblDuree1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 360, -1, -1));

        btnAjouterPays.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Boutonpays.png"))); // NOI18N
        btnAjouterPays.setMaximumSize(new java.awt.Dimension(220, 60));
        btnAjouterPays.setMinimumSize(new java.awt.Dimension(220, 60));
        btnAjouterPays.setPreferredSize(new java.awt.Dimension(220, 60));
        btnAjouterPays.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAjouterPaysMouseClicked(evt);
            }
        });
        btnAjouterPays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjouterPaysActionPerformed(evt);
            }
        });
        getContentPane().add(btnAjouterPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 500, -1, 59));

        btnAjoutMes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Boutonmessan.png"))); // NOI18N
        btnAjoutMes.setMaximumSize(new java.awt.Dimension(220, 60));
        btnAjoutMes.setMinimumSize(new java.awt.Dimension(220, 60));
        btnAjoutMes.setPreferredSize(new java.awt.Dimension(220, 60));
        btnAjoutMes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnAjoutMesMouseReleased(evt);
            }
        });
        btnAjoutMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjoutMesActionPerformed(evt);
            }
        });
        getContentPane().add(btnAjoutMes, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 570, -1, 59));

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stop.png"))); // NOI18N
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        getContentPane().add(btnStop, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 300, 120, 30));

        btnNaviguerSimulation.setText("Naviguer Simulation");
        btnNaviguerSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNaviguerSimulationActionPerformed(evt);
            }
        });
        getContentPane().add(btnNaviguerSimulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(1910, 910, 190, 50));

        btnEnregistrerSimulation.setText("Enregistrer Simulation");
        btnEnregistrerSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnregistrerSimulationActionPerformed(evt);
            }
        });
        getContentPane().add(btnEnregistrerSimulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(1900, 850, 190, 50));

        btnUndo.setBackground(new java.awt.Color(1, 1, 1));
        btnUndo.setForeground(new java.awt.Color(254, 254, 254));
        btnUndo.setText("Undo");
        btnUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUndoActionPerformed(evt);
            }
        });
        getContentPane().add(btnUndo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 970, 100, 30));

        SliderJourPasser.setBackground(new java.awt.Color(1, 1, 1));
        SliderJourPasser.setForeground(new java.awt.Color(254, 254, 254));
        SliderJourPasser.setMajorTickSpacing(10);
        SliderJourPasser.setMaximum(1);
        SliderJourPasser.setMinorTickSpacing(10);
        SliderJourPasser.setPaintLabels(true);
        SliderJourPasser.setPaintTicks(true);
        getContentPane().add(SliderJourPasser, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 970, 1130, -1));

        btnRedo.setBackground(new java.awt.Color(1, 1, 1));
        btnRedo.setForeground(new java.awt.Color(254, 254, 254));
        btnRedo.setText("Redo");
        btnRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRedoActionPerformed(evt);
            }
        });
        getContentPane().add(btnRedo, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 970, 90, 30));

        panelGererFormes.setBackground(new java.awt.Color(1, 1, 1));
        panelGererFormes.setForeground(new java.awt.Color(93, 93, 93));
        panelGererFormes.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        panelGererFormes.setAlignmentX(0.0F);
        panelGererFormes.setAlignmentY(0.0F);
        panelGererFormes.setDoubleBuffered(true);
        panelGererFormes.setMinimumSize(new java.awt.Dimension(951, 100));
        panelGererFormes.setOpaque(true);
        panelGererFormes.setPreferredSize(new java.awt.Dimension(951, 100));
        panelGererFormes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panelGererFormesStateChanged(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(48, 48, 48));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblHauteurAjoutPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblHauteurAjoutPays.setForeground(new java.awt.Color(237, 235, 235));
        lblHauteurAjoutPays.setText("Hauteur(Px) :");
        jPanel1.add(lblHauteurAjoutPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, -1, -1));

        lblLargeurAjoutPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblLargeurAjoutPays.setForeground(new java.awt.Color(237, 235, 235));
        lblLargeurAjoutPays.setText("Largeur(Px) :");
        jPanel1.add(lblLargeurAjoutPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 16, -1, -1));

        lblNbInfecterAjoutPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblNbInfecterAjoutPays.setForeground(new java.awt.Color(237, 235, 235));
        lblNbInfecterAjoutPays.setText("NbInfecter");
        jPanel1.add(lblNbInfecterAjoutPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 50, 70, 20));

        lbllTaillePopAjoutPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lbllTaillePopAjoutPays.setForeground(new java.awt.Color(237, 235, 235));
        lbllTaillePopAjoutPays.setText("Taille Population");
        jPanel1.add(lbllTaillePopAjoutPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 20, -1, -1));

        lblNom.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblNom.setForeground(new java.awt.Color(237, 235, 235));
        lblNom.setText("Nom:");
        jPanel1.add(lblNom, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 16, -1, -1));

        lblTransmessionRegionAjoutPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblTransmessionRegionAjoutPays.setForeground(new java.awt.Color(237, 235, 235));
        lblTransmessionRegionAjoutPays.setText("Taux Transmission Inter-Region (%)");
        jPanel1.add(lblTransmessionRegionAjoutPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 80, 220, -1));

        txtNomAjoutPays.setBackground(new java.awt.Color(140, 140, 140));
        txtNomAjoutPays.setColumns(20);
        txtNomAjoutPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtNomAjoutPays.setRows(5);
        txtNomAjoutPays.setText("Canada");
        jPanel1.add(txtNomAjoutPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 16, 110, 20));

        txtLargeurAjoutPays.setBackground(new java.awt.Color(140, 140, 140));
        txtLargeurAjoutPays.setColumns(20);
        txtLargeurAjoutPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtLargeurAjoutPays.setRows(5);
        txtLargeurAjoutPays.setText("200");
        jPanel1.add(txtLargeurAjoutPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 16, 44, 20));

        txtHauteurAjoutPays.setBackground(new java.awt.Color(140, 140, 140));
        txtHauteurAjoutPays.setColumns(20);
        txtHauteurAjoutPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtHauteurAjoutPays.setRows(5);
        txtHauteurAjoutPays.setText("150");
        jPanel1.add(txtHauteurAjoutPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 50, 44, 20));

        txtNbInfecterAjoutPays.setBackground(new java.awt.Color(140, 140, 140));
        txtNbInfecterAjoutPays.setColumns(20);
        txtNbInfecterAjoutPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtNbInfecterAjoutPays.setRows(5);
        txtNbInfecterAjoutPays.setText("500");
        jPanel1.add(txtNbInfecterAjoutPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 50, 120, 20));

        txtTaillePopAjoutPays.setBackground(new java.awt.Color(140, 140, 140));
        txtTaillePopAjoutPays.setColumns(20);
        txtTaillePopAjoutPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtTaillePopAjoutPays.setRows(5);
        txtTaillePopAjoutPays.setText("500000");
        jPanel1.add(txtTaillePopAjoutPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 20, 120, 20));

        txtTransmissionRegionAjoutPays.setBackground(new java.awt.Color(140, 140, 140));
        txtTransmissionRegionAjoutPays.setColumns(20);
        txtTransmissionRegionAjoutPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtTransmissionRegionAjoutPays.setRows(5);
        txtTransmissionRegionAjoutPays.setText("20");
        jPanel1.add(txtTransmissionRegionAjoutPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 80, 120, 20));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/bgPanelPays.png"))); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(-180, -20, 1410, 150));

        panelGererFormes.addTab("Ajouter Pays", jPanel1);

        jPanel2.setBackground(new java.awt.Color(48, 48, 48));

        lblHauteurAjoutRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblHauteurAjoutRegion.setForeground(new java.awt.Color(237, 235, 235));
        lblHauteurAjoutRegion.setText("Hauteur(Px) :");

        lblLargeurAjoutRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblLargeurAjoutRegion.setForeground(new java.awt.Color(237, 235, 235));
        lblLargeurAjoutRegion.setText("Largeur(Px) :");

        lblNomAjoutRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblNomAjoutRegion.setForeground(new java.awt.Color(237, 235, 235));
        lblNomAjoutRegion.setText("Nom:");

        txtHauteurAjoutRegion.setBackground(new java.awt.Color(140, 140, 140));
        txtHauteurAjoutRegion.setColumns(20);
        txtHauteurAjoutRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtHauteurAjoutRegion.setRows(5);
        txtHauteurAjoutRegion.setText("85");

        txtLargeurAjoutRegion.setBackground(new java.awt.Color(140, 140, 140));
        txtLargeurAjoutRegion.setColumns(20);
        txtLargeurAjoutRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtLargeurAjoutRegion.setRows(5);
        txtLargeurAjoutRegion.setText("75");

        txtNomAjoutRegion.setBackground(new java.awt.Color(140, 140, 140));
        txtNomAjoutRegion.setColumns(20);
        txtNomAjoutRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtNomAjoutRegion.setRows(5);
        txtNomAjoutRegion.setText("Quebec");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/bgPanelPays.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(lblNomAjoutRegion)
                .addGap(8, 8, 8)
                .addComponent(txtNomAjoutRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(lblLargeurAjoutRegion)
                .addGap(20, 20, 20)
                .addComponent(txtLargeurAjoutRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(lblHauteurAjoutRegion)
                .addGap(7, 7, 7)
                .addComponent(txtHauteurAjoutRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 1410, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNomAjoutRegion)
                    .addComponent(txtNomAjoutRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLargeurAjoutRegion)
                    .addComponent(txtLargeurAjoutRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHauteurAjoutRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHauteurAjoutRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel6)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        panelGererFormes.addTab("Ajouter Region", jPanel2);

        jPanel3.setBackground(new java.awt.Color(48, 48, 48));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnSupprimerPays.setBackground(new java.awt.Color(1, 1, 1));
        btnSupprimerPays.setForeground(new java.awt.Color(254, 254, 254));
        btnSupprimerPays.setText("SupprimerPays");
        btnSupprimerPays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSupprimerPaysActionPerformed(evt);
            }
        });
        jPanel3.add(btnSupprimerPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(725, 40, 130, -1));

        ListePaysPane.setBackground(new java.awt.Color(140, 140, 140));

        listePays.setBackground(new java.awt.Color(140, 140, 140));
        listePays.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listePaysValueChanged(evt);
            }
        });
        ListePaysPane.setViewportView(listePays);

        jPanel3.add(ListePaysPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(475, 21, 230, 60));

        lblPaysPresent.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblPaysPresent.setForeground(new java.awt.Color(237, 235, 235));
        lblPaysPresent.setText("Pays deja presents");
        jPanel3.add(lblPaysPresent, new org.netbeans.lib.awtextra.AbsoluteConstraints(535, 0, -1, -1));

        lblNomModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblNomModifPays.setForeground(new java.awt.Color(237, 235, 235));
        lblNomModifPays.setText("Nom:");
        jPanel3.add(lblNomModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, -1));

        lblTauxTransmissionRegionModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblTauxTransmissionRegionModifPays.setForeground(new java.awt.Color(237, 235, 235));
        lblTauxTransmissionRegionModifPays.setText("Taux Transmission Inter-Region : (%)");
        jPanel3.add(lblTauxTransmissionRegionModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 230, -1));

        txtNomModifPays.setBackground(new java.awt.Color(140, 140, 140));
        txtNomModifPays.setColumns(20);
        txtNomModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtNomModifPays.setRows(5);
        jPanel3.add(txtNomModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(77, 10, 110, 20));

        txtNbInfecterModifPays.setBackground(new java.awt.Color(140, 140, 140));
        txtNbInfecterModifPays.setColumns(20);
        txtNbInfecterModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtNbInfecterModifPays.setRows(5);
        jPanel3.add(txtNbInfecterModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(334, 50, 110, 20));

        txtTaillePopModifPays.setBackground(new java.awt.Color(140, 140, 140));
        txtTaillePopModifPays.setColumns(20);
        txtTaillePopModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtTaillePopModifPays.setRows(5);
        jPanel3.add(txtTaillePopModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(334, 20, 110, 20));

        txtHauteurModifPays.setBackground(new java.awt.Color(140, 140, 140));
        txtHauteurModifPays.setColumns(20);
        txtHauteurModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtHauteurModifPays.setRows(5);
        jPanel3.add(txtHauteurModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 70, 44, 20));

        txtLargeurModifPays.setBackground(new java.awt.Color(140, 140, 140));
        txtLargeurModifPays.setColumns(20);
        txtLargeurModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtLargeurModifPays.setRows(5);
        jPanel3.add(txtLargeurModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(114, 40, 44, 20));

        txtTransmissionRegionModifPays.setBackground(new java.awt.Color(140, 140, 140));
        txtTransmissionRegionModifPays.setColumns(20);
        txtTransmissionRegionModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtTransmissionRegionModifPays.setRows(5);
        jPanel3.add(txtTransmissionRegionModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 80, 44, 20));

        lblHauteurModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblHauteurModifPays.setForeground(new java.awt.Color(237, 235, 235));
        lblHauteurModifPays.setText("Hauteur(Px) :");
        jPanel3.add(lblHauteurModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));

        lblLargeurModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblLargeurModifPays.setForeground(new java.awt.Color(237, 235, 235));
        lblLargeurModifPays.setText("Largeur(Px) :");
        jPanel3.add(lblLargeurModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        lblNbInfecterModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblNbInfecterModifPays.setForeground(new java.awt.Color(237, 235, 235));
        lblNbInfecterModifPays.setText("NbInfecter");
        jPanel3.add(lblNbInfecterModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, -1, -1));

        lblTaillePopModifPays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblTaillePopModifPays.setForeground(new java.awt.Color(237, 235, 235));
        lblTaillePopModifPays.setText("Taille Population");
        jPanel3.add(lblTaillePopModifPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 20, -1, -1));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/bgPanelPays.png"))); // NOI18N
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, -1, -1));

        panelGererFormes.addTab("Modifier Pays", jPanel3);

        jPanel4.setBackground(new java.awt.Color(48, 48, 48));

        btnSupprimerModifRegion.setBackground(new java.awt.Color(1, 1, 1));
        btnSupprimerModifRegion.setForeground(new java.awt.Color(254, 254, 254));
        btnSupprimerModifRegion.setText("SupprimerRegion");
        btnSupprimerModifRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSupprimerModifRegionActionPerformed(evt);
            }
        });

        comboPaysModifRegion.setBackground(new java.awt.Color(140, 140, 140));
        comboPaysModifRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboPaysModifRegionActionPerformed(evt);
            }
        });

        tableRegionPourcentPane1.setBackground(new java.awt.Color(140, 140, 140));

        tableModifRegion.setBackground(new java.awt.Color(140, 140, 140));
        tableModifRegion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Region", "Pourcentage"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableModifRegion.getTableHeader().setReorderingAllowed(false);
        tableRegionPourcentPane1.setViewportView(tableModifRegion);

        lblChoisirRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblChoisirRegion.setForeground(new java.awt.Color(237, 235, 235));
        lblChoisirRegion.setText("Region deja presents");

        btnValiderPourcentageRegion.setBackground(new java.awt.Color(1, 1, 1));
        btnValiderPourcentageRegion.setForeground(new java.awt.Color(254, 254, 254));
        btnValiderPourcentageRegion.setText("Valider Pourcentage de Region");
        btnValiderPourcentageRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValiderPourcentageRegionActionPerformed(evt);
            }
        });

        lblNomModifRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblNomModifRegion.setForeground(new java.awt.Color(237, 235, 235));
        lblNomModifRegion.setText("Nom:");

        txtHauteurModifRegion.setBackground(new java.awt.Color(140, 140, 140));
        txtHauteurModifRegion.setColumns(20);
        txtHauteurModifRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtHauteurModifRegion.setRows(5);
        txtHauteurModifRegion.setText("40");

        txtLargeurModifRegion.setBackground(new java.awt.Color(140, 140, 140));
        txtLargeurModifRegion.setColumns(20);
        txtLargeurModifRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtLargeurModifRegion.setRows(5);
        txtLargeurModifRegion.setText("40");

        lblHauteurModifregion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblHauteurModifregion.setForeground(new java.awt.Color(237, 235, 235));
        lblHauteurModifregion.setText("Hauteur(Px) :");

        lblLargeurModifRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblLargeurModifRegion.setForeground(new java.awt.Color(237, 235, 235));
        lblLargeurModifRegion.setText("Largeur(Px) :");

        txtNomModifRegion.setBackground(new java.awt.Color(140, 140, 140));
        txtNomModifRegion.setColumns(20);
        txtNomModifRegion.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        txtNomModifRegion.setRows(5);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/bgPanelPays.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblNomModifRegion)
                        .addGap(38, 38, 38)
                        .addComponent(txtNomModifRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblLargeurModifRegion)
                        .addGap(30, 30, 30)
                        .addComponent(txtLargeurModifRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(comboPaysModifRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblHauteurModifregion)
                        .addGap(27, 27, 27)
                        .addComponent(txtHauteurModifRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(lblChoisirRegion))
                    .addComponent(tableRegionPourcentPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnSupprimerModifRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnValiderPourcentageRegion)))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNomModifRegion)
                    .addComponent(txtNomModifRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLargeurModifRegion)
                    .addComponent(txtLargeurModifRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(comboPaysModifRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHauteurModifregion)
                    .addComponent(txtHauteurModifRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(lblChoisirRegion)
                .addGap(5, 5, 5)
                .addComponent(tableRegionPourcentPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btnSupprimerModifRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnValiderPourcentageRegion))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        panelGererFormes.addTab("Modifier Region", jPanel4);

        getContentPane().add(panelGererFormes, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 850, 1120, 120));

        panelGererMesures.setBackground(new java.awt.Color(1, 1, 1));
        panelGererMesures.setForeground(new java.awt.Color(95, 90, 90));
        panelGererMesures.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        panelGererMesures.setAlignmentX(0.0F);
        panelGererMesures.setAlignmentY(0.0F);
        panelGererMesures.setDoubleBuffered(true);
        panelGererMesures.setMinimumSize(new java.awt.Dimension(951, 100));
        panelGererMesures.setOpaque(true);
        panelGererMesures.setPreferredSize(new java.awt.Dimension(951, 100));
        panelGererMesures.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panelGererMesuresStateChanged(evt);
            }
        });

        AjoutMesurePays.setBackground(new java.awt.Color(48, 48, 48));
        AjoutMesurePays.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNomAjoutMesure.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblNomAjoutMesure.setForeground(new java.awt.Color(237, 235, 235));
        lblNomAjoutMesure.setText("Nom");
        AjoutMesurePays.add(lblNomAjoutMesure, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, -1));

        lblSeuilLimiteAjoutMesure.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblSeuilLimiteAjoutMesure.setForeground(new java.awt.Color(237, 235, 235));
        lblSeuilLimiteAjoutMesure.setText("Seuil Limite (%)");
        AjoutMesurePays.add(lblSeuilLimiteAjoutMesure, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 90, 30));

        comboBoxPaysAjoutMesurePays.setBackground(new java.awt.Color(140, 140, 140));
        comboBoxPaysAjoutMesurePays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxPaysAjoutMesurePaysActionPerformed(evt);
            }
        });
        AjoutMesurePays.add(comboBoxPaysAjoutMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 130, -1));

        txtNomAjoutMesure.setBackground(new java.awt.Color(140, 140, 140));
        txtNomAjoutMesure.setPreferredSize(new java.awt.Dimension(90, 19));
        txtNomAjoutMesure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomAjoutMesureActionPerformed(evt);
            }
        });
        AjoutMesurePays.add(txtNomAjoutMesure, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 70, 170, -1));

        txtSeuilLimiteAjoutMesure.setBackground(new java.awt.Color(140, 140, 140));
        txtSeuilLimiteAjoutMesure.setText("50");
        txtSeuilLimiteAjoutMesure.setPreferredSize(new java.awt.Dimension(90, 19));
        AjoutMesurePays.add(txtSeuilLimiteAjoutMesure, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 20, 50, 20));

        btnAjouterAjoutMesure.setBackground(new java.awt.Color(1, 1, 1));
        btnAjouterAjoutMesure.setForeground(new java.awt.Color(254, 254, 254));
        btnAjouterAjoutMesure.setActionCommand("Ajouter La Frontiere");
        btnAjouterAjoutMesure.setLabel("Ajouter la Mesure");
        btnAjouterAjoutMesure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjouterAjoutMesureActionPerformed(evt);
            }
        });
        AjoutMesurePays.add(btnAjouterAjoutMesure, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 40, 130, 40));

        lbladhesionAjoutMesure.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lbladhesionAjoutMesure.setForeground(new java.awt.Color(237, 235, 235));
        lbladhesionAjoutMesure.setText("Taux Adhesion (%)");
        AjoutMesurePays.add(lbladhesionAjoutMesure, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 40, 110, 20));

        txtAdhesionAjoutMesure.setBackground(new java.awt.Color(140, 140, 140));
        txtAdhesionAjoutMesure.setText("50");
        txtAdhesionAjoutMesure.setPreferredSize(new java.awt.Dimension(90, 19));
        AjoutMesurePays.add(txtAdhesionAjoutMesure, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 40, 50, 20));

        lblTransmissionAjoutMesure1.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblTransmissionAjoutMesure1.setForeground(new java.awt.Color(237, 235, 235));
        lblTransmissionAjoutMesure1.setText("Reduction du Taux Transmission(%)");
        AjoutMesurePays.add(lblTransmissionAjoutMesure1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, 220, 20));

        txtTransmissionAjoutMesure.setBackground(new java.awt.Color(140, 140, 140));
        txtTransmissionAjoutMesure.setText("8");
        txtTransmissionAjoutMesure.setPreferredSize(new java.awt.Dimension(90, 19));
        AjoutMesurePays.add(txtTransmissionAjoutMesure, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 60, 50, 20));

        txtReproductionAjoutMesure.setBackground(new java.awt.Color(140, 140, 140));
        txtReproductionAjoutMesure.setText("5");
        txtReproductionAjoutMesure.setPreferredSize(new java.awt.Dimension(90, 19));
        AjoutMesurePays.add(txtReproductionAjoutMesure, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, 50, 20));

        lblReproductionAjoutMesure.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblReproductionAjoutMesure.setForeground(new java.awt.Color(237, 235, 235));
        lblReproductionAjoutMesure.setText("Reduction du Taux Reproduction(%)");
        AjoutMesurePays.add(lblReproductionAjoutMesure, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, 220, 20));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/bgPanelPays.png"))); // NOI18N
        AjoutMesurePays.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1120, 110));

        panelGererMesures.addTab("Ajout Mesure de Pays", AjoutMesurePays);

        ModifierMesurePays.setBackground(new java.awt.Color(48, 48, 48));
        ModifierMesurePays.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNomModifMesurePays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblNomModifMesurePays.setForeground(new java.awt.Color(237, 235, 235));
        lblNomModifMesurePays.setText("Nom");
        ModifierMesurePays.add(lblNomModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, -1));

        lblSeuilLimiteModifMesurePays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblSeuilLimiteModifMesurePays.setForeground(new java.awt.Color(237, 235, 235));
        lblSeuilLimiteModifMesurePays.setText("Seuil Limite (%)");
        ModifierMesurePays.add(lblSeuilLimiteModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 90, 30));

        lblMesurePresentesModifMesurePays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblMesurePresentesModifMesurePays.setForeground(new java.awt.Color(237, 235, 235));
        lblMesurePresentesModifMesurePays.setText("Mesure Pays Presente");
        ModifierMesurePays.add(lblMesurePresentesModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 10, 140, 30));

        comboBoxPaysModifMesurePays.setBackground(new java.awt.Color(140, 140, 140));
        comboBoxPaysModifMesurePays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxPaysModifMesurePaysActionPerformed(evt);
            }
        });
        ModifierMesurePays.add(comboBoxPaysModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 130, -1));

        ListeModifMesurePays.setBackground(new java.awt.Color(140, 140, 140));
        ListeModifMesurePays.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        listeModifMesurePays.setBackground(new java.awt.Color(140, 140, 140));
        ListeModifMesurePays.setViewportView(listeModifMesurePays);

        ModifierMesurePays.add(ListeModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 40, 240, 70));

        txtNomModifMesurePays.setBackground(new java.awt.Color(140, 140, 140));
        txtNomModifMesurePays.setPreferredSize(new java.awt.Dimension(90, 19));
        txtNomModifMesurePays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomModifMesurePaysActionPerformed(evt);
            }
        });
        ModifierMesurePays.add(txtNomModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 140, -1));

        txtSeuilLimiteModifMesurePays.setBackground(new java.awt.Color(140, 140, 140));
        txtSeuilLimiteModifMesurePays.setPreferredSize(new java.awt.Dimension(90, 19));
        ModifierMesurePays.add(txtSeuilLimiteModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 20, 50, 20));

        btnSupprimerModifMesurePays.setBackground(new java.awt.Color(1, 1, 1));
        btnSupprimerModifMesurePays.setForeground(new java.awt.Color(254, 254, 254));
        btnSupprimerModifMesurePays.setText("Supprimer la Mesure");
        btnSupprimerModifMesurePays.setActionCommand("Ajouter La Frontiere");
        btnSupprimerModifMesurePays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSupprimerModifMesurePaysActionPerformed(evt);
            }
        });
        ModifierMesurePays.add(btnSupprimerModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 40, 120, 30));

        txtTransmissionModifMesurePays.setBackground(new java.awt.Color(140, 140, 140));
        txtTransmissionModifMesurePays.setPreferredSize(new java.awt.Dimension(90, 19));
        ModifierMesurePays.add(txtTransmissionModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 60, 50, 20));

        txtReproductionModifMesurePays.setBackground(new java.awt.Color(140, 140, 140));
        txtReproductionModifMesurePays.setPreferredSize(new java.awt.Dimension(90, 19));
        ModifierMesurePays.add(txtReproductionModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 80, 50, 20));

        lblTransmissionModifMesurePays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblTransmissionModifMesurePays.setForeground(new java.awt.Color(237, 235, 235));
        lblTransmissionModifMesurePays.setText("Reduction du Taux Transmission(%)");
        ModifierMesurePays.add(lblTransmissionModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 220, 20));

        lbladhesionModifMesurePays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lbladhesionModifMesurePays.setForeground(new java.awt.Color(237, 235, 235));
        lbladhesionModifMesurePays.setText("Taux Adhesion (%)");
        ModifierMesurePays.add(lbladhesionModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 40, 110, 20));

        txtAdhesionModifMesurePays.setBackground(new java.awt.Color(140, 140, 140));
        txtAdhesionModifMesurePays.setPreferredSize(new java.awt.Dimension(90, 19));
        ModifierMesurePays.add(txtAdhesionModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 40, 50, 20));

        lblReproductionModifMesurePays.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblReproductionModifMesurePays.setForeground(new java.awt.Color(237, 235, 235));
        lblReproductionModifMesurePays.setText("Reduction du Taux Reproduction(%)");
        ModifierMesurePays.add(lblReproductionModifMesurePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, 220, 20));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/bgPanelPays.png"))); // NOI18N
        ModifierMesurePays.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, -10, 1040, 130));

        panelGererMesures.addTab("Modification Mesure de Pays", ModifierMesurePays);

        getContentPane().add(panelGererMesures, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 850, 1190, 120));

        panelGererStatsPays.setBackground(new java.awt.Color(1, 1, 1));
        panelGererStatsPays.setForeground(new java.awt.Color(254, 254, 254));

        labelAfficherStatsPays.setFont(new java.awt.Font("URW Bookman", 1, 14)); // NOI18N
        labelAfficherStatsPays.setForeground(new java.awt.Color(237, 235, 235));
        labelAfficherStatsPays.setText("Passez la souris sur un pays pour visionner son Graphe de Statistiques ici");

        javax.swing.GroupLayout panelGererStatsPaysLayout = new javax.swing.GroupLayout(panelGererStatsPays);
        panelGererStatsPays.setLayout(panelGererStatsPaysLayout);
        panelGererStatsPaysLayout.setHorizontalGroup(
            panelGererStatsPaysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGererStatsPaysLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(labelAfficherStatsPays, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelGererStatsPaysLayout.setVerticalGroup(
            panelGererStatsPaysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGererStatsPaysLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(labelAfficherStatsPays, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        getContentPane().add(panelGererStatsPays, new org.netbeans.lib.awtextra.AbsoluteConstraints(872, 849, 650, 120));
        panelMesuresSan.setVisible(false);

        panelFrontieresOuvertGraphePays.setBackground(new java.awt.Color(0, 0, 0));
        panelFrontieresOuvertGraphePays.setForeground(new java.awt.Color(255, 255, 255));

        jScrollPane2.setBackground(new java.awt.Color(0, 0, 0));
        jScrollPane2.setForeground(new java.awt.Color(255, 255, 255));

        listAfficherFrontiereOuvertureStats.setBackground(new java.awt.Color(0, 0, 0));
        listAfficherFrontiereOuvertureStats.setForeground(new java.awt.Color(150, 150, 150));
        jScrollPane2.setViewportView(listAfficherFrontiereOuvertureStats);

        javax.swing.GroupLayout panelFrontieresOuvertGraphePaysLayout = new javax.swing.GroupLayout(panelFrontieresOuvertGraphePays);
        panelFrontieresOuvertGraphePays.setLayout(panelFrontieresOuvertGraphePaysLayout);
        panelFrontieresOuvertGraphePaysLayout.setHorizontalGroup(
            panelFrontieresOuvertGraphePaysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
        );
        panelFrontieresOuvertGraphePaysLayout.setVerticalGroup(
            panelFrontieresOuvertGraphePaysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        getContentPane().add(panelFrontieresOuvertGraphePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(1530, 940, 360, 100));
        panelMesuresSan.setVisible(false);

        panelMesureGraphePays.setBackground(new java.awt.Color(0, 0, 0));
        panelMesureGraphePays.setForeground(new java.awt.Color(255, 255, 255));
        panelMesureGraphePays.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelMesureActive.setBackground(new java.awt.Color(1, 1, 1));
        labelMesureActive.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        labelMesureActive.setForeground(new java.awt.Color(255, 255, 255));
        labelMesureActive.setText("Passer La souris sur un pays pour voir l'activation des mesures");
        panelMesureGraphePays.add(labelMesureActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 320, 50));

        jScrollPaneMesureActive.setBackground(new java.awt.Color(0, 0, 0));
        jScrollPaneMesureActive.setForeground(new java.awt.Color(255, 255, 255));

        listAfficherMesureActiveStats.setBackground(new java.awt.Color(0, 0, 0));
        listAfficherMesureActiveStats.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPaneMesureActive.setViewportView(listAfficherMesureActiveStats);

        panelMesureGraphePays.add(jScrollPaneMesureActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 360, 100));

        getContentPane().add(panelMesureGraphePays, new org.netbeans.lib.awtextra.AbsoluteConstraints(1530, 840, 360, 100));
        panelMesuresSan.setVisible(false);

        panelGererStatsCarte.setBackground(new java.awt.Color(97, 97, 97));

        javax.swing.GroupLayout panelGererStatsCarteLayout = new javax.swing.GroupLayout(panelGererStatsCarte);
        panelGererStatsCarte.setLayout(panelGererStatsCarteLayout);
        panelGererStatsCarteLayout.setHorizontalGroup(
            panelGererStatsCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
        );
        panelGererStatsCarteLayout.setVerticalGroup(
            panelGererStatsCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );

        getContentPane().add(panelGererStatsCarte, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 850, 580, 120));
        panelMesuresSan.setVisible(false);

        panelGererMaladie.setBackground(new java.awt.Color(97, 97, 97));
        panelGererMaladie.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTauxMortalite.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        labelTauxMortalite.setForeground(new java.awt.Color(237, 235, 235));
        labelTauxMortalite.setText("Taux de  Mortalite (%)");
        labelTauxMortalite.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        panelGererMaladie.add(labelTauxMortalite, new org.netbeans.lib.awtextra.AbsoluteConstraints(199, 30, -1, -1));

        labelTauxReproduction.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        labelTauxReproduction.setForeground(new java.awt.Color(237, 235, 235));
        labelTauxReproduction.setText("Reproduction");
        labelTauxReproduction.setAlignmentX(0.5F);
        panelGererMaladie.add(labelTauxReproduction, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 50, -1, -1));

        btnAjouterMaladie.setBackground(new java.awt.Color(1, 1, 1));
        btnAjouterMaladie.setForeground(new java.awt.Color(254, 254, 254));
        btnAjouterMaladie.setText("Ajouter la Maladie");
        btnAjouterMaladie.setActionCommand("Ajouter La Frontiere");
        btnAjouterMaladie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjouterMaladieActionPerformed(evt);
            }
        });
        panelGererMaladie.add(btnAjouterMaladie, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 20, 130, 30));

        btnChoisirMaladie.setBackground(new java.awt.Color(1, 1, 1));
        btnChoisirMaladie.setForeground(new java.awt.Color(254, 254, 254));
        btnChoisirMaladie.setText("Choisir la Maladie");
        btnChoisirMaladie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChoisirMaladieActionPerformed(evt);
            }
        });
        panelGererMaladie.add(btnChoisirMaladie, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 50, 180, 20));

        btnSupprimerMaladie.setBackground(new java.awt.Color(1, 1, 1));
        btnSupprimerMaladie.setForeground(new java.awt.Color(254, 254, 254));
        btnSupprimerMaladie.setText("Supprimer la Maladie");
        btnSupprimerMaladie.setActionCommand("Ajouter La Frontiere");
        btnSupprimerMaladie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSupprimerMaladieActionPerformed(evt);
            }
        });
        panelGererMaladie.add(btnSupprimerMaladie, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 10, 190, 20));

        listeMaladies.setBackground(new java.awt.Color(140, 140, 140));
        listeMaladies.setForeground(new java.awt.Color(38, 38, 38));
        ScrollPane_Maladie.setViewportView(listeMaladies);

        panelGererMaladie.add(ScrollPane_Maladie, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 190, 60));

        labelTauxGuerison.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        labelTauxGuerison.setForeground(new java.awt.Color(237, 235, 235));
        labelTauxGuerison.setText("Taux de Guerison (%)");
        panelGererMaladie.add(labelTauxGuerison, new org.netbeans.lib.awtextra.AbsoluteConstraints(199, 10, -1, -1));

        pourcentage1.setFont(new java.awt.Font("URW Bookman", 1, 10)); // NOI18N
        pourcentage1.setForeground(new java.awt.Color(237, 235, 235));
        pourcentage1.setText("Nom :");
        panelGererMaladie.add(pourcentage1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, -1, -1));

        labelPossibilites.setFont(new java.awt.Font("URW Bookman", 1, 10)); // NOI18N
        labelPossibilites.setForeground(new java.awt.Color(237, 235, 235));
        labelPossibilites.setText("Maladies possible");
        panelGererMaladie.add(labelPossibilites, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, -1, -1));

        jRadiobtnAddMaladie.setBackground(new java.awt.Color(1, 1, 1));
        jRadiobtnAddMaladie.setForeground(new java.awt.Color(254, 254, 254));
        jRadiobtnAddMaladie.setText("Ajouter Maladie");
        jRadiobtnAddMaladie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadiobtnAddMaladieActionPerformed(evt);
            }
        });
        panelGererMaladie.add(jRadiobtnAddMaladie, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 10, 130, 30));

        txtTauxReproduction.setBackground(new java.awt.Color(140, 140, 140));
        txtTauxReproduction.setFont(new java.awt.Font("URW Bookman", 1, 10)); // NOI18N
        txtTauxReproduction.setPreferredSize(new java.awt.Dimension(90, 19));
        panelGererMaladie.add(txtTauxReproduction, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 50, 70, -1));

        txtTauxMortalite.setBackground(new java.awt.Color(140, 140, 140));
        txtTauxMortalite.setFont(new java.awt.Font("URW Bookman", 1, 10)); // NOI18N
        txtTauxMortalite.setPreferredSize(new java.awt.Dimension(90, 19));
        panelGererMaladie.add(txtTauxMortalite, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 30, 70, -1));

        txtTauxGuerison.setBackground(new java.awt.Color(140, 140, 140));
        txtTauxGuerison.setFont(new java.awt.Font("URW Bookman", 1, 10)); // NOI18N
        txtTauxGuerison.setPreferredSize(new java.awt.Dimension(90, 19));
        panelGererMaladie.add(txtTauxGuerison, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 70, -1));

        txtNomMaladie.setBackground(new java.awt.Color(140, 140, 140));
        txtNomMaladie.setFont(new java.awt.Font("URW Bookman", 1, 10)); // NOI18N
        txtNomMaladie.setPreferredSize(new java.awt.Dimension(90, 19));
        panelGererMaladie.add(txtNomMaladie, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, -1, -1));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/bgPanelPays.png"))); // NOI18N
        panelGererMaladie.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1180, 100));

        getContentPane().add(panelGererMaladie, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 850, -1, 100));
        panelMesuresSan.setVisible(false);

        panelGererFrontiere.setBackground(new java.awt.Color(1, 1, 1));
        panelGererFrontiere.setForeground(new java.awt.Color(99, 99, 99));
        panelGererFrontiere.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        panelGererFrontiere.setAlignmentX(0.0F);
        panelGererFrontiere.setAlignmentY(0.0F);
        panelGererFrontiere.setDoubleBuffered(true);
        panelGererFrontiere.setMinimumSize(new java.awt.Dimension(1199, 150));
        panelGererFrontiere.setOpaque(true);
        panelGererFrontiere.setPreferredSize(new java.awt.Dimension(1199, 150));
        panelGererFrontiere.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panelGererFrontiereStateChanged(evt);
            }
        });

        AjouterFrontiere.setBackground(new java.awt.Color(48, 48, 48));
        AjouterFrontiere.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNomPaysAjoutFront.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        lblNomPaysAjoutFront.setForeground(new java.awt.Color(237, 235, 235));
        lblNomPaysAjoutFront.setText("Choisissez vos pays");
        AjouterFrontiere.add(lblNomPaysAjoutFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 0, 130, -1));

        lblTypeAjoutFront.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblTypeAjoutFront.setForeground(new java.awt.Color(237, 235, 235));
        lblTypeAjoutFront.setText("Type de frontiere");
        lblTypeAjoutFront.setAlignmentX(0.5F);
        AjouterFrontiere.add(lblTypeAjoutFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, -1, -1));

        lblSeuilFermetureAjoutFront.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblSeuilFermetureAjoutFront.setForeground(new java.awt.Color(237, 235, 235));
        lblSeuilFermetureAjoutFront.setText("Seuil de fermeture (%) :");
        AjouterFrontiere.add(lblSeuilFermetureAjoutFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 50, -1, -1));

        lblTauxTransmissionAjoutFront.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblTauxTransmissionAjoutFront.setForeground(new java.awt.Color(237, 235, 235));
        lblTauxTransmissionAjoutFront.setText("Taux Transmission (%)");
        lblTauxTransmissionAjoutFront.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        AjouterFrontiere.add(lblTauxTransmissionAjoutFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 70, -1, -1));

        lblTauxadhesionAjoutFront1.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblTauxadhesionAjoutFront1.setForeground(new java.awt.Color(237, 235, 235));
        lblTauxadhesionAjoutFront1.setText("Taux  Adhesion (%)");
        lblTauxadhesionAjoutFront1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        AjouterFrontiere.add(lblTauxadhesionAjoutFront1, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 70, -1, -1));

        txtSeuilFermetureFront.setBackground(new java.awt.Color(140, 140, 140));
        txtSeuilFermetureFront.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        txtSeuilFermetureFront.setText("40");
        txtSeuilFermetureFront.setPreferredSize(new java.awt.Dimension(90, 19));
        txtSeuilFermetureFront.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSeuilFermetureFrontActionPerformed(evt);
            }
        });
        AjouterFrontiere.add(txtSeuilFermetureFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 50, 70, -1));

        comboBoxTypeAjoutFront.setBackground(new java.awt.Color(140, 140, 140));
        comboBoxTypeAjoutFront.setForeground(new java.awt.Color(1, 1, 1));
        comboBoxTypeAjoutFront.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "terrestre", "aerienne", "maritime" }));
        comboBoxTypeAjoutFront.setPreferredSize(new java.awt.Dimension(80, 19));
        AjouterFrontiere.add(comboBoxTypeAjoutFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, 110, -1));

        btnAjouterFront.setBackground(new java.awt.Color(1, 1, 1));
        btnAjouterFront.setForeground(new java.awt.Color(254, 254, 254));
        btnAjouterFront.setText("Ajouter la Frontiere");
        btnAjouterFront.setActionCommand("Ajouter La Frontiere");
        btnAjouterFront.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjouterFrontActionPerformed(evt);
            }
        });
        AjouterFrontiere.add(btnAjouterFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 40, 130, 30));

        txtTauxTransmissionAjoutFront.setBackground(new java.awt.Color(140, 140, 140));
        txtTauxTransmissionAjoutFront.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        txtTauxTransmissionAjoutFront.setText("5");
        txtTauxTransmissionAjoutFront.setToolTipText("");
        txtTauxTransmissionAjoutFront.setPreferredSize(new java.awt.Dimension(90, 19));
        AjouterFrontiere.add(txtTauxTransmissionAjoutFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 70, 40, -1));

        txtTauxAdhesionAjoutFront1.setBackground(new java.awt.Color(140, 140, 140));
        txtTauxAdhesionAjoutFront1.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        txtTauxAdhesionAjoutFront1.setText("30");
        txtTauxAdhesionAjoutFront1.setPreferredSize(new java.awt.Dimension(90, 19));
        AjouterFrontiere.add(txtTauxAdhesionAjoutFront1, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 70, 70, -1));

        comboBoxPays1AjoutFront.setBackground(new java.awt.Color(140, 140, 140));
        comboBoxPays1AjoutFront.setForeground(new java.awt.Color(8, 1, 1));
        comboBoxPays1AjoutFront.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aucun" }));
        comboBoxPays1AjoutFront.setPreferredSize(new java.awt.Dimension(120, 19));
        AjouterFrontiere.add(comboBoxPays1AjoutFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, 140, -1));

        toggleMesureAjouterFrontiere.setText("Ajouter Mesure dessus ce lien");
        toggleMesureAjouterFrontiere.setActionCommand("");
        toggleMesureAjouterFrontiere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleMesureAjouterFrontiereActionPerformed(evt);
            }
        });
        AjouterFrontiere.add(toggleMesureAjouterFrontiere, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 200, -1));

        comboBoxPays2AjoutFront.setBackground(new java.awt.Color(140, 140, 140));
        comboBoxPays2AjoutFront.setForeground(new java.awt.Color(1, 1, 1));
        comboBoxPays2AjoutFront.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aucun" }));
        comboBoxPays2AjoutFront.setPreferredSize(new java.awt.Dimension(120, 19));
        AjouterFrontiere.add(comboBoxPays2AjoutFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 140, -1));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/bgPanelPays.png"))); // NOI18N
        AjouterFrontiere.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -30, -1, 180));

        panelGererFrontiere.addTab("Ajouter Frontiere", AjouterFrontiere);

        ModifierFrontiere.setBackground(new java.awt.Color(48, 48, 48));
        ModifierFrontiere.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbFrontPresentes.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lbFrontPresentes.setForeground(new java.awt.Color(237, 235, 235));
        lbFrontPresentes.setText("Frontieres Existantes:");
        ModifierFrontiere.add(lbFrontPresentes, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 10, -1, -1));

        lblTransmissionModifFront.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblTransmissionModifFront.setForeground(new java.awt.Color(237, 235, 235));
        lblTransmissionModifFront.setText("Taux Transmission (%)");
        lblTransmissionModifFront.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        ModifierFrontiere.add(lblTransmissionModifFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, -1, -1));

        lblTauxAdhesionModifFront1.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblTauxAdhesionModifFront1.setForeground(new java.awt.Color(237, 235, 235));
        lblTauxAdhesionModifFront1.setText("Taux Adhesion (%)");
        lblTauxAdhesionModifFront1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        ModifierFrontiere.add(lblTauxAdhesionModifFront1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, 140, 20));

        lblSeuilModifFront.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        lblSeuilModifFront.setForeground(new java.awt.Color(237, 235, 235));
        lblSeuilModifFront.setText("Seuil de Fermeture (%) :");
        ModifierFrontiere.add(lblSeuilModifFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, -1, -1));

        toggleMesureModifierFrontiere.setText("Ajouter Mesure dessus ce lien");
        toggleMesureModifierFrontiere.setActionCommand("");
        ModifierFrontiere.add(toggleMesureModifierFrontiere, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 200, -1));

        txtSeuilModifFront1.setBackground(new java.awt.Color(140, 140, 140));
        txtSeuilModifFront1.setPreferredSize(new java.awt.Dimension(90, 19));
        ModifierFrontiere.add(txtSeuilModifFront1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 70, 70, 20));

        listeModifFrontieres.setBackground(new java.awt.Color(140, 140, 140));
        listeModifFrontieres.setForeground(new java.awt.Color(1, 1, 1));
        jScrollPaneModifFront.setViewportView(listeModifFrontieres);

        ModifierFrontiere.add(jScrollPaneModifFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 30, 280, 70));

        txtTransmissionModifFront.setBackground(new java.awt.Color(140, 140, 140));
        txtTransmissionModifFront.setPreferredSize(new java.awt.Dimension(90, 19));
        txtTransmissionModifFront.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransmissionModifFrontActionPerformed(evt);
            }
        });
        ModifierFrontiere.add(txtTransmissionModifFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 70, 20));

        txtTauxAdhesionModifFront1.setBackground(new java.awt.Color(140, 140, 140));
        txtTauxAdhesionModifFront1.setPreferredSize(new java.awt.Dimension(90, 19));
        ModifierFrontiere.add(txtTauxAdhesionModifFront1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 90, 70, -1));

        btnSupprimerModifFront.setBackground(new java.awt.Color(1, 1, 1));
        btnSupprimerModifFront.setForeground(new java.awt.Color(254, 254, 254));
        btnSupprimerModifFront.setText("Supprimer la frontiere");
        btnSupprimerModifFront.setActionCommand("Ajouter La Frontiere");
        btnSupprimerModifFront.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSupprimerModifFrontActionPerformed(evt);
            }
        });
        ModifierFrontiere.add(btnSupprimerModifFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 50, 170, 30));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/bgPanelPays.png"))); // NOI18N
        ModifierFrontiere.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -30, -1, 180));

        panelGererFrontiere.addTab("Modifier Frontiere", ModifierFrontiere);

        getContentPane().add(panelGererFrontiere, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 850, 1070, 120));

        panelGererSimulations.setBackground(new java.awt.Color(97, 97, 97));
        panelGererSimulations.setPreferredSize(new java.awt.Dimension(1200, 150));
        panelGererSimulations.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelNomSimulation.setFont(new java.awt.Font("URW Bookman", 1, 12)); // NOI18N
        labelNomSimulation.setForeground(new java.awt.Color(237, 235, 235));
        labelNomSimulation.setText("Nom");
        panelGererSimulations.add(labelNomSimulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 30, -1));

        btnChoisirSimulation.setBackground(new java.awt.Color(1, 1, 1));
        btnChoisirSimulation.setForeground(new java.awt.Color(254, 254, 254));
        btnChoisirSimulation.setText("Charger Simulation");
        btnChoisirSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChoisirSimulationActionPerformed(evt);
            }
        });
        panelGererSimulations.add(btnChoisirSimulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 60, 160, 30));

        btnSupprimerSimulation.setBackground(new java.awt.Color(1, 1, 1));
        btnSupprimerSimulation.setForeground(new java.awt.Color(254, 254, 254));
        btnSupprimerSimulation.setText("Supprimer Simulation");
        btnSupprimerSimulation.setActionCommand("Ajouter La Frontiere");
        btnSupprimerSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSupprimerSimulationActionPerformed(evt);
            }
        });
        panelGererSimulations.add(btnSupprimerSimulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 20, 160, 30));

        listeSimulations.setBackground(new java.awt.Color(140, 140, 140));
        listeSimulations.setForeground(new java.awt.Color(38, 38, 38));
        ScrollPane_Simulation.setViewportView(listeSimulations);

        panelGererSimulations.add(ScrollPane_Simulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 20, 280, 80));

        labelSimulationPossible.setFont(new java.awt.Font("URW Bookman", 1, 10)); // NOI18N
        labelSimulationPossible.setForeground(new java.awt.Color(237, 235, 235));
        labelSimulationPossible.setText("Simulations Sauver");
        panelGererSimulations.add(labelSimulationPossible, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 0, -1, -1));

        txtNomSimulation.setBackground(new java.awt.Color(140, 140, 140));
        txtNomSimulation.setFont(new java.awt.Font("URW Bookman", 1, 10)); // NOI18N
        txtNomSimulation.setPreferredSize(new java.awt.Dimension(90, 19));
        panelGererSimulations.add(txtNomSimulation, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 190, -1));

        getContentPane().add(panelGererSimulations, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 850, 1210, 120));
        panelMesuresSan.setVisible(false);

        panelMesuresSan.setBackground(new java.awt.Color(48, 48, 48));
        panelMesuresSan.setPreferredSize(new java.awt.Dimension(955, 101));

        javax.swing.GroupLayout panelMesuresSanLayout = new javax.swing.GroupLayout(panelMesuresSan);
        panelMesuresSan.setLayout(panelMesuresSanLayout);
        panelMesuresSanLayout.setHorizontalGroup(
            panelMesuresSanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1080, Short.MAX_VALUE)
        );
        panelMesuresSanLayout.setVerticalGroup(
            panelMesuresSanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );

        getContentPane().add(panelMesuresSan, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 850, 1080, 120));
        panelMesuresSan.setVisible(false);

        moveUp1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/transup.png"))); // NOI18N
        moveUp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUp1ActionPerformed(evt);
            }
        });
        getContentPane().add(moveUp1, new org.netbeans.lib.awtextra.AbsoluteConstraints(325, 80, 40, 30));

        moveLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/transleft.png"))); // NOI18N
        moveLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                moveLeftMouseReleased(evt);
            }
        });
        moveLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveLeftActionPerformed(evt);
            }
        });
        getContentPane().add(moveLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 120, 33, 37));

        moveDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/transdown.png"))); // NOI18N
        moveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownActionPerformed(evt);
            }
        });
        getContentPane().add(moveDown, new org.netbeans.lib.awtextra.AbsoluteConstraints(325, 170, 40, 29));

        moveRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/transright.png"))); // NOI18N
        moveRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveRightActionPerformed(evt);
            }
        });
        getContentPane().add(moveRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 120, 33, 37));

        jToggleDeplacement.setBackground(new java.awt.Color(1, 1, 1));
        jToggleDeplacement.setForeground(new java.awt.Color(238, 238, 238));
        jToggleDeplacement.setText("ON/OFF  -- > REDIM - MOVE  -- > pays / régions");
        jToggleDeplacement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jToggleDeplacementMousePressed(evt);
            }
        });
        jToggleDeplacement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleDeplacementActionPerformed(evt);
            }
        });
        getContentPane().add(jToggleDeplacement, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 80, -1, 25));

        btnScreenShot.setBackground(new java.awt.Color(1, 1, 1));
        btnScreenShot.setForeground(new java.awt.Color(238, 238, 238));
        btnScreenShot.setText("CAPTURER LA VUE COURANTE");
        btnScreenShot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnScreenShotMousePressed(evt);
            }
        });
        btnScreenShot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScreenShotActionPerformed(evt);
            }
        });
        getContentPane().add(btnScreenShot, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 80, 290, 25));

        btnCSVStats.setBackground(new java.awt.Color(1, 1, 1));
        btnCSVStats.setForeground(new java.awt.Color(238, 238, 238));
        btnCSVStats.setText("Export Stats - CSV");
        btnCSVStats.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnCSVStatsMousePressed(evt);
            }
        });
        btnCSVStats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCSVStatsActionPerformed(evt);
            }
        });
        getContentPane().add(btnCSVStats, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 80, 290, 25));

        drawingStats1.setBackground(new java.awt.Color(48, 48, 48));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/statsbg.png"))); // NOI18N
        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout drawingStats1Layout = new javax.swing.GroupLayout(drawingStats1);
        drawingStats1.setLayout(drawingStats1Layout);
        drawingStats1Layout.setHorizontalGroup(
            drawingStats1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 930, Short.MAX_VALUE)
        );
        drawingStats1Layout.setVerticalGroup(
            drawingStats1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, Short.MAX_VALUE)
        );

        getContentPane().add(drawingStats1, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 120, 930, 110));

        talkToUser1.setBackground(new java.awt.Color(48, 48, 48));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/statsbg.png"))); // NOI18N

        javax.swing.GroupLayout talkToUser1Layout = new javax.swing.GroupLayout(talkToUser1);
        talkToUser1.setLayout(talkToUser1Layout);
        talkToUser1Layout.setHorizontalGroup(
            talkToUser1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, talkToUser1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 838, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(697, 697, 697))
        );
        talkToUser1Layout.setVerticalGroup(
            talkToUser1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, Short.MAX_VALUE)
        );

        getContentPane().add(talkToUser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 725, 650, -1));

        drawingPanel.setBackground(new java.awt.Color(23, 23, 23));
        drawingPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                drawingPanelMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                drawingPanelMouseMoved(evt);
            }
        });
        drawingPanel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                drawingPanelMouseWheelMoved(evt);
            }
        });
        drawingPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                drawingPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                drawingPanelMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                drawingPanelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                drawingPanelMouseReleased(evt);
            }
        });
        drawingPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                drawingPanelKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                drawingPanelKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                drawingPanelKeyTyped(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/mapmonde.png"))); // NOI18N
        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout drawingPanelLayout = new javax.swing.GroupLayout(drawingPanel);
        drawingPanel.setLayout(drawingPanelLayout);
        drawingPanelLayout.setHorizontalGroup(
            drawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(drawingPanelLayout.createSequentialGroup()
                .addGap(292, 292, 292)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        drawingPanelLayout.setVerticalGroup(
            drawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, drawingPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );

        getContentPane().add(drawingPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 70, 1800, 775));

        lblBG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/bg.png"))); // NOI18N
        lblBG.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                lblBGMouseWheelMoved(evt);
            }
        });
        getContentPane().add(lblBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 2620, 1090));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyPressed

    private void lblBGMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_lblBGMouseWheelMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_lblBGMouseWheelMoved

    private void btnEnregistrerSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnregistrerSimulationActionPerformed
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();       
        controller.sauverSimulationPasser(dtf.format(now));
    }//GEN-LAST:event_btnEnregistrerSimulationActionPerformed

    private void btnNaviguerSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNaviguerSimulationActionPerformed
        updatePanelVisibility(false, false, false, false, false, true, false, false, false, false);
        refreshListeSimulation();
        listeSimulations.clearSelection();
        btnSupprimerSimulation.setEnabled(false);
        btnChoisirSimulation.setEnabled(false);
    }//GEN-LAST:event_btnNaviguerSimulationActionPerformed

    private void btnChoisirSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoisirSimulationActionPerformed
        if (! listeSimulations.isSelectionEmpty())
        {
            controller.chargerSimulationPasser(listeSimulations.getSelectedValue());
            SliderJourPasser.setMaximum(controller.getDtoCarteDuMonde().m_listeSimulationPasser.size()); 
        }
    }//GEN-LAST:event_btnChoisirSimulationActionPerformed

    private void btnSupprimerSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSupprimerSimulationActionPerformed
        
        if (! listeSimulations.isSelectionEmpty())
        {
            controller.deleteSimulationPasser(listeSimulations.getSelectedValue());    
            refreshListeSimulation();
            listeSimulations.clearSelection();
            updateSimlationPasserListenersShouldBeTriggered=false;
            txtNomSimulation.setText("");
            updateSimlationPasserListenersShouldBeTriggered=true;  
        }
    }//GEN-LAST:event_btnSupprimerSimulationActionPerformed

    private void btnUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUndoActionPerformed
        controller.retourDEtat();
        refreshAll();
    }//GEN-LAST:event_btnUndoActionPerformed

    private void btnRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedoActionPerformed
        controller.avanceDEtat();
        refreshAll();
    }//GEN-LAST:event_btnRedoActionPerformed

    private void txtTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTimeActionPerformed

    private void txtNomAjoutMesureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomAjoutMesureActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomAjoutMesureActionPerformed

    private void txtTransmissionModifFrontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransmissionModifFrontActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTransmissionModifFrontActionPerformed

    private void btnSupprimerModifMesurePaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSupprimerModifMesurePaysActionPerformed
        controller.deleteMesureToListMesurePays(comboBoxPaysModifMesurePays.getSelectedItem().toString(), txtNomModifMesurePays.getText());
        refreshListeMesurePays(comboBoxPaysModifMesurePays.getSelectedItem().toString());
        listeModifMesurePays.clearSelection();
        txtNomModifMesurePays.setText("");
        txtAdhesionModifMesurePays.setText("0");
        txtTransmissionModifMesurePays.setText("0");
        txtReproductionModifMesurePays.setText("0");
        txtSeuilLimiteModifMesurePays.setText("0");

    }//GEN-LAST:event_btnSupprimerModifMesurePaysActionPerformed

    private void txtNomModifMesurePaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomModifMesurePaysActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomModifMesurePaysActionPerformed

    private void comboBoxPaysModifMesurePaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxPaysModifMesurePaysActionPerformed
        updateMesurePaysListenersShouldBeTriggered = false;
        refreshListeMesurePays(comboBoxPaysModifMesurePays.getSelectedItem().toString());
        updateMesurePaysListenersShouldBeTriggered = true;
    }//GEN-LAST:event_comboBoxPaysModifMesurePaysActionPerformed

    private void btnCSVStatsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCSVStatsMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCSVStatsMousePressed

    private void btnCSVStatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCSVStatsActionPerformed
        controller.saveCSV();        // TODO add your handling code here:
    }//GEN-LAST:event_btnCSVStatsActionPerformed

    private void toggleMesureAjouterFrontiereActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleMesureAjouterFrontiereActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_toggleMesureAjouterFrontiereActionPerformed

    private void txtSeuilFermetureFrontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSeuilFermetureFrontActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSeuilFermetureFrontActionPerformed

    private void btnAjouterAjoutMesureActionPerformed(java.awt.event.ActionEvent evt){      
        String pays = comboBoxPaysAjoutMesurePays.getSelectedItem().toString();
        if (AddMesureTextFieldsAreValid(txtNomAjoutMesure.getText(),
                                     txtSeuilLimiteAjoutMesure.getText(),                                    
                                     txtAdhesionAjoutMesure.getText(),
                                     txtTransmissionAjoutMesure.getText(),
                                     txtReproductionAjoutMesure.getText()))
                {
                double adhesion = Double.parseDouble(txtAdhesionAjoutMesure.getText());
                double transmission = Double.parseDouble(txtTransmissionAjoutMesure.getText());
                double reproduction = Double.parseDouble(txtReproductionAjoutMesure.getText());
                String nom = txtNomAjoutMesure.getText();
                double p_seuil = Double.parseDouble(txtSeuilLimiteAjoutMesure.getText());
                this.controller.addMesureToListMesurePays(pays,nom, p_seuil,  adhesion,transmission,reproduction);            
            } 
        else {
                SimudemieController.TalkToUser("Vos donnees ne sont pas valide");
             }

        refreshListeMesurePays(comboBoxPaysAjoutMesurePays.getSelectedItem().toString());
    }
    private void comboBoxPaysAjoutMesurePaysActionPerformed(java.awt.event.ActionEvent evt){    
    
    }
    
    private void  updatePanelVisibility(boolean zoom, boolean frontiere, boolean maladie, boolean mesure, boolean forme, boolean simulation, boolean statsCarte, boolean statsPays, boolean afficheMesureActive, boolean afficheFrontiereOuvert)
    {
        panelGererFrontiere.setVisible(frontiere);
        panelGererMaladie.setVisible(maladie);
        panelGererMesures.setVisible(mesure);
        panelGererFormes.setVisible(forme);
        panelGererSimulations.setVisible(simulation);
        panelGererStatsCarte.setVisible(statsCarte);
        panelGererStatsPays.setVisible(statsPays);
        panelMesureGraphePays.setVisible(afficheMesureActive);
        panelFrontieresOuvertGraphePays.setVisible(afficheFrontiereOuvert);
    }
    
    private void SetUpMaladieCourant()
    {
        lblNomMaladie.setText("MALADIE : " +  controller.getDtoMaladieCourante().m_nom);
        txtGuerison.setText(controller.getDtoMaladieCourante().m_guerison * 100 + "%");
        txtMorta.setText(controller.getDtoMaladieCourante().m_mortalite * 100 + "%");
        txtReprod.setText(controller.getDtoMaladieCourante().m_reproduction.toString()); 
    }
    
    private void modfierPays() {
        int p_taille = Integer.parseInt(txtTaillePopModifPays.getText());
        int p_nbInfecter = Integer.parseInt(txtNbInfecterModifPays.getText());
        double p_taux =Double.parseDouble(txtTransmissionRegionModifPays.getText());

        if (p_nbInfecter <= p_taille) {
            this.controller.modifierPays(ancienneValeurSauver, txtNomModifPays.getText(), p_taille, p_nbInfecter, p_taux);
            
        } else {
            SimudemieController.TalkToUser("Vous devez rentrer uniquement des chiffres et ca doit etre non vide");
            SimudemieController.TalkToUser("La taille de la population doit etre superieur aux nombre d infecter ");
        }
    }
    
     private void refreshListeSimulation ()
     {
        listeModelSimulationPasser.setSize(controller.getDtoCarteDuMonde().m_listeSimulationPasser.size());
        for (int i = 0; i < controller.getDtoCarteDuMonde().m_listeSimulationPasser.size(); i++) {
            listeModelSimulationPasser.setElementAt(controller.getDtoCarteDuMonde().m_listeSimulationPasser.get(i).m_nom, i);
        }
     }
            
    private void refreshListePaysJList() {
        listeModelPays.setSize(controller.getDtoCarteDuMonde().m_listePays.size());
        for (int i = 0; i < controller.getDtoCarteDuMonde().m_listePays.size(); i++) {
            listeModelPays.setElementAt(controller.getDtoCarteDuMonde().m_listePays.get(i).m_nom, i);
        }
    }

    private void refreshTableRegion(String nomPays) {
        RegionTablemodel.setRowCount(controller.getPaysdeCarteDto(nomPays).m_listeRegion.size());

        for (int i = 0; i < controller.getPaysdeCarteDto(nomPays).m_listeRegion.size(); i++) {
            RegionDto n = controller.getPaysdeCarteDto(nomPays).m_listeRegion.get(i);
            RegionTablemodel.setValueAt(n.m_nom, i, 0);
            RegionTablemodel.setValueAt(n.m_pourcentagePop, i, 1);
        }
    }

    private void refreshListePaysComboBox(JComboBox<String> combobox) {
        String[] paysNoms = new String[controller.getDtoCarteDuMonde().m_listePays.size()];
        int x = 0;
        for (PaysDto i : controller.getDtoCarteDuMonde().m_listePays) {
            paysNoms[x] = (i.m_nom);
            x++;
        }
        defaultComboBoxModelPays = new DefaultComboBoxModel(paysNoms);
        combobox.setModel(defaultComboBoxModelPays);
    }

    private void refreshListeRegionComboBox(String nomPays, JComboBox<String> combobox) {
        String[] regionNom = new String[controller.getPaysdeCarteDto(nomPays).m_listeRegion.size()];
        int x = 0;
        for (RegionDto i : controller.getPaysdeCarteDto(nomPays).m_listeRegion) {
            regionNom[x] = (i.m_nom);
            x++;
        }
        defaultComboBoxModelRegion = new DefaultComboBoxModel(regionNom);
        combobox.setModel(defaultComboBoxModelRegion);
    }

    private void modfierMesurePays() {
        if (AddMesureTextFieldsAreValid(txtNomModifMesurePays.getText(),
                                     txtSeuilLimiteModifMesurePays.getText(),
                                     txtAdhesionModifMesurePays.getText(),
                                     txtTransmissionModifMesurePays.getText(),
                                     txtReproductionModifMesurePays.getText()))
        {
            String pays = comboBoxPaysModifMesurePays.getSelectedItem().toString();
            double adhesion = Double.parseDouble(txtAdhesionModifMesurePays.getText()); 
            double transmission = Double.parseDouble(txtTransmissionModifMesurePays.getText()); 
            double reproduction = Double.parseDouble(txtReproductionModifMesurePays.getText()); 
            String nouveauNom = txtNomModifMesurePays.getText();          
            double p_seuil = Double.parseDouble(txtSeuilLimiteModifMesurePays.getText());
            controller.modifierMesurePaysDeCarte(pays, nomMesureSauver, nouveauNom, p_seuil, adhesion,transmission,reproduction );
        }
    }

    public void refreshListeMesurePays(String nomPays) {       
       dlmListeMesurePays.setSize(controller.getPaysdeCarteDto(nomPays).m_listeMesureActive.size());
        for (int i = 0; i < controller.getPaysdeCarteDto(nomPays).m_listeMesureActive.size(); i++) {
            dlmListeMesurePays.setElementAt(controller.getPaysdeCarteDto(nomPays).m_listeMesureActive.get(i).m_nom, i);
        }
    }

    private void refreshListeMaladie() {
        dlmListeMaladie.setSize( controller.getDtoCarteDuMonde().m_listeMaladie.size());
        for (int i = 0; i <  controller.getDtoCarteDuMonde().m_listeMaladie.size(); i++) {
            dlmListeMaladie.setElementAt(controller.getDtoCarteDuMonde().m_listeMaladie.get(i).m_nom, i);
        }
    }

    public void refreshListeFrontiere() {
        dlmListeFrontiere.setSize(controller.getDtoCarteDuMonde().m_listeVoies.size());
        for (int i = 0; i < controller.getDtoCarteDuMonde().m_listeVoies.size(); i++) {
            dlmListeFrontiere.setElementAt(controller.getDtoCarteDuMonde().m_listeVoies.get(i).m_nom, i);
        }
    }

    private void modfierMaladie() {
        if (AddMaladieTextFieldsAreValid()) {
            double mortalite = Double.parseDouble(txtTauxMortalite.getText());
            double reproduction = Double.parseDouble(txtTauxReproduction.getText());
            double guerison = Double.parseDouble(txtTauxGuerison.getText());
            this.controller.modifierMaladie(nomMaladieSauver, txtNomMaladie.getText(), mortalite, reproduction, guerison);
        }
    }

    private void modifierFrontiere() {

        boolean isSelected = toggleMesureModifierFrontiere.isSelected();
        String nom = listeModifFrontieres.getSelectedValue();
        Double p_tauxTransmission = Double.parseDouble(txtTransmissionModifFront.getText());
        if (isSelected == true)
        {
            Double p_tauxAdhesion = Double.parseDouble(txtTauxAdhesionModifFront1.getText());
            Double p_seuil = Double.parseDouble(txtSeuilModifFront1.getText());
            this.controller.modifierFront(nom, p_tauxTransmission, p_tauxAdhesion, p_seuil, isSelected);
        }
        if (isSelected == false)
        {
            this.controller.modifierFront(nom, p_tauxTransmission, -1, -1, isSelected);
        }
        refreshListeFrontiere();

    }

    private void btnParaMaladieActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnParaMaladieActionPerformed
        SimudemieController.TalkToUser("Veuillez sélectionner votre maladie ou en creer une et valider vos informations.");
        SimudemieController.TalkToUser("Gerer Maladie- appuyé");
        refreshListeMaladie();
        
        updatePanelVisibility(false, false, true, false, false, false, false, false, false, false);

    }// GEN-LAST:event_btnParaMaladieActionPerformed

    
    private void raffraichirListeMesureActive()
    {
        dlmListeMesureActive.setSize(controller.getPaysdeCarteDto(controller.getPaysOver()).statsMesureEtatActive.size());
        for (int i = 0; i < controller.getPaysdeCarteDto(controller.getPaysOver()).statsMesureEtatActive.size(); i++) {
            dlmListeMesureActive.setElementAt(controller.getPaysdeCarteDto(controller.getPaysOver()).statsMesureEtatActive.get(i), i);
        }
    }
    
    private void raffraichirListeFrontiereOuvert()
    {
        dlmListefrontiereOuvert.setSize(controller.getDtoCarteDuMonde().statsFrontiereOuvert.size());
        for (int i = 0; i < controller.getDtoCarteDuMonde().statsFrontiereOuvert.size(); i++) {
            dlmListefrontiereOuvert.setElementAt(controller.getDtoCarteDuMonde().statsFrontiereOuvert.get(i), i);
        }
    }
    private void initialiserGrapheCarte()
    {
        try
        {
            panelGererStatsCarte.remove(chartPanelCarte);
        }
        catch (Exception e){}
        XYSeriesCollection dataset = new XYSeriesCollection();
        graphePresent = true;
        nbInfecter = new XYSeries("NbMalade");
        nbGueris = new XYSeries("NbGueri");
        nbDeces = new XYSeries("Nbdeces");
        taillePop = new XYSeries("PopTotal");
 
        int j = 1;
        for (Integer i : controller.getDtoCarteDuMonde().statsMalade)
        {
            nbInfecter.add(j, i);
            j++;
        }
        j=1;
        for (Integer i : controller.getDtoCarteDuMonde().statsGueri)
        {
            nbGueris.add(j, i);
            j++;
        }
        j=1;
        for (Integer i : controller.getDtoCarteDuMonde().statsDeces)
        {
            nbDeces.add(j, i);
            j++;
        }
        j=1;
        for (Integer i : controller.getDtoCarteDuMonde().statsPop)
        {
            taillePop.add(j, i);
            j++;
        }

        dataset.addSeries(nbInfecter);
        dataset.addSeries(nbGueris);
        dataset.addSeries(nbDeces);
        dataset.addSeries(taillePop);
        
        //je cree mon graphe initialchart
        JFreeChart chart = createChartCarte(dataset);  
        chart.setBackgroundPaint(Color.BLACK );
        chartPanelCarte = new ChartPanel(chart);
        chartPanelCarte.setSize(panelGererStatsCarte.getSize());
        panelGererStatsCarte.add(chartPanelCarte);
        panelGererStatsCarte.getParent().validate(); 
    }
       
     private void initialiserGraphePays()
    {
        try
        {
            panelGererStatsPays.remove(chartPanelPays);
        }
        catch (Exception e){}
        graphePresent = true;
        XYSeriesCollection dataset = new XYSeriesCollection();

        nbInfecterPays = new XYSeries("NbMalade");
        nbGuerisPays = new XYSeries("NbGueri");
        nbDecesPays = new XYSeries("Nbdeces");
        taillePopPays = new XYSeries("PopTotal");
 
        int j = 1;
        for (Integer i : controller.getPaysdeCarteDto(controller.getPaysOver()).statsMalade)
        {
            nbInfecterPays.add(j, i);
            j++;
        }
        j=1;
        for (Integer i : controller.getPaysdeCarteDto(controller.getPaysOver()).statsGueri)
        {
            nbGuerisPays.add(j, i);
            j++;
        }
        j=1;
        for (Integer i : controller.getPaysdeCarteDto(controller.getPaysOver()).statsDeces)
        {
            nbDecesPays.add(j, i);
            j++;
        }
        j=1;
        for (Integer i : controller.getPaysdeCarteDto(controller.getPaysOver()).statsPop)
        {
            taillePopPays.add(j, i);
            j++;
        }
                        
        dataset.addSeries(nbInfecterPays);
        dataset.addSeries(nbGuerisPays);
        dataset.addSeries(nbDecesPays);
        dataset.addSeries(taillePopPays);
        
        //je cree mon graphe initialchart
        JFreeChart chart = createChartPays(dataset); 
        
//        XYPlot plot = (XYPlot) chart.getPlot();
//        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
//        renderer.setSeriesShapesVisible( 0, true );
//        renderer.setSeriesShape( 0, new Rectangle2D.Double( -3.0, -3.0, 2.0, 2.0 ) );
//        plot.addDomainMarker(marker);
//        
//        j = 1;
//        
//        for (Integer i : controller.getPaysdeCarteDto(controller.getPaysOver()).mesureMisAJour)
//        {
//            if (i == 1)
//            {
//                ValueMarker marker = new ValueMarker(3);
//                marker.setPaint(Color.WHITE);
//            }
//            j++;
//        }
        
        chart.setBackgroundPaint(Color.BLACK );
        chartPanelPays = new ChartPanel(chart);
        chartPanelPays.setSize(panelGererStatsPays.getSize());
        panelGererStatsPays.add(chartPanelPays);
        panelGererStatsPays.getParent().validate(); 
    }
     
    private void btnPlaySImActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnPlaySImActionPerformed
        //je setup le data pour mon graphe de stats global
               if (!graphePresent)
        {
            initialiserGrapheCarte();
            try
            {
            initialiserGraphePays();
            }catch(Exception e){}
        }   
        
        updatePanelVisibility(false, false, false, false, false, false, true, true, true, true);
        
        btnPlaySIm.setEnabled(false);
        btnAjoutMes.setEnabled(false);
        btnAjouterPays.setEnabled(false);
        btnGererFront.setEnabled(false);
        btnParaMaladie.setEnabled(false);
        btnImport.setEnabled(false);
        btnNewSim.setEnabled(false);
        btnSaveSim.setEnabled(false);
        duree.setEnabled(false);
        btnStop.setVisible(true);
        btnPauseSim.setVisible(true);
        execution = true;
        SliderJourPasser.setMinimum(0);
        SliderJourPasser.setEnabled(true);
        speedSlider.removeChangeListener(listenerSpeedSlider);
        timer.setDelay(delay);
        speedSlider.setValue(delay);
        txtTime.setText(String.valueOf(delay));
        speedSlider.addChangeListener(listenerSpeedSlider);

        listenerforStats = new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                nomPaysOver = controller.getPaysOver();
                if (!nomPaysOver.equals("Aucun"))
                {
                    labelAfficherStatsPays.setVisible(false);
                    labelMesureActive.setVisible(false);
                    jScrollPaneMesureActive.setVisible(true);
                    listAfficherMesureActiveStats.setVisible(true);
                    raffraichirListeMesureActive();
                    initialiserGraphePays();
                }
                else
                {
                     try
                    {
                        panelGererStatsPays.remove(chartPanelPays);
                        listAfficherMesureActiveStats.setVisible(false);
                        jScrollPaneMesureActive.setVisible(false);
                        labelAfficherStatsPays.setVisible(true);
                        labelMesureActive.setVisible(true);
                    }
                    catch (Exception eh){}
                }
            }
        };
        drawingPanel.addMouseMotionListener(listenerforStats);

        ActionListener timerlistener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (compteurJour != Integer.parseInt(duree.getValue().toString())&& execution == true) {                                 
                    controller.play(compteurJour);
                    compteurJour = compteurJour +1;
                    initialiserGrapheCarte();
                    raffraichirListeFrontiereOuvert();
                    if (!nomPaysOver.equals("Aucun"))
                    {
                        raffraichirListeMesureActive();
                        initialiserGraphePays();
                        listAfficherMesureActiveStats.ensureIndexIsVisible(dlmListeMesureActive.getSize()-1);
                    }
                    listAfficherFrontiereOuvertureStats.ensureIndexIsVisible(dlmListefrontiereOuvert.getSize()-1);
                    
                    SliderJourPasser.setMaximum(compteurJour);
                    SliderJourPasser.setValue(compteurJour);                  
                } else {
                    timer.stop();
                }
            }
        };
        timer = new Timer(delay, timerlistener);
        timer.start();


    }// GEN-LAST:event_btnPlaySImActionPerformed
               
    private JFreeChart createChartCarte(XYDataset dataset) {
        JFreeChart chartStat = ChartFactory.createXYLineChart("Evolution de la Maladie", "Temps (Jour)", "Nb Individu", dataset);
             
        XYPlot plot = chartStat.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // sets paint color for each series
        renderer.setSeriesPaint(0, Color.ORANGE);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.RED);
        renderer.setSeriesPaint(3, Color.BLUE);

        // sets thickness for series (using strokes)
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        renderer.setSeriesStroke(3, new BasicStroke(1.0f));

        // sets paint color for plot outlines
        plot.setOutlinePaint(Color.BLACK);
        plot.setOutlineStroke(new BasicStroke(2.0f));

        // sets renderer for lines
        plot.setRenderer(renderer);

        // sets plot background
        plot.setBackgroundPaint(Color.DARK_GRAY);

        // sets paint color for the grid lines
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return chartStat;
    }
    
    private JFreeChart createChartPays(XYDataset dataset) {
        JFreeChart chartStat = ChartFactory.createXYLineChart(controller.getPaysOver(), "Temps (Jour)", "Nb Individu", dataset); 
        XYPlot plot = chartStat.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // sets paint color for each series
        renderer.setSeriesPaint(0, Color.ORANGE);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.RED);
        renderer.setSeriesPaint(3, Color.BLUE);

        // sets thickness for series (using strokes)
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        renderer.setSeriesStroke(3, new BasicStroke(1.0f));

        // sets paint color for plot outlines
        plot.setOutlinePaint(Color.BLACK);
        plot.setOutlineStroke(new BasicStroke(2.0f));

        // sets renderer for lines
        plot.setRenderer(renderer);

        // sets plot background
        plot.setBackgroundPaint(Color.DARK_GRAY);

        // sets paint color for the grid lines
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return chartStat;
    }
    
    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnImportActionPerformed
        controller.importSim();
        updatePanelVisibility(false, false, false, false, false, false, false, false, false,false );
        SetUpMaladieCourant();
        
        
        SimudemieController.TalkToUser("Chargement de la Carte.");
    }// GEN-LAST:event_btnImportActionPerformed

    private void btnAjouterPaysActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAjouterPaysActionPerformed
        SimudemieController.TalkToUser("Sélectionnez si vous voulez ajouter un PAYS ou une RÉGION ?");
        updatePanelVisibility(false, false, false, false, true, false, false, false, false,false);
    }// GEN-LAST:event_btnAjouterPaysActionPerformed

    private void btnAjouterPaysMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btnAjouterPaysMouseClicked

    }// GEN-LAST:event_btnAjouterPaysMouseClicked

    private void btnAjoutMesMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btnAjoutMesMouseReleased

    }// GEN-LAST:event_btnAjoutMesMouseReleased

    private void btnAjoutMesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAjoutMesActionPerformed
        SimudemieController.TalkToUser("Veuillez paramétrer votre mesure et valider vos informations.");
        updatePanelVisibility(false, false, false, true, false, false, false, false, false, false);
        refreshListePaysComboBox(comboBoxPaysAjoutMesurePays);
    }// GEN-LAST:event_btnAjoutMesActionPerformed

    private void btnZoomInActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnZoomInActionPerformed

        SimudemieController.TalkToUser("cliquez dans la carte pour QUITTER LE MODE ZOOM !");
        jToggleDeplacement.setSelected(false);
        this.controller.setModeZoom(true);
        SimudemieController.TalkToUser("MODE ZOOM !");
        if (btnPauseSim.isVisible())
        {
            updatePanelVisibility(false, false, false, false, false, false, true, true, true, true);
        }
        else
        {
            updatePanelVisibility(false, false, false, false, false, false, false, false, false, false );
        }
        this.controller.zoomIn();
         // On met l'affichage à jour
        
        
    }// GEN-LAST:event_btnZoomInActionPerformed

    private void btnZoomOutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnZoomOutActionPerformed

        SimudemieController.TalkToUser("cliquez dans la carte pour QUITTER LE MODE ZOOM !");
        jToggleDeplacement.setSelected(false);
        this.controller.setModeZoom(true);
        this.controller.zoomOut(); // TODO add your handling code here:
        if (btnPauseSim.isVisible())
        {
            updatePanelVisibility(false, false, false, false, false, false, true, true, true, true);
        }
        else
        {
            updatePanelVisibility(false, false, false, false, false, false, false, false, false, false);
        }
        
        
        
    }// GEN-LAST:event_btnZoomOutActionPerformed

    private void drawingPanelMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_drawingPanelMouseReleased
            
        controller.setMouseReleased(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect);
        if (controller.getModeZoom()) { //Si mode Zoom
            this.controller.resetScaleCarte();
            this.controller.setModeZoom(false);
        } else {

            if (jToggleDeplacement.isSelected()) { //Sinon Si Déplacement sélectionné
                this.controller.RecalculPolygonPays(this.controller.getMouseClicked().x - posXCorect,
                        this.controller.getMouseClicked().y - posYCorect);
                this.controller.RecalculPolygonRegion(this.controller.getMouseClicked().x - posXCorect,
                        this.controller.getMouseClicked().y - posYCorect);
                this.controller.addNewMovedVertex();
                this.controller.addNewMovedVertexReg();
                
                
            }
            //
            if (panelGererFormes.getSelectedIndex() == 0 && !jToggleDeplacement.isSelected()) // MODE AJOUT PAYS
            {
                try {
                    if (controller.getCtrlDown()) //Si CTRL enfoncé...Forme irréguliere
                    {
                        this.controller.addPointIrr(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect); 
                    }
                    else //Si pas CTRL enfoncé...forme réguliere
                    {
                        // Forme réguliere carré de 50 pixels
                        this.controller.addPoint(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect); 
                        this.controller.addPoint(evt.getPoint().x - posXCorect + Integer.parseInt(txtLargeurAjoutPays.getText()),
                                        evt.getPoint().y - posYCorect);
                        this.controller.addPoint(evt.getPoint().x - posXCorect + Integer.parseInt(txtLargeurAjoutPays.getText()),
                                        evt.getPoint().y + Integer.parseInt(txtHauteurAjoutPays.getText()) - posYCorect);
                        this.controller.addPoint(evt.getPoint().x - posXCorect, evt.getPoint().y + Integer.parseInt(txtHauteurAjoutPays.getText()) - posYCorect);


                        if (AddPaysTextFieldsAreValid(txtNomAjoutPays.getText(), txtTaillePopAjoutPays.getText(),
                                txtNbInfecterAjoutPays.getText(), txtTransmissionRegionAjoutPays.getText())) {
                            int p_taille = Integer.parseInt(txtTaillePopAjoutPays.getText());
                            int p_nbInfecter = Integer.parseInt(txtNbInfecterAjoutPays.getText());
                            double taux = Double.parseDouble(txtTransmissionRegionAjoutPays.getText());
                            if (p_nbInfecter <= p_taille) 
                            {
                                this.controller.ajouterPaysOuRegion(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect,
                                        txtNomAjoutPays.getText(), txtNomAjoutRegion.getText(), p_taille, p_nbInfecter, taux);
                            } 
                            else 
                            {
                                SimudemieController.TalkToUser("Le nombre d'infecter ne peut pas etre plus grand que l ataille de la population");
                            }
                        } 
                        else 
                        {
                            SimudemieController.TalkToUser("Vous devez entrer un nom pour le Pays");
                        }
                        refreshListePaysJList();
                        txtNomAjoutPays.setText("ZIMBABWE" +(int) (Math.random() * ((100 - 1) + 1)) + 1);
                        
                        
                    }
                } catch (Exception e) {
                    SimudemieController.TalkToUser("Les dimensions rentrer doivent etre des chiffre");
                }
            }
            if (panelGererFormes.getSelectedIndex() == 1 && !jToggleDeplacement.isSelected()) // MODE AJOUT RÉGION
            {
                try {
                    if (controller.getCtrlDown()) //Si CTRL enfoncé...Forme irréguliere
                    {
                        this.controller.addPointIrr(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect); 
                    }
                    this.controller.addPoint(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect);
                    this.controller.addPoint(evt.getPoint().x - posXCorect + Integer.parseInt(txtLargeurAjoutRegion.getText()),
                                    evt.getPoint().y - posYCorect);
                    this.controller.addPoint(evt.getPoint().x - posXCorect + Integer.parseInt(txtLargeurAjoutRegion.getText()),
                                    evt.getPoint().y + Integer.parseInt(txtHauteurAjoutRegion.getText()) - posYCorect);
                    this.controller.addPoint(evt.getPoint().x - posXCorect,
                            evt.getPoint().y + Integer.parseInt(txtHauteurAjoutRegion.getText()) - posYCorect);

                    if (!txtNomAjoutRegion.getText().equals("")) {
                        int p_taille = Integer.parseInt(txtTaillePopAjoutPays.getText());
                        int p_nbInfecter = Integer.parseInt(txtNbInfecterAjoutPays.getText());
                        double taux = Double.parseDouble(txtTransmissionRegionAjoutPays.getText());
                        this.controller.ajouterPaysOuRegion(
                                evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect,
                                txtNomAjoutPays.getText(), txtNomAjoutRegion.getText(), p_taille, p_nbInfecter, taux);
                    } 
                    else {
                        SimudemieController.TalkToUser("Vous devez entrer un nom pour le Pays");
                    }
                    txtNomAjoutRegion.setText("Quebec" + (int) (Math.random() * ((100 - 1) + 1)) + 1);
                    
                    
                } catch (Exception e) {
                    System.out.println(e.toString());
                    SimudemieController.TalkToUser("Les dimensions rentrer doivent etre des chiffre");
                }
            }

        }
        controller.setBoundPointClicked(false);
        controller.setBoundPointClickedReg(false);
        
        while(!controller.IsMoveValid())
        {
            controller.retourDEtat();
        }
        controller.setMoveEnCoursFalse();
         // On met l'affichage à jour

    }// GEN-LAST:event_drawingPanelMouseReleased

    private void drawingPanelMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_drawingPanelMouseEntered

    }// GEN-LAST:event_drawingPanelMouseEntered

    private void drawingPanelMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_drawingPanelMouseClicked

    }// GEN-LAST:event_drawingPanelMouseClicked

    private void moveDownActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_moveDownActionPerformed
        SimudemieController.TalkToUser("CLIQUEZ DANS LA CARTE POUR REVENIR EN MODE ÉDITION !");
        this.controller.translateCarte(0, -100);
        
    }// GEN-LAST:event_moveDownActionPerformed

    private void moveUp1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_moveUp1ActionPerformed

        SimudemieController.TalkToUser("CLIQUEZ DANS LA CARTE POUR REVENIR EN MODE ÉDITION !");
         this.controller.translateCarte(0, 100);
    }// GEN-LAST:event_moveUp1ActionPerformed

    private void moveRightActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_moveRightActionPerformed
        SimudemieController.TalkToUser("CLIQUEZ DANS LA CARTE POUR REVENIR EN MODE ÉDITION !");
        this.controller.translateCarte(-100, 0);
    }// GEN-LAST:event_moveRightActionPerformed

    private void moveLeftActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_moveLeftActionPerformed
        SimudemieController.TalkToUser("CLIQUEZ DANS LA CARTE POUR REVENIR EN MODE ÉDITION !");
         this.controller.translateCarte(100, 0);
    }// GEN-LAST:event_moveLeftActionPerformed

    private void btnGererFrontActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnGererFrontActionPerformed
        SimudemieController.TalkToUser("Veuillez Sélectionner 2 pays et le type de frontière. Validez ensuite votre sélection.");     
        updatePanelVisibility(false, true, false, false, false, false, false, false, false, false);
        refreshListePaysComboBox(comboBoxPays1AjoutFront);
        refreshListePaysComboBox(comboBoxPays2AjoutFront);       
        refreshListeFrontiere();
    }// GEN-LAST:event_btnGererFrontActionPerformed

    private void btnAjouterFrontActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAjouterFrontActionPerformed 
        if ( FrontiereTextFieldsAreValid(txtSeuilFermetureFront.getText(),
                                txtTauxTransmissionAjoutFront.getText(), 
                                txtTauxAdhesionAjoutFront1.getText(),toggleMesureAjouterFrontiere.isSelected()))
        {
            Double p_tauxTransmission = Double.parseDouble(txtTauxTransmissionAjoutFront.getText());

            
            String pays1 = comboBoxPays1AjoutFront.getSelectedItem().toString();
            String pays2 = comboBoxPays2AjoutFront.getSelectedItem().toString();
            String type = comboBoxTypeAjoutFront.getSelectedItem().toString(); 
            boolean aUneMesure = toggleMesureAjouterFrontiere.isSelected();           
            if (aUneMesure == true)
            {
                Double p_seuil = Double.parseDouble(txtSeuilFermetureFront.getText());
                Double p_tauxAdhesion = Double.parseDouble(txtTauxAdhesionAjoutFront1.getText());
                this.controller.ajoutFront(pays1, pays2, type, p_seuil, p_tauxTransmission,p_tauxAdhesion, aUneMesure);
            }
            if (aUneMesure == false)
            {
                this.controller.ajoutFront(pays1, pays2, type, -1, p_tauxTransmission, -1, aUneMesure);
            }
            SimudemieController.TalkToUser("Frontiere Ajouter avec succes");
        }

    }// GEN-LAST:event_btnAjouterFrontActionPerformed

    private void btnSupprimerModifFrontActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSupprimerModifFrontActionPerformed
        try
        {
        this.controller.supprimerFront(listeModifFrontieres.getSelectedValue());
        }catch(Exception e){}
        refreshListeFrontiere();
        updateFrontiereListenersShouldBeTriggered = false;
        txtSeuilModifFront1.setText("0");
        txtTransmissionModifFront.setText("0");
        updateFrontiereListenersShouldBeTriggered = true;     
    }// GEN-LAST:event_btnSupprimerModifFrontActionPerformed

    private void btnAjouterMaladieActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAjouterMaladieActionPerformed
        if (AddMaladieTextFieldsAreValid()) {
            double mortalite = Double.parseDouble(txtTauxMortalite.getText());
            double reproduction = Double.parseDouble(txtTauxReproduction.getText());
            double guerison = Double.parseDouble(txtTauxGuerison.getText());
            this.controller.ajoutMaladie(txtNomMaladie.getText(), mortalite, reproduction, guerison); 
        }
        refreshListeMaladie();

    }// GEN-LAST:event_btnAjouterMaladieActionPerformed

    private void btnSupprimerMaladieActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSupprimerMaladieActionPerformed
        controller.supprimerMaladie(nomMaladieSauver);
        refreshListeMaladie();
        updateMaladieListenersShouldBeTriggered = false;
        listeMaladies.clearSelection();
        txtNomMaladie.setText("");
        txtTauxMortalite.setText("0.0");
        txtTauxGuerison.setText("0.0");
        txtTauxReproduction.setText("0.0");
        updateMaladieListenersShouldBeTriggered = true;
    }// GEN-LAST:event_btnSupprimerMaladieActionPerformed

    private void btnChoisirMaladieActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnChoisirMaladieActionPerformed
        try {
            this.controller.setMaladieDeCarte(listeMaladies.getSelectedValue());
            SetUpMaladieCourant();
        } catch (Exception e) {
            SimudemieController.TalkToUser(" vous devez selectionner une maladie");
        }
    }// GEN-LAST:event_btnChoisirMaladieActionPerformed

    private void jToggleDeplacementActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleDeplacementActionPerformed

        if (jToggleDeplacement.isSelected() == true) {
            SimudemieController.TalkToUser("Vous pouvez déplacer les pays et les régions en -DRAGGING-.");
        } else {
            SimudemieController.TalkToUser("Vous êtes en mode Édition.");
        }
    }// GEN-LAST:event_jToggleDeplacementActionPerformed

    private void drawingPanelMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_drawingPanelMousePressed
        
        controller.saveEtatCourantToList();
        this.controller.updateAllPolygon();
        posXCorect = this.controller.getDtoCarteDuMonde().m_translation.width; // Assignation du m_mouseClicked dans le
                                                                             // controlleur avec la correction de la
                                                                             // translation
        posYCorect = this.controller.getDtoCarteDuMonde().m_translation.height;
        this.controller.setMouseclicked(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect);
        if (this.controller.getModeZoom()) {
            this.controller.resetScaleCarte();
        } else // Si nous ne somme pas en mode zoom, on recadrelezoom pour voir le
        {
            if (jToggleDeplacement.isSelected() == false) {
                SimudemieController.TalkToUser("OPTIONS : Clic sur boutons MOVE-REDIM pour redimensionner et bouger Pays et Régions");
                for (PaysDto pays : this.controller.getDtoCarteDuMonde().m_listePays) {
                    if (pays.m_forme.m_polygon.contains(this.controller.getMouseClicked())) {
                        int XNewTranslate = -pays.m_positionAjouter.x
                                + (this.controller.getDtoCarteDuMonde().m_panel_size_to_move.width / 2
                                        - pays.m_forme.m_widthSelectBox / 2);
                        int YNewTranslate = -pays.m_positionAjouter.y
                                + (this.controller.getDtoCarteDuMonde().m_panel_size_to_move.height / 2
                                        - pays.m_forme.m_heightSelectBox / 2);
                        

                        SimudemieController.TalkToUser("DOUBLE CLIQUEZ SUR : " + pays.m_nom + "Pour effectuer une capture du pays. ");
                        if (this.controller.getShowAllStats()) {
                            SimudemieController.TalkToUser("DOUBLE CLIC - SCREEN SHOT EFFECTUÉ");
                            drawingPanel.saveImage("", "png");
                        }
                        if (panelGererFormes.getSelectedIndex() != 1)
                        {
                            this.controller.translateNewCarte(XNewTranslate, YNewTranslate);
                            this.controller.setShowAllStats(true);
                        }
                        
                        

                        // this.controller.zoomIn();
                        // this.controller.zoomIn();
                        // this.controller.setModeZoom(true);
                    }
                }
            }

        }
        if (jToggleDeplacement.isSelected() == true) {
            SimudemieController.TalkToUser("OPTIONS : Déplacer les pays - Région ou REDIM dans la boite de REDIM");
            SimudemieController.TalkToUser("OPTIONS : Clic sur boutons MOVE-REDIM pour revenir en mode zoom - ajout Pays/Region");
            
            this.controller.setPointBoundPointsClicked(); 
            this.controller.setPointBoundPointsClickedReg();
            this.controller.movePays(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect);
            this.controller.moveRegion(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect);
        }
        
        
        // TODO add your handling code here:
    }// GEN-LAST:event_drawingPanelMousePressed

    private void drawingPanelMouseDragged(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_drawingPanelMouseDragged

        this.controller.setMousePosition(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect);
        if (jToggleDeplacement.isSelected() == true && !controller.getBoundPointClicked() && !controller.getBoundPointClickedReg()) {
                this.controller.movePays(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect);
                this.controller.moveRegion(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect);
            
                this.controller.updateAllPolygon();
                posXCorect = this.controller.getDtoCarteDuMonde().m_translation.width; // Assignation du m_mouseClicked dans le
                                                                                     // controlleur avec la correction de la
                                                                                     // translation
                posYCorect = this.controller.getDtoCarteDuMonde().m_translation.height;
                this.controller.setMouseclicked(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect);
                this.controller.RecalculPolygonPays(this.controller.getMousePosition().x - posXCorect,
                        this.controller.getMousePosition().y - posYCorect);
                this.controller.RecalculPolygonRegion(this.controller.getMouseClicked().x - posXCorect,
                        this.controller.getMouseClicked().y - posYCorect);
            
            try {
                txtHauteurModifPays.setText(String.valueOf(controller.getPaysdeCarteDto(listePays.getSelectedValue()).m_forme.m_heightSelectBox));
                txtLargeurModifPays.setText(String.valueOf(controller.getPaysdeCarteDto(listePays.getSelectedValue()).m_forme.m_widthSelectBox));
                txtHauteurModifPays.setEditable(false);
                txtLargeurModifPays.setEditable(false);
            } catch (Exception e) {
            }
            try {
                txtHauteurModifRegion.setText(String.valueOf(
                        controller.getRegionDeCarteDto(comboPaysModifRegion.getSelectedItem().toString(), nomRegionSauver).m_forme.m_heightSelectBox));
                txtLargeurModifRegion.setText(String.valueOf(
                        controller.getRegionDeCarteDto(comboPaysModifRegion.getSelectedItem().toString(), nomRegionSauver).m_forme.m_widthSelectBox));
                txtHauteurModifRegion.setEditable(false);
                txtLargeurModifRegion.setEditable(false);
            } catch (Exception e) {
            }

        }
        else
        {
             if (controller.getCtrlDown()) //Si CTRL enfoncé...Forme irréguliere
            {
                this.controller.addPointIrr(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect); 
            }
        }
        if(!controller.IsMoveValid())
        {
//            controller.retourDEtat();
        }
  
    }// GEN-LAST:event_drawingPanelMouseDragged

    private void drawingPanelMouseMoved(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_drawingPanelMouseMoved
        while(!controller.IsMoveValid())
            {
                controller.retourDEtat();
            }
        this.controller.setShowAllStats(false);
        posXCorect = this.controller.getDtoCarteDuMonde().m_translation.width;
        posYCorect = this.controller.getDtoCarteDuMonde().m_translation.height;
        this.controller.setMousePosition(evt.getPoint().x - posXCorect, evt.getPoint().y - posYCorect);
        
        controller.initListePointBoundPoints();
        this.controller.setPointBoundPointsPaysHover();
//        this.controller.setPointBoundPointsClicked();
        drawingPanel.requestFocus();
    }// GEN-LAST:event_drawingPanelMouseMoved

    private void jToggleDeplacementMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jToggleDeplacementMousePressed
    }// GEN-LAST:event_jToggleDeplacementMousePressed

    private void moveLeftMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_moveLeftMouseReleased
    }// GEN-LAST:event_moveLeftMouseReleased

    private void btnSaveSimActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSaveSimActionPerformed
        this.controller.saveSim();
        // drawingPanel.saveImage("", "jpg");

    }// GEN-LAST:event_btnSaveSimActionPerformed

    private void btnNewSimActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnNewSimActionPerformed
        controller.newSim();
        updatePanelVisibility(false, false, false, false, false, false, false, false, false, false);
    }// GEN-LAST:event_btnNewSimActionPerformed

    private void btnPauseSimActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnPauseSimActionPerformed
        btnPlaySIm.setEnabled(true);
        execution = false;
        timer.stop();
        btnStop.setVisible(true);
    }// GEN-LAST:event_btnPauseSimActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnStopActionPerformed
        execution = false;
        graphePresent = false;
        timer.stop();
        drawingPanel.removeMouseMotionListener(listenerforStats);
        
        updatePanelVisibility(false, false, false, false, false, false, false, false, false, false);
        compteurJour =0;
        controller.reinitialiserArrayStatsCarte();
        controller.reinitialiserArrayStatsPaysDeCarte();

        speedSlider.removeChangeListener(listenerSpeedSlider);
        timer.setDelay(delay);
        speedSlider.setValue(delay);
        txtTime.setText(String.valueOf(delay));
        speedSlider.addChangeListener(listenerSpeedSlider);
        SliderJourPasser.setMaximum(1);
        SliderJourPasser.setEnabled(false);
        btnPlaySIm.setEnabled(true);
        btnAjoutMes.setEnabled(true);
        btnAjouterPays.setEnabled(true);
        btnGererFront.setEnabled(true);
        btnParaMaladie.setEnabled(true);
        btnImport.setEnabled(true);
        btnNewSim.setEnabled(true);
        btnSaveSim.setEnabled(true);
        duree.setEnabled(true);
        btnStop.setVisible(false);
        btnPauseSim.setVisible(false);
        btnPlaySIm.setVisible(true);
    }// GEN-LAST:event_btnStopActionPerformed

    private void jRadiobtnAddMaladieActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadiobtnAddMaladieActionPerformed
    }// GEN-LAST:event_jRadiobtnAddMaladieActionPerformed

    private void drawingPanelMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {// GEN-FIRST:event_drawingPanelMouseWheelMoved
        SimudemieController.TalkToUser("cliquez dans la carte pour QUITTER LE MODE ZOOM !");
        SimudemieController.TalkToUser("MODE ZOOM !");
        this.controller.setModeZoom(true);
        jToggleDeplacement.setSelected(false);
        // if (evt.isControlDown()) {
        if (evt.getWheelRotation() < 0) {
            this.controller.zoomInScroll();
        } else {
            this.controller.zoomOutScroll();// TODO add your handling code here
        }
        if (btnPauseSim.isVisible())
        {
            updatePanelVisibility(false, false, false, false, false, false, true, true, true, true);
        }
        else
        {
            updatePanelVisibility(false, false, false, false, false, false, false, false, false, false);
        }
        
        // }
    }// GEN-LAST:event_drawingPanelMouseWheelMoved

    private void refreshAll() {
        refreshListeMaladie();
        refreshListeFrontiere();
        refreshListePaysJList();
        refreshListeFrontiere();
        try {
            refreshTableRegion(comboPaysModifRegion.getSelectedItem().toString());
        } catch (Exception e) {
        }
        try {
            refreshListeMesurePays(comboBoxPaysModifMesurePays.getSelectedItem().toString());
        } catch (Exception e) {
        }
    }

    private void speedSliderStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_speedSliderStateChanged
    }// GEN-LAST:event_speedSliderStateChanged

    private void comboBoxPaysMesurePaysActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_comboBoxPaysMesurePaysActionPerformed
    }// GEN-LAST:event_comboBoxPaysMesurePaysActionPerformed

    private void btnAjouterMesurePaysActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAjouterMesurePaysActionPerformed

    }// GEN-LAST:event_btnAjouterMesurePaysActionPerformed

    private void panelGererMesuresStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_panelGererMesuresStateChanged

    }// GEN-LAST:event_panelGererMesuresStateChanged

    private void btnSupprimerPaysActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSupprimerPaysActionPerformed

        try {
            String pays = listePays.getSelectedValue();
            this.controller.supprimerFrontiereDuPays(pays);
            SimudemieController.TalkToUser("Pays Supprimé : " + pays);
            controller.supprimerPays(pays);
            txtNomModifPays.setText("");
            txtTaillePopModifPays.setText("0");
            txtNbInfecterModifPays.setText("0");
            refreshListePaysJList(); 
        } catch (Exception e) {
        }
    }// GEN-LAST:event_btnSupprimerPaysActionPerformed

    private void listePaysValueChanged(javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_listePaysValueChanged

    }// GEN-LAST:event_listePaysValueChanged

    private void btnSupprimerModifRegionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSupprimerModifRegionActionPerformed
        String pays = comboPaysModifRegion.getSelectedItem().toString();
        controller.deleteRegion(pays, nomRegionSauver);
        refreshTableRegion(comboPaysModifRegion.getSelectedItem().toString());
        tableModifRegion.clearSelection();
        
        
    }// GEN-LAST:event_btnSupprimerModifRegionActionPerformed

    private void comboPaysModifRegionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_comboPaysModifRegionActionPerformed
        refreshTableRegion(comboPaysModifRegion.getSelectedItem().toString());
    }// GEN-LAST:event_comboPaysModifRegionActionPerformed

    private void btnValiderPourcentageRegionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnValiderPourcentageRegionActionPerformed
        if (tableModifRegion.isEditing()) {
            tableModifRegion.getCellEditor().stopCellEditing();
        }
        double total = 0;
        String pays = comboPaysModifRegion.getSelectedItem().toString();
        int grandeur = controller.getPaysdeCarteDto(pays).m_listeRegion.size();
        for (int i = 0; i < grandeur; i++) {
            total += Double.parseDouble(tableModifRegion.getValueAt(i, 1).toString());
        }
        if (total != 100.0) {
            SimudemieController.TalkToUser("Le total de vos regions n'est pas de 100, veuillez modifier vos valeurs.");
        } else {
            int j = 0;
            for (RegionDto i : controller.getPaysdeCarteDto(pays).m_listeRegion) {
                controller.modifierPourcentageRegion(pays, i.m_nom, Double.parseDouble(tableModifRegion.getModel().getValueAt(j, 1).toString()));             
                j++;
            }
            controller.updateStatsPays(pays);
            SimudemieController.TalkToUser("Modification des Pourcentage avec Succes");

        }
        refreshTableRegion(pays);  

    }// GEN-LAST:event_btnValiderPourcentageRegionActionPerformed

    private void panelGererFormesStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_panelGererFormesStateChanged

    }// GEN-LAST:event_panelGererFormesStateChanged

    private void drawingPanelKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_drawingPanelKeyPressed

    }// GEN-LAST:event_drawingPanelKeyPressed

    private void drawingPanelKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_drawingPanelKeyTyped

    }// GEN-LAST:event_drawingPanelKeyTyped

    private void drawingPanelKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_drawingPanelKeyReleased

    }// GEN-LAST:event_drawingPanelKeyReleased

    private void txtNomMesureBaseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtNomMesureBaseActionPerformed

    }// GEN-LAST:event_txtNomMesureBaseActionPerformed

    private void btnScreenShotActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnScreenShotActionPerformed
        drawingPanel.saveImage("", "png");
    }// GEN-LAST:event_btnScreenShotActionPerformed

    private void btnScreenShotMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btnScreenShotMousePressed

    }// GEN-LAST:event_btnScreenShotMousePressed

    private void panelGererFrontiereStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_panelGererFrontiereStateChanged

    }// GEN-LAST:event_panelGererFrontiereStateChanged

    /**
     * @param args the command line arguments
     */
   public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SimudemieGUI.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SimudemieGUI.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SimudemieGUI.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SimudemieGUI.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SimudemieGUI().setVisible(true);
            }
        });
    }
    
    // Listener keypressed
    class MKeyListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent event) {
 
        if (event.getKeyCode() == 17 && panelGererFormes.getSelectedIndex() == 0 || panelGererFormes.getSelectedIndex() == 1)   //Touche CRRL
        {
            SimudemieController.TalkToUser("CTRL - Cliquez pour ajouter Votre Forme irrégulière point par points");
            controller.setCtrlDownPressed();//A REMETTRE POUR FORME IRR
            
        }
        if (event.getKeyCode() == 17)
        {
            ctrlDown = true;       
        }

    }
    @Override
    public void keyReleased(KeyEvent event) {
        if (event.getKeyCode() == 17 && panelGererFormes.getSelectedIndex() == 0 )   //Touche CTRL Pays IRR
        {    
            SimudemieController.TalkToUser("CTRL - Combinaison entrée pour un nouveau pays irrégulier");
            int taille = Integer.parseInt(txtTaillePopAjoutPays.getText());
            int nbInfecter = Integer.parseInt(txtNbInfecterAjoutPays.getText());
            double taux = Double.parseDouble(txtTransmissionRegionAjoutPays.getText());
            if (nbInfecter <= taille) 
            {
                controller.saveEtatCourantToList();
                controller.setCtrlDownReleased(taille, nbInfecter, taux);
            } 
            else 
            {
                SimudemieController.TalkToUser("Le nombre d'infecter ne peut pas etre plus grand que l ataille de la population");
            }
            ctrlDown=false;
        }
        
        
        if (event.getKeyCode() == 17 && panelGererFormes.getSelectedIndex() == 1 )   //Touche CTRL REGION IRR
        {    
            SimudemieController.TalkToUser("CTRL - Combinaison entrée pour une nouvelle région Irrégulière");
            int taille = Integer.parseInt(txtTaillePopAjoutPays.getText());
            int nbInfecter = Integer.parseInt(txtNbInfecterAjoutPays.getText());
            double taux = Double.parseDouble(txtTransmissionRegionAjoutPays.getText());
            if (nbInfecter <= taille) 
            {
                controller.saveEtatCourantToList();
                controller.setCtrlDownReleased(taille, nbInfecter, taux);
            } 
            else 
            {
                SimudemieController.TalkToUser("Le nombre d'infecter ne peut pas etre plus grand que l ataille de la population");
            }
            ctrlDown=false;
        }
        
        
        
        if( event.getKeyCode() == 90 && ctrlDown ==true)
        {
            controller.retourDEtat();
            refreshAll();         
        }
        if( event.getKeyCode() == 89 && ctrlDown ==true)
        {
            controller.avanceDEtat();
            refreshAll();         
        }
               
            while(!controller.IsMoveValid())
            {
                controller.retourDEtat();
                refreshAll();
            }
        
        
        
    }
    
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AjoutMesurePays;
    private javax.swing.JPanel AjouterFrontiere;
    private javax.swing.JScrollPane ListeModifMesurePays;
    private javax.swing.JScrollPane ListePaysPane;
    private javax.swing.JPanel ModifierFrontiere;
    private javax.swing.JPanel ModifierMesurePays;
    private javax.swing.JScrollPane ScrollPane_Maladie;
    private javax.swing.JScrollPane ScrollPane_Simulation;
    private javax.swing.JSlider SliderJourPasser;
    private javax.swing.JButton btnAjoutMes;
    private javax.swing.JButton btnAjouterAjoutMesure;
    private javax.swing.JButton btnAjouterFront;
    private javax.swing.JButton btnAjouterMaladie;
    private javax.swing.JButton btnAjouterPays;
    private javax.swing.JButton btnCSVStats;
    private javax.swing.JButton btnChoisirMaladie;
    private javax.swing.JButton btnChoisirSimulation;
    private javax.swing.JButton btnEnregistrerSimulation;
    private javax.swing.JButton btnGererFront;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnNaviguerSimulation;
    private javax.swing.JButton btnNewSim;
    private javax.swing.JButton btnParaMaladie;
    private javax.swing.JButton btnPauseSim;
    private javax.swing.JButton btnPlaySIm;
    private javax.swing.JButton btnRedo;
    private javax.swing.JButton btnSaveSim;
    private javax.swing.JButton btnScreenShot;
    private javax.swing.JButton btnStop;
    private javax.swing.JButton btnSupprimerMaladie;
    private javax.swing.JButton btnSupprimerModifFront;
    private javax.swing.JButton btnSupprimerModifMesurePays;
    private javax.swing.JButton btnSupprimerModifRegion;
    private javax.swing.JButton btnSupprimerPays;
    private javax.swing.JButton btnSupprimerSimulation;
    private javax.swing.JButton btnUndo;
    private javax.swing.JButton btnValiderPourcentageRegion;
    private javax.swing.JButton btnZoomIn;
    private javax.swing.JButton btnZoomOut;
    private javax.swing.JComboBox<String> comboBoxPays1AjoutFront;
    private javax.swing.JComboBox<String> comboBoxPays2AjoutFront;
    private javax.swing.JComboBox<String> comboBoxPaysAjoutMesurePays;
    private javax.swing.JComboBox<String> comboBoxPaysModifMesurePays;
    private javax.swing.JComboBox<String> comboBoxTypeAjoutFront;
    private javax.swing.JComboBox<String> comboPaysModifRegion;
    private ca.ulaval.glo2004.gui.DrawingPanel drawingPanel;
    private ca.ulaval.glo2004.gui.DrawingStats drawingStats1;
    private javax.swing.JSpinner duree;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadiobtnAddMaladie;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneMesureActive;
    private javax.swing.JScrollPane jScrollPaneModifFront;
    private javax.swing.JToggleButton jToggleDeplacement;
    private javax.swing.JLabel labelAfficherStatsPays;
    private javax.swing.JLabel labelMesureActive;
    private javax.swing.JLabel labelNomSimulation;
    private javax.swing.JLabel labelPossibilites;
    private javax.swing.JLabel labelSimulationPossible;
    private javax.swing.JLabel labelTauxGuerison;
    private javax.swing.JLabel labelTauxMortalite;
    private javax.swing.JLabel labelTauxReproduction;
    private javax.swing.JLabel lbFrontPresentes;
    private javax.swing.JLabel lblBG;
    private javax.swing.JLabel lblChoisirRegion;
    private javax.swing.JLabel lblDuree1;
    private javax.swing.JLabel lblGuerison;
    private javax.swing.JLabel lblHauteurAjoutPays;
    private javax.swing.JLabel lblHauteurAjoutRegion;
    private javax.swing.JLabel lblHauteurModifPays;
    private javax.swing.JLabel lblHauteurModifregion;
    private javax.swing.JLabel lblJours;
    private javax.swing.JLabel lblLargeurAjoutPays;
    private javax.swing.JLabel lblLargeurAjoutRegion;
    private javax.swing.JLabel lblLargeurModifPays;
    private javax.swing.JLabel lblLargeurModifRegion;
    private javax.swing.JLabel lblMesure1;
    private javax.swing.JLabel lblMesurePresentesModifMesurePays;
    private javax.swing.JLabel lblMorta;
    private javax.swing.JLabel lblNbInfecterAjoutPays;
    private javax.swing.JLabel lblNbInfecterModifPays;
    private javax.swing.JLabel lblNom;
    private javax.swing.JLabel lblNomAjoutMesure;
    private javax.swing.JLabel lblNomAjoutRegion;
    private javax.swing.JLabel lblNomMaladie;
    private javax.swing.JLabel lblNomModifMesurePays;
    private javax.swing.JLabel lblNomModifPays;
    private javax.swing.JLabel lblNomModifRegion;
    private javax.swing.JLabel lblNomPaysAjoutFront;
    private javax.swing.JLabel lblPaysPresent;
    private javax.swing.JLabel lblReproductionAjoutMesure;
    private javax.swing.JLabel lblReproductionModifMesurePays;
    private javax.swing.JLabel lblSeuilFermetureAjoutFront;
    private javax.swing.JLabel lblSeuilLimiteAjoutMesure;
    private javax.swing.JLabel lblSeuilLimiteModifMesurePays;
    private javax.swing.JLabel lblSeuilModifFront;
    private javax.swing.JLabel lblTaillePopModifPays;
    private javax.swing.JLabel lblTauxAdhesionModifFront1;
    private javax.swing.JLabel lblTauxTransmissionAjoutFront;
    private javax.swing.JLabel lblTauxTransmissionRegionModifPays;
    private javax.swing.JLabel lblTauxadhesionAjoutFront1;
    private javax.swing.JLabel lblTimer;
    private javax.swing.JLabel lblTransmessionRegionAjoutPays;
    private javax.swing.JLabel lblTransmi;
    private javax.swing.JLabel lblTransmissionAjoutMesure1;
    private javax.swing.JLabel lblTransmissionModifFront;
    private javax.swing.JLabel lblTransmissionModifMesurePays;
    private javax.swing.JLabel lblTypeAjoutFront;
    private javax.swing.JLabel lbladhesionAjoutMesure;
    private javax.swing.JLabel lbladhesionModifMesurePays;
    private javax.swing.JLabel lbllTaillePopAjoutPays;
    private javax.swing.JList<String> listAfficherFrontiereOuvertureStats;
    private javax.swing.JList<String> listAfficherMesureActiveStats;
    private javax.swing.JList<String> listeMaladies;
    private javax.swing.JList<String> listeModifFrontieres;
    private javax.swing.JList<String> listeModifMesurePays;
    private javax.swing.JList<String> listePays;
    private javax.swing.JList<String> listeSimulations;
    private javax.swing.JButton moveDown;
    private javax.swing.JButton moveLeft;
    private javax.swing.JButton moveRight;
    private javax.swing.JButton moveUp1;
    private javax.swing.JPanel panelFrontieresOuvertGraphePays;
    private javax.swing.JTabbedPane panelGererFormes;
    private javax.swing.JTabbedPane panelGererFrontiere;
    private javax.swing.JPanel panelGererMaladie;
    private javax.swing.JTabbedPane panelGererMesures;
    private javax.swing.JPanel panelGererSimulations;
    private javax.swing.JPanel panelGererStatsCarte;
    private javax.swing.JPanel panelGererStatsPays;
    private javax.swing.JPanel panelMesureGraphePays;
    private javax.swing.JPanel panelMesuresSan;
    private javax.swing.JLabel pourcentage1;
    private javax.swing.JSlider speedSlider;
    private javax.swing.JTable tableModifRegion;
    private javax.swing.JScrollPane tableRegionPourcentPane1;
    private ca.ulaval.glo2004.gui.TalkToUser talkToUser1;
    private javax.swing.JRadioButton toggleMesureAjouterFrontiere;
    private javax.swing.JRadioButton toggleMesureModifierFrontiere;
    private javax.swing.JTextField txtAdhesionAjoutMesure;
    private javax.swing.JTextField txtAdhesionModifMesurePays;
    private javax.swing.JTextArea txtGuerison;
    private javax.swing.JTextArea txtHauteurAjoutPays;
    private javax.swing.JTextArea txtHauteurAjoutRegion;
    private javax.swing.JTextArea txtHauteurModifPays;
    private javax.swing.JTextArea txtHauteurModifRegion;
    private javax.swing.JTextArea txtLargeurAjoutPays;
    private javax.swing.JTextArea txtLargeurAjoutRegion;
    private javax.swing.JTextArea txtLargeurModifPays;
    private javax.swing.JTextArea txtLargeurModifRegion;
    private javax.swing.JTextArea txtMorta;
    private javax.swing.JTextArea txtNbInfecterAjoutPays;
    private javax.swing.JTextArea txtNbInfecterModifPays;
    private javax.swing.JTextField txtNomAjoutMesure;
    private javax.swing.JTextArea txtNomAjoutPays;
    private javax.swing.JTextArea txtNomAjoutRegion;
    private javax.swing.JTextField txtNomMaladie;
    private javax.swing.JTextField txtNomModifMesurePays;
    private javax.swing.JTextArea txtNomModifPays;
    private javax.swing.JTextArea txtNomModifRegion;
    private javax.swing.JTextField txtNomSimulation;
    private javax.swing.JTextArea txtReprod;
    private javax.swing.JTextField txtReproductionAjoutMesure;
    private javax.swing.JTextField txtReproductionModifMesurePays;
    private javax.swing.JTextField txtSeuilFermetureFront;
    private javax.swing.JTextField txtSeuilLimiteAjoutMesure;
    private javax.swing.JTextField txtSeuilLimiteModifMesurePays;
    private javax.swing.JTextField txtSeuilModifFront1;
    private javax.swing.JTextArea txtTaillePopAjoutPays;
    private javax.swing.JTextArea txtTaillePopModifPays;
    private javax.swing.JTextField txtTauxAdhesionAjoutFront1;
    private javax.swing.JTextField txtTauxAdhesionModifFront1;
    private javax.swing.JTextField txtTauxGuerison;
    private javax.swing.JTextField txtTauxMortalite;
    private javax.swing.JTextField txtTauxReproduction;
    private javax.swing.JTextField txtTauxTransmissionAjoutFront;
    private javax.swing.JTextField txtTime;
    private javax.swing.JTextField txtTransmissionAjoutMesure;
    private javax.swing.JTextField txtTransmissionModifFront;
    private javax.swing.JTextField txtTransmissionModifMesurePays;
    private javax.swing.JTextArea txtTransmissionRegionAjoutPays;
    private javax.swing.JTextArea txtTransmissionRegionModifPays;
    // End of variables declaration//GEN-END:variables


}
