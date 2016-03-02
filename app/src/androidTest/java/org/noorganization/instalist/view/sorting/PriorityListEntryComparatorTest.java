/*
 * Copyright 2016 Tino Siegmund, Michael Wodniok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.noorganization.instalist.view.sorting;

import android.test.AndroidTestCase;

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
        /*ShoppingList list = new ShoppingList("_TEST_list");
        Product productWithA = new Product("Aubergine", null);
        Product productWithAUml = new Product("Äpfel", null);
        Product productWithB = new Product("Banane", null);
        Product productWithUUml = new Product("Überzug", null);
        ListEntry listEntryA = new ListEntry(list, productWithA, 1.0f, false, 3);
        ListEntry listEntryAUml = new ListEntry(list, productWithAUml, 1.0f, false, 0);
        ListEntry listEntryB = new ListEntry(list, productWithB, 1.0f, false, -1);
        ListEntry listEntryUUml = new ListEntry(list, productWithUUml, 1.0f, false, 0);

        ListEntryItemWrapper listEntryItemWrapperA = new ListEntryItemWrapper(listEntryA);
        ListEntryItemWrapper listEntryItemWrapperAUml = new ListEntryItemWrapper(listEntryAUml);
        ListEntryItemWrapper listEntryItemWrapperB = new ListEntryItemWrapper(listEntryB);
        ListEntryItemWrapper listEntryItemWrapperUUml = new ListEntryItemWrapper(listEntryUUml);

        Comparator<ListEntryItemWrapper> comp = new PriorityListEntryComparator();
        assertEquals(0, comp.compare(listEntryItemWrapperA, listEntryItemWrapperA));
        assertTrue(comp.compare(listEntryItemWrapperAUml, listEntryItemWrapperA) > 0);
        assertTrue(comp.compare(listEntryItemWrapperUUml, listEntryItemWrapperAUml) > 0);
        assertTrue(comp.compare(listEntryItemWrapperB, listEntryItemWrapperUUml) > 0);

        assertTrue(comp.compare(listEntryItemWrapperUUml, listEntryItemWrapperB) < 0);
        assertTrue(comp.compare(listEntryItemWrapperAUml, listEntryItemWrapperUUml) < 0);
        assertTrue(comp.compare(listEntryItemWrapperA, listEntryItemWrapperAUml) < 0);
*/
    }

}