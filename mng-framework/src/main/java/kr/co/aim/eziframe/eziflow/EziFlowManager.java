package kr.co.aim.eziframe.eziflow;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import ezieco.eziflow.engine.EziFlowService;
import ezieco.eziflow.engine.exception.EziFlowException;
import ezieco.eziflow.engine.impl.data.ExecutionParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;

@Slf4j
@RequiredArgsConstructor
public class EziFlowManager {

	private final EziFlowService flowService;

	public void execute(String qname, Object... arguments) throws InterruptedException, ExecutionException {
		this.flowService.execute(qname, arguments);
	}

	public void executeAsync(String qname, Object... arguments) {
		this.flowService.executeAsync(qname, arguments);
	}

	public void execute(ExecutionParams params) throws InterruptedException, ExecutionException {
		this.flowService.execute(params);
	}

	public void executeAsync(ExecutionParams params) {
		this.flowService.executeAsync(params);
	}

	public void executeBpel(String transactionId, String workflowName, Object message, String correlationId) {
		if (isCorrelation(correlationId)) {

			log.debug("will be awake bpel. correlationId={}", correlationId);
			this.flowService.awake(correlationId, message);
		} else if (isCorrelation(transactionId)) {

			log.debug("will be awake bpel. transactionId={}", transactionId);
			this.flowService.awake(transactionId, message);
		} else if (StringUtils.isNotBlank(workflowName)) {

			log.debug("will be execute bpel. workflowName={}", workflowName);

			try {
				ExecutionParams executionParams = ExecutionParams.builder(workflowName, message)
						.preAction(() -> setMDCMap(transactionId, workflowName, correlationId))
                        .postAction(MDC::clear)
						.build();

				this.flowService.executeAsync(executionParams);
			} catch (EziFlowException e) {
				log.error("Cannot execute bpel. workflowName={}", workflowName, e);
				throw new IllegalArgumentException("Workflow not found: " + workflowName, e);
			}
		} else {
			log.warn("No awake. No execute. transactionId={}, correlationId={}, workflowName={}", transactionId,
					correlationId, workflowName);
		}
	}

	private boolean isCorrelation(String correlationKey) {
		if (StringUtils.isBlank(correlationKey)) {
			return false;
		}

		return this.flowService.hasCorrelationId(correlationKey);
	}

	private void setMDCMap(String transactionId, String workflowName, String correlationId) {
		Map<String, String> map = new HashMap<>();
		map.put("transactionId", transactionId);
		map.put("event", workflowName);
		map.put("workflowName", workflowName);
		map.put("correlationId", correlationId);
		map.put("messageId", workflowName);

		MDC.setContextMap(map);
	}
	
	
	public void executeBpel(String workflowName, Object message,String transactionId) {
		 if (StringUtils.isNotBlank(workflowName)) {

			log.debug("will be execute bpel. workflowName={}", workflowName);

			try {
				ExecutionParams executionParams = ExecutionParams.builder(workflowName, message)
						.preAction(() -> setMDCMap(transactionId, workflowName))
                        .postAction(MDC::clear)
						.build();

				this.flowService.executeAsync(executionParams);
			} catch (EziFlowException e) {
				log.error("Cannot found bpel. workflowName={}", workflowName);
			}
		} else {
			log.warn("No awake. No execute. transactionId={}, workflowName={}",
					new Object[] { transactionId, workflowName });
		}
	}

	private void setMDCMap(String transactionId, String workflowName) {
		Map<String, String> map = new HashMap<>();
		map.put("transactionId", transactionId);
		map.put("workflowName", workflowName);
		map.put("messageId", workflowName);

		MDC.setContextMap(map);
	}
}

