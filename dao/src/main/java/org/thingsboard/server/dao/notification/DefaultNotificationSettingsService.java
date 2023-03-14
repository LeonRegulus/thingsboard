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
package org.thingsboard.server.dao.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.settings.NotificationSettings;
import org.thingsboard.server.common.data.notification.targets.NotificationTarget;
import org.thingsboard.server.common.data.notification.targets.platform.AllUsersFilter;
import org.thingsboard.server.common.data.notification.targets.platform.OriginatorEntityOwnerUsersFilter;
import org.thingsboard.server.common.data.notification.targets.platform.PlatformUsersNotificationTargetConfig;
import org.thingsboard.server.common.data.notification.template.NotificationTemplate;
import org.thingsboard.server.dao.settings.AdminSettingsService;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultNotificationSettingsService implements NotificationSettingsService {

    private final AdminSettingsService adminSettingsService;
    private final NotificationTargetService notificationTargetService;
    private final NotificationTemplateService notificationTemplateService;
    private final NotificationRuleService notificationRuleService;

    private static final String SETTINGS_KEY = "notifications";

    @Override
    public void saveNotificationSettings(TenantId tenantId, NotificationSettings settings) {
        AdminSettings adminSettings = Optional.ofNullable(adminSettingsService.findAdminSettingsByTenantIdAndKey(tenantId, SETTINGS_KEY))
                .orElseGet(() -> {
                    AdminSettings newAdminSettings = new AdminSettings();
                    newAdminSettings.setTenantId(tenantId);
                    newAdminSettings.setKey(SETTINGS_KEY);
                    return newAdminSettings;
                });
        adminSettings.setJsonValue(JacksonUtil.valueToTree(settings));
        adminSettingsService.saveAdminSettings(tenantId, adminSettings);
    }

    @Override
    public NotificationSettings findNotificationSettings(TenantId tenantId) {
        return Optional.ofNullable(adminSettingsService.findAdminSettingsByTenantIdAndKey(tenantId, SETTINGS_KEY))
                .map(adminSettings -> JacksonUtil.treeToValue(adminSettings.getJsonValue(), NotificationSettings.class))
                .orElseGet(() -> {
                    NotificationSettings settings = new NotificationSettings();
                    settings.setDeliveryMethodsConfigs(Collections.emptyMap());
                    return settings;
                });
    }

    @Override
    public void createDefaultNotificationConfigs(TenantId tenantId) {
        if (tenantId.equals(TenantId.SYS_TENANT_ID)) {
            // TODO
            return;
        }

        NotificationTarget allUsersTarget = new NotificationTarget();
        allUsersTarget.setTenantId(tenantId);
        allUsersTarget.setName("All users");
        PlatformUsersNotificationTargetConfig targetConfig = new PlatformUsersNotificationTargetConfig();
        targetConfig.setUsersFilter(new AllUsersFilter());
        targetConfig.setDescription("All users in scope of tenant");
        allUsersTarget.setConfiguration(targetConfig);
        allUsersTarget = notificationTargetService.saveNotificationTarget(tenantId, allUsersTarget);

        NotificationTarget originatorEntityOwnerUsers = new NotificationTarget();
        originatorEntityOwnerUsers.setTenantId(tenantId);
        originatorEntityOwnerUsers.setName("Users of rule trigger entity's owner");
        targetConfig.setUsersFilter(new OriginatorEntityOwnerUsersFilter());
        targetConfig.setDescription("For usage with notification rules. For example, if alarm trigger type is chosen, " +
                "notifications will be sent to alarm owner's users, e.g. it's customer's users");
        originatorEntityOwnerUsers.setConfiguration(targetConfig);
        originatorEntityOwnerUsers = notificationTargetService.saveNotificationTarget(tenantId, originatorEntityOwnerUsers);

        NotificationTemplate alarmNotificationTemplate = new NotificationTemplate();

        /*
        * TODO:
        *  rule chain start failure
        *  alarm
        * */

    }

}
