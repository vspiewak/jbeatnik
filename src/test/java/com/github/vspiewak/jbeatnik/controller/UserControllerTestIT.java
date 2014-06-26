package com.github.vspiewak.jbeatnik.controller;

import com.github.vspiewak.jbeatnik.Application;
import com.github.vspiewak.jbeatnik.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({"server.port=9000", "management.port=9001"})
public class UserControllerTestIT {

    RestTemplate template = new TestRestTemplate();

    @Test
    public void should_return_application_json_content_type_when_get_users() throws Exception {
        HttpHeaders headers = template.getForEntity("http://localhost:9000/user", String.class).getHeaders();
        assertTrue(headers.getContentType().isCompatibleWith(MediaType.APPLICATION_JSON));
    }

}