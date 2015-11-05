/*
 * Copyright 2015 Alex Zhang aka. ztc1997
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package ztc1997.gxmu.teachingassessment.util;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

public class Utils {
    public static CharSequence trimCharSequence(CharSequence origin) {
        int start = 0, end = origin.length() - 1;
        while (origin.charAt(start) == '\n')
            start++;
        while (origin.charAt(end) == '\n')
            end--;
        return origin.subSequence(start, end + 1);
    }

    public static CharSequence getAndFormatCharSequence(Context context, int textRes, Object... format) {
        String text = Html.toHtml((Spanned) context.getText(textRes));
        String s = String.format(text, format);
        return trimCharSequence(Html.fromHtml(s));
    }
}
