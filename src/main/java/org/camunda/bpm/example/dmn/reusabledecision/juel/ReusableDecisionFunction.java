package org.camunda.bpm.example.dmn.reusabledecision.juel;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.deploy.cache.DeploymentCache;
import org.camunda.bpm.engine.variable.context.VariableContext;

public class ReusableDecisionFunction {

  public static Object singleResult(String decisionKey, VariableContext variables) {
    return evaluate(decisionKey, variables).getFirstResult().getSingleEntry();
  }

  public static DmnDecisionTableResult evaluate(String decisionKey, VariableContext variables) {
    final var dmnEngine = Context.getProcessEngineConfiguration().getDmnEngine();
    DeploymentCache deploymentCache = Context.getProcessEngineConfiguration().getDeploymentCache();
    DmnDecision decisionDefinition = (DmnDecision) deploymentCache.findDeployedLatestDecisionDefinitionByKey(
      decisionKey);

    System.out.println("Evaluating reusable decision: " + decisionDefinition);

    return dmnEngine.evaluateDecisionTable(decisionDefinition, variables);
  }

}
