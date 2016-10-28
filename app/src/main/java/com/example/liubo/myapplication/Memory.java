package com.example.liubo.myapplication;

import java.nio.ByteOrder;

import dalvik.bytecode.Opcodes;

/**
 * Created by liubo on 16/7/25.
 */
public final class Memory {

    private Memory() {
    }

    public static int peekInt(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int offset2 = offset + 1;
            offset = offset2 + 1;
            offset2 = offset + 1;
            int i = ((((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 24) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 16)) | ((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 8)) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 0);
            offset = offset2;
            return i;
        }
        int offset2 = offset + 1;
        offset = offset2 + 1;
        offset2 = offset + 1;
        int i = ((((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 0) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 8)) | ((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 16)) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 24);
        offset = offset2;
        return i;
    }

    public static long peekLong(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int offset2 = offset + 1;
            offset = offset2 + 1;
            offset2 = offset + 1;
            offset = offset2 + 1;
            int h = ((((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 24) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 16)) | ((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 8)) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 0);
            offset2 = offset + 1;
            offset = offset2 + 1;
            offset2 = offset + 1;
            int l = ((((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 24) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 16)) | ((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 8)) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 0);
            offset = offset2;
            return (((long) h) << 32) | (((long) l) & 4294967295L);
        }
        int offset2 = offset + 1;
        offset = offset2 + 1;
        offset2 = offset + 1;
        offset = offset2 + 1;
        int l = ((((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 0) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 8)) | ((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 16)) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 24);
        offset2 = offset + 1;
        offset = offset2 + 1;
        offset2 = offset + 1;
        long j = (((long) (((((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 0) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 8)) | ((src[offset] & Opcodes.OP_CONST_CLASS_JUMBO) << 16)) | ((src[offset2] & Opcodes.OP_CONST_CLASS_JUMBO) << 24))) << 32) | (((long) l) & 4294967295L);
        offset = offset2;
        return j;
    }

    public static short peekShort(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            return (short) ((src[offset] << 8) | (src[offset + 1] & Opcodes.OP_CONST_CLASS_JUMBO));
        }
        return (short) ((src[offset + 1] << 8) | (src[offset] & Opcodes.OP_CONST_CLASS_JUMBO));
    }

    public static void pokeInt(byte[] dst, int offset, int value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int i = offset + 1;
            dst[offset] = (byte) ((value >> 24) & Opcodes.OP_CONST_CLASS_JUMBO);
            offset = i + 1;
            dst[i] = (byte) ((value >> 16) & Opcodes.OP_CONST_CLASS_JUMBO);
            i = offset + 1;
            dst[offset] = (byte) ((value >> 8) & Opcodes.OP_CONST_CLASS_JUMBO);
            dst[i] = (byte) ((value >> 0) & Opcodes.OP_CONST_CLASS_JUMBO);
            offset = i;
            return;
        }
        int i = offset + 1;
        dst[offset] = (byte) ((value >> 0) & Opcodes.OP_CONST_CLASS_JUMBO);
        offset = i + 1;
        dst[i] = (byte) ((value >> 8) & Opcodes.OP_CONST_CLASS_JUMBO);
        i = offset + 1;
        dst[offset] = (byte) ((value >> 16) & Opcodes.OP_CONST_CLASS_JUMBO);
        dst[i] = (byte) ((value >> 24) & Opcodes.OP_CONST_CLASS_JUMBO);
        offset = i;
    }

    public static void pokeLong(byte[] dst, int offset, long value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int i = (int) (value >> 32);
            int i2 = offset + 1;
            dst[offset] = (byte) ((i >> 24) & Opcodes.OP_CONST_CLASS_JUMBO);
            offset = i2 + 1;
            dst[i2] = (byte) ((i >> 16) & Opcodes.OP_CONST_CLASS_JUMBO);
            i2 = offset + 1;
            dst[offset] = (byte) ((i >> 8) & Opcodes.OP_CONST_CLASS_JUMBO);
            offset = i2 + 1;
            dst[i2] = (byte) ((i >> 0) & Opcodes.OP_CONST_CLASS_JUMBO);
            i = (int) value;
            i2 = offset + 1;
            dst[offset] = (byte) ((i >> 24) & Opcodes.OP_CONST_CLASS_JUMBO);
            offset = i2 + 1;
            dst[i2] = (byte) ((i >> 16) & Opcodes.OP_CONST_CLASS_JUMBO);
            i2 = offset + 1;
            dst[offset] = (byte) ((i >> 8) & Opcodes.OP_CONST_CLASS_JUMBO);
            dst[i2] = (byte) ((i >> 0) & Opcodes.OP_CONST_CLASS_JUMBO);
            offset = i2;
            return;
        }
        int i = (int) value;
        int i2 = offset + 1;
        dst[offset] = (byte) ((i >> 0) & Opcodes.OP_CONST_CLASS_JUMBO);
        offset = i2 + 1;
        dst[i2] = (byte) ((i >> 8) & Opcodes.OP_CONST_CLASS_JUMBO);
        i2 = offset + 1;
        dst[offset] = (byte) ((i >> 16) & Opcodes.OP_CONST_CLASS_JUMBO);
        offset = i2 + 1;
        dst[i2] = (byte) ((i >> 24) & Opcodes.OP_CONST_CLASS_JUMBO);
        i = (int) (value >> 32);
        i2 = offset + 1;
        dst[offset] = (byte) ((i >> 0) & Opcodes.OP_CONST_CLASS_JUMBO);
        offset = i2 + 1;
        dst[i2] = (byte) ((i >> 8) & Opcodes.OP_CONST_CLASS_JUMBO);
        i2 = offset + 1;
        dst[offset] = (byte) ((i >> 16) & Opcodes.OP_CONST_CLASS_JUMBO);
        dst[i2] = (byte) ((i >> 24) & Opcodes.OP_CONST_CLASS_JUMBO);
        offset = i2;
    }

    public static void pokeShort(byte[] dst, int offset, short value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int offset2 = offset + 1;
            dst[offset] = (byte) ((value >> 8) & Opcodes.OP_CONST_CLASS_JUMBO);
            dst[offset2] = (byte) ((value >> 0) & Opcodes.OP_CONST_CLASS_JUMBO);
            offset = offset2;
            return;
        }
        int offset2 = offset + 1;
        dst[offset] = (byte) ((value >> 0) & Opcodes.OP_CONST_CLASS_JUMBO);
        dst[offset2] = (byte) ((value >> 8) & Opcodes.OP_CONST_CLASS_JUMBO);
        offset = offset2;
    }

}