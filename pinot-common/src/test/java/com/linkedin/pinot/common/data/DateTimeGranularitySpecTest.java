/**
 * Copyright (C) 2014-2016 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.pinot.common.data;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.linkedin.pinot.common.data.DateTimeGranularitySpec;

public class DateTimeGranularitySpecTest {

  // Test construct granularity from components
  @Test(dataProvider = "testConstructGranularityDataProvider")
  public void testConstructGranularity(int size, TimeUnit unit, DateTimeGranularitySpec granularityExpected) {
    DateTimeGranularitySpec granularityActual = null;
    try {
      granularityActual = DateTimeGranularitySpec.constructGranularity(size, unit);
    } catch (Exception e) {
      // invalid arguments
    }
    Assert.assertEquals(granularityActual, granularityExpected);
  }

  @DataProvider(name = "testConstructGranularityDataProvider")
  public Object[][] provideTestConstructGranularityData() {

    List<Object[]> entries = new ArrayList<>();

    entries.add(new Object[] {
        1, TimeUnit.HOURS, new DateTimeGranularitySpec("1:HOURS")
    });
    entries.add(new Object[] {
        5, TimeUnit.MINUTES, new DateTimeGranularitySpec("5:MINUTES")
    });
    entries.add(new Object[] {
        0, TimeUnit.HOURS, null
    });
    entries.add(new Object[] {
        -1, TimeUnit.HOURS, null
    });
    entries.add(new Object[] {
        1, null, null
    });

    return entries.toArray(new Object[entries.size()][]);
  }

  // Test granularity to millis
  @Test(dataProvider = "testGranularityToMillisDataProvider")
  public void testGranularityToMillis(DateTimeGranularitySpec granularity, Long millisExpected) {
    Long millisActual = null;
    try {
      millisActual = granularity.granularityToMillis();
    } catch (Exception e) {
      // invalid arguments
    }
    Assert.assertEquals(millisActual, millisExpected);
  }

  @DataProvider(name = "testGranularityToMillisDataProvider")
  public Object[][] provideTestGranularityToMillisData() {

    List<Object[]> entries = new ArrayList<>();

    entries.add(new Object[] {
        new DateTimeGranularitySpec("1:HOURS"), 3600000L
    });
    entries.add(new Object[] {
        new DateTimeGranularitySpec("1:MILLISECONDS"), 1L
    });
    entries.add(new Object[] {
        new DateTimeGranularitySpec("15:MINUTES"), 900000L
    });
    entries.add(new Object[] {
        new DateTimeGranularitySpec("0:HOURS"), 0L
    });
    entries.add(new Object[] {
        null, null
    });
    entries.add(new Object[] {
        new DateTimeGranularitySpec("1:DUMMY"), null
    });

    return entries.toArray(new Object[entries.size()][]);
  }

}
