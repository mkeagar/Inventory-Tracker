
package gui.batches;

import gui.common.DialogBox;
import gui.common.DialogView;
import gui.common.GridBagConstraintsExt;
import gui.common.TableOperations;
import gui.common.Tagable;
import gui.item.ItemData;
import gui.main.GUI;
import gui.product.ProductData;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import common.util.DateUtils;

public abstract class ItemBatchView extends DialogView
{

	private class ItemFormatter extends Tagable
	{
		private int column;

		public ItemFormatter(int column)
		{
			this.column = column;
		}

		@Override
		public String toString()
		{
			ItemData data = (ItemData) getTag();
			if(data != null)
			{
				switch(column)
				{
				case 0:
					return DateUtils.formatDate(data.getEntryDate());
				case 1:
					return (data.getExpirationDate() != null ? DateUtils
							.formatDate(data.getExpirationDate()) : "");
				case 2:
					return data.getBarcode();
				case 3:
					return data.getStorageUnit();
				case 4:
					return data.getProductGroup();
				default:
					assert false;
				}
			}
			return "";
		}
	}
	private class ProductFormatter extends Tagable
	{
		private int column;

		public ProductFormatter(int column)
		{
			this.column = column;
		}

		@Override
		public String toString()
		{
			ProductData data = (ProductData) getTag();
			if(data != null)
			{
				switch(column)
				{
				case 0:
					return data.getDescription();
				case 1:
					return data.getSize();
				case 2:
					return data.getCount();
				case 3:
					return data.getShelfLife();
				case 4:
					return data.getSupply();
				case 5:
					return data.getBarcode();
				default:
					assert false;
				}
			}
			return "";
		}
	}
	// --------------------------
	// Item Batch members
	// --------------------------
	protected JPanel batchPanel;
	protected JLabel barcodeLabel;
	protected JTextField barcodeField;
	protected JCheckBox scannerBox;
	protected JButton itemActionButton;
	protected JButton doneButton;

	protected JButton undoButton;
	protected JButton redoButton;
	// --------------------------
	// Product Table members
	// --------------------------
	protected JTable productTable;
	protected DefaultTableModel productTableModel;
	protected DefaultTableColumnModel productTableColumnModel;

	protected JTableHeader productTableHeader;
	protected JScrollPane productTableScrollPane;
	// --------------------------
	// Item Table members
	// --------------------------
	protected JTable itemTable;
	protected DefaultTableModel itemTableModel;
	protected DefaultTableColumnModel itemTableColumnModel;

	protected JTableHeader itemTableHeader;
	protected JScrollPane itemTableScrollPane;

	// --------------------------
	// Other members
	// --------------------------
	protected JPanel productPanel;

	protected JSplitPane splitPane;

	public ItemBatchView(GUI parent, final DialogBox dialog)
	{
		super(parent, dialog);

		dialog.setResizable(true);

		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent evt)
			{
				done();
				_dialog.dispose();
			}
		});
	}

	protected abstract void barcodeChanged();

	public void close()
	{
		_dialog.dispose();
	}

	@Override
	protected void createComponents()
	{
		createItemBatchPanel();
		createProductTable();
		createItemTable();
	}

	private void createItemBatchPanel()
	{
		KeyListener keyListener = new KeyListener()
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
				return;
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				barcodeChanged();
			}

			@Override
			public void keyTyped(KeyEvent arg0)
			{
				return;
			}
		};

		barcodeLabel = new JLabel(getBarcodeLabel());
		Font font = createFont(barcodeLabel.getFont(), ContentFontSize);
		barcodeLabel.setFont(font);

		barcodeField = new JTextField(15);
		barcodeField.setFont(font);
		barcodeField.addKeyListener(keyListener);

		scannerBox = new JCheckBox("Use barcode scanner");
		scannerBox.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if(eventsAreDisabled())
				{
					return;
				}
				useScannerChanged();
			}
		});

		itemActionButton = new JButton(getItemActionName());
		itemActionButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg)
			{
				itemAction();
			}
		});

		doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg)
			{
				done();
			}
		});

		undoButton = new JButton("Undo");
		undoButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg)
			{
				undo();
			}
		});

		redoButton = new JButton("Redo");
		redoButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg)
			{
				redo();
			}
		});
	}

	@SuppressWarnings("serial")
	private void createItemTable()
	{

		MouseAdapter mouseListener = new MouseAdapter()
		{

			private void handleMouseEvent(MouseEvent e)
			{
				if(eventsAreDisabled())
				{
					return;
				}
				if(e.getSource() == itemTableHeader)
				{
					// if (e.getButton() == MouseEvent.BUTTON1 &&
					// e.getID() == MouseEvent.MOUSE_PRESSED) {
					// int clickedColumnIndex =
					// commentsColumnModel.getColumnIndexAtX(e.getX());
					// if (clickedColumnIndex >= 0) {
					// updateCommentSortOrder(clickedColumnIndex);
					// }
					// }
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				handleMouseEvent(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				handleMouseEvent(e);
			}
		};

		itemTableColumnModel = new DefaultTableColumnModel();
		TableColumn column =
				createTableColumn(0, "Entry Date", ContentFontSize);
		itemTableColumnModel.addColumn(column);
		column = createTableColumn(1, "Expiration Date", ContentFontSize);
		itemTableColumnModel.addColumn(column);
		column = createTableColumn(2, "Item Barcode", ContentFontSize);
		itemTableColumnModel.addColumn(column);
		column = createTableColumn(3, "Storage Unit", ContentFontSize);
		itemTableColumnModel.addColumn(column);
		column = createTableColumn(4, "Product Group", ContentFontSize);
		itemTableColumnModel.addColumn(column);

		itemTableModel = new DefaultTableModel(0, 5)
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		itemTable = new JTable(itemTableModel, itemTableColumnModel);
		itemTable.setFont(createFont(itemTable.getFont(), ContentFontSize));
		itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener()
				{
					@Override
					public void valueChanged(ListSelectionEvent evt)
					{
						if(eventsAreDisabled())
						{
							return;
						}
						if(evt.getValueIsAdjusting())
						{
							return;
						}
						selectedItemChanged();
					}
				});
		itemTable.addMouseListener(mouseListener);

		itemTableHeader = itemTable.getTableHeader();
		itemTableHeader.setReorderingAllowed(false);
		itemTableHeader.addMouseListener(mouseListener);

		itemTableScrollPane =
				new JScrollPane(itemTable,
						ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		itemTableScrollPane.setPreferredSize(new Dimension(600, 300));
		itemTableScrollPane.setBorder(createTitledBorder("Items",
				BorderFontSize));
	}

	@SuppressWarnings("serial")
	private void createProductTable()
	{

		MouseAdapter mouseListener = new MouseAdapter()
		{

			private void handleMouseEvent(MouseEvent e)
			{
				if(eventsAreDisabled())
				{
					return;
				}
				if(e.getSource() == productTableHeader)
				{
					// if (e.getButton() == MouseEvent.BUTTON1 &&
					// e.getID() == MouseEvent.MOUSE_PRESSED) {
					// int clickedColumnIndex =
					// commentsColumnModel.getColumnIndexAtX(e.getX());
					// if (clickedColumnIndex >= 0) {
					// updateCommentSortOrder(clickedColumnIndex);
					// }
					// }
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				handleMouseEvent(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				handleMouseEvent(e);
			}
		};

		productTableColumnModel = new DefaultTableColumnModel();
		TableColumn column =
				createTableColumn(0, "Description", ContentFontSize);
		productTableColumnModel.addColumn(column);
		column = createTableColumn(1, "Size", ContentFontSize);
		productTableColumnModel.addColumn(column);
		column = createTableColumn(2, "Count", ContentFontSize);
		productTableColumnModel.addColumn(column);
		column = createTableColumn(3, "Shelf Life", ContentFontSize);
		productTableColumnModel.addColumn(column);
		column = createTableColumn(4, "3-Month Supply", ContentFontSize);
		productTableColumnModel.addColumn(column);
		column = createTableColumn(5, "Product Barcode", ContentFontSize);
		productTableColumnModel.addColumn(column);

		productTableModel = new DefaultTableModel(0, 6)
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};

		productTable = new JTable(productTableModel, productTableColumnModel);
		productTable
				.setFont(createFont(productTable.getFont(), ContentFontSize));
		productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		productTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener()
				{
					@Override
					public void valueChanged(ListSelectionEvent evt)
					{
						if(eventsAreDisabled())
						{
							return;
						}
						if(evt.getValueIsAdjusting())
						{
							return;
						}
						selectedProductChanged();
					}
				});
		productTable.addMouseListener(mouseListener);

		productTableHeader = productTable.getTableHeader();
		productTableHeader.setReorderingAllowed(false);
		productTableHeader.addMouseListener(mouseListener);

		productTableScrollPane =
				new JScrollPane(productTable,
						ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		productTableScrollPane.setPreferredSize(new Dimension(600, 300));
		productTableScrollPane.setBorder(createTitledBorder("Products",
				BorderFontSize));
	}

	protected abstract void done();

	public void enableItemAction(boolean value)
	{
		itemActionButton.setEnabled(value);
	}

	public void enableRedo(boolean value)
	{
		redoButton.setEnabled(value);
	}

	public void enableUndo(boolean value)
	{
		undoButton.setEnabled(value);
	}

	public String getBarcode()
	{
		return barcodeField.getText();
	}

	// --------------------------
	// Abstract method interface
	// --------------------------
	protected abstract String getBarcodeLabel();

	protected abstract String getItemActionName();

	public ItemData getSelectedItem()
	{
		int selectedIndex = itemTable.getSelectedRow();
		if(selectedIndex >= 0)
		{
			ItemFormatter formatter =
					(ItemFormatter) itemTableModel.getValueAt(selectedIndex, 0);
			return (ItemData) formatter.getTag();
		}
		return null;
	}

	//
	//
	//

	public ProductData getSelectedProduct()
	{
		int selectedIndex = productTable.getSelectedRow();
		if(selectedIndex >= 0)
		{
			ProductFormatter formatter =
					(ProductFormatter) productTableModel.getValueAt(
							selectedIndex, 0);
			return (ProductData) formatter.getTag();
		}
		return null;
	}

	public boolean getUseScanner()
	{
		return scannerBox.isSelected();
	}

	public void giveBarcodeFocus()
	{
		boolean disabledEvents = disableEvents();
		try
		{
			barcodeField.requestFocusInWindow();
		}
		finally
		{
			if(disabledEvents)
			{
				enableEvents();
			}
		}
	}

	protected abstract void itemAction();

	@Override
	protected void layoutComponents()
	{
		batchPanel = new JPanel();
		batchPanel.setLayout(new GridBagLayout());

		GridBagConstraintsExt c = new GridBagConstraintsExt();
		c.ipadx = 2;
		c.ipady = 2;
		c.insets = new Insets(5, 5, 5, 5);

		c.place(0, 0, 1, 1);
		batchPanel.add(barcodeLabel, c);

		c.place(1, 0, 2, 1);
		batchPanel.add(barcodeField, c);

		c.place(3, 0, 2, 1);
		batchPanel.add(scannerBox, c);

		c.place(1, 1, 1, 1);
		batchPanel.add(itemActionButton, c);

		c.place(2, 1, 1, 1);
		batchPanel.add(undoButton, c);

		c.place(3, 1, 1, 1);
		batchPanel.add(redoButton, c);

		c.place(4, 1, 1, 1);
		batchPanel.add(doneButton, c);

		setMaximumSize(batchPanel);

		productPanel = new JPanel();
		productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));

		productPanel.add(Box.createRigidArea(new Dimension(10, 10)));
		productPanel.add(batchPanel);
		productPanel.add(Box.createRigidArea(new Dimension(10, 10)));
		productPanel.add(productTableScrollPane);

		splitPane =
				new JSplitPane(JSplitPane.VERTICAL_SPLIT, productPanel,
						itemTableScrollPane);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(splitPane);
	}

	protected abstract void redo();

	protected abstract void selectedItemChanged();

	protected abstract void selectedProductChanged();

	public void selectItem(ItemData item)
	{
		boolean disabledEvents = disableEvents();
		try
		{
			for(int i = 0; i < itemTableModel.getRowCount(); ++i)
			{
				ItemFormatter formatter =
						(ItemFormatter) itemTableModel.getValueAt(i, 0);
				ItemData id = (ItemData) formatter.getTag();
				if(id == item)
				{
					TableOperations.selectTableRow(itemTable, i);
					return;
				}
			}
		}
		finally
		{
			if(disabledEvents)
			{
				enableEvents();
			}
		}
	}

	public void selectProduct(ProductData product)
	{
		boolean disabledEvents = disableEvents();
		try
		{
			for(int i = 0; i < productTableModel.getRowCount(); ++i)
			{
				ProductFormatter formatter =
						(ProductFormatter) productTableModel.getValueAt(i, 0);
				ProductData pd = (ProductData) formatter.getTag();
				if(pd == product)
				{
					TableOperations.selectTableRow(productTable, i);
					return;
				}
			}
		}
		finally
		{
			if(disabledEvents)
			{
				enableEvents();
			}
		}
	}

	public void setBarcode(String barcode)
	{
		boolean disabledEvents = disableEvents();
		try
		{
			barcodeField.setText(barcode);
		}
		finally
		{
			if(disabledEvents)
			{
				enableEvents();
			}
		}
	}

	public void setItems(ItemData[] items)
	{
		boolean disabledEvents = disableEvents();
		try
		{
			itemTableModel.setRowCount(0);
			for(ItemData id: items)
			{
				ItemFormatter[] row = new ItemFormatter[5];
				row[0] = new ItemFormatter(0);
				row[0].setTag(id);
				row[1] = new ItemFormatter(1);
				row[1].setTag(id);
				row[2] = new ItemFormatter(2);
				row[2].setTag(id);
				row[3] = new ItemFormatter(3);
				row[3].setTag(id);
				row[4] = new ItemFormatter(4);
				row[4].setTag(id);
				itemTableModel.addRow(row);
			}
		}
		finally
		{
			if(disabledEvents)
			{
				enableEvents();
			}
		}
	}

	protected void setMaximumSize(JComponent c)
	{
		Dimension preferred = c.getPreferredSize();
		Dimension maximum =
				new Dimension(Integer.MAX_VALUE, (int) preferred.getHeight());
		c.setMaximumSize(maximum);
	}

	public void setProducts(ProductData[] products)
	{
		boolean disabledEvents = disableEvents();
		try
		{
			productTableModel.setRowCount(0);
			for(ProductData pd: products)
			{
				ProductFormatter[] row = new ProductFormatter[6];
				row[0] = new ProductFormatter(0);
				row[0].setTag(pd);
				row[1] = new ProductFormatter(1);
				row[1].setTag(pd);
				row[2] = new ProductFormatter(2);
				row[2].setTag(pd);
				row[3] = new ProductFormatter(3);
				row[3].setTag(pd);
				row[4] = new ProductFormatter(4);
				row[4].setTag(pd);
				row[5] = new ProductFormatter(5);
				row[5].setTag(pd);
				productTableModel.addRow(row);
			}
		}
		finally
		{
			if(disabledEvents)
			{
				enableEvents();
			}
		}
	}

	public void setUseScanner(boolean value)
	{
		boolean disabledEvents = disableEvents();
		try
		{
			scannerBox.setSelected(value);
		}
		finally
		{
			if(disabledEvents)
			{
				enableEvents();
			}
		}
	}

	//
	//
	//

	protected abstract void undo();

	protected abstract void useScannerChanged();

}
