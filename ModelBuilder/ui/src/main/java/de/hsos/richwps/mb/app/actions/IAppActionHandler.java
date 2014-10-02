package de.hsos.richwps.mb.app.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * To be implemented by action handler instances.
 *
 * @author dziegenh
 */
public interface IAppActionHandler extends ActionListener {

    @Override
    public void actionPerformed(ActionEvent e);

}
