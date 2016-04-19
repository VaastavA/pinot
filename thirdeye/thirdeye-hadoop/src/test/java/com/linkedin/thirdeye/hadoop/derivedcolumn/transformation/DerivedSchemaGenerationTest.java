/**
 * Copyright (C) 2014-2015 LinkedIn Corp. (pinot-core@linkedin.com)
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
package com.linkedin.thirdeye.hadoop.derivedcolumn.transformation;

import java.io.IOException;
import java.util.Properties;

import org.apache.avro.Schema;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.linkedin.thirdeye.hadoop.ThirdEyeConfig;
import com.linkedin.thirdeye.hadoop.ThirdEyeConfigConstants;

public class DerivedSchemaGenerationTest {
  private static final String SCHEMA_FILE = "schema.avsc";

  DerivedColumnTransformationPhaseJob job = new DerivedColumnTransformationPhaseJob("derived_column_transformation", null);
  Schema inputSchema;
  ThirdEyeConfig thirdeyeConfig;
  Properties props;

  @BeforeTest
  public void setup() throws IOException {
    inputSchema = new Schema.Parser().parse(ClassLoader.getSystemResourceAsStream(SCHEMA_FILE));

    props = new Properties();
    props.setProperty(ThirdEyeConfigConstants.THIRDEYE_TABLE_NAME.toString(), "collection");
    props.setProperty(ThirdEyeConfigConstants.THIRDEYE_DIMENSION_NAMES.toString(), "d1,d2,d3");
    props.setProperty(ThirdEyeConfigConstants.THIRDEYE_METRIC_NAMES.toString(), "m1,m2");
    props.setProperty(ThirdEyeConfigConstants.THIRDEYE_METRIC_TYPES.toString(), "INT,INT");
    props.setProperty(ThirdEyeConfigConstants.THIRDEYE_TIMECOLUMN_NAME.toString(), "hoursSinceEpoch");

  }

  @Test
  public void testDerivedColumnsSchemaGeneration() throws Exception{
    ThirdEyeConfig thirdeyeConfig = ThirdEyeConfig.fromProperties(props);
    Schema outputSchema = job.newSchema(thirdeyeConfig, inputSchema);
    Assert.assertEquals(inputSchema.getFields().size(), outputSchema.getFields().size(),
        "Input schema should be same as output schema if no topk/whitelist in config");

    props.setProperty(ThirdEyeConfigConstants.THIRDEYE_TOPK_DIMENSION_NAMES.toString(), "d2,");
    props.setProperty(ThirdEyeConfigConstants.THIRDEYE_TOPK_DIMENSION_METRICNAMES.toString(), "m1");
    props.setProperty(ThirdEyeConfigConstants.THIRDEYE_TOPK_DIMENSION_KVALUES.toString(), "1");
    props.setProperty(ThirdEyeConfigConstants.THIRDEYE_WHITELIST_DIMENSION_NAMES.toString(), "d2,d3");
    props.setProperty(ThirdEyeConfigConstants.THIRDEYE_WHITELIST_DIMENSION.toString() + ".d2" , "a,b,c");
    props.setProperty(ThirdEyeConfigConstants.THIRDEYE_WHITELIST_DIMENSION.toString() + ".d3", "x,y");

    thirdeyeConfig = ThirdEyeConfig.fromProperties(props);
    outputSchema = job.newSchema(thirdeyeConfig, inputSchema);
    Assert.assertEquals(inputSchema.getFields().size() + 2, outputSchema.getFields().size(),
        "Input schema should not be same as output schema if topk/whitelist in config");

    Assert.assertEquals(outputSchema.getField("d2_raw") != null, true,
        "Output schema should have _raw entries for columsn in topk/whitelist");
    Assert.assertEquals(outputSchema.getField("d3_raw") != null, true,
        "Output schema should have _raw entries for columsn in whitelist");
  }

}
