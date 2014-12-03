package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.sp.client.ows.SPClient;
import de.hsos.richwps.sp.client.ows.gettypes.WPS;
import de.hsos.richwps.sp.client.ows.posttypes.PostBoundingBoxData;
import de.hsos.richwps.sp.client.ows.posttypes.PostComplexData;
import de.hsos.richwps.sp.client.ows.posttypes.PostInAndOutputForm;
import de.hsos.richwps.sp.client.ows.posttypes.PostInput;
import de.hsos.richwps.sp.client.ows.posttypes.PostLiteralData;
import de.hsos.richwps.sp.client.ows.posttypes.PostOutput;
import de.hsos.richwps.sp.client.ows.posttypes.PostProcess;
import de.hsos.richwps.sp.client.ows.posttypes.PostWPS;
import java.util.ArrayList;

/**
 *
 * @author dziegenh
 */
public class Publisher {

    public void publishProcess(WPS[] spWpss, ProcessEntity process) {
        String server = process.getServer();

        try {

            WPS wps = null;
            for (WPS aWps : spWpss) {
                if (aWps.getEndpoint().equals(server)) {
                    wps = aWps;
                }
            }

            PostWPS postWps = new PostWPS();
            // TODO handle non-existing WPS ! (->postWps)
            if (null == wps) {
//                PostWPS postWPS = new PostWPS();
//                postWPS.setEndpoint(new URL(server));
//                postWPS.setRichWPSEndpoint(new URL(wps.getRichWPSEndpoint()));

            } else {
                postWps.setRdfId(wps.getRDFID());
            }

            PostProcess postProcess = new PostProcess();
            postProcess.setWps(postWps);
            postProcess.setIdentifier(process.getOwsIdentifier());
            postProcess.setBstract(process.getOwsAbstract());
            postProcess.setTitle(process.getOwsTitle());

            Object value;
            value = process.getPropertyValue(ProcessEntity.PROPERTIES_KEY_VERSION);
            postProcess.setProcessVersion((String) value);

            ArrayList<PostInput> postInputs = new ArrayList<>();

            for (ProcessPort aPort : process.getInputPorts()) {
                PostInput postPort = new PostInput();
                postPort.setBstract(aPort.getOwsAbstract());
                postPort.setIdentifier(aPort.getOwsIdentifier());
                postPort.setTitle(aPort.getOwsTitle());

//                value = aPort.getPropertyValue(ProcessPort.PROPERTY_KEY_MAXOCCURS);
                value = new Integer(0);
                postPort.setMaxOcc((int) value);
//
//                value = aPort.getPropertyValue(ProcessPort.PROPERTY_KEY_MINOCCURS);
                postPort.setMinOcc((int) value);
                // TODO set max mb !!
                value = aPort.getPropertyValue(ProcessPort.PROPERTY_KEY_MAXMB);
//                    postPort.setOcc((int) value);

                PostInAndOutputForm datatype = null;
                switch (aPort.getDatatype()) {
                    case LITERAL:
                        datatype = new PostLiteralData();
                        // TODO set default value !
                        break;
                    case COMPLEX:
                        datatype = new PostComplexData();
                        // TODO set Format(s) !!
                        break;
                    case BOUNDING_BOX:
                        datatype = new PostBoundingBoxData();
                        break;
                }

                if (null != datatype) {
                    postPort.setPostInputFormChoice(datatype);
                }

                postInputs.add(postPort);
            }

            postProcess.setInputs(postInputs);

            ArrayList<PostOutput> postOutputs = new ArrayList<>();

            for (ProcessPort aPort : process.getOutputPorts()) {
                PostOutput postPort = new PostOutput();
                postPort.setBstract(aPort.getOwsAbstract());
                postPort.setIdentifier(aPort.getOwsIdentifier());
                postPort.setTitle(aPort.getOwsTitle());

                // TODO set max mb !!
                value = aPort.getPropertyValue(ProcessPort.PROPERTY_KEY_MAXMB);
//                    postPort.setOcc((int) value);

                PostInAndOutputForm datatype = null;
                switch (aPort.getDatatype()) {
                    case LITERAL:
                        datatype = new PostLiteralData();
                        // TODO set default value !
                        break;
                    case COMPLEX:
                        datatype = new PostComplexData();
                        // TODO set Format(s) !!
                        break;
                    case BOUNDING_BOX:
                        datatype = new PostBoundingBoxData();
                        break;
                }

                if (null != datatype) {
                    postPort.setPostOutputFormChoice(datatype);
                }

                postOutputs.add(postPort);
            }
            postProcess.setOutputs(postOutputs);

            SPClient.getInstance().postProcess(postProcess);

        } catch (Exception ex) {
            Logger.log(ex);
        }
    }

}
