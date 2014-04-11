/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class PropertiesView extends TitledComponent {

    private final int cardGHap = 10;
    private MultiProcessCard multiProcessCard;
    private SingleProcessCard singleProcessCard;
    private JPanel voidCard;
    private JPanel contentPanel;

    protected static enum CARDS {

        NO_SELECTION, PROCESS_SINGLE_SELECTION, PROCESS_MULTI_SELECTION
    }

    public PropertiesView(String title) {
        // TODO move String to config/constants
        super(title, new JPanel());

        contentPanel = (JPanel) getComponent(1);

        contentPanel.setLayout(new CardLayout(cardGHap, cardGHap));
        contentPanel.add(getVoidCard(), CARDS.NO_SELECTION.name());
        contentPanel.add(getSingleProcessCard(), CARDS.PROCESS_SINGLE_SELECTION.name());
        contentPanel.add(getMultiProcessesCard(), CARDS.PROCESS_MULTI_SELECTION.name());
//        add(new JLabel(""));
    }

    public void setSelectedProcesses(List<IProcessEntity> processes) {

        if (1 == processes.size()) {
            getSingleProcessCard().setProcess(processes.get(0));
            getCardLayout().show(getContentPanel(), CARDS.PROCESS_SINGLE_SELECTION.name());
        } else if (1 < processes.size()) {
            getMultiProcessesCard().setProcesses(processes);
            getCardLayout().show(getContentPanel(), CARDS.PROCESS_MULTI_SELECTION.name());
        } else {
            getCardLayout().show(getContentPanel(), CARDS.NO_SELECTION.name());
        }

    }

    protected CardLayout getCardLayout() {
        return (CardLayout) getContentPanel().getLayout();
    }

    private Component getVoidCard() {
        if (null == voidCard) {
            voidCard = new JPanel(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, 20, TableLayout.PREFERRED, 20, TableLayout.PREFERRED}}));

            class mockHeader extends JLabel {

                public mockHeader(String title) {
                    super(title);
//                    setBackground(new Color(0x000000));
                    int inset = 2;
                    setFont(getFont().deriveFont(Font.BOLD));
                    setBorder(new EmptyBorder(inset, inset, inset, inset));
                    setForeground(Color.WHITE);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    Color color1 = new Color(0x505050);
                    Color color2 = new Color(0x808090);
                    GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                    g2d.setPaint(gp);
                    g.fillRect(0, 0, w, h);
                    g.setColor(getForeground());
                    super.paintComponent(g);
                }

            }
            final int headerHeight = 20;
//            final Border border = new EmptyBorder(headerHeight, 0, 0, 0);

            JPanel procPanel = new JPanel(new TableLayout(new double[][]{{TableLayout.FILL}, {headerHeight, TableLayout.FILL}}));
            procPanel.add(new mockHeader("Process"), "0 0 ");
            TableModel mockTM = new DefaultTableModel(2, 2);
            mockTM.setValueAt("Name", 0, 0);
            mockTM.setValueAt("MSRLD5selection", 0, 1);
            mockTM.setValueAt("Abstract", 1, 0);
            mockTM.setValueAt("Selection of MSRLD data.", 1, 1);
            JTable jTable = new JTable(mockTM);
            procPanel.add(jTable, "0 1");
            voidCard.add(procPanel, "0 0");

            procPanel = new JPanel(new TableLayout(new double[][] {{TableLayout.FILL}, {headerHeight, TableLayout.FILL}}));
            procPanel.add(new mockHeader("Inputs"), "0 0 ");
            mockTM = new DefaultTableModel(2, 2);
            mockTM.setValueAt("MSRL-D5 Daten", 0, 0);
            mockTM.setValueAt("Complex", 0, 1);
            mockTM.setValueAt("Bewertungsjahr", 1, 0);
            mockTM.setValueAt("Literal", 1, 1);
            jTable = new JTable(mockTM);
//            jTable.setBorder(border);
            procPanel.add(jTable, "0 1");
            voidCard.add(procPanel, "0 2");

            procPanel = new JPanel(new TableLayout(new double[][] {{TableLayout.FILL}, {headerHeight, TableLayout.FILL}}));
            procPanel.add(new mockHeader("Outputs"), "0 0 ");
            mockTM = new DefaultTableModel(3, 2);
            mockTM.setValueAt("relevant Algea", 0, 0);
            mockTM.setValueAt("Complex", 0, 1);
            mockTM.setValueAt("relevant Seagras", 1, 0);
            mockTM.setValueAt("Complex", 1, 1);
            mockTM.setValueAt("relevant Zears", 2, 0);
            mockTM.setValueAt("Complex", 2, 1);
            jTable = new JTable(mockTM);
//            jTable.setBorder(border);
            procPanel.add(jTable, "0 1");
            voidCard.add(procPanel, "0 4");
        }
        return voidCard;
    }

    private SingleProcessCard getSingleProcessCard() {
        if (null == singleProcessCard) {
            singleProcessCard = new SingleProcessCard();
        }

        return singleProcessCard;
    }

    private MultiProcessCard getMultiProcessesCard() {
        if (null == multiProcessCard) {
            multiProcessCard = new MultiProcessCard();
        }

        return multiProcessCard;
    }

    protected JPanel getContentPanel() {
        return contentPanel;
    }
}
