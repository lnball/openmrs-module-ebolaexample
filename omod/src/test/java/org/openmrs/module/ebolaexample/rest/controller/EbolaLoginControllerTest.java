package org.openmrs.module.ebolaexample.rest.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.ebolaexample.rest.WebMethods;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.LinkedHashMap;

import static junit.framework.Assert.assertEquals;

public class EbolaLoginControllerTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    @Autowired
    private WebMethods webMethods;

    private MockHttpServletResponse response;
    private String requestURI;

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
        response = new MockHttpServletResponse();
        requestURI = "ebola/login";
    }

    @Test
    public void shouldReturnUUIDForAuthenticatedUserAndProvider() throws Exception {
        User user = Context.getAuthenticatedUser();
        String teamUsername = user.getUsername();
        Provider provider = Context.getProviderService().getProvidersByPerson(user.getPerson()).iterator().next();
        String providerId = provider.getIdentifier();

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI);
        request.setContentType(MediaType.APPLICATION_JSON.toString());
        request.addHeader("content-type", "application/json");
        String content = "{\"username\": \"" + teamUsername + "\", \"provider\": \"" + providerId + "\"}";
        request.setContent(content.getBytes());
        response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);

        LinkedHashMap parsedUser = (LinkedHashMap<String, String>) responseObject.get("user");
        assertEquals(parsedUser.get("uuid"), user.getUuid());
        LinkedHashMap parsedProvider = (LinkedHashMap) responseObject.get("provider");
        assertEquals(parsedProvider.get("uuid"), provider.getUuid());
    }

    @Test
    public void shouldLoginValidUser() throws Exception {
        User user = Context.getAuthenticatedUser();
        String teamUsername = user.getUsername();
        Provider provider = Context.getProviderService().getProvidersByPerson(user.getPerson()).iterator().next();
        String providerId = provider.getIdentifier();

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI);
        request.addHeader("content-type", "application/json");
        String content = "{\"username\": \"" + teamUsername + "\", \"provider\": \"" + providerId + "\"}";
        request.setContent(content.getBytes());
        webMethods.handle(request);

        assertEquals(Context.getAuthenticatedUser().getUuid(), user.getUuid());
    }

    @Test
    public void shouldReturnCurrentUserForGet() throws Exception {
        User user = Context.getAuthenticatedUser();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI);
        request.addHeader("content-type", "application/json");
        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);

        assertEquals(((LinkedHashMap) responseObject.get("user")).get("uuid"), user.getUuid());
        assertEquals(((LinkedHashMap) responseObject.get("person")).get("uuid"), user.getPerson().getUuid());
    }

    @Test
    public void shouldReturnUserProviderAndPerson() throws Exception {
        User user = Context.getAuthenticatedUser();
        String teamUsername = user.getUsername();
        Provider provider = Context.getProviderService().getProvidersByPerson(user.getPerson()).iterator().next();
        String providerId = provider.getIdentifier();

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI);
        request.addHeader("content-type", "application/json");
        String content = "{\"username\": \"" + teamUsername + "\", \"provider\": \"" + providerId + "\"}";
        request.setContent(content.getBytes());
        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);

        assertEquals(((LinkedHashMap) responseObject.get("person")).get("uuid"), provider.getPerson().getUuid());
        assertEquals(((LinkedHashMap) responseObject.get("user")).get("uuid"), user.getUuid());
        assertEquals(((LinkedHashMap) responseObject.get("person")).get("uuid"), provider.getPerson().getUuid());
    }

    @Test
    public void shouldReturnUnknownProviderForAuthenticatedUserWithInvalidProviderId() throws Exception {
        setupUnknownProvider();
        User user = Context.getAuthenticatedUser();
        String teamUsername = user.getUsername();

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI);
        request.addHeader("content-type", "application/json");
        String content = "{\"username\": \"" + teamUsername + "\", \"provider\": \"Nonsense provider id\"}";
        request.setContent(content.getBytes());
        response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);

        LinkedHashMap parsedProvider = (LinkedHashMap) responseObject.get("provider");
        Provider unknownProvider = Context.getProviderService().getUnknownProvider();
        assertEquals(parsedProvider.get("uuid"), unknownProvider.getUuid());

        LinkedHashMap parsedPerson = (LinkedHashMap) responseObject.get("person");
        assertEquals(user.getPerson().getUuid(), parsedPerson.get("uuid"));
    }

    @Test(expected=ContextAuthenticationException.class)
    public void shouldThrowExceptionForInvalidUsername() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI);
        request.addHeader("content-type", "application/json");
        String content = "{\"username\": \"Nonsense username\", \"provider\": \"Nonsense provider id\"}";
        request.setContent(content.getBytes());
        response = webMethods.handle(request);
    }

    public void setupUnknownProvider() {
        Provider provider = new Provider();
        provider.setName("Unknown Provider");
        provider.setIdentifier("Test Unknown Provider");
        provider = Context.getProviderService().saveProvider(provider);
        GlobalProperty gp = new GlobalProperty(OpenmrsConstants.GP_UNKNOWN_PROVIDER_UUID, provider.getUuid(), null);
        Context.getAdministrationService().saveGlobalProperty(gp);
        // Delete this EMR reference once EMR fixes https://issues.openmrs.org/browse/EA-56.
        // We should only need the OpenMRSConstants GP above, but the EMRApi created their own property.
        gp = new GlobalProperty(EmrApiConstants.GP_UNKNOWN_PROVIDER, provider.getUuid(), null);
        Context.getAdministrationService().saveGlobalProperty(gp);
    }
}

