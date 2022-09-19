package io.github.nemesismate.goalbox.metadata;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface GoalBoxMetadataRepository extends ReactiveCrudRepository<GoalboxMetadata, UUID> {

    Flux<GoalboxMetadata> findAllByOwnerAddress(String ownerAddress);

}
