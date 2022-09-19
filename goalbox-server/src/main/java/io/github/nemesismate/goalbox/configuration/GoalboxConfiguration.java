package io.github.nemesismate.goalbox.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

@EnableConfigurationProperties(GoalboxProperties.class)
@Configuration
@RequiredArgsConstructor
public class GoalboxConfiguration {

    private final GoalboxProperties properties;

    @Bean
    public Web3j web3j() {
        Web3jService web3jService = new HttpService(properties.getWeb3j().getClientAddress());
        return Web3j.build(web3jService);
    }

    @Bean
    ContractGasProvider contractGasProvider() {
        return new StaticGasProvider(properties.getGasPrice(), properties.getGasLimit());
    }

    @Bean
    TransactionManager systemTransactionManager() {
        return new RawTransactionManager(web3j(), Credentials.create(properties.getAccountKeys().getSystem()), properties.getChainId());
    }

}
