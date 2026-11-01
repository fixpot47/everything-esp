package io.github.fixpot47.flamehighlight.mixin;

import io.github.fixpot47.flamehighlight.FlameConfig;
import net.minecraft.client.particle.FlameParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Modifies vanilla flame sprites only, leaving all other particle classes untouched. */
@Mixin(FlameParticle.class)
public abstract class FlameParticleMixin {
    @Inject(method = "getQuadSize", at = @At("RETURN"), cancellable = true)
    private void flamehighlight$quadSize(float partialTick, CallbackInfoReturnable<Float> cir) {
        if (FlameConfig.enabled) {
            cir.setReturnValue(cir.getReturnValue() * 1.85f);
        }
    }

    @Inject(method = "getLightColor", at = @At("RETURN"), cancellable = true)
    private void flamehighlight$fullBright(float partialTick, CallbackInfoReturnable<Integer> cir) {
        if (FlameConfig.enabled) {
            cir.setReturnValue(0x00F000F0);
        }
    }
}
