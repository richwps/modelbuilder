/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.appEvents.IAppEventObserver;
import de.hsos.richwps.mb.infoTabsView.InfoTabs;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author dziegenh
 */
public class AppInfoTabs extends InfoTabs {

    private boolean prependTime = true;
    private Calendar calendar = Calendar.getInstance(Locale.getDefault());
//    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat., Locale.getDefault());
    private DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault());
    
    public AppInfoTabs() {
        setTextColor(AppConstants.INFOTABS_TEXTCOLOR);
        setMinimumSize(AppConstants.INFOTABS_MIN_SIZE);

        // Create/add tabs
        for (String[] tabData : AppConstants.INFOTABS) {
            addTab(tabData[0], tabData[1]);
        }

        AppEventService.getInstance().registerObserver(new IAppEventObserver() {
            @Override
            public void eventOccured(AppEvent e) {
                String command = e.getCommand();
                String message = e.getMessage();

                for (String[] infoTab : AppConstants.INFOTABS) {
                    String tabId = infoTab[0];
                    if (tabId.equals(command)) {
                        if(prependTime) {
                            calendar.setTime(new Date());
                            StringBuilder sb = new StringBuilder(message.length() + 10);
                            sb.append("(");
                            sb.append(timeFormat.format(calendar.getTime()));
                            sb.append(") ");
                            sb.append(message);
                            message = sb.toString();
                        }

                        output(tabId, message);
                    }
                }
            }
        });
    }

    public boolean isPrependTime() {
        return prependTime;
    }

    public void setPrependTime(boolean prependTime) {
        this.prependTime = prependTime;
    }

}
