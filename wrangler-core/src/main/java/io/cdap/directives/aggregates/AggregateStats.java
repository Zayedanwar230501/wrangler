/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cdap.directives.aggregates;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.*;

import java.util.Collections;
import java.util.List;

/**
 * A directive that aggregates total byte size and total time duration
 * from a given dataset and outputs a single row with those aggregates.
 *
 * Example usage:
 * aggregate-stats :sizeCol :timeCol totalSizeMB totalTimeSec
 */

public class AggregateStats implements Directive {

    private String sizeCol;
    private String timeCol;
    private String outputSizeCol;
    private String outputTimeCol;

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = new UsageDefinition.Builder("aggregate-stats");
        builder.define("size_col", TokenType.COLUMN_NAME);
        builder.define("time_col", TokenType.COLUMN_NAME);
        builder.define("out_size_col", TokenType.COLUMN_NAME);
        builder.define("out_time_col", TokenType.COLUMN_NAME);
        return builder.build();
    }

    @Override
    public void initialize(Arguments args) throws DirectiveParseException {
        try {
            sizeCol = ((ColumnName) args.value("size_col")).value();
            timeCol = ((ColumnName) args.value("time_col")).value();
            outputSizeCol = ((ColumnName) args.value("out_size_col")).value();
            outputTimeCol = ((ColumnName) args.value("out_time_col")).value();
        } catch (Exception e) {
            throw new DirectiveParseException("Failed to parse arguments for aggregate-stats directive.", e);
        }
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context)
            throws DirectiveExecutionException, ErrorRowException {

        long totalBytes = 0L;
        long totalMillis = 0L;

        for (Row row : rows) {
            Object sizeObj = row.getValue(sizeCol);
            Object timeObj = row.getValue(timeCol);

            if (sizeObj == null || timeObj == null) {
                continue; // skip rows with missing values
            }

            try {
                ByteSize byteSize = new ByteSize(sizeObj.toString());
                TimeDuration timeDuration = new TimeDuration(timeObj.toString());

                totalBytes += byteSize.getBytes();
                totalMillis += timeDuration.getMilliseconds();

            } catch (Exception e) {
                throw new DirectiveExecutionException("Error parsing ByteSize or TimeDuration: " + e.getMessage(), e);
            }
        }

        double totalMB = totalBytes / (1024.0 * 1024.0);
        double totalSeconds = totalMillis / 1000.0;

        Row output = new Row();
        output.add(outputSizeCol, totalMB);
        output.add(outputTimeCol, totalSeconds);

        return Collections.singletonList(output);
    }

    @Override
    public void destroy() {

    }
}
