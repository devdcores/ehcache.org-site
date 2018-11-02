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

import java.util.UUID;

public class StateRepositoryMessageFactory {

  private final String cacheId;
  private final String mapId;
  private final UUID clientId;

  public StateRepositoryMessageFactory(String cacheId, String mapId, UUID clientId) {
    this.cacheId = cacheId;
    this.mapId = mapId;
    this.clientId = clientId;
  }

  public StateRepositoryOpMessage getMessage(Object key) {
    return new StateRepositoryOpMessage.GetMessage(cacheId, mapId, key, clientId);
  }

  public StateRepositoryOpMessage putIfAbsentMessage(Object key, Object value) {
    return new StateRepositoryOpMessage.PutIfAbsentMessage(cacheId, mapId, key, value, clientId);
  }

  public StateRepositoryOpMessage entrySetMessage() {
    return new StateRepositoryOpMessage.EntrySetMessage(cacheId, mapId, clientId);
  }

}
