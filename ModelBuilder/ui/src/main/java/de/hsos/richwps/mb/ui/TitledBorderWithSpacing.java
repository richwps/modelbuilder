/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import static javax.swing.border.TitledBorder.ABOVE_BOTTOM;
import static javax.swing.border.TitledBorder.ABOVE_TOP;
import static javax.swing.border.TitledBorder.BELOW_BOTTOM;
import static javax.swing.border.TitledBorder.BELOW_TOP;
import static javax.swing.border.TitledBorder.BOTTOM;
import static javax.swing.border.TitledBorder.CENTER;
import static javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION;
import static javax.swing.border.TitledBorder.DEFAULT_POSITION;
import static javax.swing.border.TitledBorder.LEADING;
import static javax.swing.border.TitledBorder.LEFT;
import static javax.swing.border.TitledBorder.RIGHT;
import static javax.swing.border.TitledBorder.TOP;
import static javax.swing.border.TitledBorder.TRAILING;

/**
 * Modified version of TitledBorder.
 * Added custom spacing value between border and component.
 * Behavior of the TitledBorder spacing value is a bit strange...
 *
 * @author dziegenh
 */
public class TitledBorderWithSpacing extends TitledBorder {

    protected int spacing = TitledBorder.EDGE_SPACING;

    private Point textLoc = new Point();

    public TitledBorderWithSpacing(String title, int spacing) {
        super(title);
        this.spacing = spacing;
    }

    @Override
    /**
     * Copy&Paste from TitledBorder - changed EDGE_SPACING to spacing.
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        Border border = getBorder();

        if (getTitle() == null || getTitle().equals("")) {
            if (border != null) {
                border.paintBorder(c, g, x, y, width, height);
            }
            return;
        }

        Rectangle grooveRect = new Rectangle(x + spacing, y + spacing,
                                             width - (spacing * 2),
                                             height - (spacing * 2));
        Font font = g.getFont();
        Color color = g.getColor();

        g.setFont(getFont(c));

        JComponent jc = (c instanceof JComponent) ? (JComponent)c : null;


        FontMetrics fm = g.getFontMetrics(font);
        int         fontHeight = fm.getHeight();
        int         descent = fm.getDescent();
        int         ascent = fm.getAscent();
        int         diff;
        int         stringWidth = fm.stringWidth(getTitle());
        Insets      insets;

        if (border != null) {
            insets = border.getBorderInsets(c);
        } else {
            insets = new Insets(0, 0, 0, 0);
        }

        int titlePos = getTitlePosition();
        switch (titlePos) {
            case ABOVE_TOP:
                diff = ascent + descent + (Math.max(spacing,
                                 TEXT_SPACING*2) - spacing);
                grooveRect.y += diff;
                grooveRect.height -= diff;
                textLoc.y = grooveRect.y - (descent + TEXT_SPACING);
                break;
            case TOP:
            case DEFAULT_POSITION:
                diff = Math.max(0, ((ascent/2) + TEXT_SPACING) - spacing);
                grooveRect.y += diff;
                grooveRect.height -= diff;
                textLoc.y = (grooveRect.y - descent) +
                (insets.top + ascent + descent)/2;
                break;
            case BELOW_TOP:
                textLoc.y = grooveRect.y + insets.top + ascent + TEXT_SPACING;
                break;
            case ABOVE_BOTTOM:
                textLoc.y = (grooveRect.y + grooveRect.height) -
                (insets.bottom + descent + TEXT_SPACING);
                break;
            case BOTTOM:
                grooveRect.height -= fontHeight/2;
                textLoc.y = ((grooveRect.y + grooveRect.height) - descent) +
                        ((ascent + descent) - insets.bottom)/2;
                break;
            case BELOW_BOTTOM:
                grooveRect.height -= fontHeight;
                textLoc.y = grooveRect.y + grooveRect.height + ascent +
                        TEXT_SPACING;
                break;
        }

	int justification = getTitleJustification();
	if(c.getComponentOrientation().isLeftToRight()) {
	    if(justification==LEADING ||
	       justification==DEFAULT_JUSTIFICATION) {
	        justification = LEFT;
	    }
	    else if(justification==TRAILING) {
	        justification = RIGHT;
	    }
	}
	else {
	    if(justification==LEADING ||
	       justification==DEFAULT_JUSTIFICATION) {
	        justification = RIGHT;
	    }
	    else if(justification==TRAILING) {
	        justification = LEFT;
	    }
	}

        switch (justification) {
            case LEFT:
                textLoc.x = grooveRect.x + TEXT_INSET_H + insets.left;
                break;
            case RIGHT:
                textLoc.x = (grooveRect.x + grooveRect.width) -
                        (stringWidth + TEXT_INSET_H + insets.right);
                break;
            case CENTER:
                textLoc.x = grooveRect.x +
                        ((grooveRect.width - stringWidth) / 2);
                break;
        }

        // If title is positioned in middle of border AND its fontsize
	// is greater than the border's thickness, we'll need to paint
	// the border in sections to leave space for the component's background
	// to show through the title.
        //
        if (border != null) {
            if (((titlePos == TOP || titlePos == DEFAULT_POSITION) &&
		  (grooveRect.y > textLoc.y - ascent)) ||
		 (titlePos == BOTTOM &&
		  (grooveRect.y + grooveRect.height < textLoc.y + descent))) {

                Rectangle clipRect = new Rectangle();

                // save original clip
                Rectangle saveClip = g.getClipBounds();

                // paint strip left of text
                clipRect.setBounds(saveClip);
                if (computeIntersection(clipRect, x, y, textLoc.x-1-x, height)) {
                    g.setClip(clipRect);
                    border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                                  grooveRect.width, grooveRect.height);
                }

                // paint strip right of text
                clipRect.setBounds(saveClip);
                if (computeIntersection(clipRect, textLoc.x+stringWidth+1, y,
                               x+width-(textLoc.x+stringWidth+1), height)) {
                    g.setClip(clipRect);
                    border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                                  grooveRect.width, grooveRect.height);
                }

                if (titlePos == TOP || titlePos == DEFAULT_POSITION) {
                    // paint strip below text
                    clipRect.setBounds(saveClip);
                    if (computeIntersection(clipRect, textLoc.x-1, textLoc.y+descent,
                                        stringWidth+2, y+height-textLoc.y-descent)) {
                        g.setClip(clipRect);
                        border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                                  grooveRect.width, grooveRect.height);
                    }

                } else { // titlePos == BOTTOM
		  // paint strip above text
                    clipRect.setBounds(saveClip);
                    if (computeIntersection(clipRect, textLoc.x-1, y,
                          stringWidth+2, textLoc.y - ascent - y)) {
                        g.setClip(clipRect);
                        border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                                  grooveRect.width, grooveRect.height);
                    }
                }

                // restore clip
                g.setClip(saveClip);

            } else {
                border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                                  grooveRect.width, grooveRect.height);
            }
        }

        Graphics2D g2d = (Graphics2D) g;
        g.setColor(getTitleColor());
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawString(getTitle(), textLoc.x, textLoc.y);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);

        g.setFont(font);
        g.setColor(color);
    }

    /**
     * Copy&Paste from TitledBorder.
     * @param dest
     * @param rx
     * @param ry
     * @param rw
     * @param rh
     * @return
     */
    protected static boolean computeIntersection(Rectangle dest,
                                               int rx, int ry, int rw, int rh) {
	int x1 = Math.max(rx, dest.x);
	int x2 = Math.min(rx + rw, dest.x + dest.width);
	int y1 = Math.max(ry, dest.y);
	int y2 = Math.min(ry + rh, dest.y + dest.height);
        dest.x = x1;
        dest.y = y1;
        dest.width = x2 - x1;
        dest.height = y2 - y1;

	if (dest.width <= 0 || dest.height <= 0) {
	    return false;
	}
        return true;
    }

}
