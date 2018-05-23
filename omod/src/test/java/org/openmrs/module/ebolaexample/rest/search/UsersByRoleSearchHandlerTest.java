package org.openmrs.module.ebolaexample.rest.search;

import static junit.framework.TestCase.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.ebolaexample.rest.WardResource;
import org.openmrs.module.ebolaexample.rest.WebMethods;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Integration tests for the DrugConceptSearchHandler
 */
public class UsersByRoleSearchHandlerTest extends BaseModuleWebContextSensitiveTest {
    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    @Autowired
    private WebMethods webMethods;

    private WardResource resource;
    private MockHttpServletResponse response;
    private String requestURI;

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
        response = new MockHttpServletResponse();
        requestURI = "concept";
    }

    @Test
    public void shouldReturnUsersByRole() throws Exception {
        User user = Context.getUserService().getUserByUuid("1010d442-e134-11de-babe-001e378eb67e");
        Role role = Context.getUserService().getRole("System Developer");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/v1/user");
        request.addParameter("role", role.getRole());
        request.addHeader("content-type", "application/json");

        response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        List<LinkedHashMap> results = (List<LinkedHashMap>) responseObject.get("results");

        boolean userInResults = false;
        for (LinkedHashMap result : results) {
            userInResults = userInResults || result.get("display").equals(user.getUsername());
            String uuid = (String) result.get("uuid");
            assertTrue(Context.getUserService().getUserByUuid(uuid).getRoles().contains(role));
        }
        assertTrue(userInResults);
    }

    @Test
    public void shouldReturnNoUsersForUnusedRole() throws Exception {
        Role role = new Role("Nonsense role", "Nonsense role");
        Context.getUserService().saveRole(role);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/v1/user");
        request.addParameter("role", role.getRole());
        request.addHeader("content-type", "application/json");

        response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        List<LinkedHashMap> results = (List<LinkedHashMap>) responseObject.get("results");

        assertTrue(results.isEmpty());
    }
}