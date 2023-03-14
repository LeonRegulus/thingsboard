/**
 * Copyright © 2016-2023 The Thingsboard Authors
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
package org.thingsboard.server.service.notification.rule.trigger;

import org.thingsboard.server.common.data.notification.info.NotificationInfo;
import org.thingsboard.server.common.data.notification.rule.trigger.NotificationRuleTriggerConfig;
import org.thingsboard.server.common.data.notification.rule.trigger.NotificationRuleTriggerType;

public interface NotificationRuleTriggerProcessor<T, C extends NotificationRuleTriggerConfig> {

    boolean matchesFilter(T triggerObject, C triggerConfig);

    default boolean matchesClearRule(T triggerObject, C triggerConfig) {
        return false;
    }

    NotificationInfo constructNotificationInfo(T triggerObject, C triggerConfig);

    NotificationRuleTriggerType getTriggerType();

}
