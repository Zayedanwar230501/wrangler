/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;

/**
 * The TimeDuration class wraps a time duration value (e.g., 5ms, 2s) and converts it to milliseconds.
 *
 * <p>This class provides methods to extract the canonical millisecond value of the token,
 * retrieve the type of the token, and generate a JSON representation.</p>
 */
@PublicEvolving
public class TimeDuration implements Token {

    private final double value;
    private final String unit;

    /**
     * Constructs a TimeDuration token from a string like "10ms" or "2.5s".
     *
     * @param input the duration string with units.
     */
    public TimeDuration(String input) {
        input = input.trim().toLowerCase();
        this.unit = input.replaceAll("[0-9.]", "").trim();
        this.value = Double.parseDouble(input.replaceAll("[^0-9.]", ""));
    }

    /**
     * Returns the time duration in milliseconds.
     *
     * @return long value in milliseconds.
     */
    public long getMilliseconds() {
        switch (unit) {
            case "ms": return (long) value;
            case "s": return (long) (value * 1000);
            case "m": return (long) (value * 60 * 1000);
            case "h": return (long) (value * 60 * 60 * 1000);
            case "d": return (long) (value * 24 * 60 * 60 * 1000);
            default: throw new IllegalArgumentException("Unknown time unit: " + unit);
        }
    }

    @Override
    public Long value() {
        return getMilliseconds();
    }

    @Override
    public TokenType type() {
        return TokenType.TIME_DURATION;
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", TokenType.TIME_DURATION.name());
        object.addProperty("value", getMilliseconds());
        return object;
    }
}
