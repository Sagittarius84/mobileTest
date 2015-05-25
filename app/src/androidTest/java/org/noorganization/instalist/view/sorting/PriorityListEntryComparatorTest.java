package org.noorganization.instalist.view.sorting;

import android.test.AndroidTestCase;

import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;

import java.util.Comparator;
import java.util.Locale;

public class PriorityListEntryComparatorTest extends AndroidTestCase {

    private Locale defaultLocale;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.GERMAN);
    }

    @Override
    public void tearDown() throws Exception {
        Locale.setDefault(defaultLocale);
    }

    public void testCompare() throws Exception {
        ShoppingList list = new ShoppingList("_TEST_list");
        Product productWithA = new Product("Aubergine", null);
        Product productWithAUml = new Product("Äpfel", null);
        Product productWithB = new Product("Banane", null);
        Product productWithUUml = new Product("Überzug", null);
        ListEntry listEntryA = new ListEntry(list, productWithA, 1.0f, false, 3);
        ListEntry listEntryAUml = new ListEntry(list, productWithAUml, 1.0f, false, 0);
        ListEntry listEntryB = new ListEntry(list, productWithB, 1.0f, false, -1);
        ListEntry listEntryUUml = new ListEntry(list, productWithUUml, 1.0f, false, 0);

        Comparator<ListEntry> comp = new PriorityListEntryComparator();
        assertEquals(0, comp.compare(listEntryA, listEntryA));
        assertTrue(comp.compare(listEntryAUml, listEntryA) > 0);
        assertTrue(comp.compare(listEntryUUml, listEntryAUml) > 0);
        assertTrue(comp.compare(listEntryB, listEntryUUml) > 0);

        assertTrue(comp.compare(listEntryUUml, listEntryB) < 0);
        assertTrue(comp.compare(listEntryAUml, listEntryUUml) < 0);
        assertTrue(comp.compare(listEntryA, listEntryAUml) < 0);

    }

}