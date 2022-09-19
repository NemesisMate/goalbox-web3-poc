package io.github.nemesismate.goalbox.contract;

import io.github.nemesismate.goalbox.contracts.GoalboxContract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

import static io.github.nemesismate.goalbox.Web3jHelper.async;

@Service
@RequiredArgsConstructor
public class GoalboxContractService {

    private final Web3j web3j;
    private final ContractGasProvider contractGasProvider;

    private final TransactionManager systemTransactionManager;

    public Mono<GoalboxContract> deploy(String ownerAddress) {
        return async(GoalboxContract.deploy(web3j, systemTransactionManager, contractGasProvider, BigInteger.ZERO, ownerAddress));
    }

    public Mono<GoalboxContract> load(String contractAddress) {
        return Mono.fromCallable(() -> GoalboxContract.load(contractAddress, web3j, systemTransactionManager, contractGasProvider));
    }

}
