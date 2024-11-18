package com.github.kuramastone.pokeparticles.common.utils;

import org.joml.Vector3f;

public class StringUtils {
    public static Vector3f splitIntoVec3(String data) {
        String[] val = data.split("/");
        if(val.length != 3) {
            throw new IllegalArgumentException("Unable to parse data string '%s' into three values. It should be formatted like '0/1.1/2.0'!");
        }
        float x = Float.parseFloat(val[0]) / 255.0f;
        float y = Float.parseFloat(val[1]) / 255.0f;
        float z = Float.parseFloat(val[2]) / 255.0f;
        return new Vector3f(x, y, z);
    }
}
