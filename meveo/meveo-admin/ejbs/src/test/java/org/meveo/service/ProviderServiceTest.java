/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.service;

import java.util.List;

import org.jboss.seam.Component;
import org.manaty.BaseIntegrationTest;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.local.UserServiceLocal;
import org.meveo.service.crm.local.ProviderServiceLocal;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProviderServiceTest extends BaseIntegrationTest {

    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(new DataSetOperation("dataSets/BaseData.xml"));
    }

    @Test(groups = { "service" })
    public void findUsersProvidersTest() throws Exception {
        new ComponentTest() {
            @Override
            protected void testComponents() throws Exception {
                ProviderServiceLocal providerService = (ProviderServiceLocal) Component.getInstance("providerService");
                UserServiceLocal userServiceLocal = (UserServiceLocal) Component.getInstance("userService");
                User user = userServiceLocal.findById((long) 1);
                List<Provider> providers = providerService.findUsersProviders(user.getUserName());
                Assert.assertEquals(providers.size(), 1);

            }
        }.run();
        new ComponentTest() {
            @Override
            protected void testComponents() throws Exception {
                ProviderServiceLocal providerService = (ProviderServiceLocal) Component.getInstance("providerService");
                UserServiceLocal userServiceLocal = (UserServiceLocal) Component.getInstance("userService");
                User user = userServiceLocal.findById((long) 2);
                List<Provider> providers = providerService.findUsersProviders(user.getUserName());
                Assert.assertEquals(providers.size(), 2);

            }
        }.run();

    }

}
