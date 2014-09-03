/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.dsl.Export;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.server.DeployView;
import de.hsos.richwps.mb.server.Mock;
import de.hsos.richwps.mb.server.entity.DeployConfig;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author dziegenh
 */
public class AppDeployManager {

    private App app;

    public AppDeployManager(App app) {
        this.app = app;
    }

    private GraphView getGraphView() {
        return app.getGraphView();
    }

    private JFrame getFrame() {
        return app.getFrame();
    }

    boolean deploy() {
        String rola = generateRola();
        if (null == rola) {
            return false;
        }

        // TODO create gui for wpsurl etc. !!
        String wpsurl = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";
        String wpsturl = wpsurl;

        RichWPSProvider instance = new RichWPSProvider();
        try {
            instance.connect(wpsurl, wpsturl);
        } catch (Exception ex) {
            getLogger(AppDeployManager.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }
        DeployView deployView = new DeployView(getFrame(), "asd");
        deployView.init(new LinkedList<DeployConfig>());
        deployView.setVisible(true);

//        public DeployRequest(
//        final String endpoint,
//        final String identifier,
//        final String title,
//        final String processversion,
//        final String deploymentprofile
        DeployRequest dto = new DeployRequest("localhost", "test", "test", "1.0", "ROLA");

        assembleDeployRequest(dto);

//        instance.deploy(dto);
//        instance.disconnect(wpsurl, wpsturl);
        return true;
    }

    private String generateRola() {
        try {
            String dslFile = "generated.rola";
//            new Export(getGraphView().getGraph().clone()).export(dslFile);
            new Export(getGraphView().getGraph()).export(dslFile);

            String content = null;
            File file = new File(dslFile); //for ex foo.txt
            FileReader reader = new FileReader(file);
            try {
                char[] chars = new char[(int) file.length()];
                reader.read(chars);
                content = new String(chars);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader.close();
            }
            JOptionPane.showMessageDialog(getFrame(), content, "Generated ROLA", JOptionPane.PLAIN_MESSAGE);
            return content;

        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder(200);
            sb.append(AppConstants.DEPLOYMENT_FAILED);
            sb.append('\n');
            sb.append(AppConstants.SEE_LOGGING_TABS);

            JOptionPane.showMessageDialog(getFrame(), sb.toString());

            String exMsg;
            if (ex.getMessage() == null || ex.getMessage().isEmpty()) {
                exMsg = ex.getClass().getSimpleName();
            } else {
                exMsg = ex.getMessage();
            }

            sb = new StringBuilder(200);
            sb.append(AppConstants.DEPLOYMENT_FAILED);
            sb.append('\n');
            sb.append(String.format(AppConstants.ERROR_MSG_IS_FORMAT, exMsg));

            AppEventService.getInstance().fireAppEvent(sb.toString(), Mock.getInstance());
            Logger
                    .getLogger(App.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void assembleDeployRequest(DeployRequest dto) {
        // TODO transform graph model to request 
        //        dto.addInput(this.createComplexDataInput());
//        dto.addInput(this.createLiteralDataInput());
//        dto.addOutput(this.createComplexDataOutput());
//        dto.addOutput(this.createLiteralDataOutput());
//        dto.setExecutionUnit("Execunit.");
    }

}
