package io.github.nemesismate.goalbox;

import io.github.nemesismate.goalbox.contract.GoalboxContractService;
import io.github.nemesismate.goalbox.contracts.GoalboxContract;
import io.github.nemesismate.goalbox.metadata.GoalboxMetadata;
import io.github.nemesismate.goalbox.metadata.GoalboxMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.github.nemesismate.goalbox.Web3jHelper.async;

@Service
@RequiredArgsConstructor
public class GoalboxService {

    private final GoalboxMetadataService goalboxMetadataService;
    private final GoalboxContractService goalboxContractService;
    private final GoalboxMapper goalboxMapper;

    public Mono<Goalbox> create(Goalbox goalbox) {
        return goalboxContractService.deploy(goalbox.ownerAddress())
                .flatMap(account -> goalboxMetadataService.save(goalboxMapper.map(goalbox, account.getContractAddress()))
                        .flatMap(metadata -> map(account, metadata)))
                .cache(); // Avoids cancelling
    }

    public Flux<Goalbox> get(String ownerAddress) {
        return goalboxMetadataService.getAll(ownerAddress)
                .flatMap(metadata -> goalboxContractService.load(metadata.getContractAddress())
                        .flatMap(account -> map(account, metadata)));
    }

    Mono<Goalbox> map(GoalboxContract contract, GoalboxMetadata metadata) {
        return Mono.zip(
                async(contract.getValue()),
                async(contract.isSettled())
        ).map(contractData -> goalboxMapper.mapWithContractData(metadata, contractData.getT1(), contractData.getT2()));
    }

}
