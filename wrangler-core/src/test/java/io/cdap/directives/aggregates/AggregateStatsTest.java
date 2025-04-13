/*
 * Copyright © <year> Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package io.cdap.directives.aggregates;

import io.cdap.directives.aggregates.AggregateStats;
import io.cdap.wrangler.api.Pair;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.TestingRig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsTest {

    private AggregateStats directive;

    @Before
    public void setup() throws Exception {
        directive = new AggregateStats();
    }

    @Test
    public void testAggregation() throws Exception {
        // Sample rows for testing (make sure to use columns like "size" and "duration" similar to your data)
        List<Row> rows = Arrays.asList(
                new Row("size", "1MB").add("duration", "500ms"),
                new Row("size", "2MB").add("duration", "1500ms"),
                new Row("size", "512KB").add("duration", "1s")
        );

        // Directives to be applied (adjust this based on the correct syntax)
        String[] directives = new String[]{
                "aggregate-stats :size :duration :totalSizeMB :totalTimeSec"
        };

        // Expected output after applying the directive
        double[] expectedSizes = new double[]{3.5};  // Total size: 1MB + 2MB + 0.5MB
        double[] expectedDurations = new double[]{3.0};  // Total time: 500ms + 1500ms + 1000ms

        // Execute the directive with the rows
        Pair<List<Row>, List<Row>> result = TestingRig.executeWithErrors(directives, rows);
        List<Row> results = result.getFirst();
        List<Row> errors = result.getSecond();

        // Assert the size of the results and errors
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(0, errors.size());  // Ensure no errors during processing

        // Assert the results are correct
        Row output = results.get(0);

        // Check the total size (in MB)
        Assert.assertEquals(expectedSizes[0], (double) output.getValue("totalSizeMB"), 0.001);

        // Check the total duration (in seconds)
        Assert.assertEquals(expectedDurations[0], (double) output.getValue("totalTimeSec"), 0.001);
    }
}
