package io.github.nemesismate.goalbox.metadata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GoalboxMetadataService {

    private final GoalBoxMetadataRepository goalBoxMetadataRepository;

    public Mono<GoalboxMetadata> save(GoalboxMetadata goalBoxMetadata) {
        return goalBoxMetadataRepository.save(goalBoxMetadata);
    }

    public Flux<GoalboxMetadata> getAll(String ownerAddress) {
        return goalBoxMetadataRepository.findAllByOwnerAddress(ownerAddress);
    }

}
