package com.eternalcode.combat.border.animation.particle;

import com.eternalcode.combat.border.BorderPoint;
import com.github.retrooper.packetevents.protocol.color.AlphaColor;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleColorData;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleData;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleDustData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.awt.Color;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class ParticleSettings extends OkaeriConfig {

    @Comment("# Enable particle animation?")
    public boolean enabled = true;

    @Comment("# Particle type - https://javadocs.packetevents.com/com/github/retrooper/packetevents/protocol/particle/type/ParticleTypes.html")
    public ParticleType type = ParticleTypes.DUST;
    @Comment({
        " ",
        "# Particle color (used only for DUST or ENTITY_EFFECT particle type)",
        "# You can set hex color e.g. \"#ca4c45\" or use \"RAINBOW\" to generate rainbow gradient based on x and z coordinates."
    })
    public ParticleColor color = ParticleColor.RAINBOW;
    public int count = 5;
    public float scale = 1.0F;
    public float maxSpeed  = 0.0F;
    public float offsetX = 0.2F;
    public float offsetY = 0.2F;
    public float offsetZ = 0.2F;

    public WrapperPlayServerParticle getParticle(BorderPoint point) {
        return getParticle(point, type);
    }

    private <T extends ParticleData>  WrapperPlayServerParticle getParticle(BorderPoint point, ParticleType<T> type) {
        T particleData = this.createData(type, point);
        Particle<?> dust = new Particle<>(type, particleData);
        return new WrapperPlayServerParticle(
            dust,
            true,
            new Vector3d(point.x(), point.y(), point.z()),
            new Vector3f(orElse(offsetX, 0.1F), orElse(offsetY, 0.1F), orElse(offsetZ, 0.1F)),
            orElse(maxSpeed, 0.0F),
            count,
            true
        );
    }

    private <T> T orElse(T nullable, T or) {
        return nullable != null ? nullable : or;
    }

    @SuppressWarnings("unchecked")
    private <T extends ParticleData> T createData(ParticleType<T> type, BorderPoint point) {
        if (type.equals(ParticleTypes.DUST)) {
            Color color = this.color.getColor(point);
            return (T) new ParticleDustData(orElse(scale, 1.0F), color.getRed(), color.getGreen(), color.getBlue());
        }

        if (type.equals(ParticleTypes.ENTITY_EFFECT)) {
            Color color = this.color.getColor(point);
            AlphaColor alphaColor = new AlphaColor(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
            return (T) new ParticleColorData(alphaColor);
        }

        return ParticleData.emptyData();
    }

}
