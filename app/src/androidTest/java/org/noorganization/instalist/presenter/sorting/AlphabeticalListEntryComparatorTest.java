package org.noorganization.instalist.presenter.sorting;

import android.test.AndroidTestCase;

import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.presenter.modelwrappers.ListEntryItemWrapper;
import org.noorganization.instalist.presenter.sorting.shoppingList.AlphabeticalListEntryComparator;

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

        ListEntryItemWrapper listEntryItemWrapperA = new ListEntryItemWrapper(listEntryA);
        ListEntryItemWrapper listEntryItemWrapperAUml = new ListEntryItemWrapper(listEntryAUml);
        ListEntryItemWrapper listEntryItemWrapperB = new ListEntryItemWrapper(listEntryB);
        ListEntryItemWrapper listEntryItemWrapperUUml = new ListEntryItemWrapper(listEntryUUml);

        Comparator<ListEntryItemWrapper> comp = new AlphabeticalListEntryComparator();
        assertEquals(0, comp.compare(listEntryItemWrapperA, listEntryItemWrapperA));
        assertTrue(comp.compare(listEntryItemWrapperAUml, listEntryItemWrapperA) < 0);
        assertTrue(comp.compare(listEntryItemWrapperB, listEntryItemWrapperA) > 0);
        assertTrue(comp.compare(listEntryItemWrapperB, listEntryItemWrapperAUml) > 0);
        assertTrue(comp.compare(listEntryItemWrapperUUml, listEntryItemWrapperAUml) > 0);
        assertTrue(comp.compare(listEntryItemWrapperUUml, listEntryItemWrapperB) > 0);
    }
}