package org.camunda.bpm.example.dmn.reusabledecision.juel;

import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.util.ReflectUtil;
import org.camunda.bpm.engine.variable.context.VariableContext;
import org.springframework.stereotype.Component;

@Component
public class ReusableDecisionPlugin extends AbstractProcessEnginePlugin {

  @Override
  public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    processEngineConfiguration.getExpressionManager()
      .addFunction("dmn", ReflectUtil.getMethod(ReusableDecisionFunction.class, "singleResult",
        String.class, VariableContext.class));

    System.out.println("Registered engine plugin: ReusableDecisionPlugin");
  }
}
