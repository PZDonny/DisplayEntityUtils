package net.donnypz.displayentityutils.utils.DisplayEntities;

import com.google.gson.*;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticleBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Type;
import java.util.HashSet;

@ApiStatus.Internal
final class JSONAdapter_FramePoint implements JsonDeserializer<FramePoint> {

    JSONAdapter_FramePoint(){}

    @Override
    public FramePoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        JsonElement particlesEl = obj.get("particles");
        obj.remove("particles");
        FramePoint fp = new Gson().fromJson(obj, FramePoint.class);

        if (particlesEl != null && particlesEl.isJsonArray()) {
            JsonArray arr = particlesEl.getAsJsonArray();
            fp.particles = new HashSet<>();
            for (JsonElement elem : arr) {
                AnimationParticle particle = deserializeParticle(elem.getAsJsonObject(), ctx);
                if (particle != null){
                    particle.initializeParticle();
                    fp.addParticle(particle);
                }
            }
        }

        return fp;
    }

    private AnimationParticle deserializeParticle(JsonObject obj, JsonDeserializationContext ctx) {
        String particleName = obj.get("particleName").getAsString();
        return ctx.deserialize(obj, AnimationParticleBuilder.getAnimationParticleClass(particleName));
    }
}