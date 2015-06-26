package org.noorganization.instalist.view.sorting;

import android.test.AndroidTestCase;

import junit.framework.TestCase;

import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;

import java.util.Comparator;
import java.util.Locale;

public class AlphabeticalListEntryComparatorTest extends AndroidTestCase {

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
        ListEntry listEntryA = new ListEntry(list, productWithA, 1.0f);
        ListEntry listEntryAUml = new ListEntry(list, productWithAUml, 1.0f);
        ListEntry listEntryB = new ListEntry(list, productWithB, 1.0f);
        ListEntry listEntryUUml = new ListEntry(list, productWithUUml, 1.0f);

        Comparator<ListEntry> comp = new AlphabeticalListEntryComparator();
        assertEquals(0, comp.compare(listEntryA, listEntryA));
        assertTrue(comp.compare(listEntryAUml, listEntryA) < 0);
        assertTrue(comp.compare(listEntryB, listEntryA) > 0);
        assertTrue(comp.compare(listEntryB, listEntryAUml) > 0);
        assertTrue(comp.compare(listEntryUUml, listEntryAUml) > 0);
        assertTrue(comp.compare(listEntryUUml, listEntryB) > 0);
    }
}