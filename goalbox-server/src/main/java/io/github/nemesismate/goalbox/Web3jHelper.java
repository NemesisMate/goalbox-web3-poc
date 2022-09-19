package io.github.nemesismate.goalbox;

import lombok.experimental.UtilityClass;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@UtilityClass
public class Web3jHelper {

    public static <T> Mono<T> async(RemoteCall<T> remoteCall) {
        return Mono.fromCompletionStage(remoteCall.sendAsync());
    }

    public static <T> Mono<T> async(RemoteFunctionCall<T> remoteFunctionCall) {
        return Mono.fromCompletionStage(remoteFunctionCall.sendAsync());
    }

    public static  <T, U> Function<T, Mono<U>> async(Function<T, RemoteFunctionCall<U>> remoteFunctionCallSupplier) {
        return element -> async(remoteFunctionCallSupplier.apply(element));
    }

}
