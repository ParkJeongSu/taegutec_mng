package kr.co.aim.eziframe.eziflow;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;

import ezieco.eziflow.engine.action.ActionDTOProvider;
import ezieco.eziflow.engine.action.ActionEnum;
import ezieco.eziflow.engine.action.impl.Case;
import ezieco.eziflow.engine.action.impl.ExecuteAction;
import ezieco.eziflow.engine.action.impl.Expression;
import ezieco.eziflow.engine.action.impl.Receive;
import ezieco.eziflow.engine.action.impl.Wait;
import ezieco.eziflow.engine.action.impl.While;
import ezieco.eziflow.engine.event.external.EventSubscriber;
import ezieco.eziflow.engine.event.external.data.EziFlowActionEventLog;
import ezieco.eziflow.engine.event.external.data.dto.impl.InvokeActionDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EziFlowEventSubscriber implements EventSubscriber {

	@Override
	public void onAction(EziFlowActionEventLog eventDelivery) {
		switch (eventDelivery.getEventType()) {
			case BEFORE_ACTION -> onStart(eventDelivery);
			case AFTER_ACTION -> onEnd(eventDelivery);
			case ACTION_FAILED -> onException(eventDelivery);
			case ACTION_TIMEOUT -> onTimeout(eventDelivery);
			case ACTION_INTERRUPTED -> onInterrupt(eventDelivery);
		}
	}

	private void onStart(EziFlowActionEventLog actionEventLog) {
		String actionText = getActionText(actionEventLog);

		switch (actionEventLog.getActionType()) {
			case RECEIVE -> logReceiveStart(actionText, actionEventLog);
			case CASE_OPTION -> {
				if (actionEventLog.getAction() instanceof Case caseAction) {
					log.info("{} condition={}", actionText, StringUtils.replace(caseAction.getCondition(), "\n", " "));
				}
			}
			case WHILE_LOOP -> {
				if (actionEventLog.getAction() instanceof While whilee) {
					log.info("{} condition={} collection={}",
							new Object[] { actionText, whilee.getCondition(), whilee.getCollection() });
				}
			}
			case WAIT -> {
				if (actionEventLog.getAction() instanceof Wait wait) {
					String timeout = wait.getTimeoutString();
					log.info("{} timeout={}", actionText, timeout);
				}
			}
			case EXPRESSION -> {
				if (actionEventLog.getAction() instanceof Expression expression) {
					StringBuilder sb = new StringBuilder();
					for (String function : expression.getFunctionList()) {
						sb.append("\n  ").append(function);
					}
					log.info("{}{}", actionText, sb.toString());
				}
			}
			case EXECUTE_TASK -> logExecuteTaskStart(actionEventLog);
			case INVOKE -> logInvokeAction(actionText, actionEventLog);
			default -> {
				// no-op
			}
		}

		log.info(actionEventLog.toString());
	}

	private void onEnd(EziFlowActionEventLog actionEventLog) {
		String actionText = getActionText(actionEventLog);

		switch (actionEventLog.getActionType()) {
			case RECEIVE -> logReceiveEnd(actionText, actionEventLog);
			case EXECUTE_TASK -> logExecuteTaskEnd(actionEventLog);
			case INVOKE -> logInvokeAction(actionText, actionEventLog);
			default -> {
				// no-op
			}
		}

		log.info(actionEventLog.toString());
	}

	private void onException(EziFlowActionEventLog actionEventLog) {
		String actionText = getActionText(actionEventLog);
		log.error(actionText, actionEventLog.getException());
	}

	private void onInterrupt(EziFlowActionEventLog actionEventLog) {
		String actionText = getActionText(actionEventLog);
		log.error(actionText);
	}

	private void onTimeout(EziFlowActionEventLog actionEventLog) {
		String actionText = getActionText(actionEventLog);
		if (actionEventLog.getActionType() == ActionEnum.RECEIVE
				&& actionEventLog.getAction() instanceof Receive receive) {
			if (receive.getExecuteAction() != null
					&& receive.getExecuteAction().getFlowRuntimeContext() != null
					&& receive.getExecuteAction().getFlowRuntimeContext().isReceived()) {
				String correlationVariableName = receive.getCorrelationVariableName();
				if (StringUtils.isNotBlank(correlationVariableName)
						&& receive.getExecuteAction().getFlowRuntimeContext().getVariableData() != null) {
					String correlationVariableValue = String.valueOf(receive.getExecuteAction().getFlowRuntimeContext()
							.getVariableData().getValue(correlationVariableName));
					String timeout = receive.getTimeoutString();

					log.error("{} {}={} timeout={}", actionText, correlationVariableName, correlationVariableValue, timeout);
					return;
				}
			}
		}

		log.info(actionEventLog.toString());
	}

	private String getActionText(EziFlowActionEventLog actionEventLog) {
		return actionEventLog.getActionType().getName();
	}

	/**
	 * Checks if a Receive action has been received and has a correlation variable.
	 */
	private boolean isReceivedWithCorrelation(Receive receive) {
		return receive.getExecuteAction() != null
				&& receive.getExecuteAction().getFlowRuntimeContext() != null
				&& receive.getExecuteAction().getFlowRuntimeContext().isReceived()
				&& StringUtils.isNotBlank(receive.getCorrelationVariableName());
	}

	/**
	 * Logs RECEIVE action start event.
	 */
	private void logReceiveStart(String actionText, EziFlowActionEventLog actionEventLog) {
		if (actionEventLog.getAction() instanceof Receive receive && isReceivedWithCorrelation(receive)) {
			log.info("{} {} timeout={}", actionText, receive.getCorrelationVariableName(), receive.getTimeoutString());
		}
	}

	/**
	 * Logs RECEIVE action end event.
	 */
	private void logReceiveEnd(String actionText, EziFlowActionEventLog actionEventLog) {
		if (actionEventLog.getAction() instanceof Receive receive && isReceivedWithCorrelation(receive)) {
			log.info(actionText);
		}
	}

	/**
	 * Logs EXECUTE_TASK action start event.
	 */
	private void logExecuteTaskStart(EziFlowActionEventLog actionEventLog) {
		if (actionEventLog.getAction() instanceof ExecuteAction executeAction) {
			log.info("{} {}", executeAction.getId(), actionEventLog.getAction().getName());
		} else {
			logUnexpectedActionType("EXECUTE_TASK", actionEventLog);
		}
	}

	/**
	 * Logs EXECUTE_TASK action end event with elapsed time.
	 */
	private void logExecuteTaskEnd(EziFlowActionEventLog actionEventLog) {
		if (actionEventLog.getAction() instanceof ExecuteAction executeAction) {
			if (executeAction.getStartTime() != null && executeAction.getEndTime() != null) {
				long millis = Duration.between(executeAction.getStartTime(), executeAction.getEndTime()).toMillis();
				log.info("{} {} elapsed={}ms", executeAction.getId(), actionEventLog.getAction().getName(), millis);
			} else {
				log.info("{} {}", executeAction.getId(), actionEventLog.getAction().getName());
			}
		} else {
			logUnexpectedActionType("EXECUTE_TASK", actionEventLog);
		}
	}

	/**
	 * Logs INVOKE action event (shared between onStart and onEnd).
	 */
	private void logInvokeAction(String actionText, EziFlowActionEventLog actionEventLog) {
		if (actionEventLog.getAction() instanceof ActionDTOProvider dtoProvider) {
			Object dto = dtoProvider.getDTO();

			if (dto instanceof InvokeActionDTO invokeActionDTO) {
				if (!StringUtils.equals(invokeActionDTO.getClassName(), "bpelj")) {
					log.info("{} {} {} {}", actionText, invokeActionDTO.getClassName(), 
							invokeActionDTO.getMethodName(), actionEventLog.getAction());
				} else {
					log.info("invokeWorkflow {}", invokeActionDTO.getMethodName());
				}
			} else {
				log.warn("Expected InvokeActionDTO but got: {}", dto != null ? dto.getClass().getSimpleName() : "null");
			}
		}
	}

	/**
	 * Logs warning for unexpected action type.
	 */
	private void logUnexpectedActionType(String expectedType, EziFlowActionEventLog actionEventLog) {
		log.warn("Unexpected action type for {}: {}",
				expectedType,
				actionEventLog.getAction() != null ? actionEventLog.getAction().getClass().getSimpleName() : "null");
	}
}

