package datepicker;

/**
Copyright 2004 Juan Heyns. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY JUAN HEYNS ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JUAN HEYNS OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors and should not be interpreted as representing official policies, either expressed
or implied, of Juan Heyns.
*/
package org.jdatepicker.impl;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.devgrus.MainView;

import org.jdatepicker.*;
import org.jdatepicker.constraints.DateSelectionConstraint;


/**
 * Created on 25 Mar 2004
 * Refactored 21 Jun 2004
 * Refactored 14 May 2009
 * Refactored 16 April 2010
 * Updated 26 April 2010
 * Updated 10 August 2012
 * 
 * @author Juan Heyns
 * @author JC Oosthuizen
 * @author Yue Huang
 */
public class JDatePickerImpl extends JPanel implements JDatePicker {

	private static final long serialVersionUID = 2814777654384974503L;
	
	private Popup popup;
	private JFormattedTextField formattedTextField;
	private JButton button;
	
	private JDatePanelImpl datePanel;

    public JDatePickerImpl() {
        this(new JDatePanelImpl());
    }
    
    /* Custom By SeoDong */
    private MainView mainview;

	/**
	 * You are able to set the format of the date being displayed on the label.
	 * Formatting is described at:
	 * 
	 * http://java.sun.com/j2se/1.4.2/docs/api/java/text/SimpleDateFormat.html
	 * 
	 * @param datePanel
	 */
	public JDatePickerImpl(JDatePanelImpl datePanel) {
		this.datePanel = datePanel;

		//Initialise Variables
		popup = null;
		datePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        InternalEventHandler internalEventHandler = new InternalEventHandler();

		//Create Layout
		SpringLayout layout = new SpringLayout();
        setLayout(layout);

        //Create and Add Components
		//Add and Configure TextField
		formattedTextField = new JFormattedTextField(new DateComponentFormatter(ComponentManager.getInstance().getComponentFormatDefaults().getSelectedDateFormat()));
		DateModel<?> model = datePanel.getModel();
		setTextFieldValue(formattedTextField, model.getYear(), model.getMonth(), model.getDay(), model.isSelected());
		formattedTextField.setEditable(false);		
		add(formattedTextField);
        layout.putConstraint(SpringLayout.WEST, formattedTextField, 0, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, formattedTextField);

        /* Custom By SeoDong */
        mainview = datePanel.getMainView();
        formattedTextField.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {		
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(mainview != null) {
					mainview.updateListByCalendar(formattedTextField.getText().replace(". ", "-"));
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {	
			}
        	
        });
        
		//Add and Configure Button
		button = new JButton();
		button.setFocusable(true);
        Icon icon = ComponentManager.getInstance().getComponentIconDefaults().getPopupButtonIcon();
        button.setIcon(icon);
        if(icon == null) {
            // reset to caption
            button.setText("...");
        } else {
            // remove text
            button.setText("");
        }
		add(button);
        layout.putConstraint(SpringLayout.WEST, button, 1, SpringLayout.EAST, formattedTextField);
        layout.putConstraint(SpringLayout.EAST, this, 0, SpringLayout.EAST, button);
        layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, button);
		
		//Do layout formatting
		int h = (int)button.getPreferredSize().getHeight();
		int w = (int)datePanel.getPreferredSize().getWidth();
		button.setPreferredSize(new Dimension(h, h));
		formattedTextField.setPreferredSize(new Dimension(w-h-1, h));

		//Add event listeners
		addHierarchyBoundsListener(internalEventHandler);
//TODO		addAncestorListener(listener)
		button.addActionListener(internalEventHandler);
		formattedTextField.addPropertyChangeListener("value", internalEventHandler);
		datePanel.addActionListener(internalEventHandler);
		datePanel.getModel().addChangeListener(internalEventHandler);
        long eventMask = MouseEvent.MOUSE_PRESSED;
        Toolkit.getDefaultToolkit().addAWTEventListener(internalEventHandler, eventMask);
	}
	
	public void addActionListener(ActionListener actionListener) {
		datePanel.addActionListener(actionListener);
	}

	public void removeActionListener(ActionListener actionListener) {
		datePanel.removeActionListener(actionListener);
	}

	public DateModel<?> getModel() {
		return datePanel.getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.jdatepicker.JDatePicker#setTextEditable(boolean)
	 */
	public void setTextEditable(boolean editable) {
		formattedTextField.setEditable(editable);
	}
	
	/* (non-Javadoc)
	 * @see org.jdatepicker.JDatePicker#isTextEditable()
	 */
	public boolean isTextEditable() {
		return formattedTextField.isEditable();
	}
	
	/* (non-Javadoc)
	 * @see org.jdatepicker.JDatePicker#setButtonFocusable(boolean)
	 */
	public void setButtonFocusable(boolean focusable) {
		button.setFocusable(focusable);
	}
	
	/* (non-Javadoc)
	 * @see org.jdatepicker.JDatePicker#getButtonFocusable()
	 */
	public boolean getButtonFocusable() {
		return button.isFocusable();
	}

	/* (non-Javadoc)
	 * @see org.jdatepicker.JDatePicker#getJDateInstantPanel()
	 */
	public JDatePanel getJDateInstantPanel() {
		return datePanel;
	}

	/**
	 * Called internally to popup the dates.
	 */
	private void showPopup() {
		if (popup == null){
			PopupFactory fac = new PopupFactory();
			Point xy = getLocationOnScreen();
			datePanel.setVisible(true);
			popup = fac.getPopup(this, datePanel, (int) xy.getX(), (int) (xy.getY()+this.getHeight()));
			popup.show();
		}
	}
	
	/**
	 * Called internally to hide the popup dates. 
	 */
	private void hidePopup() {
		if (popup != null) {
			popup.hide();
			popup = null;
		}
	}

    private Set getAllComponents(Component component) {
        Set children = new HashSet();
        children.add(component);
        if (component instanceof Container) {
            Container container = (Container)component;
            Component[] components = container.getComponents();
            for (int i = 0; i < components.length; i++) {
                children.addAll(getAllComponents(components[i]));
            }
        }
        return children;
    }

    public boolean isDoubleClickAction() {
        return datePanel.isDoubleClickAction();
    }

    public boolean isShowYearButtons() {
        return datePanel.isShowYearButtons();
    }

    public void setDoubleClickAction(boolean doubleClickAction) {
        datePanel.setDoubleClickAction(doubleClickAction);
    }

    public void setShowYearButtons(boolean showYearButtons) {
        datePanel.setShowYearButtons(showYearButtons);
    }

    private void setTextFieldValue(JFormattedTextField textField, int year, int month, int day, boolean isSelected) {
        if (!isSelected) {
            textField.setValue(null);
        }
        else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            textField.setValue(calendar);
        }
    }

    public void addDateSelectionConstraint(DateSelectionConstraint constraint) {
        datePanel.addDateSelectionConstraint(constraint);
    }

    public void removeDateSelectionConstraint(DateSelectionConstraint constraint) {
        datePanel.removeDateSelectionConstraint(constraint);
    }

    public void removeAllDateSelectionConstraints() {
        datePanel.removeAllDateSelectionConstraints();
    }

    public Set<DateSelectionConstraint> getDateSelectionConstraints() {
        return datePanel.getDateSelectionConstraints();
    }

    public int getTextfieldColumns() {
        return formattedTextField.getColumns();
    }

    public void setTextfieldColumns(int columns) {
        formattedTextField.setColumns(columns);
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (!aFlag) {
            hidePopup();
        }
        super.setVisible(aFlag);
    }

    /**
	 * This internal class hides the public event methods from the outside 
	 */
	private class InternalEventHandler implements ActionListener, HierarchyBoundsListener, ChangeListener, PropertyChangeListener, AWTEventListener {

		public void ancestorMoved(HierarchyEvent arg0) {
			hidePopup();
		}

		public void ancestorResized(HierarchyEvent arg0) {
			hidePopup();
		}

		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == button){
				if (popup == null) {
					showPopup();
				}
				else {
					hidePopup();
				}
			} 
			else if (arg0.getSource() == datePanel){
				hidePopup();
			}
		}

		public void stateChanged(ChangeEvent arg0) {
			if (arg0.getSource() == datePanel.getModel()) {
				DateModel<?> model = datePanel.getModel();
				setTextFieldValue(formattedTextField, model.getYear(), model.getMonth(), model.getDay(), model.isSelected());
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (formattedTextField.isEditable() && formattedTextField.getValue() != null) {
				Calendar value = (Calendar)formattedTextField.getValue();
                DateModel model = new UtilCalendarModel(value);
				// check constraints
				if (!datePanel.checkConstraints(model)) {
					// rollback
					formattedTextField.setValue(evt.getOldValue());
					return;
				}
				datePanel.getModel().setDate(value.get(Calendar.YEAR), value.get(Calendar.MONTH), value.get(Calendar.DATE));
				datePanel.getModel().setSelected(true);
			}
		}

        public void eventDispatched(AWTEvent event) {
            if (MouseEvent.MOUSE_CLICKED == event.getID() && event.getSource() != button) {
                Set components = getAllComponents(datePanel);
                boolean clickInPopup = false;
                for (Object component: components) {
                    if (event.getSource() == component) {
                        clickInPopup = true;
                    }
                }
                if (!clickInPopup) {
                    hidePopup();
                }
            }
        }

    }

}