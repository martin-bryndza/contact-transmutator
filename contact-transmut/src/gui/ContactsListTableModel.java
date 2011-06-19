/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import contacttransmut.InternalDocColumnSchema;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Martin
 */
public class ContactsListTableModel extends AbstractTableModel{

    private static Document internalDoc;
    private static InternalDocColumnSchema columnSchema;
    private int rowCount = 0;
    private int columnCount = 0;
    private ArrayList<ArrayList<String>> data;

    public void initTable(Document internalD, InternalDocColumnSchema columnSch){
        internalDoc = internalD;
        columnSchema = columnSch;

        NodeList contactList = internalDoc.getElementsByTagName("contact");

        rowCount = contactList.getLength();
        columnCount = columnSchema.getColumnCount();

        fireTableStructureChanged();

        data = new ArrayList<ArrayList<String>>();
        data.clear();
        for (int i = 0; i < contactList.getLength(); i++) {
            Element contact = (Element) contactList.item(i);
            data.add(new ArrayList<String>());
            ArrayList<String> currentList = data.get(i);
            for (int z=0; z<columnCount; z++){
                currentList.add("");
            }
            NodeList uncategorizedList = contact.getElementsByTagName("uncategorized");
            for (int j = 0; j < uncategorizedList.getLength(); j++) {
                Element uncategorized = (Element) uncategorizedList.item(j);
                NodeList dataList = uncategorized.getElementsByTagName("data");
                for (int k = 0; k < dataList.getLength(); k++) {
                    Element eData = (Element) dataList.item(k);
                    currentList.set(Integer.parseInt(eData.getAttribute("counter")), eData.getTextContent());
                }
            }
        }

        fireTableDataChanged();
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= rowCount || columnIndex>= columnCount){
            throw new IndexOutOfBoundsException("Index out of bounds. Max is "
                    + String.valueOf(rowCount-1) + ", " + String.valueOf(columnCount-1)
                    + ". Input values: "
                    + String.valueOf(rowIndex) + ", " + String.valueOf(columnIndex));
        }
        return data.get(rowIndex).get(columnIndex);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return String.valueOf(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }



}
