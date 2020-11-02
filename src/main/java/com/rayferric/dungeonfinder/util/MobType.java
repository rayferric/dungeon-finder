package com.rayferric.dungeonfinder.util;

import org.jetbrains.annotations.NotNull;

public enum MobType {
    NONE("none"),
    CREEPER("creeper"),
    SKELETON("skeleton"),
    SPIDER("spider"),
    GIANT("giant"),
    ZOMBIE("zombie"),
    SLIME("slime"),
    GHAST("ghast"),
    ZOMBIE_PIGMAN("zombie_pigman"),
    ENDERMAN("enderman"),
    CAVE_SPIDER("cave_spider"),
    SILVERFISH("silverfish"),
    BLAZE("blaze"),
    MAGMA_CUBE("magma_cube"),
    ENDER_DRAGON("ender_dragon"),
    WITHER("wither"),
    BAT("bat"),
    WITCH("witch"),
    ENDERMITE("endermite"),
    GUARDIAN("guardian"),
    SHULKER("shulker"),
    PIG("pig"),
    SHEEP("sheep"),
    COW("cow"),
    CHICKEN("chicken"),
    SQUID("squid"),
    WOLF("wolf"),
    MOOSHROOM("mooshroom"),
    SNOW_GOLEM("snowman"),
    OCELOT("ocelot"),
    IRON_GOLEM("villager_golem"),
    HORSE("horse");

    @Override
    public String toString() {
        return id;
    }

    public static MobType findById(@NotNull String id) {
        for(MobType mob : values()) {
            if(mob.id.equalsIgnoreCase(id))
                return mob;
        }
        return NONE;
    }

    private final String id;

    MobType(@NotNull String id) {
        this.id = id;
    }
}
