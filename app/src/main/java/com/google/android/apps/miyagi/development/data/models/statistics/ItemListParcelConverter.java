package com.google.android.apps.miyagi.development.data.models.statistics;

import org.parceler.ParcelConverter;
import org.parceler.Parcels;

/**
 * Created by lukaszweglinski on 10.01.2017.
 */

public class ItemListParcelConverter implements ParcelConverter<PagesWrapper> {
    @Override
    public void toParcel(PagesWrapper input, android.os.Parcel parcel) {
        if (input == null) {
            parcel.writeInt(-1);
        } else {
            parcel.writeInt(input.size());
            for (Page item : input) {
                parcel.writeParcelable(Parcels.wrap(item), 0);
            }
        }
    }

    @Override
    public PagesWrapper fromParcel(android.os.Parcel parcel) {
        int size = parcel.readInt();
        if (size < 0) {
            return null;
        }
        PagesWrapper items = new PagesWrapper();
        for (int i = 0; i < size; ++i) {
            items.add(Parcels.unwrap(parcel.readParcelable(Page.class.getClassLoader())));
        }
        return items;
    }
}