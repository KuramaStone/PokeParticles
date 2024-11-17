package com.github.kuramastone.pokeparticles.common.utils;

import net.minecraft.util.math.Vec3d;

public class ParticleMath {

    public static Vec3d rotateAroundAxisX(Vec3d v, double degrees) {
        double y = v.getY();
        double z = v.getZ();
        double cosAngle = Math.cos(Math.toRadians(degrees));
        double sinAngle = Math.sin(Math.toRadians(degrees));

        double newY = y * cosAngle - z * sinAngle;
        double newZ = y * sinAngle + z * cosAngle;

        return new Vec3d(v.getX(), newY, newZ);
    }

    public static Vec3d rotateAroundAxisY(Vec3d v, double degrees) {
        double x = v.getX();
        double z = v.getZ();
        double cosAngle = Math.cos(Math.toRadians(degrees));
        double sinAngle = Math.sin(Math.toRadians(degrees));

        double newX = x * cosAngle + z * sinAngle;
        double newZ = -x * sinAngle + z * cosAngle;

        return new Vec3d(newX, v.getY(), newZ);
    }

}