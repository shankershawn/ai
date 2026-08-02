package com.shankarsan.ai.feature.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class ActuatorSteps {

  @Autowired
  private MockMvc mockMvc;

  private MockHttpServletResponse healthResponse;

  @When("the health endpoint is requested")
  public void healthEndpointIsRequested() throws Exception {
    MvcResult result = mockMvc.perform(get("/actuator/health")).andReturn();
    healthResponse = result.getResponse();
  }

  @Then("a successful health response should be returned")
  public void successfulHealthResponseReturned() throws Exception {
    assertThat(healthResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(healthResponse.getContentAsString()).contains("UP");
  }
}
