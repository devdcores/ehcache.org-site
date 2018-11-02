/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ehcache.impl.serialization;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * LongSerializerTest
 */
public class LongSerializerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(LongSerializerTest.class);

  @Test
  public void testCanSerializeAndDeserialize() throws ClassNotFoundException {
    LongSerializer serializer = new LongSerializer();
    long now = System.currentTimeMillis();
    LOGGER.info("LongSerializer test with seed {}", now);
    Random random = new Random(now);

    for (int i = 0; i < 100; i++) {
      long l = random.nextLong();
      Long read = serializer.read(serializer.serialize(l));
      assertThat(read, is(l));
    }
  }

  @Test(expected = NullPointerException.class)
  public void testReadThrowsOnNullInput() throws ClassNotFoundException {
    new LongSerializer().read(null);
  }

  @Test(expected = NullPointerException.class)
  public void testSerializeThrowsOnNullInput() {
    new LongSerializer().serialize(null);
  }
}