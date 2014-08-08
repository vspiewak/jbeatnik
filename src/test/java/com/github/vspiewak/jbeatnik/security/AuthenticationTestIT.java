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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class AuthenticationTestIT {

    private static final String BASE_URL = "http://localhost:9000";
    private RestTemplate template;

    @Before
    public void before() {
        template = new TestRestTemplate();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    public void should_return_http_unauthorized_without_authentication() throws Exception {

        ResponseEntity<String> response = template.getForEntity(BASE_URL, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));

    }

    @Test
    public void should_return_health_check_without_authentication() throws Exception {

        ResponseEntity<String> response = template.getForEntity(BASE_URL + "/manage/health", String.class);
        String status = JsonPath.read(response.getBody(), "$.status");

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(status, equalTo("UP"));

    }

    @Test
    public void should_return_http_unauthorized_after_bad_admin_authentication() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "admin");
        params.add("password", "badpassword");

        ResponseEntity<String> response = template.postForEntity(BASE_URL + "/auth/login", params, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));

    }

    @Test
    public void should_return_http_ok_after_admin_authentication() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "admin");
        params.add("password", "admin");

        ResponseEntity<String> response = template.postForEntity(BASE_URL + "/auth/login", params, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

    }

    @Test
    public void should_return_http_unauthorized_after_logout() throws Exception {

        ResponseEntity<String> response = template.getForEntity(BASE_URL + "/auth/logout", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        response = template.getForEntity(BASE_URL + "/user", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));

    }

    @Test
    public void should_return_user_after_authentification_then_unauthorized_after_logout() throws Exception {

        ResponseEntity<String> response = template.getForEntity(BASE_URL + "/user", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "admin");
        params.add("password", "admin");

        response = template.postForEntity(BASE_URL + "/auth/login", params, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        response = template.getForEntity(BASE_URL + "/user", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        response = template.getForEntity(BASE_URL + "/auth/logout", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        response = template.getForEntity(BASE_URL + "/user", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));

    }

}