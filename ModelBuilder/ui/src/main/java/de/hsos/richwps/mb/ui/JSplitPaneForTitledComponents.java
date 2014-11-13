package de.hsos.richwps.mb.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JSplitPane;

/**
 *
 * @author dziegenh
 */
public class JSplitPaneForTitledComponents extends JSplitPane {

    public JSplitPaneForTitledComponents() {
        super();

        init();
    }

    public JSplitPaneForTitledComponents(int newOrientation) {
        super(newOrientation);
        init();
    }

    public JSplitPaneForTitledComponents(
            int newOrientation,
            boolean newContinuousLayout) {

        super(newOrientation, newContinuousLayout);

        init();
    }

    public JSplitPaneForTitledComponents(
            int newOrientation,
            Component newLeftComponent,
            Component newRightComponent) {

        super(newOrientation, newLeftComponent, newRightComponent);
        init();
    }

    public JSplitPaneForTitledComponents(
            int newOrientation,
            boolean newContinuousLayout,
            Component newLeftComponent,
            Component newRightComponent) {

        super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
        init();
    }

    private Integer lastDividerLocation;

    private void init() {

        getComponent(0).addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {

                    // Toggle between user's divider location and the centered location
                    if (null == lastDividerLocation) {
                        int tmpLocation = getDividerLocation();
                        setDividerLocation(.5);
                        lastDividerLocation = tmpLocation;
                    } else {
                        setDividerLocation(lastDividerLocation);
                        lastDividerLocation = null;
                    }
                }
            }

        });

        // reset saved divider location when changed by the user.
        addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent pce) {
                        lastDividerLocation = null;
                    }
                });

        // TODO design a fancy divider :)
        
//        setBorder(new EmptyBorder(AppConstants.DEFAULT_COMPONENT_INSETS));
//        setUI(new BasicSplitPaneUI() {
//            @Override
//            public BasicSplitPaneDivider createDefaultDivider() {
//                BasicSplitPaneDivider basicSplitPaneDivider = new BasicSplitPaneDivider(this) {
//
//                    @Override
//                    public void setBorder(Border b) {
//                    }
//
//                    @Override
//                    public void paint(Graphics g) {
//
//                        // Style 1 + 2
////                        Rectangle fillRect = new Rectangle(0, 0, getSize().width, getSize().height);
//                        
//                        // Style 3
//                        Rectangle fillRect = new Rectangle(0, 0, getSize().width, getSize().height);
//
//                        if (getOrientation() == HORIZONTAL_SPLIT) {
//                            fillRect.y = TitledComponent.DEFAULT_TITLE_HEIGHT -1;
//                            
////                            fillRect.x = (int) Math.floor(getSize().width / 2.);
////                            fillRect.width = (int) Math.ceil(getSize().width / 2.);
//                            
//                            //style 3
//                            fillRect.x = (int) Math.floor(getSize().width / 2.);
//                            fillRect.width = fillRect.x;
//                        }
//
//                        // Style 1
////                        g.setColor(AppConstants.SELECTION_BG_COLOR.darker());
////                        g.fillRect(fillRect.x, fillRect.y, fillRect.width, fillRect.height);
//                        
//                        // Style 2
////                        g.setColor(AppConstants.SELECTION_BG_COLOR.darker());
////                        g.drawRect(fillRect.x, fillRect.y, fillRect.width-1, fillRect.height-1);
////                        g.setColor(AppConstants.SELECTION_BG_COLOR);
////                        g.fillRect(fillRect.x+1, fillRect.y+1, fillRect.width-2, fillRect.height-2);
//
//                        // Style 3
//                        g.setColor(AppConstants.SELECTION_BG_COLOR.darker());
//                        g.drawLine(fillRect.x, fillRect.y, fillRect.width, fillRect.height);
//                        
//                        
//                        super.paint(g);
//                    }
//                };
//
//                return basicSplitPaneDivider;
//            }
//        });
//
//        setDividerSize(4);
    }

}
