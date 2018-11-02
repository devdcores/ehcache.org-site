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

package org.ehcache.clustered.common.internal.messages;

import java.io.Serializable;

public abstract class StateRepositoryOpMessage extends EhcacheEntityMessage implements Serializable {

  public enum StateRepositoryOp {
    GET,
    PUT_IF_ABSENT,
    ENTRY_SET,
  }

  private final String cacheId;
  private final String mapId;

  private StateRepositoryOpMessage(String cacheId, String mapId) {
    this.cacheId = cacheId;
    this.mapId = mapId;
  }

  public String getCacheId() {
    return cacheId;
  }

  public String getMapId() {
    return mapId;
  }

  @Override
  public Type getType() {
    return Type.STATE_REPO_OP;
  }

  public abstract StateRepositoryOp operation();

  @Override
  public byte getOpCode() {
    return getType().getCode();
  }

  @Override
  public String toString() {
    return getType() + "#" + operation();
  }

  private static abstract class KeyBasedMessage extends StateRepositoryOpMessage {

    private final Object key;

    private KeyBasedMessage(final String cacheId, final String mapId, final Object key) {
      super(cacheId, mapId);
      this.key = key;
    }

    public Object getKey() {
      return key;
    }

  }

  public static class GetMessage extends KeyBasedMessage {

    public GetMessage(final String cacheId, final String mapId, final Object key) {
      super(cacheId, mapId, key);
    }

    @Override
    public StateRepositoryOp operation() {
      return StateRepositoryOp.GET;
    }
  }

  public static class PutIfAbsentMessage extends KeyBasedMessage {

    private final Object value;

    public PutIfAbsentMessage(final String cacheId, final String mapId, final Object key, final Object value) {
      super(cacheId, mapId, key);
      this.value = value;
    }

    public Object getValue() {
      return value;
    }

    @Override
    public StateRepositoryOp operation() {
      return StateRepositoryOp.PUT_IF_ABSENT;
    }
  }

  public static class EntrySetMessage extends StateRepositoryOpMessage {

    public EntrySetMessage(final String cacheId, final String mapId) {
      super(cacheId, mapId);
    }

    @Override
    public StateRepositoryOp operation() {
      return StateRepositoryOp.ENTRY_SET;
    }
  }

}
