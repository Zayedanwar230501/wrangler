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
 * The ByteSize class wraps a size value (e.g., 10KB, 1.5MB) and converts it to bytes.
 *
 * <p>This class provides methods to extract the canonical byte value of the token,
 * retrieve the type of the token, and generate a JSON representation.</p>
 */
@PublicEvolving
public class ByteSize implements Token {

    private final double value;
    private final String unit;

    /**
     * Constructs a ByteSize token from a string like "10KB" or "1.5MB".
     *
     * @param input the size string with units.
     */
    public ByteSize(String input) {
        input = input.trim().toUpperCase();
        this.unit = input.replaceAll("[0-9.]", "").trim();
        this.value = Double.parseDouble(input.replaceAll("[^0-9.]", ""));
    }

    /**
     * Returns the byte value of this size.
     *
     * @return long value in bytes.
     */
    public long getBytes() {
        switch (unit) {
            case "B": return (long) value;
            case "KB": return (long) (value * 1024);
            case "MB": return (long) (value * 1024 * 1024);
            case "GB": return (long) (value * 1024 * 1024 * 1024);
            case "TB": return (long) (value * 1024L * 1024 * 1024 * 1024);
            default: throw new IllegalArgumentException("Unknown byte unit: " + unit);
        }
    }

    @Override
    public Long value() {
        return getBytes();
    }

    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", TokenType.BYTE_SIZE.name());
        object.addProperty("value", getBytes());
        return object;
    }
}
