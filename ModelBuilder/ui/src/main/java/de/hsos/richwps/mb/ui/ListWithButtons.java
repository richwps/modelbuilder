package de.hsos.richwps.mb.ui;

import de.hsos.richwps.mb.app.AppConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import layout.TableLayout;

/**
 * A list component with (optional) buttons for adding, editing and deleting
 * list items.
 *
 * @author dziegenh
 * @param <E> type of list items
 */
public class ListWithButtons<E> extends JPanel {

    protected Window parent;
    private JButton addItemButton;
    private JButton editItemButton;
    private JButton deleteItemButton;
    protected final JList<E> viewList;
    protected DefaultListModel<E> listModel;

    // button IDs
    public static int BTN_ADD_ID = 1;
    public static int BTN_EDIT_ID = 1 << 1;
    public static int BTN_DELETE_ID = 1 << 2;

    /**
     * Creates a list with add, edit and delete buttons.
     *
     * @param parent
     */
    public ListWithButtons(Window parent) {
        this(parent, BTN_ADD_ID | BTN_EDIT_ID | BTN_DELETE_ID);
    }

    /**
     * Creates a list with the given buttons.
     *
     * @param parent
     * @param buttonIds
     */
    public ListWithButtons(Window parent, int buttonIds) {
        super();
        this.parent = parent;

        // create desired buttons
        if ((buttonIds & BTN_ADD_ID) > 0) {
            addItemButton = createAddButton();
        } else {
            addItemButton = null;
        }

        if ((buttonIds & BTN_EDIT_ID) > 0) {
            editItemButton = createEditButton();
        } else {
            editItemButton = null;
        }

        if ((buttonIds & BTN_DELETE_ID) > 0) {
            deleteItemButton = createDeleteButton();
        } else {
            deleteItemButton = null;
        }

        listModel = new DefaultListModel<>();
        viewList = createList();
    }

    public JButton getAddItemButton() {
        return addItemButton;
    }

    public JButton getDeleteItemButton() {
        return deleteItemButton;
    }

    public JButton getEditItemButton() {
        return editItemButton;
    }

    public void init() {
        this.init(new LinkedList<E>());
    }

    public void init(List<E> items) {
        double[][] layoutSize = new double[][]{
            {TableLayout.FILL, TableLayout.PREFERRED},
            {TableLayout.PREFERRED, TableLayout.FILL}
        };
        setLayout(new TableLayout(layoutSize));

        setItems(items);
        add(viewList, "0 0");

        // Add Buttons
        add(createButtonsPanel(), "1 0");

        if (items.isEmpty()) {
            // disable buttons if list is empty
            if (null != editItemButton) {
                editItemButton.setEnabled(false);
            }
            if (null != deleteItemButton) {
                deleteItemButton.setEnabled(false);
            }
        } else {
            viewList.setSelectedIndex(0);
        }
    }

    public void setItems(List<E> items) {
        listModel.removeAllElements();
        for (E anItem : items) {
            listModel.addElement(anItem);
        }
    }

    private JList<E> createList() {
        final JList<E> viewList = new JList<>(listModel);
        viewList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        viewList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                E selectedValue = viewList.getSelectedValue();
                boolean hasSelection = null != selectedValue;
                editItemButton.setEnabled(hasSelection);
                deleteItemButton.setEnabled(hasSelection);
            }
        });
        viewList.setBorder(new LineBorder(Color.DARK_GRAY, 1));

        return viewList;
    }

    private Container createButtonsPanel() {
        JToolBar pseudoBar = new JToolBar(JToolBar.VERTICAL);
        pseudoBar.setFloatable(false);

        if (null != addItemButton) {
            pseudoBar.add(addItemButton);
        }

        if (null != editItemButton) {
            pseudoBar.add(editItemButton);
        }

        if (null != deleteItemButton) {
            pseudoBar.add(deleteItemButton);
        }

        return pseudoBar;
    }

    protected JButton createAddButton() {
        JButton button = new JButton(UIManager.getIcon(AppConstants.ICON_ADD_KEY));
        button.setToolTipText("Add item");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        return button;
    }

    protected JButton createEditButton() {
        JButton button = new JButton(UIManager.getIcon(AppConstants.ICON_EDIT_KEY));
        button.setToolTipText("Edit selected item");

        return button;
    }

    protected JButton createDeleteButton() {
        JButton button = new JButton(UIManager.getIcon(AppConstants.ICON_DELETE_KEY));
        button.setToolTipText("Delete selected item");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showOptionDialog(parent, "Delete the selected item?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (choice == JOptionPane.YES_OPTION) {
                    listModel.removeElementAt(viewList.getSelectionModel().getMinSelectionIndex());
                }
            }
        });

        return button;
    }

    public void addItemToList(E item) {
        listModel.addElement(item);

        // select added item
        int idx = listModel.getSize() - 1;
        viewList.getSelectionModel().setSelectionInterval(idx, idx);
        viewList.invalidate();
        viewList.updateUI();
    }

    public void addSelectionListener(ListSelectionListener listener) {
        viewList.getSelectionModel().addListSelectionListener(listener);
    }

    public E getSelectedItem() {
        return viewList.getSelectedValue();
    }

    public List<E> getAllItems() {
        List<E> list = new LinkedList<>();

        for (int i = 0; i < listModel.getSize(); i++) {
            list.add(listModel.get(i));
        }

        return list;
    }

    public void clear() {
        listModel.removeAllElements();
        // TODO need to update UI ?
    }

    public void selectItem(E item) {
        viewList.setSelectedValue(item, true);
    }

    public int getSelectedIndex() {
        return viewList.getSelectedIndex();
    }

    public void setItemAt(int index, E value) {
        listModel.setElementAt(value, index);
    }

}
