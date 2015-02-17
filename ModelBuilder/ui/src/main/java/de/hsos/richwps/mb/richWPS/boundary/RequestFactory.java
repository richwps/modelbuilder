package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ProfileRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;

/**
 *
 * @author dalcacer
 * @version 0.1
 */
public class RequestFactory {


    public static IRequest createDescribeRequest(
            final String wpsendpoint, final String identifier) {
        DescribeRequest request = new DescribeRequest();
        request.setEndpoint(wpsendpoint);
        request.setIdentifier(identifier);
        return request;
    }

    public static IRequest createExecuteRequest(final String wpsendpoint,
            final String identifier) {
        ExecuteRequest request = new ExecuteRequest();
        request.setEndpoint(wpsendpoint);
        request.setIdentifier(identifier);
        return request;
    }

    public static IRequest createDeployRequest(final String wpsendpoint,
            final String richwpsendpoint, final String identifier,
            final String title, final String version) {
        return new DeployRequest(wpsendpoint, richwpsendpoint,
                identifier, title, version, IRichWPSProvider.DEPLOYMENTPROFILE);
    }

    public static IRequest createProfileRequest(final String wpsendpoint,
            final String richwpsendpoint, final String identifier,
            final String title, final String version) {
        return new ProfileRequest(wpsendpoint, richwpsendpoint,
                identifier, title, version, IRichWPSProvider.DEPLOYMENTPROFILE);
    }

    public static IRequest createTestRequest(final String wpsendpoint,
            final String richwpsendpoint,
            final String identifier, final String title, final String version) {
        return new TestRequest(wpsendpoint, richwpsendpoint,
                identifier, title, version, IRichWPSProvider.DEPLOYMENTPROFILE);
    }

    public static IRequest createUndeployRequest(final String wpsendpoint,
            final String richwpsendpoint, final String identifier) {
        return new UndeployRequest(wpsendpoint, richwpsendpoint, identifier);
    }

}
