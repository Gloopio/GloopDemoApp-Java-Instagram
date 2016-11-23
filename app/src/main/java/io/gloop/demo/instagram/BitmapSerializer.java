package io.gloop.demo.instagram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Map;

import io.gloop.serializers.GloopSerializeToString;

/**
 * Created by Alex Untertrifaller on 23.11.16.
 */

public class BitmapSerializer extends GloopSerializeToString<Bitmap> {

    @Override
    public String serialize(Bitmap o, Map map) {

        // TODO find better implementation

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        o.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return Arrays.toString(stream.toByteArray());
    }

    @Override
    public Bitmap deserialize(String s, Map map) {
//        byte[] image = s.getBytes();

        // TODO find better implementation

        s = s.substring(1, s.length() - 1);
        String[] split = s.split(", ");
        byte[] tmp = new byte[split.length];
        int i = 0;
        for (String t : split) {
            tmp[i++] = Byte.decode(t);
        }

        return BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
    }
}
