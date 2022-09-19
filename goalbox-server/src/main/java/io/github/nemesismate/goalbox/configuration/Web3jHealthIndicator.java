package io.github.nemesismate.goalbox.configuration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.NetListening;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Health check indicator for {@link Web3j}
 */
@RequiredArgsConstructor
@Component
public class Web3jHealthIndicator extends AbstractReactiveHealthIndicator {

    @NonNull
    private final Web3j web3j;

    @Override
    protected Mono<Health> doHealthCheck(Health.Builder builder) {
        return Mono.fromCompletionStage(web3j.netListening().sendAsync())
                .filter(NetListening::isListening)
                .flatMap(ignored -> Mono.fromCompletionStage(populateDetails(builder)).then(Mono.fromCallable(builder::up)))
                .switchIfEmpty(Mono.fromCallable(builder::down))
                .onErrorResume(error -> Mono.fromCallable(() -> {
                    error.printStackTrace(); return builder.down(error);
                }))
                .map(Health.Builder::build);
    }

    private CompletionStage<Void> populateDetails(Health.Builder builder) {
        return CompletableFuture.allOf(netVersion(builder), clientVersion(builder), blockNumber(builder), protocolVersion(builder), netPeerCount(builder));
    }

    private CompletableFuture<Health.Builder> netVersion(Health.Builder builder) {
        return web3j.netVersion()
                .sendAsync()
                .thenApply(netVersion -> builder.withDetail("netVersion", netVersion.getNetVersion()));
    }

    private CompletableFuture<Health.Builder> clientVersion(Health.Builder builder) {
        return web3j.web3ClientVersion()
                .sendAsync()
                .thenApply(web3ClientVersion -> builder.withDetail("clientVersion", web3ClientVersion.getWeb3ClientVersion()));
    }

    private CompletableFuture<Health.Builder> blockNumber(Health.Builder builder) {
        return web3j.ethBlockNumber()
                .sendAsync()
                .thenApply(ethBlockNumber -> builder.withDetail("blockNumber", ethBlockNumber.getBlockNumber()));
    }

    private CompletableFuture<Health.Builder> protocolVersion(Health.Builder builder) {
        return web3j.ethProtocolVersion()
                .sendAsync()
                .thenApply(ethProtocolVersion -> builder.withDetail("protocolVersion", String.valueOf(ethProtocolVersion.getProtocolVersion())));
    }

    private CompletableFuture<Health.Builder> netPeerCount(Health.Builder builder) {
        return web3j.ethProtocolVersion()
                .sendAsync()
                .thenApply(ethProtocolVersion -> builder.withDetail("netPeerCount", String.valueOf(ethProtocolVersion.getProtocolVersion())));
    }
}
