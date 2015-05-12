package io.autoscaling.devopscon.util;

import io.autoscaling.devopscon.HttpRequestProcessorVerticle;
import io.autoscaling.devopscon.StarterVerticle;
import io.autoscaling.devopscon.cache.RedisReadVerticle;

/**
 * Created by sascha.moellering on 12/05/2015.
 */
public final class VerticleNames {
    private  VerticleNames() {

    }

    public static final String STARTER_VERTICLE = StarterVerticle.class.getName();
    public static final String HTTP_REQUEST_PROCESSOR_VERTICLE = HttpRequestProcessorVerticle.class.getName();
    public static final String REDIS_READ_VERTICLE = RedisReadVerticle.class.getName();
}
