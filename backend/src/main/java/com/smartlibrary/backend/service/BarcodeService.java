package com.smartlibrary.backend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class BarcodeService {

    public byte[] generateCode128Png(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("barcode value is required");
        }

        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(value, BarcodeFormat.CODE_128, 600, 180, hints);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", output);
            return output.toByteArray();
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("Failed to generate barcode image", e);
        }
    }
}
