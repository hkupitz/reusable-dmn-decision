package org.camunda.bpm.example.dmn.test;

import static org.assertj.core.api.Assertions.entry;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.junit.Assert.assertEquals;

import java.util.Map;
import org.camunda.bpm.dmn.engine.DmnDecisionResultEntries;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

public class ReusableDecisionTest {

  @Rule
  public ProcessEngineRule processEngineRule = new ProcessEngineRule();

  @Test
  @Deployment(resources = {"drd/drd_chaining.dmn"})
  public void testReusableDecision_viaDRDChaining() {
    DecisionService decisionService = processEngineRule.getDecisionService();

    DmnDecisionResultEntries mainDecisionOneResult = decisionService
      .evaluateDecisionByKey("drd_customerAcceptanceDecision")
      .variables(Map.of("customerType", "special",
                        "customerHistory", "bad",
                        "driversLicenseYears", 4))
      .evaluate()
      .getSingleResult();

    DmnDecisionResultEntries mainDecisionTwoResult = decisionService
      .evaluateDecisionByKey("drd_customerRiskMessage")
      .variables(Map.of("customerType", "special",
                        "customerHistory", "bad"))
      .evaluate()
      .getSingleResult();

    assertEquals(mainDecisionOneResult.get("decision"), "accepted");
    assertEquals(mainDecisionTwoResult.get("riskMessage"), "The customer has an acceptable risk.");
  }

  @Test
  @Deployment(resources = {"plugin/plugin_reusable_decision.dmn",
    "plugin/plugin_main_decision_one.dmn", "plugin/plugin_main_decision_two.dmn"})
  public void testReusableDecision_viaPlugin() {
    DecisionService decisionService = processEngineRule.getDecisionService();

    DmnDecisionResultEntries mainDecisionOneResult = decisionService
      .evaluateDecisionByKey("plugin_customerAcceptanceDecision")
      .variables(Map.of("customerType", "special",
                        "customerHistory", "bad",
                        "driversLicenseYears", 4))
      .evaluate()
      .getSingleResult();

    DmnDecisionResultEntries mainDecisionTwoResult = decisionService
      .evaluateDecisionByKey("plugin_customerRiskMessage")
      .variables(Map.of("customerType", "special",
        "customerHistory", "bad"))
      .evaluate()
      .getSingleResult();

    assertEquals(mainDecisionOneResult.get("decision"), "accepted");
    assertEquals(mainDecisionTwoResult.get("riskMessage"), "The customer has an acceptable risk.");
  }

  @Test
  @Deployment(resources = {"bpmn/bpmn_chaining.bpmn", "bpmn/bpmn_reusable_decision.dmn",
    "bpmn/bpmn_main_decision_one.dmn", "bpmn/bpmn_main_decision_two.dmn"})
  public void testReusableDecision_viaBPMN() {
    ProcessInstance processInstance = processEngineRule.getRuntimeService()
      .startProcessInstanceByKey("bpmnDecisionChaining", Map.of("customerType", "special",
                                                                "customerHistory", "bad",
                                                                "driversLicenseYears", 4));

    assertThat(processInstance).isStarted();

    complete(task());

    assertThat(processInstance).variables().contains(entry("generalRisk", "medium"),
                                                     entry("customerAcceptance", "accepted"),
                                                     entry("customerRiskMessage", "The customer has an acceptable risk."));
  }
}
