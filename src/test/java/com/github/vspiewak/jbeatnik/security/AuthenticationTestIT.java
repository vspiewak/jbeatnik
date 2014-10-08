package com.github.vspiewak.jbeatnik.security;

import com.github.vspiewak.jbeatnik.Application;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class AuthenticationTestIT {

    private static final String BASE_URL = "http://localhost:9000";
    //
    private RestTemplate template;

    @Before
    public void before() {

        template = new TestRestTemplate();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

    }

    private OAuth2RestTemplate authenticate(String username, String password) {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setAccessTokenUri(BASE_URL + "/oauth/token");
        resource.setClientId("jbeatnikapp");
        resource.setClientSecret("myOAuthSecret");
        resource.setUsername(username);
        resource.setPassword(password);
        return new OAuth2RestTemplate(resource);
    }

    @Test
    public void should_return_http_unauthorized_without_authentication() throws Exception {

        ResponseEntity<String> response = template.getForEntity(BASE_URL + "/api/profile", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));

    }

    @Test
    public void should_return_health_check_without_authentication() throws Exception {

        ResponseEntity<String> response = template.getForEntity(BASE_URL + "/manage/health", String.class);
        String status = JsonPath.read(response.getBody(), "$.status");

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(status, equalTo("UP"));

    }

    @Test(expected = OAuth2AccessDeniedException.class)
    public void should_return_http_unauthorized_after_bad_authentication() throws Exception {

        OAuth2RestTemplate oauth2Template = authenticate("badusername", "badpassword");
        oauth2Template.getAccessToken();

    }

    @Test
    public void should_return_access_token_after_authentication() throws Exception {

        OAuth2RestTemplate oauth2Template = authenticate("admin", "admin");
        OAuth2AccessToken accessToken = oauth2Template.getAccessToken();
        assertThat(accessToken, notNullValue());

    }

    @Test
    public void should_not_return_metrics_without_authentication() throws Exception {

        ResponseEntity<String> response = template.getForEntity(BASE_URL + "/manage/metrics", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));

    }

    @Test
    public void should_return_metrics_after_authentication() throws Exception {

        OAuth2RestTemplate oauth2Template = authenticate("admin", "admin");

        ResponseEntity<String> response = oauth2Template.getForEntity(BASE_URL + "/manage/metrics", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

    }

}