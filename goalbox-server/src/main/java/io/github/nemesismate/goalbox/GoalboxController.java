package io.github.nemesismate.goalbox;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/goalbox", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GoalboxController {

    private final GoalboxService goalboxService;

    @CrossOrigin(origins = "*")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Goalbox> create(@RequestBody Goalbox goalbox) {
        return goalboxService.create(goalbox);
    }

    @CrossOrigin(origins = "*")
    @GetMapping
    public Flux<Goalbox> get(@RequestParam String ownerAddress) {
        return goalboxService.get(ownerAddress);
    }

}
