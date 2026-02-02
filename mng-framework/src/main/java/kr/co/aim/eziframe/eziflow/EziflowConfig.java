package kr.co.aim.eziframe.eziflow;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ezieco.eziflow.engine.EziFlowBootstrapper;
import ezieco.eziflow.engine.EziFlowService;
import ezieco.eziflow.engine.config.EziFlowConfigurator;
import ezieco.eziflow.engine.core.EziFlowAppContext;
import ezieco.eziflow.engine.event.external.EventSubscriber;
import ezieco.eziflow.engine.management.EziFlowMeterRegistryProvider;
import ezieco.eziflow.engine.management.impl.EziFlowMeterRegistryProviderImpl;
import ezieco.eziflow.engine.spring.boot.property.EziFlowYmlConfigBinder;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnClass(EziFlowService.class)
@Profile({"pex","tex","scheduler"})
public class EziflowConfig {

    @Bean
    @ConditionalOnMissingBean
    EziFlowMeterRegistryProvider eziFlowMeterRegistryProvider(MeterRegistry meterRegistry) {
        return new EziFlowMeterRegistryProviderImpl(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    EziFlowConfigurator flowConfig(EziFlowYmlConfigBinder configBinder, EziFlowAppContext flowAppContext,
                                   EziFlowMeterRegistryProvider meterRegistryProvider) {

        return EziFlowConfigurator.builder(configBinder.getEziFlowProperties(), flowAppContext)
                .withMeterRegistryProvider(meterRegistryProvider).build();
    }

    @Bean
    @ConditionalOnMissingBean
    EventSubscriber eziFlowEventSubscriber() {
        return new EziFlowEventSubscriber();
    }

    @Bean
    @ConditionalOnMissingBean
    EziFlowService eziFlowService(EziFlowConfigurator eziFlowConfigurator, EventSubscriber eventSubscriber) {

        EziFlowService flowService = EziFlowBootstrapper.run(eziFlowConfigurator);
        flowService.registerSubscriber(eventSubscriber);

        return flowService;
    }

    @Bean
    @ConditionalOnMissingBean
    EziFlowManager eziFlowManager(EziFlowService eziFlowService) {
        return new EziFlowManager(eziFlowService);
    }
}