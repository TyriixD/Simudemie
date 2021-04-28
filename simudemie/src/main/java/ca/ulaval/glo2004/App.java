package ca.ulaval.glo2004;


import ca.ulaval.glo2004.gui.SimudemieGUI;
import org.apache.commons.math3.distribution.BinomialDistribution;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;



public class App {
    //Exemple de creation d'une fenetre et d'un bouton avec swing. Lorsque vous allez creer votre propre GUI
    // Vous n'aurez pas besoin d'ecrire tout ce code, il sera genere automatiquement par intellij ou netbeans
    // Par contre vous aurez a creer les actions listener pour vos boutons et etc.
    public static void main(String[] args) {
        
        
        SimudemieGUI mainWindow = new SimudemieGUI();
        mainWindow.setVisible(true);
        mainWindow.setResizable(true);
        
    }
}

