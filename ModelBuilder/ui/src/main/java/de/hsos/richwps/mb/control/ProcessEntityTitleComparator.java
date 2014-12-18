package de.hsos.richwps.mb.control;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.ui.UiHelper;
import java.util.Comparator;

/**
 *
 * @author dziegenh
 */
public class ProcessEntityTitleComparator implements Comparator<ProcessEntity> {

    @Override
    public int compare(ProcessEntity o1, ProcessEntity o2) {
        String o1Title = UiHelper.avoidNull(o1.getOwsTitle());
        String o2Title = UiHelper.avoidNull(o2.getOwsTitle());
        
        o1Title = o1Title.toLowerCase();
        o2Title = o2Title.toLowerCase();
        
        return o1Title.compareTo(o2Title);
    }

}
