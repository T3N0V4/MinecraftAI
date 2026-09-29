package com.jano.minecraftai.ai;

public class AIService {

    private final AIModelRouter router;

    public AIService(
            AIModelRouter router
    ) {
        this.router = router;
    }

    public AIResponse respond(
            AIRequest request
    ) {
        return router.respond(request);
    }
}
