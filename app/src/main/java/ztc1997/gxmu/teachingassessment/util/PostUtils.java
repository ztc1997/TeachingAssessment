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

import java.util.Map;

import ztc1997.gxmu.teachingassessment.Constants;

public class PostUtils {
    public static String paramsmap2ParamsString(Map<String, String> params) {
        if (params == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            sb.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }
        if (sb.charAt(sb.length() - 1) == '&')
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String mergeUrlActionParams(String action, Map<String, String> params) {
        return mergeUrlActionParams(Constants.URL, action, params);
    }

    public static String mergeUrlActionParams(String url, String action, Map<String, String> params) {
        return url + "/" + action + "." + Constants.SUFFIX + "?" + paramsmap2ParamsString(params);
    }
}
