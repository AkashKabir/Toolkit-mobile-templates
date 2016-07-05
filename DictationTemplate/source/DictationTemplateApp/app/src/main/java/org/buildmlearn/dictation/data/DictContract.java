package org.buildmlearn.dictation.data;

import android.provider.BaseColumns;

/**
 * Created by Anupam (opticod) on 4/7/16.
 */

public class DictContract {

    public static final class Dict implements BaseColumns {

        public static final String TABLE_NAME = "dictation";

        public static final String TITLE = "title";
        public static final String PASSAGE = "passage";

    }
}
