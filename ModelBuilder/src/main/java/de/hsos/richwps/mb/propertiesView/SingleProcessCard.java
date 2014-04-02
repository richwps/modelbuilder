/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author dziegenh
 */
public class SingleProcessCard extends JPanel {

    public SingleProcessCard() {
        super();

        // TODO set/configure layout
    }

    // TODO completely MOCKED !!! make it beautiful :)
    void setProcess(IProcessEntity process) {
        removeAll();

        add(new JLabel(process.getId()));
        add(new JLabel(process.getTitle()));

        JPanel inputPanel = new JPanel();
        for (ProcessPort inport : process.getInputPorts()) {
            inputPanel.add(new JLabel(inport.getOwsTitle()));
        }
        inputPanel.setBorder(new TitledBorder(AppConstants.PROPERTIES_INPUTS_TITLE));

        JPanel outputPanel = new JPanel();
        for (ProcessPort outport : process.getOutputPorts()) {
            outputPanel.add(new JLabel(outport.getOwsTitle()));
        }
        outputPanel.setBorder(new TitledBorder(AppConstants.PROPERTIES_OUTPUTS_TITLE));

        add(inputPanel);
        add(outputPanel);

        updateUI();
    }

}
