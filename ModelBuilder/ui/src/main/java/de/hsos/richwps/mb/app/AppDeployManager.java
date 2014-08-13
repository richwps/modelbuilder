/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.dsl.Export;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.server.Mock;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    void deploy() {
        try {
            String dslFile = "generated.rola";
            new Export(getGraphView().getGraph().clone()).export(dslFile);

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
    }

}
